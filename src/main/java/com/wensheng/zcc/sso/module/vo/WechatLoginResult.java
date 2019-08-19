package com.wensheng.zcc.sso.module.vo;

import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
@Data
public class WechatLoginResult {
  String resp;
  OAuth2AccessToken oAuth2AccessToken;

}
