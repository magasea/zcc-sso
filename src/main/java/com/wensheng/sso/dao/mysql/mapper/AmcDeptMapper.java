package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDept;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDeptExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcDeptMapper {
    long countByExample(AmcDeptExample example);

    int deleteByExample(AmcDeptExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcDept record);

    int insertSelective(AmcDept record);

    List<AmcDept> selectByExampleWithRowbounds(AmcDeptExample example, RowBounds rowBounds);

    List<AmcDept> selectByExample(AmcDeptExample example);

    AmcDept selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcDept record, @Param("example") AmcDeptExample example);

    int updateByExample(@Param("record") AmcDept record, @Param("example") AmcDeptExample example);

    int updateByPrimaryKeySelective(AmcDept record);

    int updateByPrimaryKey(AmcDept record);
}