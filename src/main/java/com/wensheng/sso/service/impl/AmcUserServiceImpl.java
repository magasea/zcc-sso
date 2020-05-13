package com.wensheng.sso.service.impl;

import com.wensheng.sso.aop.AmcUserOpLogger;
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
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleExample;
import com.wensheng.sso.module.helper.AmcCmpyEnum;
import com.wensheng.sso.module.helper.AmcDeptEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.module.helper.AmcSpecialUserEnum;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.service.AmcUserService;
import com.wensheng.sso.service.KafkaService;
import com.wensheng.sso.service.util.QueryParam;
import com.wensheng.sso.service.util.UserUtils;
import com.wensheng.sso.utils.AmcAppPermCheckUtil;
import com.wensheng.sso.utils.AmcDateUtils;
import com.wensheng.sso.utils.ExceptionUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import com.wensheng.sso.utils.SQLUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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

  @Autowired
  SSOTokenCheckServiceImpl ssoTokenCheckService;

  @Resource(name = "ssoTokenServices")
  private ConsumerTokenServices tokenServices;

  @Resource(name = "tokenStore")
  private TokenStore tokenStore;

  @Value("${spring.security.oauth2.client.registration.amc-admin.client-id}")
  private String amcAdminClientId;

  private final String defaultPasswd = "wensheng";
  public static final String cacheUserPageKey = "cacheUserPageKey";

  @Autowired
  KafkaService kafkaService;

  @Override
  @CacheEvict(allEntries = true)
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
  @AmcUserOpLogger
  @Transactional(rollbackFor = Exception.class)
  @CacheEvict(allEntries = true)
  public AmcUser createUser(AmcUser amcUser) throws Exception {
    int cnt = amcUserMapper.insertSelective(amcUser);
    if(cnt > 0){
      if(amcUser.getTitle() != null || amcUser.getDeptId() != null){
        updateUserRole(amcUser);
      }
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
  @AmcUserOpLogger
  @Transactional(rollbackFor = Exception.class)
  public AmcUser createAmcUser(AmcUser amcUser) throws Exception {
    AmcUser amcUserCreated = createUserAndRole(amcUser);
    return amcUserCreated;
  }



  @Override
  @CacheEvict
  @AmcUserOpLogger
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
  @AmcUserOpLogger
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
  @Cacheable
  public List<AmcUser> getAllUsers() {
    List<AmcUser> amcUsers =  amcUserMapper.selectByExample(null);
    amcUsers.stream().forEach(amcUser -> amcUser.setPassword(""));
    return amcUsers;
  }

  @Override
  @AmcUserOpLogger
  @CacheEvict(allEntries = true)
  @Transactional(rollbackFor = Exception.class)
  public boolean userMod(AmcUser amcUser) throws Exception {
    amcUser.setPassword(null);
    String mobilePhone = amcUser.getMobilePhone();
    amcUser.setMobilePhone(null);
    AmcUser historyUser = amcUserMapper.selectByPrimaryKey(amcUser.getId());

    amcUserMapper.updateByPrimaryKeySelective(amcUser);
    AmcUser amcUserUpdated = amcUserMapper.selectByPrimaryKey(amcUser.getId());
    if(amcUser.getDeptId() != null || amcUser.getTitle() != null){
      updateUserRole(amcUserUpdated);
    }
    ssoTokenCheckService.revokenTokenByUserName(mobilePhone);
    amcUser.setMobilePhone(mobilePhone);
    if(!amcUser.getTitle().equals(historyUser.getTitle()) || !amcUser.getDeptId().equals(historyUser.getDeptId()) ||
        !amcUser.getLocation().equals( historyUser.getLocation()) || !amcUser.getLgroup().equals(historyUser.getLgroup()) ||
        !amcUser.getValid().equals(historyUser.getValid())){
      kafkaService.send(amcUser);
    }
    return true;
  }
  @Override
  @CacheEvict(allEntries = true)
  public boolean updateUserRole(AmcUser amcUser) throws Exception {
    AmcDeptEnum amcDeptEnum = AmcDeptEnum.lookupByDisplayIdUtil(amcUser.getDeptId().intValue());
    AmcSSOTitleEnum amcSSOTitleEnum = AmcSSOTitleEnum.lookupByDisplayIdUtil(amcUser.getTitle());
    List<AmcSSORolesEnum> amcSSORolesEnums = AmcAppPermCheckUtil.getRoleByUserProperties(amcDeptEnum, amcSSOTitleEnum);
    if(CollectionUtils.isEmpty(amcSSORolesEnums)){
      log.error("User with id:{}, name:{}, phone:{} dept:{} title:{} doesn't have valid dept id and tilte",
          amcUser.getId(), amcUser.getUserName(), amcUser.getMobilePhone(), amcUser.getDeptId(), amcUser.getTitle());
      throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_PARAM, String.format("User with id:%d, name:%s, "
              + "phone:%s dept:%d title:%d doesn't have valid dept id and tilte",
          amcUser.getId(), amcUser.getUserName(), amcUser.getMobilePhone(), amcUser.getDeptId(), amcUser.getTitle()));
    }
    AmcUserRoleExample amcUserRoleExample = new AmcUserRoleExample();
    amcUserRoleExample.createCriteria().andUserIdEqualTo(amcUser.getId());
    amcUserRoleMapper.deleteByExample(amcUserRoleExample);
    for(AmcSSORolesEnum amcSSORolesEnum: amcSSORolesEnums){
      AmcUserRole amcUserRole = new AmcUserRole();
      amcUserRole.setRoleId(Long.valueOf(amcSSORolesEnum.getId()));
      amcUserRole.setUserId(amcUser.getId());
      amcUserRoleMapper.insertSelective(amcUserRole);
    }
    return true;
  }

  @Override
  public boolean initSysAdmin() {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andUserNameEqualTo(AmcSpecialUserEnum.SYSTEM_ADMIN1.getName());
    amcUserExample.or().andMobilePhoneEqualTo(AmcSpecialUserEnum.SYSTEM_ADMIN1.getMobileNum());
    List<AmcUser> amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(!CollectionUtils.isEmpty(amcUsers)){


      for(AmcUser amcUser: amcUsers){
        delUser(amcUser.getId());
      }
    }
    makeNewSysAdmin(AmcSpecialUserEnum.SYSTEM_ADMIN1.getName(),AmcSpecialUserEnum.SYSTEM_ADMIN1.getMobileNum());
    amcUserExample.clear();
    amcUserExample.createCriteria().andUserNameEqualTo(AmcSpecialUserEnum.SYSTEM_ADMIN2.getName());
    amcUserExample.or().andMobilePhoneEqualTo(AmcSpecialUserEnum.SYSTEM_ADMIN2.getMobileNum());
    amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(!CollectionUtils.isEmpty(amcUsers)){


      for(AmcUser amcUser: amcUsers){
        delUser(amcUser.getId());
      }
    }
    makeNewSysAdmin(AmcSpecialUserEnum.SYSTEM_ADMIN2.getName(),AmcSpecialUserEnum.SYSTEM_ADMIN2.getMobileNum());


    amcUserExample.clear();
    amcUserExample.createCriteria().andUserNameEqualTo(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getName());
    amcUserExample.or().andMobilePhoneEqualTo(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getMobileNum());
    amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(!CollectionUtils.isEmpty(amcUsers)){


      for(AmcUser amcUser: amcUsers){
        delUser(amcUser.getId());
      }
    }
    makeNewZccAdmin(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getName(), AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getMobileNum()
        , Long.valueOf(AmcDeptEnum.SPECIAL_ZCC_SYS.getId()));


    amcUserExample.clear();
    amcUserExample.createCriteria().andUserNameEqualTo(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getName());
    amcUserExample.or().andMobilePhoneEqualTo(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getMobileNum());
    amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(!CollectionUtils.isEmpty(amcUsers)){


      for(AmcUser amcUser: amcUsers){
        delUser(amcUser.getId());
      }
    }
    makeNewZccAdmin(AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getName(), AmcSpecialUserEnum.ZCC_SYSTEM_ADMIN.getMobileNum()
        , Long.valueOf(AmcDeptEnum.SPECIAL_ZCC_SYS.getId()));

    amcUserExample.clear();
    amcUserExample.createCriteria().andUserNameEqualTo(AmcSpecialUserEnum.ZCC_CO_ADMIN.getName());
    amcUserExample.or().andMobilePhoneEqualTo(AmcSpecialUserEnum.ZCC_CO_ADMIN.getMobileNum());
    amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(!CollectionUtils.isEmpty(amcUsers)){


      for(AmcUser amcUser: amcUsers){
        delUser(amcUser.getId());
      }
    }
    makeNewZccAdmin(AmcSpecialUserEnum.ZCC_CO_ADMIN.getName(), AmcSpecialUserEnum.ZCC_CO_ADMIN.getMobileNum()
        , Long.valueOf(AmcDeptEnum.SPECIAL_ZCC_CO.getId()));

    return false;
  }

  private void makeNewSysAdmin(String systemAdminName, String mobileNum) {
    AmcUser amcUser = new AmcUser();
    amcUser.setUserName(systemAdminName);
    amcUser.setMobilePhone(mobileNum);
    amcUser.setPassword(UserUtils.getEncode(systemAdminName));
    amcUser.setCompanyId(Long.valueOf(AmcCmpyEnum.CMPY_WENSHENG.getId()));
    amcUser.setValid(AmcUserValidEnum.VALID.getId());
    amcUser.setDeptId(Long.valueOf(AmcDeptEnum.TECH_DEPT.getId()));
    amcUser.setTitle(AmcSSOTitleEnum.TITLE_MGR.getId());
    amcUserMapper.insertSelective(amcUser);
    AmcUserRole amcUserRole = new AmcUserRole();
    amcUserRole.setUserId(amcUser.getId());
    amcUserRole.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_SYS_ADM.getId()));
    amcUserRole.setCreateBy(1L);
    amcUserRole.setCreateDate( Date.from( LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant()));
    amcUserRoleMapper.insertSelective(amcUserRole);
  }

//  private void makeNewZccSysAdmin(String systemAdminName, String mobileNum) {
//    AmcUser amcUser = new AmcUser();
//    amcUser.setUserName(systemAdminName);
//    amcUser.setMobilePhone(mobileNum);
//    amcUser.setPassword(UserUtils.getEncode(systemAdminName));
//    amcUser.setCompanyId(Long.valueOf(AmcCmpyEnum.CMPY_WENSHENG.getId()));
//    amcUser.setValid(AmcUserValidEnum.VALID.getId());
//    amcUser.setDeptId(Long.valueOf(AmcDeptEnum.SPECIAL_ZCC_SYS.getId()));
//    amcUser.setTitle(AmcSSOTitleEnum.TITLE_MGR.getId());
//    amcUserMapper.insertSelective(amcUser);
//    AmcUserRole amcUserRole = new AmcUserRole();
//    amcUserRole.setUserId(amcUser.getId());
//    amcUserRole.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_SYS_ADM.getId()));
//    amcUserRole.setCreateBy(1L);
//    amcUserRole.setCreateDate( Date.from( LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant()));
//    amcUserRoleMapper.insertSelective(amcUserRole);
//  }

  private void makeNewZccAdmin(String systemAdminName, String mobileNum, Long deptId) {
    AmcUser amcUser = new AmcUser();
    amcUser.setUserName(systemAdminName);
    amcUser.setMobilePhone(mobileNum);
    amcUser.setPassword(UserUtils.getEncode(systemAdminName));
    amcUser.setCompanyId(Long.valueOf(AmcCmpyEnum.CMPY_WENSHENG.getId()));
    amcUser.setValid(AmcUserValidEnum.VALID.getId());
    amcUser.setDeptId(deptId);
    amcUser.setTitle(AmcSSOTitleEnum.TITLE_MGR.getId());
    amcUserMapper.insertSelective(amcUser);
    AmcUserRole amcUserRole = new AmcUserRole();
    amcUserRole.setUserId(amcUser.getId());
    amcUserRole.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_SYS_ADM.getId()));
    amcUserRole.setCreateBy(1L);
    amcUserRole.setCreateDate( Date.from( LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant()));
    amcUserRoleMapper.insertSelective(amcUserRole);
  }

  @CacheEvict(allEntries = true)
  @Override
  public void modifyUserValidState(Long userId, AmcUserValidEnum amcUserValidEnum) throws Exception{


    AmcUser amcUser = new AmcUser();
    amcUser.setValid(amcUserValidEnum.getId());
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andIdEqualTo(userId);
    amcUserMapper.updateByExampleSelective(amcUser, amcUserExample);

    if( AmcUserValidEnum.INVALID == amcUserValidEnum){
      log.info("will disableuser:{}", userId);
      disableUser(userId);
    }
  }

  @CacheEvict(allEntries = true)
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
    if( AmcUserValidEnum.INVALID == amcUserValidEnum){
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
  @Cacheable
//  @CachePut( key = "#root.target.cacheUserPageKey")
  public List<AmcUser> queryUserPage(int offset, int size, QueryParam queryParam, Map<String, Direction> orderByParam) {
    AmcUserExample amcUserExample = SQLUtils.getAmcUserExample(queryParam);
    RowBounds rowBounds = new RowBounds(offset, size);
    try{

      amcUserExample.setOrderByClause(SQLUtils.getOrderBy(orderByParam, rowBounds));
    }catch (Exception ex){
      log.error("set order by exception:", ex);
    }
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    amcUsers.forEach(item -> item.setPassword(""));

    return amcUsers;
  }

  @Override
  public Long queryUserCount(QueryParam queryParam) {
    AmcUserExample amcUserExample = SQLUtils.getAmcUserExample(queryParam);
    return amcUserMapper.countByExample(amcUserExample);
  }

  @Override
  @AmcUserOpLogger
  public boolean resetUserPwd(Long userId) {
    if(userId <= 0){
      return false;
    }
    AmcUser amcUser =  amcUserMapper.selectByPrimaryKey(userId);
    if(amcUser == null){
      return false;
    }
    amcUser.setPassword( UserUtils.getEncode( defaultPasswd));
    amcUserMapper.updateByPrimaryKeySelective(amcUser);
    return true;
  }

  @Override
  @AmcUserOpLogger
  public boolean changePwd(String originPwd, String newPwd, String mobilePhone) throws Exception {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andMobilePhoneEqualTo(mobilePhone);
    List<AmcUser> amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(CollectionUtils.isEmpty(amcUsers)){
      throw ExceptionUtils.getAmcException(AmcExceptions.NO_SUCHUSER, String.format("无此用户 mobilePhone:%s",
          mobilePhone));
    }
    if(!UserUtils.match(originPwd, amcUsers.get(0).getPassword())){
      throw ExceptionUtils.getAmcException(AmcExceptions.NO_SUCHUSER, "原密码不正确, 如需重置密码可以联系技术部在单"
          + "点登录管理中心申请重置密码");
    }
    amcUsers.get(0).setPassword(com.wensheng.sso.utils.UserUtils.getEncode(newPwd));
    amcUserMapper.updateByPrimaryKey(amcUsers.get(0));
    kafkaService.send(amcUsers.get(0));
    return true;
  }

  private AmcUser createUserAndRole(AmcUser amcUser) throws Exception {
    if(StringUtils.isEmpty(amcUser.getPassword())){
      amcUser.setPassword(defaultPasswd);
    }
    amcUser.setPassword(UserUtils.getEncode(amcUser.getPassword()));
    AmcUserExample amcUserExample = new AmcUserExample();
    boolean hasUName = false;
    boolean hasPhoneNum = false;
    if(!StringUtils.isEmpty(amcUser.getUserName())){
      hasUName  = true;
    }
    if(!StringUtils.isEmpty(amcUser.getMobilePhone())){
      hasPhoneNum = true;
    }

    if(hasUName && hasPhoneNum){
      amcUserExample.createCriteria().andUserNameEqualTo(amcUser.getUserName());
      amcUserExample.or().andMobilePhoneEqualTo(amcUser.getMobilePhone());
      List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
      if(CollectionUtils.isEmpty(amcUsers)){
        amcUserMapper.insertSelective(amcUser);
      }else{
        throw ExceptionUtils.getAmcException(AmcExceptions.DUPLICATE_OBJECT, String.format("用户名:%s 或者 "
                + "手机号:%s 已经存在，请尝试别的用户名或者手机号",
            amcUser.getUserName(), amcUser.getMobilePhone()));
      }
    }else if(hasPhoneNum){
      amcUserExample.createCriteria().andMobilePhoneEqualTo(amcUser.getMobilePhone());
      List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
      if(CollectionUtils.isEmpty(amcUsers)){
        amcUserMapper.insertSelective(amcUser);
      }else{
        throw ExceptionUtils.getAmcException(AmcExceptions.DUPLICATE_OBJECT, String.format("用户名手机号:%s 已经存在，请尝试别的手机号",
             amcUser.getMobilePhone()));
      }
    }else if(hasUName){
      amcUserExample.createCriteria().andUserNameEqualTo(amcUser.getUserName());
      List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
      if(CollectionUtils.isEmpty(amcUsers)){
        amcUserMapper.insertSelective(amcUser);
      }else{
        throw ExceptionUtils.getAmcException(AmcExceptions.DUPLICATE_OBJECT, String.format("用户名:%s 已经存在，请尝试别的用户名",
            amcUser.getUserName()));
      }
    }

    else{
      amcUserMapper.insertSelective(amcUser);
    }
    kafkaService.send(amcUser);

//    List<Long> roleIds = new ArrayList<>();
//    AmcSSORolesEnum amcSSORolesEnum =
//        AmcTitleRoleUtil.getRoleByTitle(AmcSSOTitleEnum.lookupByDisplayIdUtil(amcUser.getTitle()));
//    roleIds.add(Long.valueOf(amcSSORolesEnum.getId()));
//    addRolesForUser(amcUser.getId(), roleIds);
    updateUserRole(amcUser);
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
