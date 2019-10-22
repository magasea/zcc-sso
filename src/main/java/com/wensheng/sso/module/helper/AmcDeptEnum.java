package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
public enum AmcDeptEnum {
  BUSINESS_DEPT("BUSINESS_DEPT","业务部门", 1),
  ESTATE_DEPT("ESTATE_DEPT","地产部门", 2),
  RISKCTRL_DEPT("RISKCTRL_DEPT","风控部门", 3),
  EQUITY_DEPT("EQUITY_DEPT","股权部门", 4),
  HEADQUARTER_DEPT("HEADQUARTER_DEPT","总部",5),
  TECH_DEPT("TECH_DEPT","技术部门",6),
  OUTSIDE("OUTSIDE","公司外部合作伙伴等",7),
  FINANCE_DEPT("FINANCE_DEPT","财务部门",8),
  PERSONNEL_DEPT("PERSONNEL_DEPT","人事部门",9),

  ;
  private String name;
  private String cname;
  private Integer id;

  AmcDeptEnum(String name, String cname, int id){
    this.name = name;
    this.cname = cname;
    this.id = id;
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
}
