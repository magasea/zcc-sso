package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
public enum AmcPermEnum {
  PERM_CMPY("PERM_CMPY",1),
  PERM_JD("PERM_JD",2),
  PERM_ZCC("PERM_ZCC",3),
  PERM_MARKETDATA("PERM_MARKETDATA",4),
  PERM_ANALYSIS("PERM_ANALYSIS",5),
  ;
  private String name;
  private Integer id;

  AmcPermEnum(String name, int id){
    this.name = name;
    this.id = id;
  }

  private static final Function<String, AmcPermEnum> func =
      EnumUtils.lookupMap(AmcPermEnum.class, e -> e.getName());
  public static AmcPermEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcPermEnum> funcStatus =
      EnumUtils.lookupMap(AmcPermEnum.class, e -> e.getId());
  public static AmcPermEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }


  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }}
