package com.wensheng.sso.config;

import io.grpc.netty.shaded.io.netty.channel.ChannelOutboundBuffer.MessageProcessor;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {


  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getOutputStream().println("{ \"msg\": \"" + authException.getMessage() + "\" }");
    response.getOutputStream().println("{ \"errCode\": \"" + HttpStatus.UNAUTHORIZED.value() + "\" }");

  }
}