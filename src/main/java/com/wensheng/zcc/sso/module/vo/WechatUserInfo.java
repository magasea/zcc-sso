package com.wensheng.zcc.sso.module.vo;

import java.util.List;
import lombok.Data;

@Data
public class WechatUserInfo {
  String openid;
  String nickname;
  Integer sex;
  String province;
  String city;
  String country;
  String headimgurl;
  List<String> privilege;
  String unionid;
}
