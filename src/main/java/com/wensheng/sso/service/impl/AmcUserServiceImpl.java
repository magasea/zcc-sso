package com.wensheng.sso.service.impl;

import com.wensheng.sso.aop.AmcUserQueryChecker;
import com.wensheng.sso.dao.mysql.mapper.AmcPermissionMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcRoleMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcRolePermissionMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserRoleMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRolePermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample.Criteria;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleExample;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.service.AmcUserService;
import com.wensheng.sso.service.util.QueryParam;
import com.wensheng.sso.service.util.UserUtils;
import com.wensheng.sso.utils.AmcTitleRoleUtil;
import com.wensheng.sso.utils.SQLUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author chenwei on 3/14/19
 * @project zcc-backend
 */
@Service
@Slf4j
@CacheConfig(cacheNames = {"USER"})
public class AmcUserServiceImpl implements AmcUserService {

  @Autowired
  AmcUserMapper amcUserMapper;

  @Autowired
  AmcUserRoleMapper amcUserRoleMapper;

  @Autowired
  AmcRoleMapper amcRoleMapper;

  @Autowired
  AmcRolePermissionMapper amcRolePermissionMapper;

  @Autowired
  AmcPermissionMapper amcPermissionMapper;

  @Resource(name = "ssoTokenServices")
  private ConsumerTokenServices tokenServices;

  @Resource(name = "tokenStore")
  private TokenStore tokenStore;

  @Value("${spring.security.oauth2.client.registration.amc-admin.client-id}")
  private String amcAdminClientId;


  @Override
  @CacheEvict
  public void modifyUserRole(Long userId, List<Long> roleIds) {
    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(userId);
    List<AmcUserRole> amcUserRoles = amcUserRoleMapper.selectByExample(amcUserRoleExample);
    List<Long> addRoleIds = new ArrayList<>();
    final Set<Long>  updateRoleIds = roleIds.stream().collect(Collectors.toSet());
    final Set<Long>  historyRoleIds = amcUserRoles.stream().map(item -> item.getRoleId()).collect(Collectors.toSet());
    if(!CollectionUtils.isEmpty(amcUserRoles)){

      addRoleIds = updateRoleIds.stream().filter(item -> !historyRoleIds.contains(item)).collect(Collectors.toList());
      addRolesForUser(userId, addRoleIds);

    }

    if(!CollectionUtils.isEmpty(roleIds)){

      List<Long> delRoleIds =
          historyRoleIds.stream().filter( item -> !updateRoleIds.contains(item)).collect(Collectors.toList());
      deleteRolesFromUser(userId, delRoleIds);
    }

  }

  @Override
  public AmcUser createUser(AmcUser amcUser) {
    int cnt = amcUserMapper.insertSelective(amcUser);
    if(cnt > 0){
      return amcUser;
    }else{
      log.error(String.format("Failed to insert user"));
    }
    return amcUser;
  }

  @Override
  public List<AmcRole> getAmcRoles() {
    return amcRoleMapper.selectByExample(null);
  }

  @Override
  public List<AmcRolePermission> getAmcRolePerms() {
    return amcRolePermissionMapper.selectByExample(null);
  }

  @Override
  public AmcUser createAmcAdmin(AmcUser amcUser) {
    return null;
  }


  @Override
  public AmcUser createAmcUser(AmcUser amcUser) throws Exception {
    AmcUser amcUserCreated = createUserAndRole(amcUser);
    return amcUserCreated;
  }



  @Override
  @CacheEvict
  @Transactional
  public void delUser(Long userId) {
    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(userId);
    amcUserRoleMapper.deleteByExample(amcUserRoleExample);
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andIdEqualTo(userId);
    amcUserMapper.deleteByExample(amcUserExample);
  }

  @Override
  @Cacheable
  public AmcUser getUserById(Long userId) {
    return amcUserMapper.selectByPrimaryKey(userId);
  }

  @CacheEvict
  @Override
  public void disableUser(Long userId) {
    AmcUser amcUser = amcUserMapper.selectByPrimaryKey(userId);
    if(null == amcUser){
      log.error("Failed to find user with id:{}", userId);
      return;
    }
    Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByClientIdAndUserName(amcAdminClientId,
        amcUser.getMobilePhone());
    for(OAuth2AccessToken oAuth2AccessToken : accessTokens){
      log.info("Revoke token:{}", oAuth2AccessToken.getValue());
      tokenServices.revokeToken(oAuth2AccessToken.getValue());
    }
  }

  @Override
  public List<AmcUser> getAmcUsers(Long amcId) {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andCompanyIdEqualTo(amcId);
    List<AmcUser> amcUsers = this.getAmcUsers(amcUserExample);
    amcUsers.stream().forEach(amcUser -> amcUser.setPassword(""));
    return amcUsers;
  }

  @AmcUserQueryChecker
  @Override
  public List<AmcUser> getAmcUsers(AmcUserExample amcUserExample){
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    return amcUsers;
  }

  @Override
  public List<AmcUserRole> getAmcUserRoles(Long userId) {
    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(userId);
    List<AmcUserRole> amcUserRoles = amcUserRoleMapper.selectByExample(amcUserRoleExample);
    return amcUserRoles;
  }

  @Override
  public List<AmcUser> getAmcUserByPhoneNum(String phoneNum) {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andMobilePhoneEqualTo(phoneNum);
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    return amcUsers;
  }

  @Override
  public List<AmcUser> getAllUsers() {
    List<AmcUser> amcUsers =  amcUserMapper.selectByExample(null);
    amcUsers.stream().forEach(amcUser -> amcUser.setPassword(""));
    return amcUsers;
  }

  @CacheEvict
  @Override
  public void modifyUserValidState(Long userId, AmcUserValidEnum amcUserValidEnum) throws Exception{


    AmcUser amcUser = new AmcUser();
    amcUser.setValid(amcUserValidEnum.getId());
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andIdEqualTo(userId);
    amcUserMapper.updateByExampleSelective(amcUser, amcUserExample);

    if( AmcUserValidEnum.LOCKED == amcUserValidEnum){
      log.info("will disableuser:{}", userId);
      disableUser(userId);
    }
  }

  @CacheEvict
  @Override
  public void modifyUserValidState(Long userId, Long amcId, AmcUserValidEnum amcUserValidEnum) throws Exception {


    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(userId);
//    List<AmcUserRole> amcUserRoles = amcUserRoleMapper.selectByExample(amcUserRoleExample);
//    if(!CollectionUtils.isEmpty(amcUserRoles)){
//      AmcUserRole amcUserRole =
//          amcUserRoles.stream().filter( item -> ( item.getRoleId() == AmcRolesEnum.ROLE_SYSTEM_ADMIN.getId() ||
//              item.getRoleId() == AmcRolesEnum.ROLE_AMC_ADMIN.getId())).findAny().orElse(null);
//      if(amcUserRole != null){
//        throw ExceptionUtils.getAmcException(AmcExceptions.NOT_AUTHORIZED_FORTHISTASK, String.format("target user with "
//            + "role:%s", AmcRolesEnum.lookupByDisplayIdUtil(amcUserRole.getRoleId().intValue()).getName()));
//      }
//    }

    AmcUser amcUser = new AmcUser();
    amcUser.setValid(amcUserValidEnum.getId());
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andIdEqualTo(userId).andCompanyIdEqualTo(amcId);

    amcUserMapper.updateByExampleSelective(amcUser, amcUserExample);
    if( AmcUserValidEnum.LOCKED == amcUserValidEnum){
      log.info("will disableuser:{}", userId);
      disableUser(userId);
    }
  }

  @Override
  public List<AmcPermission> getAmcPerms() {
    List<AmcPermission> amcPermissions = amcPermissionMapper.selectByExample(null);
    return amcPermissions;
  }

  @Override
  public List<AmcUser> searchUserByPhone(String mobilePhone) {

    AmcUserExample amcUserExample = new AmcUserExample();
    StringBuilder sb = new StringBuilder("%");
    sb.append(mobilePhone).append("%");
    amcUserExample.createCriteria().andMobilePhoneLike(sb.toString());
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    return amcUsers;
  }

  @Override
  public List<AmcUser> searchUserByName(String name) {
    AmcUserExample amcUserExample = new AmcUserExample();
    StringBuilder sb = new StringBuilder("%");
    sb.append(name).append("%");
    amcUserExample.createCriteria().andNickNameLike(sb.toString());
    amcUserExample.or(amcUserExample.createCriteria().andUserNameLike(sb.toString()));
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);

    return amcUsers;
  }

  @Override
  public List<AmcUser> queryUserPage(int offset, int size, QueryParam queryParam, Map<String, Direction> orderByParam) {
    AmcUserExample amcUserExample = SQLUtils.getAmcUserExample(queryParam);
    RowBounds rowBounds = new RowBounds(offset, size);
    try{

      amcUserExample.setOrderByClause(SQLUtils.getOrderBy(orderByParam, rowBounds));
    }catch (Exception ex){
      log.error("set order by exception:", ex);
    }
    List<AmcUser> amcUsers = amcUserMapper.selectByExampleWithRowbounds(amcUserExample, rowBounds);
    amcUsers.forEach(item -> item.setPassword(""));

    return amcUsers;
  }

  @Override
  public Long queryUserCount(QueryParam queryParam) {
    AmcUserExample amcUserExample = SQLUtils.getAmcUserExample(queryParam);
    return amcUserMapper.countByExample(amcUserExample);
  }

  private AmcUser createUserAndRole(AmcUser amcUser) throws Exception {
    amcUser.setPassword(UserUtils.getEncode(amcUser.getPassword()));
    amcUserMapper.insertSelective(amcUser);
    List<Long> roleIds = new ArrayList<>();
    AmcSSORolesEnum amcSSORolesEnum =
        AmcTitleRoleUtil.getRoleByTitle(AmcSSOTitleEnum.lookupByDisplayIdUtil(amcUser.getTitle()));
    roleIds.add(Long.valueOf(amcSSORolesEnum.getId()));
    addRolesForUser(amcUser.getId(), roleIds);
    return amcUser;
  }

  private void addRolesForUser(Long userId, List<Long> addRoleIds) {
    if(CollectionUtils.isEmpty(addRoleIds)){
      return;
    }
    for(Long roleId: addRoleIds){
      AmcUserRole amcUserRole = new AmcUserRole();
      amcUserRole.setUserId(userId);
      amcUserRole.setRoleId(roleId);
      amcUserRoleMapper.insertSelective(amcUserRole);
    }
  }

  private void deleteRolesFromUser(Long userId, List<Long> roleIds){
    if(CollectionUtils.isEmpty(roleIds)){
      return;
    }
    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(userId).andRoleIdIn(roleIds);
    amcUserRoleMapper.deleteByExample(amcUserRoleExample);
  }
}
