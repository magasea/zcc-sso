package com.wensheng.sso.utils;

import com.wensheng.sso.module.helper.AmcAPPEnum;
import com.wensheng.sso.module.helper.AmcDeptEnum;
import com.wensheng.sso.module.helper.AmcLocationEnum;
import com.wensheng.sso.module.helper.AmcPermEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AmcAppPermCheckUtil {

  public static boolean checkPermApp(Collection<GrantedAuthority> authorities, AmcAPPEnum amcAPPEnum) throws Exception {
    AmcPermEnum amcPermEnum = getPermFromApp(amcAPPEnum);
    boolean result = authorities.contains(new SimpleGrantedAuthority(amcPermEnum.getName()));
    for(GrantedAuthority grantedAuthority: authorities){
      if(grantedAuthority.getAuthority().equals(amcPermEnum.getName())){
        return true;
      }
    }
    return false;

  }

  public static AmcPermEnum getPermFromApp(AmcAPPEnum amcAPPEnum) throws Exception {
    switch (amcAPPEnum){
      case AMCAPP_JD:
        return AmcPermEnum.PERM_JD;
      case AMCAPP_ZCC:
        return AmcPermEnum.PERM_ZCC;
      case AMCAPP_MARKETDATA:
        return AmcPermEnum.PERM_MARKETDATA;
      case AMCAPP_ANALYSIS:
        return AmcPermEnum.PERM_ANALYSIS;
      case AMCAPP_SSO:
        return AmcPermEnum.PERM_SSO;

    }
    throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_ENUM, String.format("no AmcPermEnum defined for "
        + "amcAppEnum:%s", amcAPPEnum.toString()));
  }


  public static AmcDeptEnum getAmcDeptEnumFromExcelOfBusinessSys(String businessDeptName) throws Exception {
    if(businessDeptName.equals("上海文盛资产管理股份有限公司")  ){
      return AmcDeptEnum.HEADQUARTER_DEPT;
    }
    if(businessDeptName.contains("地区")){
      return AmcDeptEnum.BUSINESS_DEPT;
    }

    if(businessDeptName.contains("地产")){
      return AmcDeptEnum.ESTATE_DEPT;
    }
    if(businessDeptName.contains("投资")){
      return AmcDeptEnum.EQUITY_DEPT;
    }
    if(businessDeptName.contains("风控") || businessDeptName.contains("风险控制")){
      return AmcDeptEnum.RISKCTRL_DEPT;
    }
    throw ExceptionUtils.getAmcException(AmcExceptions.NO_SUCHAMCDEPT, businessDeptName);
  }

  public static int getAmcGroupFromExcelOfBusinessSys(String businessOrg) throws Exception {

    if(businessOrg.contains("一")){
      return 1;
    }
    if(businessOrg.contains("二")){
      return 2;
    }
    if(businessOrg.contains("三")){
      return 3;
    }
    if(businessOrg.contains("四")){
      return 4;
    }
    if(businessOrg.contains("五")){
      return 5;
    }
    if(businessOrg.contains("六")){
      return 6;
    }
    if(businessOrg.contains("七")){
      return 7;
    }
    System.out.println(String.format("input org is not parseable to group:%s", businessOrg));
    return -1;
  }

  public static AmcLocationEnum getAmcLocationEnumFromExcelOfBusinessSys(String businessDeptName) throws Exception {


      if(businessDeptName.contains("上海")){
        return AmcLocationEnum.SHANGHAI_LOCATION;
      }
      if(businessDeptName.contains("北京")){
        return AmcLocationEnum.BEIJING_LOCATION;
      }
      if(businessDeptName.contains("广东")){
        return AmcLocationEnum.GUANGDONG_LOCATION;
      }
      if(businessDeptName.contains("浙江")){
        return AmcLocationEnum.ZHEJIANG_LOCATION;
      }
      if(businessDeptName.contains("江苏")){
        return AmcLocationEnum.JIANGSU_LOCATION;
      }


    throw ExceptionUtils.getAmcException(AmcExceptions.NO_SUCHAMCDEPT, businessDeptName);
  }
  public static List<AmcSSORolesEnum> getRoleByUserProperties(AmcDeptEnum amcDeptEnum, AmcSSOTitleEnum amcSSOTitleEnum){
    List<AmcSSORolesEnum>  amcSSORolesEnums = new ArrayList<>();
    switch (amcDeptEnum){
      case TECH_DEPT:

        switch (amcSSOTitleEnum){
          case TITLE_MGR:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_MGR);
            break;
          case TITLE_LDR:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_LDR);
            break;
          case TITLE_STAFF:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_STAFF);
            break;
          default:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_SYS_ADM);
            return amcSSORolesEnums;

        }
        break;
      case EQUITY_DEPT:
      case BUSINESS_DEPT:
      case HEADQUARTER_DEPT:
      case RISKCTRL_DEPT:
      case ESTATE_DEPT:
        switch (amcSSOTitleEnum){
          case TITLE_MGR:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_MGR);
            break;
          case TITLE_LDR:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_LDR);
            break;
          case TITLE_STAFF:
            amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_STAFF);
            break;
          default:

            return amcSSORolesEnums;

        }
      case OUTSIDE:
        amcSSORolesEnums.add(AmcSSORolesEnum.ROLE_SSO_PARTNER);
        return  amcSSORolesEnums;

    }
    return amcSSORolesEnums;
  }


  public static int getTitleGroupFromExcelOfBusinessSys(String stringCellValue) {
    if(!stringCellValue.contains("兼") ){
      return -1;
    }
    if(stringCellValue.contains("一")){
      return 1;
    }
    if(stringCellValue.contains("二")){
      return 2;
    }
    if(stringCellValue.contains("三")){
      return 3;
    }
    if(stringCellValue.contains("四")){
      return 4;
    }
    if(stringCellValue.contains("五")){
      return 5;
    }
    if(stringCellValue.contains("六")){
      return 6;
    }
    if(stringCellValue.contains("七")){
      return 7;
    }
    System.out.println(String.format("input org is not parseable to group:%s", stringCellValue));
    return -1;
  }

  public static AmcSSOTitleEnum getTitleFromExcelOfBusinessSys(String stringCellValue) {
    if(stringCellValue.contains("组员") || stringCellValue.contains("专员")){
      return AmcSSOTitleEnum.TITLE_STAFF;
    }
    if(stringCellValue.contains("组长")){
      if(stringCellValue.contains("兼")){
        return AmcSSOTitleEnum.TITLE_MGR;
      }
      return AmcSSOTitleEnum.TITLE_LDR;
    }
    if(stringCellValue.contains("经理") || stringCellValue.contains("助理") || stringCellValue.contains("其他")){
      return AmcSSOTitleEnum.TITLE_MGR;
    }
    //系统管理员
    if(stringCellValue.contains("系统管理员")){
      return AmcSSOTitleEnum.TITLE_SYS_ADM;
    }
    return AmcSSOTitleEnum.TITLE_STAFF;
  }
}
