package com.wensheng.sso.module.vo;

import lombok.Data;

/**
 * @author chenwei on 2/1/19
 * @project zcc-backend
 */
@Data
public class LoginVo {
  String userName;
  String phoneNum;
  String password;
  String verfCode;

}
