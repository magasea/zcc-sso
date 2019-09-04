package com.wensheng.sso.service.impl;


import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.service.UserService;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chenwei on 2/1/19
 * @project zcc-backend
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles(value = "dev")
public class UserServiceImplTest {

  @Autowired
  UserService userService;

  @Test
  public void getPermissions() {
    AmcUser amcUser = new AmcUser();
    amcUser.setId(1L);
    List<String> authorities = userService.getPermissions(amcUser);
    authorities.stream().forEach(auth -> System.out.println(auth));

  }
}