package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/26/19
 * @project miniapp-backend
 */
public enum  AmcUserValidEnum {

  VALID(1, "用户账户正常"),
  LOCKED(2, "用户账户被锁"),
  DELETED(3, "用户账户被删除"),
        ;
  int id;
  String name;
  AmcUserValidEnum(int id, String name){
    this.id = id;
    this.name = name;
  }


  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  private static final Function<String, AmcUserValidEnum> func =
      EnumUtils.lookupMap(AmcUserValidEnum.class, e -> e.getName());
  public static AmcUserValidEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcUserValidEnum> funcStatus =
      EnumUtils.lookupMap(AmcUserValidEnum.class, e -> e.getId());
  public static AmcUserValidEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }
}
