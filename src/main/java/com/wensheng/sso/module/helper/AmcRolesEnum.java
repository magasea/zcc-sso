package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 2/26/19
 * @project zcc-backend
 */
public enum AmcRolesEnum {
  ROLE_SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN", 1),
  ROLE_AMC_ADMIN("ROLE_AMC_ADMIN", 2),
  ROLE_AMC_USER("ROLE_AMC_USER", 3),
  ROLE_ZCC_CLIENT("ROLE_ZCC_CLIENT", 4),
  ROLE_LAWYER("ROLE_LAWYER", 5),
  ;
  AmcRolesEnum(String name , int id){
    this.name = name;
    this.id = id;
  }
  private String name;
  private int id;


  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  private static final Function<String, AmcRolesEnum> func =
      EnumUtils.lookupMap(AmcRolesEnum.class, e -> e.getName());
  public static AmcRolesEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcRolesEnum> funcStatus =
      EnumUtils.lookupMap(AmcRolesEnum.class, e -> e.getId());
  public static AmcRolesEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }
}
