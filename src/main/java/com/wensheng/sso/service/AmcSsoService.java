package com.wensheng.sso.service;

import com.wensheng.sso.module.common.WechatUserLocation;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
public interface AmcSsoService {
  OAuth2AccessToken generateToken(Long userId);
  UserDetails getUserDetailByUserId(Long userId);
  boolean handleNameList(MultipartFile multipartFile) throws Exception;
  boolean handleJDList(MultipartFile multipartFile) throws Exception;
}
