package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
public enum AmcCmpyEnum {
  CMPY_WENSHENG("CMPY_WENSHENG","上海文盛资产管理股份有限公司", 1),
  CMPY_JIAWO("CMPY_JIAWO","嘉沃", 2),
  CMPY_DINGHUI("CMPY_DINGHUI","鼎晖", 3),
  CMPY_ZHUHAI("CMPY_ZHUHAI","珠海", 4),

  ;
  private String name;
  private String cname;
  private Integer id;

  AmcCmpyEnum(String name, String cname, int id){
    this.name = name;
    this.cname = cname;
    this.id = id;
  }

  private static final Function<String, AmcCmpyEnum> func =
      EnumUtils.lookupMap(AmcCmpyEnum.class, e -> e.getName());
  public static AmcCmpyEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcCmpyEnum> funcId =
      EnumUtils.lookupMap(AmcCmpyEnum.class, e -> e.getId());
  public static AmcCmpyEnum lookupByDisplayIdUtil(Integer id) {
    return funcId.apply(id);
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
