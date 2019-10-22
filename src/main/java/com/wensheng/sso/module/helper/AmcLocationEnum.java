package com.wensheng.sso.module.helper;

import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
public enum AmcLocationEnum {
  BEIJING_LOCATION("BEIJING_LOCATION","北京地区", 1),
  SHANGHAI_LOCATION("SHANGHAI_LOCATION","上海地区", 2),
  JIANGSU_LOCATION("JIANGSU_LOCATION","江苏地区", 3),
  ZHEJIANG_LOCATION("ZHEJIANG_LOCATION","浙江地区", 4),
  GUANGDONG_LOCATION("GUANGDONG_LOCATION","广东地区", 5),
  OTHER_LOCATION("OTHER_LOCATION","其他地区", 6),
  ;
  private String name;
  private String cname;
  private Integer id;

  AmcLocationEnum(String name, String cname, int id){
    this.name = name;
    this.cname = cname;
    this.id = id;
  }

  private static final Function<String, AmcLocationEnum> func =
      EnumUtils.lookupMap(AmcLocationEnum.class, e -> e.getName());
  public static AmcLocationEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, AmcLocationEnum> funcId =
      EnumUtils.lookupMap(AmcLocationEnum.class, e -> e.getId());
  public static AmcLocationEnum lookupByDisplayIdUtil(Integer id) {
    return funcId.apply(id);
  }


  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }}
