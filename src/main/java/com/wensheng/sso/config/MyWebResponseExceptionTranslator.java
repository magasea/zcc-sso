package com.wensheng.sso.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;


@Component
public class MyWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
  @Override
  public ResponseEntity<OAuth2Exception> translate(Exception exception) throws Exception {
    if (exception instanceof OAuth2Exception) {
      OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(new CustomOauthException(oAuth2Exception.getMessage()));
    }else if(exception instanceof AuthenticationException){
      AuthenticationException authenticationException = (AuthenticationException) exception;
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(new CustomOauthException(authenticationException.getMessage()));
    }
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new CustomOauthException(exception.getMessage()));
  }
}