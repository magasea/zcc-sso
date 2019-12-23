package com.wensheng.sso.utils;

public class LoginExceptionUtils {

  public static enum LoginExceptionEnum{
    USERNAME_ERROR("10001", "USERNAME_ERROR","用户名错误"),
    USERPASSWD_ERROR("10002", "USERPASSWD_ERROR","密码错误"),
    NOAUTHORITY_ERROR("10003", "NOAUTHORITY_ERROR","没有权限"),
    USERDISABLED_ERROR("10004", "USERDISABLED_ERROR","用户被禁用"),

    ;
    String errorCode;
    String errorMsg;
    String desc;
    LoginExceptionEnum(String errorCode, String errorMsg, String desc){
      this.errorCode = errorCode;
      this.errorMsg = errorMsg;
      this.desc = desc;
    }

    @Override
    public String toString() {
      return String.format("%s %s %s", errorCode, errorMsg, desc);

    }
  }

}
