package com.wensheng.zcc.sso.service;

import com.wensheng.zcc.common.mq.kafka.module.WechatUserLocation;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.zcc.sso.module.vo.WechatCode2SessionVo;
import com.wensheng.zcc.sso.module.vo.WechatLoginResult;
import com.wensheng.zcc.sso.module.vo.WechatPhoneRegistry;
import com.wensheng.zcc.sso.module.vo.WechatUserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
public interface AmcSsoService {
  OAuth2AccessToken generateToken(Long userId);
  UserDetails getUserDetailByUserId(Long userId);
}
