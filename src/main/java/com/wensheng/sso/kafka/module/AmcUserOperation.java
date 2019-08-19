package com.wensheng.sso.kafka.module;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author chenwei on 4/3/19
 * @project miniapp-backend
 */
@Data
public class AmcUserOperation<T> {
  @Indexed
  Long userId;
  String userName;
  Integer actionId;
  @Indexed
  String methodName;
  List<T> param;
  LocalDateTime dateTime;

}
