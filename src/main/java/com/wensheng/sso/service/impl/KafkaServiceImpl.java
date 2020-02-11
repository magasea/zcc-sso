package com.wensheng.sso.service.impl;

import com.wensheng.sso.kafka.KafkaParams;
import com.wensheng.sso.module.common.WechatUserLocation;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.sso.service.KafkaService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
@Service
public class KafkaServiceImpl implements KafkaService {
  @Autowired
  KafkaTemplate kafkaTemplate;

  @Value("${env.name}")
  String env;

  @Value("${kafka.topic_amc_login}")
  String userLoginTopic;


  private String MQ_TOPIC_SSO_USERCHANGED = null;



  @PostConstruct
  void init(){
    MQ_TOPIC_SSO_USERCHANGED = String.format("%s_%s",KafkaParams.MQ_TOPIC_SSO_USERCHANGED, env);
  }


  @Override
  public void send(AmcUser amcUser) {
    kafkaTemplate.send(MQ_TOPIC_SSO_USERCHANGED, amcUser);
  }

  @Override
  public void send(String topic, AmcUser amcUser) {
    kafkaTemplate.send(userLoginTopic, amcUser);
  }

}
