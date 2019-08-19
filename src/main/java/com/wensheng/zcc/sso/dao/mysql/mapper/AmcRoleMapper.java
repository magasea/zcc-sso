package com.wensheng.zcc.sso.dao.mysql.mapper;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcRole;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcRoleMapper {
    long countByExample(AmcRoleExample example);

    int deleteByExample(AmcRoleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcRole record);

    int insertSelective(AmcRole record);

    List<AmcRole> selectByExampleWithRowbounds(AmcRoleExample example, RowBounds rowBounds);

    List<AmcRole> selectByExample(AmcRoleExample example);

    AmcRole selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcRole record, @Param("example") AmcRoleExample example);

    int updateByExample(@Param("record") AmcRole record, @Param("example") AmcRoleExample example);

    int updateByPrimaryKeySelective(AmcRole record);

    int updateByPrimaryKey(AmcRole record);
}