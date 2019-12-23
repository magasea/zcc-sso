package com.wensheng.sso.service.impl;


import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserRoleMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserRoleRuleMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleRule;
import com.wensheng.sso.module.helper.AmcDeptEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.service.UserService;
import com.wensheng.sso.utils.AmcAppPermCheckUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

/**
 * @author chenwei on 2/1/19
 * @project zcc-backend
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles(value = "dev")
@Slf4j
public class UserServiceImplTest {
  @Autowired
  AmcUserRoleMapper amcUserRoleMapper;

  @Autowired
  AmcUserMapper amcUserMapper;

  @Autowired
  AmcUserRoleRuleMapper amcUserRoleRuleMapper;

  @Autowired
  UserService userService;

  @Test
  public void getPermissions() {
    AmcUser amcUser = new AmcUser();
    amcUser.setId(1L);
    List<String> authorities = userService.getPermissions(amcUser);
    authorities.stream().forEach(auth -> System.out.println(auth));

  }

  @Test
  public void updateUserRole(){
    List<AmcUser> amcUsers = amcUserMapper.selectByExample(null);
    AmcDeptEnum amcDeptEnum;
    AmcSSOTitleEnum amcSSOTitleEnum;
    for(AmcUser amcUser: amcUsers){
      try{
         amcDeptEnum =  AmcDeptEnum.lookupByDisplayIdUtil(amcUser.getDeptId().intValue());
         amcSSOTitleEnum = AmcSSOTitleEnum.lookupByDisplayIdUtil(amcUser.getTitle());
         if(amcDeptEnum == null || amcSSOTitleEnum == null){
           throw new Exception("Failed to find dept and title for user");
         }
      }catch (Exception ex){
        log.error("User with id:{}, name:{}, phone:{} dept:{} title:{} doesn't have valid dept id and tilte",
            amcUser.getId(), amcUser.getUserName(), amcUser.getMobilePhone(), amcUser.getDeptId(), amcUser.getTitle());
        continue;
      }



      List<AmcSSORolesEnum> amcSSORolesEnums = AmcAppPermCheckUtil.getRoleByUserProperties(amcDeptEnum, amcSSOTitleEnum);
      if(CollectionUtils.isEmpty(amcSSORolesEnums)){
        log.error("User with id:{}, name:{}, phone:{} dept:{} title:{} doesn't have valid dept id and tilte",
            amcUser.getId(), amcUser.getUserName(), amcUser.getMobilePhone(), amcUser.getDeptId(), amcUser.getTitle());
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
    }
  }

  @Test
  public void initUserRoleDeptTitle(){

    amcUserRoleRuleMapper.deleteByExample(null);
    List<AmcUserRoleRule> amcUserRoleRules = new ArrayList<>();
    //    技术部门
    AmcUserRoleRule amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.TECH_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_MGR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_MGR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.TECH_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_STAFF.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_STAFF.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.TECH_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;


    //    业务部门
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.BUSINESS_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.BUSINESS_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.BUSINESS_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;

    //    风控部门
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.RISKCTRL_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.RISKCTRL_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.RISKCTRL_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;


    //    地产部门
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.ESTATE_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.ESTATE_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.ESTATE_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;

    //    股权部门
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.EQUITY_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.EQUITY_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.EQUITY_DEPT.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;

    //    财务部门
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.FINANCE_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.FINANCE_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.FINANCE_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;

    //    人事部门
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.PERSONNEL_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.PERSONNEL_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.PERSONNEL_DEPT.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;

    //    总部
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.ALL_CMPY.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.ALL_CMPY.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;
//    amcUserRoleRule = new AmcUserRoleRule();
//    amcUserRoleRule.setDeptId(AmcDeptEnum.ALL_CMPY.getId());
//    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
//    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
//    amcUserRoleRules.add(amcUserRoleRule);
//    amcUserRoleRule = null;


    //    合作伙伴
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.OUTSIDE.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.OUTSIDE.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;
    amcUserRoleRule = new AmcUserRoleRule();
    amcUserRoleRule.setDeptId(AmcDeptEnum.OUTSIDE.getId());
    amcUserRoleRule.setTitle(AmcSSOTitleEnum.TITLE_LDR.getId());
    amcUserRoleRule.setRoleId(AmcSSORolesEnum.ROLE_SSO_LDR.getId());
    amcUserRoleRules.add(amcUserRoleRule);
    amcUserRoleRule = null;


    for(AmcUserRoleRule amcUserRoleRuleItem: amcUserRoleRules){
      amcUserRoleRuleMapper.insertSelective(amcUserRoleRuleItem);
    }

  }
}