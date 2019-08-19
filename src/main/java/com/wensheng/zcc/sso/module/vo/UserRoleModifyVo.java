package com.wensheng.zcc.sso.module.vo;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcUser;
import java.util.List;
import lombok.Data;

/**
 * @author chenwei on 3/14/19
 * @project zcc-backend
 */
@Data
public class UserRoleModifyVo {
  AmcUser amcUser;
  List<AmcRole> amcRoleList;

}
