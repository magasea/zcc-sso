package com.wensheng.sso.service;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dto.ContactorDTO;
import java.util.List;

public interface AmcSsoGrpcService {

  void checkContactorName(List<ContactorDTO> contactorDTOList);
  List<AmcUser> getAmcUsersByIds(List<Long> ids);

}
