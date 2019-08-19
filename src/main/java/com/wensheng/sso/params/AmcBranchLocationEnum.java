package com.wensheng.sso.params;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/29/19
 * @project miniapp-backend
 */
public enum AmcBranchLocationEnum {

  GUANGDONG(1, "guangdong", "广东"),
  ZHEJIANG(2, "zhejiang", "浙江"),
  JIANGSU(3, "jiangsu", "江苏"),
  BEIJING(4,"beijing","北京"),
  SHANGHAI(5, "shanghai", "上海"),
  OTHER(6, "other", "其他"),
  ;

  int id;
  String name;
  String cname;

  AmcBranchLocationEnum(int id, String name, String cname){
    this.id = id;
    this.name = name;
    this.cname = cname;
  }

  private static final Function<Integer, AmcBranchLocationEnum> func =
      EnumUtils.lookupMap(AmcBranchLocationEnum.class, e -> e.getId());
  public static AmcBranchLocationEnum lookupByIdUtil(int id) {
    return func.apply(id);
  }


  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCname() {
    return cname;
  }
}
