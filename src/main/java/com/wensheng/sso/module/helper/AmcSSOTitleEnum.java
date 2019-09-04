package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 2/26/19
 * @project zcc-backend
 */
public enum AmcSSOTitleEnum {
  TITLE_SYS_ADM("SYS_ADM","系统管理员",  1),
  TITLE_MGR("TITLE_MGR", "总经理/副总/总助",2),
  TITLE_LDR("TITLE_LDR","组长", 3),
  TITLE_STAFF("TITLE_STAFF", "组员", 4),
  TITLE_PARTNER("TITLE_PARTNER", "合作方", 5),
  ;
  AmcSSOTitleEnum(String name ,String cname, int id){
    this.name = name;
    this.id = id;
    this.cname = cname;
  }
  private String name;
  private String cname;
  private int id;


  public String getName() {
    return name;
  }
  public String getCname() {
    return cname;
  }


  public int getId() {
    return id;
  }

  private static final Function<String, AmcSSOTitleEnum> func =
      EnumUtils.lookupMap(AmcSSOTitleEnum.class, e -> e.getName());
  public static AmcSSOTitleEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<String, AmcSSOTitleEnum> funcCname =
      EnumUtils.lookupMap(AmcSSOTitleEnum.class, e -> e.getCname());
  public static AmcSSOTitleEnum lookupByDisplayCNameUtil(String cname) {
    return funcCname.apply(cname);
  }
  
  private static final Function<Integer, AmcSSOTitleEnum> funcStatus =
      EnumUtils.lookupMap(AmcSSOTitleEnum.class, e -> e.getId());
  public static AmcSSOTitleEnum lookupByDisplayIdUtil(Integer id) {
    return funcStatus.apply(id);
  }
}
