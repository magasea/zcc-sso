package com.wensheng.sso.service;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
public interface KafkaService {


  void send(AmcUser amcUser);
  void send(String topic, AmcUser amcUser);

}
