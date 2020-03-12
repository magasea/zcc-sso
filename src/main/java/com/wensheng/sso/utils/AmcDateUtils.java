package com.wensheng.sso.utils;

import com.google.protobuf.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author chenwei on 1/2/19
 * @project zcc-backend
 */
public class AmcDateUtils {

  /**
   *
   * @param input date format yyyy-MM-dd
   * @return
   */

  public static Date getDateFromStr(String input) throws ParseException {
    DateFormat df = null;
    if(input.contains("年")){
      df = new SimpleDateFormat("yyyy'年'MM'月'dd'日'", Locale.CHINA);
    }else {
      df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    }
    Date result =  df.parse(input);
    return  result;
//    System.out.println(result);
  }

  public static Date getDateSecondsDiff(int seconds){
    Date date = Date.from(ZonedDateTime.now().plus(seconds, ChronoUnit.SECONDS).toInstant());
    return date;
  }


  public static LocalDateTime getLocalDateTime(){
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
  }

  public static Timestamp fromLocalDate(LocalDate localDate) {
    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    return Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano()).build()
        ;
  }

  public static String getFormatedDate(){
    Date date = new Date();
    String modifiedDate= new SimpleDateFormat("yyMMdd").format(date);
    return modifiedDate;
  }

  public static LocalDate toLocalDate(Timestamp timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.of("UTC"))
        .toLocalDate();
  }
  public static LocalDate toLocalDate(Long timestamp){
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC")).toLocalDate();
  }

  public static Date getDataBaseDefaultOldDate(){
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
    calendar.set(1900, 0,1, 0 , 0, 0);
    return calendar.getTime();
  }

  public static Date toUTCDate(Long timestamp){
    long timeStampLocal = timestamp;
    if(String.valueOf(timestamp).length() <= 11){
      timeStampLocal = timestamp*1000;
    }
    return Date.from(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStampLocal), ZoneId.of(
        "UTC")).atZone(ZoneId.of(
        "UTC")).toInstant());

  }

  public static Date toLocalDateTime(Long timestamp){
    ZoneId defaultZoneId = ZoneId.systemDefault();
    return Date.from(toLocalDate(timestamp).atStartOfDay(defaultZoneId).toInstant());
  }


  public static Date getDateMonthsDiff(int months){
    Date date = Date.from(ZonedDateTime.now().minusMonths(months).toInstant());
    return date;
  }

  public static String getDateStrMonthsDiff(int months){
    Date date = getDateMonthsDiff(months);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String strDate = dateFormat.format(date);
    return strDate;
  }

  public static int getCurrentYear(){
    int year = Calendar.getInstance().get(Calendar.YEAR);
    return year;
  }

  public static Date getCurrentDate() {
    return Date.from(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")).atZone(ZoneId.of("UTC")).toInstant());
  }

  public static Date getDateFromLocalDate(LocalDateTime localDateTime){
    return Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant());
  }
}
