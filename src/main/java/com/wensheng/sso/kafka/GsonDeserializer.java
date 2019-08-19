package com.wensheng.sso.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
@Slf4j
public class GsonDeserializer<T> implements Deserializer<T> {

  public static final String CONFIG_VALUE_CLASS = "value.deserializer.class";
  public static final String CONFIG_KEY_CLASS = "key.deserializer.class";
  private Class<T> cls;

  private Gson gson =
      new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).registerTypeAdapter(LocalDateTime.class,
      new DateTimeDeserializer()).create();


  @Override
  public void configure(Map<String, ?> config, boolean isKey) {
    String configKey = isKey ? CONFIG_KEY_CLASS : CONFIG_VALUE_CLASS;
    String clsName = String.valueOf(config.get(configKey));

    try {
      cls = (Class<T>) Class.forName(clsName);
    } catch (ClassNotFoundException e) {
      System.err.printf("Failed to configure GsonDeserializer. " +
              "Did you forget to specify the '%s' Stringproperty ?%n",
          configKey);
    }
  }


  @Override
  public T deserialize(String topic, byte[] bytes) {
    try {
      return (T) gson.fromJson(new InputStreamReader(new ByteArrayInputStream(bytes),"UTF-8"), cls);
    } catch (UnsupportedEncodingException e) {
      log.error("Failed to desserialize ", e);
      e.printStackTrace();
      return null;
    }
  }


  @Override
  public void close() {}
}
