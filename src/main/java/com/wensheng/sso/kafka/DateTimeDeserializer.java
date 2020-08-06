package com.wensheng.sso.kafka;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author chenwei on 4/2/19
 * @project miniapp-backend
 */
public class DateTimeDeserializer implements JsonDeserializer {

  String pattern = "yyyy-MM-dd HH:mm:SS";

  DateFormat df = new SimpleDateFormat(pattern);

  @Override
  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    try {
      String result = json.getAsJsonPrimitive().getAsString();

      return df.parse(result);
    } catch (ParseException e) {
      e.printStackTrace();

    }
    return null;
  }

}