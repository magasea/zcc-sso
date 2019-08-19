package com.wensheng.sso.kafka;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * @author chenwei on 4/2/19
 * @project miniapp-backend
 */
public class DateTimeSerializer implements JsonSerializer {


  @Override
  public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
    if((o instanceof LocalDateTime || type == LocalDateTime.class) && o != null){
      return new JsonPrimitive(((LocalDateTime) o).toString());
    }
    return null;
  }
}
