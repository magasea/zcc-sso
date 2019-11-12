package com.wensheng.sso.config;

import lombok.Data;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
@Data
public class CustomOauthException extends OAuth2Exception {
  String errorCode = "";
  public CustomOauthException(String msg) {
    super(msg);
  }
}
