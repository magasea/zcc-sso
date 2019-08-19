package com.wensheng.sso.module.vo;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRole;
import lombok.Data;

/**
 * @author chenwei on 2/20/19
 * @project zcc-backend
 */
@Data
public class UserCreateVo {

  String userName;
  String password;
  String repPasswd;
  String phoneNum;
  String verificationCode;
  String department;
  String group;
  String email;
  AmcRole amcRole;

}
