package com.wensheng.zcc.sso.service.util;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class TokenUtil {
  public static String extractHeaderToken(HttpServletRequest request) {
    Enumeration<String> headers = request.getHeaders("Token");
    while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
      String value = headers.nextElement();
      if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
        String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
        // Add this here for the auth details later. Would be better to change the signature of this method.
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
            value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
        int commaIndex = authHeaderValue.indexOf(',');
        if (commaIndex > 0) {
          authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
      }
    }

    headers = request.getHeaders("Authorization");
    while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
      String value = headers.nextElement();
      if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
        String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
        // Add this here for the auth details later. Would be better to change the signature of this method.
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
            value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
        int commaIndex = authHeaderValue.indexOf(',');
        if (commaIndex > 0) {
          authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
      }
    }

    return null;
  }
}
