package com.wensheng.sso.dao.mysql.mapper.ext;

import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.ext.AmcUserExt;

/**
 * @author chenwei on 2/1/19
 * @project zcc-backend
 */
public interface AmcUserExtMapper extends AmcUserMapper {
  AmcUserExt  selectByExtExample(Long id);

}
