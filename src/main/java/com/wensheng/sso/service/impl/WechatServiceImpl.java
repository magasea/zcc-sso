package com.wensheng.sso.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wensheng.sso.dao.mysql.mapper.AmcWechatUserMapper;
import com.wensheng.sso.module.common.WechatUserLocation;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUserExample;
import com.wensheng.sso.module.helper.AmcPermEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.vo.WechatCode2SessionVo;
import com.wensheng.sso.module.vo.WechatLoginResult;
import com.wensheng.sso.module.vo.WechatPhoneRegistry;
import com.wensheng.sso.module.vo.WechatUserInfo;
import com.wensheng.sso.service.KafkaService;
import com.wensheng.sso.service.WechatService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

  @Value("${weixin.loginUrl}")
  String loginUrl;

  @Value("${weixin.open.loginUrl}")
  String loginOpenUrl;

  @Value("${weixin.open.getUserInfoUrl}")
  String getUserInfoUrl;

  @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.client-id}")
  private String amcWechatClientId;

  @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.secret}")
  private String amcWechatSecret;


  @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.scopes}")
  private String amcWechatScopes;

  @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.authorizedGrantTypes}")
  private String amcWechatAuthorizedGrantTypes;

  @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.redirectUris}")
  private String amcWechatRedirectUris;


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
  AmcWechatUserMapper amcWechatUserMapper;

  @Autowired
  TokenStore tokenStore;


  DefaultTokenServices tokenWechatServices = null;

  @Autowired
  TokenEnhancer wechatTokenEnhancer;

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
    baseClientDetails.setClientId(amcWechatClientId);
    baseClientDetails.setAccessTokenValiditySeconds(accessTokenValidSeconds);
    if(!StringUtils.isEmpty(amcWechatAuthorizedGrantTypes)){
      baseClientDetails.setAuthorizedGrantTypes(Arrays.stream(amcWechatAuthorizedGrantTypes.split(",")).collect(Collectors.toList()));
    }
    baseClientDetails.setClientSecret(amcWechatSecret);
    if(!StringUtils.isEmpty(amcWechatScopes)){
      baseClientDetails.setScope(Arrays.stream(amcWechatScopes.split(",")).collect(Collectors.toList()));
    }
    Map<String, BaseClientDetails> clientParam = new HashMap<>();
    clientParam.put(amcWechatClientId, baseClientDetails);
//    clientDetailsService.setClientDetailsStore(clientParam);
    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(wechatTokenEnhancer, accessTokenConverter));

    tokenWechatServices = tokenWechatServices();
    tokenWechatServices.setTokenEnhancer(tokenEnhancerChain);



//    tokenWechatServices.setClientDetailsService(clientDetailsService);
  }

  private DefaultTokenServices tokenWechatServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore);
    defaultTokenServices.setSupportRefreshToken(true);
    defaultTokenServices.setReuseRefreshToken(false);
    defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValidSeconds);
    defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValidSeconds);
    return defaultTokenServices;
  }

  @Override
  public WechatLoginResult loginWechat(String code) {
    String loginWechatUrl = loginUrl.replace("JSCODE", code);
    ResponseEntity<WechatCode2SessionVo> responseEntity = restTemplate.getForEntity(loginWechatUrl,
        WechatCode2SessionVo.class);
    String info = gson.toJson(responseEntity.getBody());
    log.info(String.format("got response from wechat:%s", info));
    WechatLoginResult wechatLoginResult = new WechatLoginResult();
    wechatLoginResult.setResp(info);
    if(StringUtils.isEmpty(responseEntity.getBody().getErrcode())){
      wechatLoginResult.setOAuth2AccessToken(generateToken(responseEntity.getBody()));
    }
    CUWechatUser((responseEntity.getBody()));
    return wechatLoginResult;
  }

  @Override
  public AmcWechatUser CUWechatUser(WechatCode2SessionVo wechatCode2SessionVo) {
    AmcWechatUserExample amcWechatUserExample = new AmcWechatUserExample();
    if(StringUtils.isEmpty(wechatCode2SessionVo.getSessionKey())){
      return null;
    }

    amcWechatUserExample.createCriteria().andWechatOpenidEqualTo(wechatCode2SessionVo.getOpenid());
    AmcWechatUser amcWechatUser = new AmcWechatUser();

    amcWechatUser.setSessionKey(wechatCode2SessionVo.getSessionKey());
    amcWechatUser.setWechatOpenid(wechatCode2SessionVo.getOpenid());
    amcWechatUser.setWechatUnionId(wechatCode2SessionVo.getUnionid());
    List<AmcWechatUser> amcWechatUsersHistory =  amcWechatUserMapper.selectByExample(amcWechatUserExample);
    if(CollectionUtils.isEmpty(amcWechatUsersHistory)){
      amcWechatUserMapper.insertSelective(amcWechatUser);

    }else{
      if(amcWechatUsersHistory.get(0).getSessionKey().equals(wechatCode2SessionVo.getSessionKey())){
        log.info(String.format("session key:%s not changed", wechatCode2SessionVo.getSessionKey()));
        return amcWechatUsersHistory.get(0);
      }
      amcWechatUserMapper.updateByExampleSelective(amcWechatUser, amcWechatUserExample);

    }
    kafkaService.send(amcWechatUser);
    return amcWechatUser;
  }

  @Override
  public String registryPhone(WechatPhoneRegistry wechatPhoneRegistry) {
    AmcWechatUserExample amcWechatUserExample = new AmcWechatUserExample();
    amcWechatUserExample.createCriteria().andWechatOpenidEqualTo(wechatPhoneRegistry.getOpenId());
    List<AmcWechatUser> amcWechatUsers = amcWechatUserMapper.selectByExample(amcWechatUserExample);
    if(!CollectionUtils.isEmpty(amcWechatUsers)){
      String sessionKey = amcWechatUsers.get(0).getSessionKey();
      String phoneNumber = decodePhone(wechatPhoneRegistry.getEncryptedData(), wechatPhoneRegistry.getIv(),
          sessionKey);
      PhoneObject phoneObject = null;
      if(!StringUtils.isEmpty(phoneNumber)){
        phoneObject =   gson.fromJson(phoneNumber, PhoneObject.class);
        amcWechatUsers.get(0).setPhoneNumber(String.format("%s-%s",phoneObject.countryCode,phoneObject.phoneNumber));
        amcWechatUserMapper.updateByPrimaryKeySelective(amcWechatUsers.get(0));
      }
      return String.format("%s-%s",phoneObject.countryCode,phoneObject.phoneNumber);
    }

    return null;
  }

  @Override
  public String registryUserLocation(WechatUserLocation wechatUserLocation) {
    kafkaService.send(wechatUserLocation);
    return "succeed";
  }

  @Override
  public WechatLoginResult loginWechatOpenPlatform(String code) {
    String loginWechatUrl = String.format(loginOpenUrl, code);
    ResponseEntity<WechatCode2SessionVo> responseEntity = restTemplate.getForEntity(loginWechatUrl,
        WechatCode2SessionVo.class);
    String info = gson.toJson(responseEntity.getBody());
    log.info(String.format("got response from wechat:%s", info));
    WechatLoginResult wechatLoginResult = new WechatLoginResult();
    wechatLoginResult.setResp(info);
    if(StringUtils.isEmpty(responseEntity.getBody().getErrcode())){
      wechatLoginResult.setOAuth2AccessToken(generateToken(responseEntity.getBody()));
    }
    CUWechatUser((responseEntity.getBody()));
    return wechatLoginResult;
  }

  @Override
  public WechatUserInfo getWechatUserInfo(String openId, String accessToken) {
    String url = String.format(getUserInfoUrl, accessToken, openId);
    ResponseEntity<WechatUserInfo> responseEntity = restTemplate.getForEntity(url,
        WechatUserInfo.class);
    String info = gson.toJson(responseEntity.getBody());
    log.info(String.format("got response from wechat:%s", info));

    return responseEntity.getBody();
  }

  private OAuth2AccessToken generateToken(WechatCode2SessionVo wechatCode2SessionVo){
    HashMap<String, String> authorizationParameters = new HashMap<String, String>();
    authorizationParameters.put("scope", amcWechatScopes);
    authorizationParameters.put("username",
        StringUtils.isEmpty(wechatCode2SessionVo.getUnionid())? wechatCode2SessionVo.getOpenid():
            wechatCode2SessionVo.getUnionid());
    authorizationParameters.put("client_id", amcWechatClientId);
    authorizationParameters.put("grant", amcWechatAuthorizedGrantTypes);
    if(!StringUtils.isEmpty(wechatCode2SessionVo.getAccessToken())){
      authorizationParameters.put("accessToken", wechatCode2SessionVo.getAccessToken());
    }
    if(!StringUtils.isEmpty(wechatCode2SessionVo.getRefreshToken())){
      authorizationParameters.put("refreshToken", wechatCode2SessionVo.getRefreshToken());
    }
    List<String> scopes = new ArrayList<>();
    if(!StringUtils.isEmpty(amcWechatScopes)){
      Arrays.stream(amcWechatScopes.split(",")).forEach(item -> scopes.add(item.trim()));
    }
    Set<String> scopesSet = scopes.stream().collect(Collectors.toSet());
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AmcSSORolesEnum.ROLE_SSO_WECHATCLIENT.name()));



    OAuth2Request authorizationRequest = new OAuth2Request(authorizationParameters, amcWechatClientId, authorities,true, scopesSet, null,
        amcWechatRedirectUris, null, null);

    User userPrincipal = null;
    // Create principal and auth token
    String wechatUserId = "";
    if(StringUtils.isEmpty(wechatCode2SessionVo.getUnionid())){
      log.error("Failed to get unionid for user:{}", wechatCode2SessionVo.getOpenid());
      wechatUserId = wechatCode2SessionVo.getOpenid();
    }else{
      wechatUserId = wechatCode2SessionVo.getUnionid();
    }
    if(!StringUtils.isEmpty(wechatCode2SessionVo.getSessionKey())){

      userPrincipal = new User(wechatUserId,
          wechatCode2SessionVo.getSessionKey(),
          true, true,
          true, true,
          authorities);
    }else if(!StringUtils.isEmpty(wechatCode2SessionVo.getAccessToken())){
      userPrincipal = new User(wechatUserId, wechatCode2SessionVo.getAccessToken(), true, true, true
          , true,
          authorities);
    }

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal,
        null, authorities) ;

    OAuth2Authentication auth2Authentication = new OAuth2Authentication(authorizationRequest, authenticationToken);


    OAuth2AccessToken token = tokenWechatServices.createAccessToken(auth2Authentication);
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
  @Data
  private class PhoneObject{
    String phoneNumber;
    String countryCode;

  }
}
