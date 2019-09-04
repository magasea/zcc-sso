package com.wensheng.sso.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chenwei on 2/1/19
 * @project zcc-backend
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(value = "dev")
public class BasicConfigurationIntegrationTest {
  TestRestTemplate restTemplate;
  URL base;
  @LocalServerPort
  int port;

  @Before
  public void setUp() throws MalformedURLException {
    restTemplate = new TestRestTemplate("chenwei", "wensheng");
    base = new URL("http://localhost:" + port+
        "/login");
  }

  @Test
  public void whenLoggedUserRequestsHomePage_ThenSuccess()
      throws IllegalStateException, IOException {
    LoginVo loginVo = new LoginVo();
    loginVo.setUserName("chenwei");
    loginVo.setPassword("wensheng");
    ResponseEntity<String> response
        = restTemplate.postForEntity(base.toString(), loginVo, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response
        .getBody()
        .contains("wensheng"));
  }

  @Test
  public void whenUserWithWrongCredentials_thenUnauthorizedPage()
      throws Exception {

    restTemplate = new TestRestTemplate("user", "wrongpassword");
    ResponseEntity<String> response
        = restTemplate.getForEntity(base.toString(), String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertTrue(response
        .getBody()
        .contains("Unauthorized"));
  }

  @Data
  public class LoginVo {
    String userName;
    String phoneNum;
    String password;
    String verfCode;

  }
}
