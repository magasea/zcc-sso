package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPwdrstToken;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPwdrstTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcPwdrstTokenMapper {
    long countByExample(AmcPwdrstTokenExample example);

    int deleteByExample(AmcPwdrstTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcPwdrstToken record);

    int insertSelective(AmcPwdrstToken record);

    List<AmcPwdrstToken> selectByExampleWithRowbounds(AmcPwdrstTokenExample example, RowBounds rowBounds);

    List<AmcPwdrstToken> selectByExample(AmcPwdrstTokenExample example);

    AmcPwdrstToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcPwdrstToken record, @Param("example") AmcPwdrstTokenExample example);

    int updateByExample(@Param("record") AmcPwdrstToken record, @Param("example") AmcPwdrstTokenExample example);

    int updateByPrimaryKeySelective(AmcPwdrstToken record);

    int updateByPrimaryKey(AmcPwdrstToken record);
}