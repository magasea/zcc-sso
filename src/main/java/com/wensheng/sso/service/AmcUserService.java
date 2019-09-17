package com.wensheng.sso.service;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRolePermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.service.util.QueryParam;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author chenwei on 3/14/19
 * @project zcc-backend
 */
public interface AmcUserService {

  public void modifyUserRole(Long userId, List<Long> roleIds);

  AmcUser createUser(AmcUser amcUser);

  List<AmcRole> getAmcRoles();

  List<AmcRolePermission> getAmcRolePerms();

  AmcUser createAmcAdmin(AmcUser amcUser);

  AmcUser createAmcUser(AmcUser amcUser) throws Exception;

  void delUser(Long userId);

  AmcUser getUserById(Long userId);

  void disableUser(Long userId);

  List<AmcUser> getAmcUsers(Long amcId);
  List<AmcUser> getAmcUsers(AmcUserExample amcUserExample);

  List<AmcUserRole> getAmcUserRoles(Long userId);

    List<AmcUser> getAmcUserByPhoneNum(String phoneNum);

  List<AmcUser> getAllUsers();

  void modifyUserValidState(Long userId, AmcUserValidEnum amcUserValidEnum) throws Exception;

  void modifyUserValidState(Long userId, Long amcId, AmcUserValidEnum amcUserValidEnum) throws Exception;

  List<AmcPermission> getAmcPerms();

  List<AmcUser> searchUserByPhone(String mobilePhone);
  List<AmcUser> searchUserByName(String name);

  List<AmcUser> queryUserPage(int offset, int size, QueryParam queryParam, Map<String, Direction> orderByParam);

  Long queryUserCount(QueryParam queryParam);
}
