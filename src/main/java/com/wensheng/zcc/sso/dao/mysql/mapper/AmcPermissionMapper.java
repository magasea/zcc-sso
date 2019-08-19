package com.wensheng.zcc.sso.dao.mysql.mapper;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcPermission;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcPermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcPermissionMapper {
    long countByExample(AmcPermissionExample example);

    int deleteByExample(AmcPermissionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcPermission record);

    int insertSelective(AmcPermission record);

    List<AmcPermission> selectByExampleWithRowbounds(AmcPermissionExample example, RowBounds rowBounds);

    List<AmcPermission> selectByExample(AmcPermissionExample example);

    AmcPermission selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcPermission record, @Param("example") AmcPermissionExample example);

    int updateByExample(@Param("record") AmcPermission record, @Param("example") AmcPermissionExample example);

    int updateByPrimaryKeySelective(AmcPermission record);

    int updateByPrimaryKey(AmcPermission record);
}