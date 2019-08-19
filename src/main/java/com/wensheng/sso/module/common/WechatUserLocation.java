package com.wensheng.sso.module.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author chenwei on 4/1/19
 * @project miniapp-backend
 */
@Data
public class WechatUserLocation {
  String openId;
  BigDecimal latitude;
  BigDecimal longitude;
  BigDecimal speed;
  BigDecimal accuracy;
  BigDecimal altitude;
  BigDecimal verticalAccuracy;
  BigDecimal horizontalAccuracy;
  LocalDateTime localDateTime;
}
