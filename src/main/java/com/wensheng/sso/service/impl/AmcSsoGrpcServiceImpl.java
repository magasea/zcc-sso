package com.wensheng.sso.service.impl;


import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

import com.wensheng.sso.config.LogInterceptor;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dto.ContactorDTO;
import com.wensheng.sso.service.AmcSsoGrpcService;
import com.wensheng.sso.service.AmcUserService;
import com.wensheng.sso.utils.AmcBeanUtils;
import com.wenshengamc.sso.AmcContactorName;
import com.wenshengamc.sso.AmcSSOContactorName;
import com.wenshengamc.sso.AmcSSOGrpcServiceGrpc.AmcSSOGrpcServiceImplBase;
import com.wenshengamc.sso.CheckSSOContactorNameResp;
import com.wenshengamc.sso.SSOUser;
import com.wenshengamc.sso.SSOUserResp;
import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@GRpcService(interceptors = LogInterceptor.class)
@Service
@Slf4j
public class AmcSsoGrpcServiceImpl extends AmcSSOGrpcServiceImplBase implements AmcSsoGrpcService {

  @Autowired
  AmcUserService amcUserService;

  @Override
  public void checkContactorName(List<ContactorDTO> contactorDTOList) {
    amcUserService.checkContactorNames(contactorDTOList);
  }

  @Override
  public List<AmcUser> getAmcUsersByIds(List<Long> ids) {
    return amcUserService.getAmcUsersByIds(ids);
  }


  @Override
  public void checkSSOContactorName(com.wenshengamc.sso.CheckSSOContactorNameReq request,
      io.grpc.stub.StreamObserver<com.wenshengamc.sso.CheckSSOContactorNameResp> responseObserver) {
    List<ContactorDTO> contactorDTOS = new ArrayList<>();
    for(AmcContactorName amcContactorName: request.getContactorNamesList()){
      ContactorDTO contactorDTO = new ContactorDTO();
      contactorDTO.setContactorName(amcContactorName.getContactorName());
      contactorDTO.setPhoneNum(amcContactorName.getPhone());
      contactorDTOS.add(contactorDTO);
    }
    try{
      checkContactorName(contactorDTOS);
      CheckSSOContactorNameResp.Builder cssocnrBuilder = CheckSSOContactorNameResp.newBuilder();
      for(ContactorDTO contactorDTO : contactorDTOS){
        if(!contactorDTO.isFound()){
          continue;
        }
        AmcSSOContactorName.Builder acnBuilder = AmcSSOContactorName.newBuilder();
        acnBuilder.setContactorName(contactorDTO.getContactorName());
        acnBuilder.setPhone(contactorDTO.getPhoneNum());
        acnBuilder.setId(contactorDTO.getSsoUserId());
        cssocnrBuilder.addSsoContactorNames(acnBuilder);
      }
      responseObserver.onNext(cssocnrBuilder.build());
      responseObserver.onCompleted();
    }catch (Exception  ex){
      log.error("Failed to handle check by:{}", request.getContactorNamesOrBuilderList(), ex);
      responseObserver.onError(ex);
    }

  }

  @Override
  public void getSSOUsersByIds(com.wenshengamc.sso.GetSSOUsersByIdsReq request,
      io.grpc.stub.StreamObserver<com.wenshengamc.sso.SSOUserResp> responseObserver) {
    try{

      List<AmcUser> amcUsers = getAmcUsersByIds(request.getSsoUserIdsList());
      SSOUserResp.Builder ssourBuilder = SSOUserResp.newBuilder();

      for(AmcUser amcUser: amcUsers){
        SSOUser.Builder ssouBuilder = SSOUser.newBuilder();
        AmcBeanUtils.copyProperties(amcUser, ssouBuilder);
        ssourBuilder.addSsoUsers(ssouBuilder);
      }
      responseObserver.onNext(ssourBuilder.build());
      responseObserver.onCompleted();
    }catch (Exception ex){
      log.error("Failed to get by ids:{}", request.getSsoUserIdsList(), ex);
      responseObserver.onError(ex);
    }

  }

}
