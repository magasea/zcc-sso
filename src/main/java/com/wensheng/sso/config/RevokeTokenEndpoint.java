package com.wensheng.sso.config;

import com.wensheng.sso.service.util.TokenUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Resource(name = "ssoTokenServices")
    ConsumerTokenServices tokenServices;

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token")
    @ResponseBody
    public void revokeToken(HttpServletRequest request) {

      String authorization = TokenUtil.extractHeaderToken( request);
        if (authorization != null) {
            tokenServices.revokeToken(authorization);
        }
    }




}