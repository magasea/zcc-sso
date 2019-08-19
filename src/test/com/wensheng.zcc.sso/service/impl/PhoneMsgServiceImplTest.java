package com.wensheng.zcc.sso.service.impl;

import static org.junit.Assert.*;

import com.wensheng.zcc.sso.service.PhoneMsgService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chenwei on 2/22/19
 * @project zcc-backend
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles(value = "dev")
public class PhoneMsgServiceImplTest {

  @Autowired
  PhoneMsgService phoneMsgService;

  @Test
  public void generateVerificationCodeToPhone() {

    System.out.println(phoneMsgService.generateVerificationCodeToPhone("13611894398"));
  }

  @Test
  public void verifyPhoneAndVerifyCode() {
    System.out.println(phoneMsgService.verifyPhoneAndVerifyCode("13611894398", "497897"));
  }

  @Test
  public void saveVerificationCode() {
    phoneMsgService.saveVerificationCode("13611894398", "497897");
  }
}