package com.wensheng.zcc.sso.dao.mysql.mapper;

import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcCompany;
import com.wensheng.zcc.sso.module.dao.mysql.auto.entity.AmcCompanyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcCompanyMapper {
    long countByExample(AmcCompanyExample example);

    int deleteByExample(AmcCompanyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcCompany record);

    int insertSelective(AmcCompany record);

    List<AmcCompany> selectByExampleWithRowbounds(AmcCompanyExample example, RowBounds rowBounds);

    List<AmcCompany> selectByExample(AmcCompanyExample example);

    AmcCompany selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcCompany record, @Param("example") AmcCompanyExample example);

    int updateByExample(@Param("record") AmcCompany record, @Param("example") AmcCompanyExample example);

    int updateByPrimaryKeySelective(AmcCompany record);

    int updateByPrimaryKey(AmcCompany record);
}