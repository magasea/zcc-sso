package com.wensheng.sso.controller;

import com.wensheng.sso.aop.AmcUserCreateChecker;
import com.wensheng.sso.aop.AmcUserModifyChecker;
import com.wensheng.sso.module.helper.AmcCmpyEnum;
import com.wensheng.sso.service.AmcEMailService;
import com.wensheng.sso.service.impl.AmcEmailServiceImpl;
import com.wensheng.sso.service.util.AmcPage;
import com.wensheng.sso.service.util.PageReqRepHelper;
import com.wensheng.sso.service.util.QueryParam;
import com.wensheng.sso.utils.AmcBeanUtils;
import com.wensheng.sso.utils.ExceptionUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcCompany;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDept;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.module.vo.AmcCmpyDeptVo;
import com.wensheng.sso.module.vo.UserRoleModifyVo;
import com.wensheng.sso.service.AmcBasicService;
import com.wensheng.sso.service.AmcSsoService;
import com.wensheng.sso.service.AmcUserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chenwei on 3/14/19
 * @project zcc-backend
 */
@Controller
@RequestMapping("/amc/user")
@Slf4j
public class AmcUserController {

  @Autowired
  AmcUserService amcUserService;

  @Autowired
  AmcBasicService amcBasicService;

  @Autowired
  AmcSsoService amcSsoService;

  @Autowired
  private ConsumerTokenServices tokenServices;

  @Autowired
  private TokenStore tokenStore;

  @Value("${spring.security.oauth2.client.registration.amc-admin.client-id}")
  private String amcAdminClientId;

  @Autowired
  AmcEMailService amcEmailService;


  @PreAuthorize("hasRole('AMC_ADMIN') and hasPermission(#amcId,'crud_amcuser')")
  @RequestMapping(value = "/amcid/{amcid}/amc-user/create", method = RequestMethod.POST)
  @ResponseBody
  public AmcUser createUser(@RequestBody AmcUser amcUser) throws Exception {

    AmcUser amcUserResult = amcUserService.createUser(amcUser);
    return amcUserResult;
  }

  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  @RequestMapping(value = "/amcid/{amcid}/amc-user/create_amc_admin", method = RequestMethod.POST)
  @ResponseBody
  public AmcUser createAmcAdmin(@RequestBody AmcUser amcUser){
    AmcUser amcUserResult = amcUserService.createAmcAdmin(amcUser);
    return amcUserResult;
  }

  @PreAuthorize("hasAnyRole('ROLE_SSO_SYSTEM_ADMIN','SSO_LDR','SSO_MGR')")
  @RequestMapping(value = "/amcid/{amcId}/amc-user/create_amc_user", method = RequestMethod.POST)
  @ResponseBody
  public String createAmcUser(@RequestBody AmcUser amcUser, @PathVariable Long amcId) throws Exception {
    if(null == amcId || amcId < 0){
      amcId = Long.valueOf(AmcCmpyEnum.CMPY_WENSHENG.getId());
    }
    amcUser.setCompanyId(amcId);
    AmcUser amcUserResult = amcUserService.createAmcUser(amcUser);
    if(amcUserResult == null){
      return "false";
    }

    return "succeed";
  }

  @RequestMapping(value = "/amcid/{amcId}/amc-user/amcUsersByPage", method = RequestMethod.POST)
  @ResponseBody
  public AmcPage<AmcUser> getAmcUsersByPage( @PathVariable Long amcId, @RequestBody QueryParam queryParam){

    Map<String, Direction> orderByParam = PageReqRepHelper.getOrderParam(queryParam.getPageInfo());


    if (CollectionUtils.isEmpty(orderByParam)) {
      orderByParam.put("id", Direction.DESC);
      orderByParam.put("user_name", Direction.DESC);
      orderByParam.put("update_date", Direction.DESC);
    }
    List<AmcUser> queryResults = null;
    Long totalCount = null;
    int offset = PageReqRepHelper.getOffset(queryParam.getPageInfo());
    try{

        queryResults = amcUserService.queryUserPage(offset, queryParam.getPageInfo().getSize(), queryParam,
            orderByParam);
        totalCount = amcUserService.queryUserCount(queryParam);


    }catch (Exception ex){
      log.error("got error when query:"+ex.getMessage());
      throw ex;
    }

//    Page<AmcAssetVo> page = PageReqRepHelper.getPageResp(totalCount, queryResults, assetQueryParam.getPageInfo());
    return PageReqRepHelper.getAmcPage(queryResults, totalCount );

  }

//  @PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasRole('AMC_ADMIN') and hasPermission(#amcId,'crud_amcuser'))")
  @RequestMapping(value = "/amcid/{amcId}/amc-user/amcUsers", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcUser> getAmcUsers( @PathVariable Long amcId){

    List<AmcUser> amcUserResult = amcUserService.getAmcUsers(amcId);
    return amcUserResult;

  }

  @PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasRole('AMC_ADMIN') and hasPermission(#amcId,'crud_amcuser'))")
  @RequestMapping(value = "/amcid/{amcId}/amc-user/amcUsers/searchByPhone", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcUser> searchAmcUserByPhone( @PathVariable Long amcId, @RequestParam("mobilePhone") String mobilePhone)
      throws Exception {
    if(StringUtils.isEmpty(mobilePhone)){
      throw ExceptionUtils.getAmcException(AmcExceptions.MISSING_MUST_PARAM,
          String.format("mobilePhone:%s", mobilePhone));
    }

    List<AmcUser> amcUserResult = amcUserService.searchUserByPhone(mobilePhone);
    return amcUserResult;

  }

  @PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasRole('AMC_ADMIN') and hasPermission(#amcId,'crud_amcuser'))")
  @RequestMapping(value = "/amcid/{amcId}/amc-user/amcUsers/searchByName", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcUser> searchAmcUserByName( @PathVariable Long amcId, @RequestParam("name") String name)
      throws Exception {
    if(StringUtils.isEmpty(name)){
      throw ExceptionUtils.getAmcException(AmcExceptions.MISSING_MUST_PARAM,
          String.format("name:%s", name));
    }

    List<AmcUser> amcUserResult = amcUserService.searchUserByName(name);
    return amcUserResult;

  }

  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  @RequestMapping(value = "/amc-user/allUsers", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcUser> getAllUsers( ){

    List<AmcUser> amcUserResult = amcUserService.getAllUsers();
    return amcUserResult;

  }
  @AmcUserModifyChecker
  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  @RequestMapping(value = "/amc-user/modifyUser", method = RequestMethod.POST)
  @ResponseBody
  public String modifyUser(@RequestParam Long userId, @RequestParam AmcUserValidEnum amcUserValidEnum) throws Exception{
    amcUserService.modifyUserValidState(userId, amcUserValidEnum);
    return "successed";
  }

  @RequestMapping(value = "/amc-user/delUser", method = RequestMethod.POST)
  @ResponseBody
  public String delUser(@RequestParam Long userId){
    amcUserService.delUser(userId);
    return "successed";
  }


  @RequestMapping(value = "/amc-user/userMod", method = RequestMethod.POST)
  @ResponseBody
  public String userMod(@RequestBody AmcUser amcUser) throws Exception{
    amcUserService.userMod(amcUser);
    return "successed";
  }

  @AmcUserModifyChecker
  @PreAuthorize("hasRole('AMC_ADMIN') and hasPermission(#amcId, 'crud_amcuser')")
  @RequestMapping(value = "/amcid/{amcId}/amc-user/modifyUser", method = RequestMethod.POST)
  @ResponseBody
  public String modifyUser(@RequestParam Long userId,
      @PathVariable Long amcId, @RequestParam AmcUserValidEnum amcUserValidEnum) throws Exception {
    amcUserService.modifyUserValidState(userId, amcId, amcUserValidEnum);

    return "successed";
  }


  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  @RequestMapping(value = "/amcid/{amcid}/dept/amc-user/modifyRole", method = RequestMethod.POST)
  @ResponseBody
  public String modifyRole(@RequestBody UserRoleModifyVo userRoleModifyVo ){
    List<Long> roleIds =  userRoleModifyVo.getAmcRoleList().stream().map(item-> item.getId()).collect(Collectors.toList());
    amcUserService.modifyUserRole(userRoleModifyVo.getAmcUser().getId(), roleIds);
    return "successed";
  }





  @PreAuthorize("hasRole('SYSTEM_ADMIN')")
  @RequestMapping(method = RequestMethod.POST, value = "/amcid/{amcId}/amc-user/amcUsers/revokeByName")
  @ResponseBody
  public void revokeTokenByUserName( @RequestParam String userName) {

    Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByClientIdAndUserName(amcAdminClientId,
        userName);
    for(OAuth2AccessToken oAuth2AccessToken : accessTokens){
      tokenServices.revokeToken(oAuth2AccessToken.getValue());
    }
  }

  @RequestMapping(value = "/amcid/amcUsers", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcBasicUser> getAmcBasicUsers(){
    List<AmcUser> amcUsers = amcUserService.getAllUsers();
    List<AmcBasicUser> amcBasicUsers = new ArrayList<>();
    for(AmcUser amcUser: amcUsers){
      AmcBasicUser amcBasicUser = new AmcBasicUser();
      AmcBeanUtils.copyProperties(amcUser, amcBasicUser);
      amcBasicUsers.add(amcBasicUser);
    }
    return amcBasicUsers;
  }

  @RequestMapping(value = "/sso/getUserDetailsById/{userId}", method = RequestMethod.POST)
  @ResponseBody
  public UserDetails getUserDetailsById(@PathVariable Long userId){

    return amcSsoService.getUserDetailByUserId(userId);
  }

  @RequestMapping(value = "/sso/resetUserPwd", method = RequestMethod.POST)
  @ResponseBody
  public void resetUserPwd(@RequestParam("userId") Long userId) throws Exception {
    amcUserService.resetUserPwd(userId);
  }


  @RequestMapping(value = "/sso/passwdReset", method = RequestMethod.POST)
  @ResponseBody
  public void passwdReset(@RequestParam("email") String email) throws Exception {

     amcEmailService.confirmReset(email);
  }

  @RequestMapping(value = "/sso/setNewPassword", method = RequestMethod.POST)
  @ResponseBody
  public void passwdReset(@RequestParam("originPwd") String originPwd, @RequestParam("newPwd") String newPwd,
      @RequestParam("newPwdAgain") String newPwdAgain,
      @RequestParam("mobilePhone") String mobilePhone) throws Exception {
    if(StringUtils.isEmpty(originPwd) || StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(mobilePhone) ||
        StringUtils.isEmpty(newPwdAgain)){
      throw ExceptionUtils.getAmcException(AmcExceptions.MISSING_MUST_PARAM);
    }
    if(!newPwd.equals(newPwdAgain)){
      throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_PARAM, "新密码不一致， 请重新输入");
    }
    amcUserService.changePwd(originPwd, newPwd, mobilePhone);
  }

  @RequestMapping(value = "/sso/initSysAdmin", method = RequestMethod.POST)
  @ResponseBody
  public void initSysAdmin() throws Exception {

    amcUserService.initSysAdmin();
  }
  @Data
  class AmcBasicUser{
    Long id;
    String userName;
    String mobilePhone;
  }




}
