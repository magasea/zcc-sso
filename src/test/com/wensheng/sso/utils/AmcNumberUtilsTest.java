package com.wensheng.sso.utils;

import io.jsonwebtoken.lang.Assert;
import org.junit.Test;


public class AmcNumberUtilsTest {
  @Test
  public void isNumber(){
    Assert.isTrue( AmcNumberUtils.isNumeric("101212123423412341"));
  }
}
