package com.wensheng.sso.service.impl;

import com.wensheng.sso.dao.mysql.mapper.OauthAccessTokenMapper;
import com.wensheng.sso.service.SSOTokenCheckService;
import java.util.Collection;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SSOTokenCheckServiceImpl implements SSOTokenCheckService {

  @Autowired
  OauthAccessTokenMapper oauthAccessTokenMapper;

  @Autowired
  @Qualifier("ssoTokenServices")
  ConsumerTokenServices ssoTokenServices;

  @Resource(name = "tokenStore")
  private TokenStore tokenStore;

  @Value("${spring.security.oauth2.client.registration.amc-admin.client-id}")
  private String amcAdminId;


  @Value("${spring.security.oauth2.client.registration.amc-client.client-id}")
  private String amcClientId;


  @Scheduled(cron = "${spring.task.scheduling.cronExprTrd}")
  @Override
  public void checkAccessTokens() {
    Collection<OAuth2AccessToken> oAuth2AccessTokens = tokenStore.findTokensByClientId(amcAdminId);
    boolean result = false;
    for (OAuth2AccessToken oauthAccessToken : oAuth2AccessTokens) {
      if (oauthAccessToken.isExpired()) {
        result = ssoTokenServices.revokeToken(oauthAccessToken.getValue());
        log.info("revoke expired token:{} result:{}", oauthAccessToken.getValue(), result);
      }
    }
    oAuth2AccessTokens = tokenStore.findTokensByClientId(amcClientId);
    for (OAuth2AccessToken oauthAccessToken1 : oAuth2AccessTokens) {
      if (oauthAccessToken1.isExpired()) {
        result = ssoTokenServices.revokeToken(oauthAccessToken1.getValue());
        log.info("revoke expired token:{} result:{}", oauthAccessToken1.getValue(), result);
      }
    }
  }

  public boolean revokenTokenByUserName(String userName){
    Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByClientIdAndUserName(amcAdminId,
        userName);
    for(OAuth2AccessToken oAuth2AccessToken : accessTokens){
      ssoTokenServices.revokeToken(oAuth2AccessToken.getValue());
    }
    return true;
  }
}


