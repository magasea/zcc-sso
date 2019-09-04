package com.wensheng.sso.utils;

import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;

public class AmcTitleRoleUtil {

  public static AmcSSORolesEnum getRoleByTitle(AmcSSOTitleEnum amcSSOTitleEnum) throws Exception {
    switch(amcSSOTitleEnum){
      case TITLE_LDR:
        return AmcSSORolesEnum.ROLE_SSO_LDR;
      case TITLE_STAFF:
        return AmcSSORolesEnum.ROLE_SSO_STAFF;
      case TITLE_MGR:
        return AmcSSORolesEnum.ROLE_SSO_MGR;
      case TITLE_SYS_ADM:
        return AmcSSORolesEnum.ROLE_SSO_SYS_ADM;
      case TITLE_PARTNER:
        return AmcSSORolesEnum.ROLE_SSO_PARTNER;
    }
    throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_ENUM, String.format("输入的 amcSSOTitleENum:%s 找不到对应的橘色",
        amcSSOTitleEnum.toString()));
  }

}
