package com.wensheng.sso.aop;

import com.google.gson.Gson;
import com.wensheng.sso.module.dao.mongo.AmcUserOpLog;
import com.wensheng.sso.utils.ExceptionUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.vo.AmcUserDetail;
import com.wensheng.sso.service.AmcUserService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.CollectionUtils;

/**
 * @author chenwei on 1/15/19
 * @project zcc-backend
 */
@Aspect
@EnableAspectJAutoProxy
@Configuration
@Slf4j
public class AmcAspect {

  @Autowired
  AmcUserService amcUserService;

  @Autowired
  MongoTemplate mongoTemplate;

  Gson gson = new Gson();


  @Around("@annotation(AmcUserCreateChecker)")
  public Object aroundUserCreate(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("now get the point cut");
    AmcUser amcUser = (AmcUser) joinPoint.getArgs()[0];

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication == null || ! (authentication.getDetails() instanceof OAuth2AuthenticationDetails)){
      log.error("it is web user");
      throw new InsufficientAuthenticationException("access token required");
    }
    log.info(authentication.getDetails().toString());
    AmcUserDetail amcUserDetail = (AmcUserDetail) authentication.getPrincipal();

    if(amcUserDetail.getId() != null && 0 < amcUserDetail.getId() && !authentication.getAuthorities().contains(
        new SimpleGrantedAuthority(AmcSSORolesEnum.ROLE_SSO_LDR.getName()))){
//      AmcUser currUser = amcUserService.getUserById(amcUserDetail.getId());
      amcUser.setLocation(amcUserDetail.getLocation());
      amcUser.setDeptId(amcUserDetail.getDeptId());
    }

    return joinPoint.proceed(new Object[]{amcUser,joinPoint.getArgs()[1]});
  }

  @Around("@annotation(AmcUserOpLogger)")
  public Object aroundUserOp(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("now get the point cut of aroundUserOp");
    try{
      AmcUserOpLog amcUserOpLog = new AmcUserOpLog();
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if(authentication == null || ! (authentication.getDetails() instanceof OAuth2AuthenticationDetails)){
        log.error("it is not a login user");
      }else{
        AmcUserDetail amcUserDetail = (AmcUserDetail) authentication.getPrincipal();
        amcUserOpLog.setUserId(amcUserDetail.getId());

      }
      amcUserOpLog.setCallFunc(joinPoint.getSignature().getName());
      amcUserOpLog.setParams(gson.toJson(joinPoint.getArgs()));
      amcUserOpLog.setDateTime(Instant.now().getEpochSecond());
      mongoTemplate.save(amcUserOpLog);
    }catch (Exception ex){
      log.error("Failed to get info aroundUserOp", ex);
    }
    return joinPoint.proceed(joinPoint.getArgs());
  }

  @Around("@annotation(AmcUserQueryChecker)")
  public Object aroundUserQuery(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("now get the point cut");
    AmcUserExample amcUserExample = (AmcUserExample) joinPoint.getArgs()[0];

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    AmcUserDetail amcUserDetail = (AmcUserDetail) authentication.getPrincipal();

    if(amcUserDetail.getId() != null && 0 < amcUserDetail.getId() && authentication.getAuthorities().contains(
        AmcSSORolesEnum.ROLE_SSO_LDR.getName())){
      log.info("It is amc_admin query, so only local users can be selected");
      amcUserExample.createCriteria().andDeptIdEqualTo(amcUserDetail.getDeptId()).andLocationEqualTo(amcUserDetail.getLocation());

    }

    return joinPoint.proceed(new Object[]{amcUserExample});
  }

  @Around("@annotation(AmcUserModifyChecker)")
  public Object aroundUserModify(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("now get the point cut");
    CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
    Long targetUserId = null;
    for(int idx = 0; idx < codeSignature.getParameterNames().length; idx ++){
      if( "userId".equals(codeSignature.getParameterNames()[idx])){
        targetUserId = (Long) joinPoint.getArgs()[idx];
        break;
      }
    }
    List<AmcUserRole> amcUserRoleList =  amcUserService.getAmcUserRoles(targetUserId);
    if(CollectionUtils.isEmpty(amcUserRoleList)){
      log.error("Target user:{} doesn't have role", targetUserId);
      callReturn(joinPoint);
    }
    Set<Long> roleIds = amcUserRoleList.stream().map(item -> item.getRoleId()).collect(Collectors.toSet());

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    Set<String> roleAndPerms =
//        authentication.getAuthorities().stream().map(item -> ((GrantedAuthority) item).getAuthority()).collect(
//            Collectors.toSet());
    AmcUserDetail amcUserDetail = (AmcUserDetail) authentication.getPrincipal();
    if(roleIds.contains(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_SYS_ADM.getId()))){
      throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_USER_OPERATION,"修改系统管理员的用户属性请联系开发技术部");
    }
    if(amcUserDetail.getId() != null && 0 < amcUserDetail.getId() ){
      List<AmcUserRole> currUserRoleList = amcUserService.getAmcUserRoles(amcUserDetail.getId());

      if(CollectionUtils.isEmpty(currUserRoleList)){
        log.error("Current user:{} doesn't have role", amcUserDetail.getId());
      }
      boolean isSysAdmin = false;
      for(AmcUserRole amcUserRole: currUserRoleList){
        if(amcUserRole.getRoleId() == AmcSSORolesEnum.ROLE_SSO_SYS_ADM.getId()){
          isSysAdmin = true;
        }
        if(roleIds.contains(amcUserRole.getRoleId())){
          throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_USER_OPERATION,"不能修改同一个级别的用户属性");
        }
      }
      AmcUser amcUser = amcUserService.getUserById(targetUserId);
      if(amcUserDetail.getLocation() != null && !amcUserDetail.getLocation().equals(amcUser.getLocation()) && !isSysAdmin ){
        throw ExceptionUtils.getAmcException(AmcExceptions.INVALID_USER_OPERATION,String.format("非系统管理员,"
            + "不能修改另一个区域的用户属性, 当前用户地域:%d 要修改的用户地域:%d", amcUserDetail.getLocation(), amcUser.getLocation()));
      }

      log.info("It is amc_admin query, so only local users can be selected");

    }

    return joinPoint.proceed();
  }

  private Object callReturn(ProceedingJoinPoint joinPoint) throws Throwable {
    return joinPoint.proceed();
  }

}
