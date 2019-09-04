package com.wensheng.sso.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author chenwei on 1/30/19
 * @project zcc-backend
 */
public class UserUtils {

  private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


  public static String getEncode(String password){
    return passwordEncoder.encode(password);
  }

  public static boolean match( String rawPw, String encodedPw){
    return passwordEncoder.matches(rawPw, encodedPw);
  }

}
