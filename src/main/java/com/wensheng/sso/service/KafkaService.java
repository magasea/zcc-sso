package com.wensheng.sso.service;

import com.wensheng.sso.module.common.WechatUserLocation;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUser;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
public interface KafkaService {


  void send(WechatUserLocation wechatUserLocation);
  void send(AmcWechatUser amcWechatUser);
}
