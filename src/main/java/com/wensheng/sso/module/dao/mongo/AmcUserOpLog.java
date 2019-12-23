package com.wensheng.sso.module.dao.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "AMC_USEROP_LOG")
@Data
public class AmcUserOpLog {
  @Id
  String _id;
  @Indexed(direction = IndexDirection.DESCENDING)
  Long userId;
  String callFunc;
  String params;
  Long dateTime;
}
