package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcUserMapper {
    long countByExample(AmcUserExample example);

    int deleteByExample(AmcUserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcUser record);

    int insertSelective(AmcUser record);

    List<AmcUser> selectByExampleWithRowbounds(AmcUserExample example, RowBounds rowBounds);

    List<AmcUser> selectByExample(AmcUserExample example);

    AmcUser selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcUser record, @Param("example") AmcUserExample example);

    int updateByExample(@Param("record") AmcUser record, @Param("example") AmcUserExample example);

    int updateByPrimaryKeySelective(AmcUser record);

    int updateByPrimaryKey(AmcUser record);
}