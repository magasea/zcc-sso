package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleRule;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserRoleRuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcUserRoleRuleMapper {
    long countByExample(AmcUserRoleRuleExample example);

    int deleteByExample(AmcUserRoleRuleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcUserRoleRule record);

    int insertSelective(AmcUserRoleRule record);

    List<AmcUserRoleRule> selectByExampleWithRowbounds(AmcUserRoleRuleExample example, RowBounds rowBounds);

    List<AmcUserRoleRule> selectByExample(AmcUserRoleRuleExample example);

    AmcUserRoleRule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcUserRoleRule record, @Param("example") AmcUserRoleRuleExample example);

    int updateByExample(@Param("record") AmcUserRoleRule record, @Param("example") AmcUserRoleRuleExample example);

    int updateByPrimaryKeySelective(AmcUserRoleRule record);

    int updateByPrimaryKey(AmcUserRoleRule record);
}