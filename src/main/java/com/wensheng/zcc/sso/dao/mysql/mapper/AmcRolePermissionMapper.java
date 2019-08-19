package com.wensheng.zcc.sso.dao.mysql.mapper;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcRolePermission;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcRolePermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcRolePermissionMapper {
    long countByExample(AmcRolePermissionExample example);

    int deleteByExample(AmcRolePermissionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcRolePermission record);

    int insertSelective(AmcRolePermission record);

    List<AmcRolePermission> selectByExampleWithRowbounds(AmcRolePermissionExample example, RowBounds rowBounds);

    List<AmcRolePermission> selectByExample(AmcRolePermissionExample example);

    AmcRolePermission selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcRolePermission record, @Param("example") AmcRolePermissionExample example);

    int updateByExample(@Param("record") AmcRolePermission record, @Param("example") AmcRolePermissionExample example);

    int updateByPrimaryKeySelective(AmcRolePermission record);

    int updateByPrimaryKey(AmcRolePermission record);
}