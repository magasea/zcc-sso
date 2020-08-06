package com.wensheng.sso.kafka;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author chenwei on 4/2/19
 * @project miniapp-backend
 */
public class DateTimeSerializer implements JsonSerializer {

  private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

  @Override
  public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
    if((o instanceof LocalDateTime || type == LocalDateTime.class) && o != null){
      return new JsonPrimitive(((LocalDateTime) o).format(dateTimeFormatter).toString());
    }
    if((o instanceof Date || type == LocalDateTime.class) && o != null){
      return new JsonPrimitive(sdf.format((Date) o));
    }
    return null;
  }
}
