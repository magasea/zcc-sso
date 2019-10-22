package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 2/26/19
 * @project zcc-backend
 */
public enum AmcSSORolesEnum {
  ROLE_SSO_SYS_ADM("ROLE_SSO_SYS_ADM", "系统管理员",1),
  ROLE_SSO_MGR("ROLE_SSO_MGR", "总助以上经理",2),
  ROLE_SSO_LDR("ROLE_SSO_LDR", "组长",3),
  ROLE_SSO_STAFF("ROLE_SSO_STAFF", "组员",4),
  ROLE_SSO_PARTNER("ROLE_SSO_PARTNER", "合作方",5),
  ROLE_SSO_WECHATCLIENT("ROLE_SSO_WECHATCLIENT", "微信用户" ,6),
  ;
  AmcSSORolesEnum(String name , String cname, int id){
    this.name = name;
    this.id = id;
    this.cname = cname;
  }
  private String name;
  private int id;
  private String cname;

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  private static final Function<String, AmcSSORolesEnum> func =
      EnumUtils.lookupMap(AmcSSORolesEnum.class, e -> e.getName());
  public static AmcSSORolesEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcSSORolesEnum> funcStatus =
      EnumUtils.lookupMap(AmcSSORolesEnum.class, e -> e.getId());
  public static AmcSSORolesEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }
}
