package com.wensheng.sso.service;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dto.AmcUserModDto;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
public interface KafkaService {


  void send(AmcUserModDto amcUser);
  void send(String topic, AmcUser amcUser);

}
