package com.wensheng.zcc.sso.module.vo;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcCompany;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcDept;
import java.util.List;
import lombok.Data;

/**
 * @author chenwei on 3/15/19
 * @project zcc-backend
 */
@Data
public class AmcCmpyDeptVo {
  AmcCompany amcCompany;
  List<AmcDept> amcDeptList;

}
