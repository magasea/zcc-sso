package com.wensheng.sso.controller;

import com.wensheng.sso.module.helper.AmcAPPEnum;
import com.wensheng.sso.module.helper.AmcCmpyEnum;
import com.wensheng.sso.module.helper.AmcDeptEnum;
import com.wensheng.sso.module.helper.AmcLocationEnum;
import com.wensheng.sso.module.helper.AmcPermEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.module.vo.AmcDeptPermsVo;
import com.wensheng.sso.module.vo.AmcRolePermissionVo;
import com.wensheng.sso.params.AmcBranchLocationEnum;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRolePermission;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.service.AmcSsoService;
import com.wensheng.sso.service.AmcUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chenwei on 3/15/19
 * @project zcc-backend
 */
@Controller
@RequestMapping("/amc/sso/basicInfo")
public class AmcBasicInfoController {

  @Autowired
  AmcUserService amcUserService;


  @Autowired
  AmcSsoService amcSsoService;


  @RequestMapping(value = "/role-perms", method = RequestMethod.POST)
  @ResponseBody
  public List<AmcRolePermissionVo> getAmcRolePerms(){
    List<AmcRolePermission> amcRolePermissions = amcUserService.getAmcRolePerms();
    List<AmcRolePermissionVo> amcRolePermissionVos = convert(amcRolePermissions);

    return amcRolePermissionVos;
  }

  private List<AmcRolePermissionVo> convert(List<AmcRolePermission> amcRolePermissions) {
    List<AmcRolePermissionVo> amcRolePermissionVos = new ArrayList<>();
    for(AmcRolePermission amcRolePermission: amcRolePermissions){
      AmcRolePermissionVo amcRolePermissionVo = new AmcRolePermissionVo();
      amcRolePermissionVo.setDeptName(AmcDeptEnum.lookupByDisplayIdUtil(amcRolePermission.getDeptId()).getName());
      amcRolePermissionVo.setPermissionName(
          AmcPermEnum.lookupByDisplayIdUtil(amcRolePermission.getPermissionId().intValue()).getName());
      amcRolePermissionVo.setRoleName(AmcSSORolesEnum.lookupByDisplayIdUtil(amcRolePermission.getRoleId().intValue()).getName());
      amcRolePermissionVos.add(amcRolePermissionVo);
    }
    return amcRolePermissionVos;
  }

//  @RequestMapping(value = "/perms", method = RequestMethod.POST)
//  @ResponseBody
//  public List<AmcPermission> getAmcPerms(){
//    List<AmcPermission> amcPermissions = amcUserService.getAmcPerms();
//    return amcPermissions;
//  }

  @RequestMapping(value = "/userStates", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getUserStates(){

    List<String> result = new ArrayList<>();
    for(AmcUserValidEnum amcUserValidEnum : AmcUserValidEnum.values()){
      result.add(String.format("%d:%s", amcUserValidEnum.getId(), amcUserValidEnum.getName()));
    }
    return result;
  }
  @RequestMapping(value = "/location", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getLocations(){

    List<String> result = new ArrayList<>();
    for(AmcLocationEnum amcLocationEnum : AmcLocationEnum.values()){
      result.add(String.format("%d:%s", amcLocationEnum.getId(), amcLocationEnum.getName()));
    }
    return result;
  }

  @RequestMapping(value = "/amcDepts", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcDepts(){

    List<String> result = new ArrayList<>();
    for(AmcDeptEnum amcDeptEnum : AmcDeptEnum.values()){
      result.add(String.format("%d:%s:%s", amcDeptEnum.getId(), amcDeptEnum.getName(),
          amcDeptEnum.getCname()));
    }
    return result;
  }

  @RequestMapping(value = "/amcTitles", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcTitles(){

    List<String> result = new ArrayList<>();
    for(AmcSSOTitleEnum amcSSOTitleEnum : AmcSSOTitleEnum.values()){
      result.add(String.format("%d:%s:%s", amcSSOTitleEnum.getId(), amcSSOTitleEnum.getName(),
          amcSSOTitleEnum.getCname()));
    }
    return result;
  }

  @RequestMapping(value = "/amcRoles", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcRoles(){

    List<String> result = new ArrayList<>();
    for(AmcSSORolesEnum amcSSORolesEnum : AmcSSORolesEnum.values()){
      result.add(String.format("%d:%s", amcSSORolesEnum.getId(), amcSSORolesEnum.getName()
          ));
    }
    return result;
  }

  @RequestMapping(value = "/amcPerms", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcPerms(){

    List<String> result = new ArrayList<>();
    for(AmcPermEnum amcPermEnum : AmcPermEnum.values()){
      result.add(String.format("%d:%s", amcPermEnum.getId(), amcPermEnum.getName()
          ));
    }
    return result;
  }
  @RequestMapping(value = "/amcApps", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcApps(){

    List<String> result = new ArrayList<>();
    for(AmcAPPEnum amcAPPEnum : AmcAPPEnum.values()){
      result.add(String.format("%d:%s:%s", amcAPPEnum.getId(), amcAPPEnum.getName(), amcAPPEnum.getCname()
      ));
    }
    return result;
  }


  @RequestMapping(value = "/amcCompanies", method = RequestMethod.POST)
  @ResponseBody
  public List<String> getAmcCompanies(){

    List<String> result = new ArrayList<>();
    for(AmcCmpyEnum amcCmpyEnum : AmcCmpyEnum.values()){
      result.add(String.format("%d:%s:%s", amcCmpyEnum.getId(), amcCmpyEnum.getName(), amcCmpyEnum.getCname()
      ));
    }
    return result;
  }

  @RequestMapping(value = "/getRolePerms", method = RequestMethod.POST)
  @ResponseBody
  public Map<Integer, List<Long>> getRolePerms(){
    return amcSsoService.getAmcRolePerm();
  }

  @RequestMapping(value = "/modRolePerms", method = RequestMethod.POST)
  @ResponseBody
  public boolean modRolePerms(@RequestBody  Map<Integer, List<Long>> deptPerms){
    return amcSsoService.updateAmcRolePerm(deptPerms);
  }
}
