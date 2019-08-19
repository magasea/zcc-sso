package com.wensheng.sso.kafka;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * @author chenwei on 4/2/19
 * @project miniapp-backend
 */
public class DateTimeDeserializer implements JsonDeserializer {

  @Override
  public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
  }
}
