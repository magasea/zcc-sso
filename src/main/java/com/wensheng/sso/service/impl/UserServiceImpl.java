package com.wensheng.sso.service.impl;

import com.wensheng.sso.dao.mysql.mapper.AmcPermissionMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcRoleMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcRolePermissionMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserRoleMapper;
import com.wensheng.sso.dao.mysql.mapper.ext.AmcUserExtMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRoleExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.ext.AmcUserExt;
import com.wensheng.sso.module.helper.AmcAPPEnum;
import com.wensheng.sso.module.helper.AmcPermEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.module.vo.AmcUserDetail;
import com.wensheng.sso.service.UserService;
import com.wensheng.sso.utils.AmcAppPermCheckUtil;
import com.wensheng.sso.utils.AmcBeanUtils;
import com.wensheng.sso.utils.AmcDateUtils;
import com.wensheng.sso.utils.AmcNumberUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import com.wensheng.sso.utils.LoginExceptionUtils;
import com.wensheng.sso.utils.LoginExceptionUtils.LoginExceptionEnum;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author chenwei on 1/30/19
 * @project zcc-backend
 */
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired
  AmcUserMapper amcUserMapper;

  @Autowired
  AmcPermissionMapper amcPermissionMapper;

  @Autowired
  AmcRolePermissionMapper amcRolePermissionMapper;

  @Autowired
  AmcUserRoleMapper amcUserRoleMapper;

  @Autowired
  AmcUserExtMapper amcUserExtMapper;

  @Autowired
  AmcRoleMapper amcRoleMapper;

  @Autowired
  PasswordEncoder passwordEncoder;
//
//  @Autowired
//  AuthenticationManager authenticationManagerBean;
//
//  @Autowired
//  DefaultTokenServices tokenServices;
//
//  @Autowired
//  DaoAuthenticationProvider daoAuthenticationProvider;

  @Override
  public List<AmcUser> getUserByPhone(String phoneNum) {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andMobilePhoneEqualTo(phoneNum);
    return amcUserMapper.selectByExample(amcUserExample);
  }

  @Override
  public UserDetails loadUserByUsername(String inputParam) throws UsernameNotFoundException {

    AmcAPPEnum amcAPPEnum = null;
    AmcPermEnum amcPermEnum = null;
    String[] userInfos = null;
    boolean withId = false;
    if(!inputParam.contains(":")){
      log.error("Should contain amcAppInfo");
    }else {
      withId = true;
      userInfos = inputParam.split(":");
      amcAPPEnum =  AmcAPPEnum.lookupByDisplayIdUtil(Integer.valueOf(userInfos[1]));
      if( null == amcAPPEnum){
        throw new PreAuthenticatedCredentialsNotFoundException(String.format("没有该应用:%s", userInfos[1]));
      }
    }
    AmcUserExample amcUserExample = new AmcUserExample();
    boolean isPhone = false;
    if((userInfos == null && AmcNumberUtils.isNumeric(inputParam)) || (userInfos != null && AmcNumberUtils.isNumeric(userInfos[0]))){
      isPhone = false;
      if(userInfos == null){
        amcUserExample.createCriteria().andMobilePhoneEqualTo(inputParam);

      }else{
        amcUserExample.createCriteria().andMobilePhoneEqualTo(userInfos[0]);

      }
    }else{
      if(userInfos == null){
        amcUserExample.createCriteria().andUserNameEqualTo(inputParam);

      }else{
        amcUserExample.createCriteria().andUserNameEqualTo(userInfos[0]);

      }
    }

    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    if(CollectionUtils.isEmpty(amcUsers)){
      log.error("cannot find user for withId:{} isPhone:{} input param:{} ", withId, isPhone, inputParam);

      throw new InternalAuthenticationServiceException(String.format("%s%s",
          LoginExceptionEnum.USERNAME_ERROR.toString(), String.format("系统中没有该用户[%s]", inputParam) ));
    }

    List<String> authorities = getPermissions(amcUsers.get(0));

    List<GrantedAuthority> grantedAuthorityAuthorities = new ArrayList<>();
    authorities.forEach( auth -> grantedAuthorityAuthorities.add(new SimpleGrantedAuthority(auth)));
    if(userInfos != null){
      try {
        boolean hasPermOnApp = AmcAppPermCheckUtil.checkPermApp(grantedAuthorityAuthorities, amcAPPEnum);
        if(!hasPermOnApp){
          throw new InternalAuthenticationServiceException(String.format("%s%s",LoginExceptionEnum.NOAUTHORITY_ERROR.toString(),
              String.format("用户:%s 不能访问该应用:%s", amcUsers.get(0).getId(), amcAPPEnum.getCname() )));
        }
      } catch (Exception e) {
        log.error("user cannot access the app ", e);
        throw new InternalAuthenticationServiceException(String.format("%s%s",
            LoginExceptionEnum.NOAUTHORITY_ERROR.toString(), e.getMessage()));
      }
    }

    boolean userEnabled = amcUsers.get(0).getValid().equals(AmcUserValidEnum.VALID.getId());
    boolean isExpired = false;
    try {
      if(amcUsers.get(0).getExpireDate().after( AmcDateUtils.getDateFromStr("1901-01-01"))
          && amcUsers.get(0).getExpireDate().before(AmcDateUtils.getCurrentDate())){
        userEnabled = false;
        isExpired = true;
      }
    } catch (ParseException e) {
      log.error("Failed to handle date compare :{}", e);
    }

    if(!userEnabled){
      log.error("user is disabled:{}", inputParam);
      throw new InternalAuthenticationServiceException(String.format("%s",
          LoginExceptionEnum.NOAUTHORITY_ERROR.toString(), isExpired? "超出账户有效期":""));
    }


    AmcUserDetail amcUserDetail = new AmcUserDetail(amcUsers.get(0).getMobilePhone(),"",userEnabled, userEnabled,userEnabled,
        userEnabled,grantedAuthorityAuthorities);
    AmcBeanUtils.copyProperties(amcUsers.get(0), amcUserDetail);

//    UserDetails userDetails =
//        User.builder().authorities(grantedAuthorityAuthorities).username(amcUsers.get(0).getMobilePhone()).password(amcUsers.get(0).getPassword()).disabled(!amcUsers.get(0).getValid().equals(
//        AmcUserValidEnum.VALID.getId())).build();
    return amcUserDetail;
  }


  @Override
  public List<String> getPermissions(AmcUser amcUser){

    AmcUserExt amcUserExt = amcUserExtMapper.selectByExtExample(amcUser.getId());
    List<String> authorities = new ArrayList<>();
//    boolean isAmcOper = false;
//    if(amcUser.getCompanyId() != null && amcUser.getCompanyId() > 0){
//      authorities.add(String.format("PERM_%s_READ",amcUser.getCompanyId()));
//      isAmcOper = true;
//    }
    boolean isCompanyUser = false;
    if(amcUser.getCompanyId() != null && amcUser.getCompanyId() > 0){
      isCompanyUser = true;
    }
    StringBuilder sb = new StringBuilder();
    for(AmcPermission amcPermission: amcUserExt.getAmcPermissions()){
      sb.setLength(0);
      if(amcPermission.getName().contains("AMC") && isCompanyUser){
        sb.append("PERM_").append(amcUser.getCompanyId()).append("_").append(amcPermission.getName().substring("PERM_".length()));
        authorities.add(sb.toString());
      }else{
        authorities.add(amcPermission.getName());
      }
    }
    for(AmcRole amcRole: amcUserExt.getAmcRoles()){
      authorities.add(amcRole.getName());
//      if(isAmcOper && amcRole.getName().equals(AmcRolesEnum.ROLE_AMC_ADMIN.name())){
//        authorities.add(String.format("PERM_%s_WRITE",amcUser.getCompanyId()));
//      }
    }
    return authorities;

  }

  @Override
  public AmcUser createDefaultAmcUser(AmcUser amcUser) {
    amcUser.setPassword(passwordEncoder.encode(amcUser.getPassword()));
    amcUserExtMapper.insertSelective(amcUser);
    Long userId = amcUser.getId();
    AmcUserRole amcUserRole = new AmcUserRole();
    amcUserRole.setCreateBy(userId);
    amcUserRole.setCreateDate(Date.from(Instant.now()));
    amcUserRole.setUserId(userId);
    AmcRoleExample amcRoleExample = new AmcRoleExample();
    amcRoleExample.createCriteria().andNameEqualTo(AmcSSORolesEnum.ROLE_SSO_STAFF.name());
    List<AmcRole> amcRoles = amcRoleMapper.selectByExample(amcRoleExample);
    Long roleId = amcRoles.get(0).getId();
    amcUserRole.setRoleId(roleId);

    amcUserRoleMapper.insert(amcUserRole);
    amcUser.setId(userId);
    return amcUser;

  }

  @Override
  public Map<String, String> getTokens(AmcUser amcUser) {
//    authenticationManagerBean;
//    Authentication authentication = new UsernamePasswordAuthenticationToken(amcUser.getMobilePhone(),
//        amcUser.getPassword());
//
//    tokenServices.createAccessToken( authenticationManagerBean.authenticate(authentication) )
    return null;
  }

  @Override
  public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andIdEqualTo(userId);
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
    if(CollectionUtils.isEmpty(amcUsers)){
      throw new UsernameNotFoundException(AmcExceptions.NO_SUCHUSER.toString());
    }
    List<String> authorities = getPermissions(amcUsers.get(0));

    List<GrantedAuthority> grantedAuthorityAuthorities = new ArrayList<>();
    authorities.forEach( auth -> grantedAuthorityAuthorities.add(new SimpleGrantedAuthority(auth)));
    boolean userEnabled = amcUsers.get(0).getValid().equals(AmcUserValidEnum.VALID.getId());
    AmcUserDetail amcUserDetail = new AmcUserDetail(amcUsers.get(0).getMobilePhone(),"",userEnabled, userEnabled,userEnabled,
        userEnabled,grantedAuthorityAuthorities);
    AmcBeanUtils.copyProperties(amcUsers.get(0), amcUserDetail);

//    UserDetails userDetails =
//        User.builder().authorities(grantedAuthorityAuthorities).username(amcUsers.get(0).getMobilePhone()).password(amcUsers.get(0).getPassword()).disabled(!amcUsers.get(0).getValid().equals(
//        AmcUserValidEnum.VALID.getId())).build();
    return amcUserDetail;
  }


}
