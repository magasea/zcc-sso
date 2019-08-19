package com.wensheng.zcc.sso.service.impl;

import com.wensheng.zcc.common.mq.kafka.KafkaParams;
import com.wensheng.zcc.common.mq.kafka.module.WechatUserLocation;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.zcc.sso.service.KafkaService;
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


  private String MQ_TOPIC_WECHAT_USERLOCATION = null;
  private String MQ_TOPIC_WECHAT_USERCREATE = null;



  @PostConstruct
  void init(){
    MQ_TOPIC_WECHAT_USERLOCATION = String.format("%s_%s",KafkaParams.MQ_TOPIC_WECHAT_USERLOCATION, env);
    MQ_TOPIC_WECHAT_USERCREATE = String.format("%s_%s",KafkaParams.MQ_TOPIC_WECHAT_USERCREATE, env);
  }


  @Override
  public void send(WechatUserLocation wechatUserLocation) {
    kafkaTemplate.send(MQ_TOPIC_WECHAT_USERLOCATION, wechatUserLocation);
  }

  @Override
  public void send(AmcWechatUser amcWechatUser) {
    kafkaTemplate.send(MQ_TOPIC_WECHAT_USERCREATE, amcWechatUser);
  }
}
