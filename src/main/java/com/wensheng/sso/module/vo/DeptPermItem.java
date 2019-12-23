package com.wensheng.sso.module.vo;

import java.util.List;
import lombok.Data;

@Data
public class DeptPermItem {
  private Integer deptId;

  private List<Long> permId;
}
