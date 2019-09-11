package com.wensheng.sso.utils;

import com.wensheng.sso.module.helper.AmcAPPEnum;
import com.wensheng.sso.module.helper.AmcPermEnum;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

public class AmcAppPermCheckUtil {

  public static boolean checkPermApp(Collection<GrantedAuthority> authorities, AmcAPPEnum amcAPPEnum) throws Exception {
    AmcPermEnum amcPermEnum = getPermFromApp(amcAPPEnum);
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
    }
    throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_ENUM, String.format("no AmcPermEnum defined for "
        + "amcAppEnum:%s", amcAPPEnum.toString()));
  }



}
