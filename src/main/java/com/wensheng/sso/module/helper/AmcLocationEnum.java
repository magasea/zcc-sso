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
  NANJING_LOCATION("NANJING_LOCATION","南京地区", 3),
  HANGZHOU_LOCATION("HANGZHOU_LOCATION","杭州地区", 4),
  GUANGZHOU_LOCATION("GUANGZHOU_LOCATION","广州地区", 5),
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
