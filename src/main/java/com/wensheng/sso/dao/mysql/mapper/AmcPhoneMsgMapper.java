package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPhoneMsg;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPhoneMsgExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcPhoneMsgMapper {
    long countByExample(AmcPhoneMsgExample example);

    int deleteByExample(AmcPhoneMsgExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcPhoneMsg record);

    int insertSelective(AmcPhoneMsg record);

    List<AmcPhoneMsg> selectByExampleWithRowbounds(AmcPhoneMsgExample example, RowBounds rowBounds);

    List<AmcPhoneMsg> selectByExample(AmcPhoneMsgExample example);

    AmcPhoneMsg selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcPhoneMsg record, @Param("example") AmcPhoneMsgExample example);

    int updateByExample(@Param("record") AmcPhoneMsg record, @Param("example") AmcPhoneMsgExample example);

    int updateByPrimaryKeySelective(AmcPhoneMsg record);

    int updateByPrimaryKey(AmcPhoneMsg record);
}