package com.wensheng.zcc.sso.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wensheng.zcc.sso.dao.mysql.mapper.AmcWechatUserMapper;
import com.wensheng.zcc.sso.module.helper.AmcPermEnum;
import com.wensheng.zcc.sso.module.helper.AmcRolesEnum;
import com.wensheng.zcc.sso.module.vo.WechatCode2SessionVo;
import com.wensheng.zcc.sso.service.AmcSsoService;
import com.wensheng.zcc.sso.service.KafkaService;
import com.wensheng.zcc.sso.service.UserService;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
@Service
@Slf4j
public class AmcSsoServiceImpl implements AmcSsoService {

  @Value("${weixin.loginUrl}")
  String loginUrl;

  @Value("${weixin.open.loginUrl}")
  String loginOpenUrl;

  @Value("${weixin.open.getUserInfoUrl}")
  String getUserInfoUrl;

  @Value("${spring.security.oauth2.client.registration.amc-client.client-id}")
  private String amcClientId;

  @Value("${spring.security.oauth2.client.registration.amc-client.secret}")
  private String amcSecret;


  @Value("${spring.security.oauth2.client.registration.amc-client.scopes}")
  private String amcScopes;

  @Value("${spring.security.oauth2.client.registration.amc-client.authorizedGrantTypes}")
  private String amcAuthorizedGrantTypes;

  @Value("${spring.security.oauth2.client.registration.amc-client.redirectUris}")
  private String amcRedirectUris;


  @Value("${weixin.appId}")
  String appId;

  @Value("${weixin.appSecret}")
  String appSecret;

  @Autowired
  KafkaService kafkaService;

  private final int accessTokenValidSeconds = 365*24*60*60;


  private int refreshTokenValidSeconds = 2592000;

  private RestTemplate restTemplate = new RestTemplate();

  private Gson gson = new Gson();


  @Autowired
  UserService userService;

  @Autowired
  TokenStore tokenStore;

  @Autowired
  DefaultTokenServices tokenServices ;

  @Autowired
  TokenEnhancer tokenEnhancer;

  @Autowired
  JwtAccessTokenConverter accessTokenConverter;



  public static final int INIT_VECTOR_LENGTH = 16;




  private InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService() ;

  @PostConstruct
  private  void init(){
    GsonBuilder gson = new GsonBuilder();
    restTemplate.getMessageConverters().removeIf(item -> item instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().removeIf(item -> item instanceof MappingJackson2XmlHttpMessageConverter);
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));

    gsonHttpMessageConverter.setGson(gson.create());

    restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
    BaseClientDetails baseClientDetails = new BaseClientDetails();
    baseClientDetails.setClientId(amcClientId);
    baseClientDetails.setAccessTokenValiditySeconds(accessTokenValidSeconds);
    if(!StringUtils.isEmpty(amcAuthorizedGrantTypes)){
      baseClientDetails.setAuthorizedGrantTypes(Arrays.stream(amcAuthorizedGrantTypes.split(",")).collect(Collectors.toList()));
    }
    baseClientDetails.setClientSecret(amcSecret);
    if(!StringUtils.isEmpty(amcScopes)){
      baseClientDetails.setScope(Arrays.stream(amcScopes.split(",")).collect(Collectors.toList()));
    }
    Map<String, BaseClientDetails> clientParam = new HashMap<>();
    clientParam.put(amcClientId, baseClientDetails);
//    clientDetailsService.setClientDetailsStore(clientParam);
//    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, accessTokenConverter));




//    tokenWechatServices.setClientDetailsService(clientDetailsService);
  }



  private OAuth2AccessToken generateTokenByUserId(Long userId){
    HashMap<String, String> authorizationParameters = new HashMap<String, String>();
    authorizationParameters.put("scope", amcScopes);
    UserDetails userDetails = userService.loadUserByUserId(userId);

    authorizationParameters.put("client_id", amcClientId);
    authorizationParameters.put("grant", amcAuthorizedGrantTypes);

    List<String> scopes = new ArrayList<>();
    if(!StringUtils.isEmpty(amcScopes)){
      Arrays.stream(amcScopes.split(",")).forEach(item -> scopes.add(item.trim()));
    }
    Set<String> scopesSet = scopes.stream().collect(Collectors.toSet());

    OAuth2Request authorizationRequest = new OAuth2Request(authorizationParameters, amcClientId, userDetails.getAuthorities(),true,
        scopesSet,
        null,
        amcRedirectUris, null, null);



    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
        null, userDetails.getAuthorities()) ;

    OAuth2Authentication auth2Authentication = new OAuth2Authentication(authorizationRequest, authenticationToken);


    OAuth2AccessToken token = tokenServices.createAccessToken(auth2Authentication);
//    token = accessTokenConverter.enhance(token, auth2Authentication);

    return token;
  }





  public String decodePhone(String encryptedData, String iv, String sessionKey){
    try {
      byte[] sessionKeyBytes = Base64.decode(sessionKey);
      byte[] encryptedDataBytes = Base64.decode(encryptedData);
      byte[] ivBytes =  Base64.decode(iv);

      Security.addProvider(new BouncyCastleProvider());


      SecretKeySpec skeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
      AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
      parameters.init(new IvParameterSpec(ivBytes, 0, INIT_VECTOR_LENGTH));
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(ivBytes, 0, INIT_VECTOR_LENGTH));

      byte[] decrypted = cipher.doFinal(encryptedDataBytes);
      if (null != decrypted && decrypted.length > 0) {
        String result = new String(decrypted, "UTF-8");
        return  result;
      }
    } catch (Exception ex) {
      log.error("Failed to decode phoneNumber", ex);
      ex.printStackTrace();
    }


    return null;
  }

  @Override
  public OAuth2AccessToken generateToken(Long userId) {
    return generateTokenByUserId(userId);
  }

  @Override
  public UserDetails getUserDetailByUserId(Long userId) {
    UserDetails userDetails = userService.loadUserByUserId(userId);
    return userDetails;
  }

  @Data
  private class PhoneObject{
    String phoneNumber;
    String countryCode;

  }
}
