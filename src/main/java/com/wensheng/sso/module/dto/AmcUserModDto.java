package com.wensheng.sso.module.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class AmcUserModDto implements Serializable {
  private static final long serialVersionUID = 1L;
  AmcUserDto amcUserDtoHis;
  AmcUserDto amcUserDtoCurr;
}
