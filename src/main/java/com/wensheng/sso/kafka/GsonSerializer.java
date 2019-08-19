package com.wensheng.sso.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.commons.codec.Charsets;
import org.apache.kafka.common.serialization.Serializer;


/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
public class GsonSerializer<T> implements Serializer<T> {

  public static final String CONFIG_VALUE_CLASS = "value.deserializer.class";
  public static final String CONFIG_KEY_CLASS = "key.deserializer.class";
  private Class<T> cls;

  private Gson gson =
      new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).registerTypeAdapter(LocalDateTime.class,
          new DateTimeSerializer()).create();

  @Override
  public void configure(Map<String, ?> config, boolean isKey) {
    // this is called right after construction
    // use it for initialisation
    String configKey = isKey ? CONFIG_KEY_CLASS : CONFIG_VALUE_CLASS;
    String clsName = String.valueOf(config.get(configKey));

    try {
      cls = (Class<T>) Class.forName(clsName);
    } catch (ClassNotFoundException e) {
      System.err.printf("Failed to configure GsonDeserializer. " +
              "Did you forget to specify the '%s' property ?%n",
          configKey);
    }
  }

  @Override
  public byte[] serialize(String s, T t) {
    return gson.toJson(t).getBytes(Charsets.UTF_8);
  }

  @Override
  public void close() {
    // this is called right before destruction
  }
}
