package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

public enum AmcAPPEnum {

  AMCAPP_CMPY("AMCAPP_CMPY","全公司", 1),
  AMCAPP_JD("AMCAPP_JD","尽调工具",2),
  AMCAPP_ZCC("AMCAPP_ZCC","债查查",3),
  AMCAPP_MARKETDATA("AMCAPP_MARKETDATA","存量分析",4),
  AMCAPP_ANALYSIS("AMCAPP_ANALYSIS","拍卖交易分析",5),
  
  ;
  
  private String name;
  private String cname;
  private int id;

  AmcAPPEnum(String name, String cname, int id){
    this.name = name;
    this.cname = cname;
    this.id = id;
  }
  private static final Function<String, AmcAPPEnum> func =
      EnumUtils.lookupMap(AmcAPPEnum.class, e -> e.getName());
  public static AmcAPPEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcAPPEnum> funcStatus =
      EnumUtils.lookupMap(AmcAPPEnum.class, e -> e.getId());
  public static AmcAPPEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }


  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }

  public String getCname() {
    return cname;
  }
}
