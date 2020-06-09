package com.wensheng.sso.module.helper;


import com.wensheng.sso.utils.base.EnumUtils;
import java.util.function.Function;

/**
 * @author chenwei on 1/15/19
 * @project zcc-backend
 */
public enum ImagePathClassEnum {
  DEBT(1, "debt"),
  ASSET(2, "asset"),
  SALEMENU(3, "salemenu"),
  SALEBANNER(4, "salebanner"),
  SALEMENUPAGE(5, "salemenupage"),
//  SALEBANNERPAGE(6, "salebannerpage"),

  SALEFLOORPAGE(6, "salefloorpage"),
  CONTACTORIMG(7, "contactorimg"),
  CONTACTORWXIMG(8, "contactorwximg"),
  ;
  int id;
  String name;
  ImagePathClassEnum(int id, String name){
    this.id = id;
    this.name = name;
  }



  public String getName(){
    return name;
  }

  private static final Function<String, ImagePathClassEnum> func =
      EnumUtils.lookupMap(ImagePathClassEnum.class, e -> e.getName());
  public static ImagePathClassEnum lookupByDisplayNameUtil(String name) {
    return func.apply(name);
  }

  private static final Function<Integer, ImagePathClassEnum> funcId =
      EnumUtils.lookupMap(ImagePathClassEnum.class, e -> e.getId());
  public static ImagePathClassEnum lookupByIdUtil(Integer id) {
    return funcId.apply(id);
  }
  public int getId() {
    return id;
  }


}
