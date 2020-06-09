package com.wensheng.sso.module.dto;

import lombok.Data;

@Data
public class ContactorDTO {
  String contactorName;
  String phoneNum;
  Long ssoUserId;
  boolean found = false;
}
