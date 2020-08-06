package com.wensheng.sso.config;

import com.wensheng.sso.kafka.GsonSerializer;
import com.wensheng.sso.kafka.KafkaParams;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
@Configuration
public class KafkaConfig {

  @Autowired
  private KafkaProperties kafkaProperties;

  private String MQ_TOPIC_SSO_USERCHANGED = KafkaParams.MQ_TOPIC_SSO_USERCHANGED;

  // Producer configuration

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> props =
        new HashMap<>(kafkaProperties.buildProducerProperties());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        GsonSerializer.class);
    return props;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public NewTopic adviceTopic() {
    return new NewTopic(MQ_TOPIC_SSO_USERCHANGED, 3, (short) 1);
  }


}
