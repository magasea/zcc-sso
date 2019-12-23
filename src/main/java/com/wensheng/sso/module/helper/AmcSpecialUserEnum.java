package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

public enum AmcSpecialUserEnum {
  SYSTEM_ADMIN1(1, "system_admin1", "1234567890123"),
  SYSTEM_ADMIN2(2, "system_admin2","1234567890124"),
  ZCC_SYSTEM_ADMIN(3, "zccadmin_system", "1234567890125"),
  ZCC_CO_ADMIN(4, "zccadmin_co", "1234567890126"),
  ;


  int id;
  String name;
  String mobileNum;

  AmcSpecialUserEnum(int id, String name, String mobileNum){
    this.id = id;
    this.name = name;
    this.mobileNum = mobileNum;
  }
  private static final Function<String, AmcSpecialUserEnum> func =
      EnumUtils.lookupMap(AmcSpecialUserEnum.class, e -> e.getName());
  public static AmcSpecialUserEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcSpecialUserEnum> funcId =
      EnumUtils.lookupMap(AmcSpecialUserEnum.class, e -> e.getId());
  public static AmcSpecialUserEnum lookupByDisplayIdUtil(Integer id) {
    return funcId.apply(id);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getMobileNum() {
    return mobileNum;
  }
}
