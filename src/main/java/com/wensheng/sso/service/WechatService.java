package com.wensheng.sso.service;

import com.wensheng.sso.module.common.WechatUserLocation;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.sso.module.vo.WechatCode2SessionVo;
import com.wensheng.sso.module.vo.WechatLoginResult;
import com.wensheng.sso.module.vo.WechatPhoneRegistry;
import com.wensheng.sso.module.vo.WechatUserInfo;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
public interface WechatService {
  public WechatLoginResult loginWechat(String code);

  public AmcWechatUser CUWechatUser(WechatCode2SessionVo wechatCode2SessionVo);

  public String registryPhone(WechatPhoneRegistry wechatPhoneRegistry);


  String registryUserLocation(WechatUserLocation wechatUserLocation);

  WechatLoginResult loginWechatOpenPlatform(String code);

  WechatUserInfo getWechatUserInfo(String openId, String accessToken);
}
