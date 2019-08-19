package com.wensheng.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MethodSecurityConfig  extends GlobalMethodSecurityConfiguration {


  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    OAuth2MethodSecurityExpressionHandler expressionHandler =
        new OAuth2MethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(new SecurityPermissionEvaluator());
    return expressionHandler;
  }
}
