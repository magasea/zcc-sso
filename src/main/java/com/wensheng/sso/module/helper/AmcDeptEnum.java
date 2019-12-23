package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
public enum AmcDeptEnum {
  BUSINESS_DEPT("BUSINESS_DEPT","业务部门", 1, true),
  ESTATE_DEPT("ESTATE_DEPT","地产部门", 2, true),
  RISKCTRL_DEPT("RISKCTRL_DEPT","风控部门", 3, true),
  EQUITY_DEPT("EQUITY_DEPT","股权部门", 4, true),
//  ALL_CMPY("ALL_CMPY","全公司",5, true),
  TECH_DEPT("TECH_DEPT","技术部门",6, true),
  OUTSIDE("OUTSIDE","公司外部合作伙伴等",7, false),
//  FINANCE_DEPT("FINANCE_DEPT","财务部门",8),
//  PERSONNEL_DEPT("PERSONNEL_DEPT","人事部门",9),

  SPECIAL_ZCC_SYS("SPECIAL_ZCC_SYS", "债查查特殊部门", -9, false),
  SPECIAL_ZCC_CO("SPECIAL_ZCC_SYS", "债查查特殊部门", -10, false),

  ;
  private String name;
  private String cname;
  private Integer id;
  private boolean used = true;

  AmcDeptEnum(String name, String cname, int id, boolean used){
    this.name = name;
    this.cname = cname;
    this.id = id;
    this.used = used;
  }

  private static final Function<String, AmcDeptEnum> func =
      EnumUtils.lookupMap(AmcDeptEnum.class, e -> e.getName());
  public static AmcDeptEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcDeptEnum> funcStatus =
      EnumUtils.lookupMap(AmcDeptEnum.class, e -> e.getId());
  public static AmcDeptEnum lookupByDisplayIdUtil(Integer id) {
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

  public boolean isUsed() {
    return used;
  }
}
