package com.wensheng.sso.config;

import com.google.gson.Gson;
import com.wensheng.sso.utils.ExceptionUtils;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private Gson gson;

  @PostConstruct
  void init(){
    gson = new Gson();
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

//    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    ResponseObj jsonResponse = new ResponseObj();
    jsonResponse.setErrCode(HttpStatus.UNAUTHORIZED.value());
    jsonResponse.setMessage(exception.getMessage());
    response.getWriter().append(jsonResponse.toString());
    response.setStatus(HttpStatus.OK.value());

    super.onAuthenticationFailure(request, response, exception);
  }
  @Data
  class ResponseObj{
    int errCode;
    String message;
  }
}
