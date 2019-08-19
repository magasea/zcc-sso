package com.wensheng.zcc.sso.service.util;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

/**
 * @author chenwei on 1/30/19
 * @project zcc-backend
 */
public class UserUtilsTest {

  @Test
  public void getEncode() {
    System.out.println(UserUtils.getEncode("wensheng"));
//    $2a$10$fCc3t8/bcE1IZSt1ZaBq3O1A73kU7ok1FVlRyvetAUAEhTPiuqA3e

    System.out.println(UserUtils.match("wensheng", "$2a$10$fCc3t8/bcE1IZSt1ZaBq3O1A73kU7ok1FVlRyvetAUAEhTPiuqA3e"));

  }

  @Test
  public void getBasicAuth(){
    String auth = "fooClientIdPassword" + ":" + "secret";
    byte[] encodedAuth = Base64.encodeBase64(
        auth.getBytes(Charset.forName("US-ASCII")) );
    String authHeader = "Basic " + new String( encodedAuth );
    System.out.println(authHeader);

  }
}