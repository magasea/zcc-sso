package com.wensheng.zcc.sso.dao.mysql.mapper;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcUserRole;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcUserRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcUserRoleMapper {
    long countByExample(AmcUserRoleExample example);

    int deleteByExample(AmcUserRoleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcUserRole record);

    int insertSelective(AmcUserRole record);

    List<AmcUserRole> selectByExampleWithRowbounds(AmcUserRoleExample example, RowBounds rowBounds);

    List<AmcUserRole> selectByExample(AmcUserRoleExample example);

    AmcUserRole selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcUserRole record, @Param("example") AmcUserRoleExample example);

    int updateByExample(@Param("record") AmcUserRole record, @Param("example") AmcUserRoleExample example);

    int updateByPrimaryKeySelective(AmcUserRole record);

    int updateByPrimaryKey(AmcUserRole record);
}