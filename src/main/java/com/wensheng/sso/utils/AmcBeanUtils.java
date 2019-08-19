package com.wensheng.sso.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author chenwei on 1/8/19
 * @project zcc-backend
 */
public class AmcBeanUtils {
  private final static String dateStr = "1900-01-01 00:00:00";
  private static Date defaultDate = null;

  static {
    try {
      defaultDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public static String[] getNullPropertyNames (Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet<String>();
    for(java.beans.PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null) emptyNames.add(pd.getName());
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  // then use Spring BeanUtils to copy and ignore null
  public static void copyProperties(Object src, Object target) {
    BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
  }

  public static void fillNullObjects(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        if (field.get(object) != null) {
          continue;
        }
        else if (field.getType().equals(Integer.class)) {
          field.set(object, -1);
        }
        else if (field.getType().equals(String.class)) {
          field.set(object, "-1");
        }
//        else if (field.getType().equals(Boolean.class)){
//          field.set(object, false);
//        }
        else if (field.getType().equals(Character.class)) {
          field.set(object, '\u0000');
        }
        else if (field.getType().equals(Byte.class)) {
          field.set(object, (byte) 0);
        }
        else if(field.getType().equals(Date.class)){
          field.set(object, defaultDate);
        }
//        else if (field.getType().equals(Float.class)) {
//          field.set(object, 0.0f);
//        }
//        else if (field.getType().equals(Double.class)) {
//          field.set(object, 0.0d);
//        }
//        else if (field.getType().equals(Short.class)) {
//          field.set(object, (short) 0);
//        }
        else if (field.getType().equals(Long.class)) {
          field.set(object, -1L);
        }
        else if (field.getType().getDeclaredFields().length > 0){
          for (Constructor<?> constructor : field.getClass().getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
              field.set(object, constructor.newInstance());
              fillNullObjects(field.get(object));
            }
          }
        }
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

}
