package com.wensheng.sso.service;

/**
 * @author chenwei on 2/21/19
 * @project zcc-backend
 */
public interface PhoneMsgService {

  /**
   *
   * @param phoneNum
   * @return verify code
   */
  String generateVerificationCodeToPhone(String phoneNum);

  boolean verifyPhoneAndVerifyCode(String phoneNum, String vcode);

  void saveVerificationCode(String phoneNum, String vcode);

}
