package com.wensheng.zcc.sso.service;

import com.wensheng.zcc.common.mq.kafka.module.WechatUserLocation;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcWechatUser;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
public interface KafkaService {


  void send(WechatUserLocation wechatUserLocation);
  void send(AmcWechatUser amcWechatUser);
}
