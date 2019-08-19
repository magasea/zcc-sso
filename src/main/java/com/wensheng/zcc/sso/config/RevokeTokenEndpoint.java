package com.wensheng.zcc.sso.config;

import com.wensheng.zcc.sso.service.util.TokenUtil;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import java.util.Enumeration;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Resource(name = "tokenServices")
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