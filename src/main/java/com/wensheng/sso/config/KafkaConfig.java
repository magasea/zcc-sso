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

  private String MQ_TOPIC_WECHAT_USERLOCATION = KafkaParams.MQ_TOPIC_WECHAT_USERLOCATION;

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
    return new NewTopic(MQ_TOPIC_WECHAT_USERLOCATION, 3, (short) 1);
  }

  // Consumer configuration

  // If you only need one kind of deserialization, you only need to set the
  // Consumer configuration properties. Uncomment this and remove all others below.
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>(
//                kafkaProperties.buildConsumerProperties()
//        );
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                JsonDeserializer.class);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                "tpd-loggers");
//
//        return props;
//    }


}
