package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcWechatUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AmcWechatUserMapper {
    long countByExample(AmcWechatUserExample example);

    int deleteByExample(AmcWechatUserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AmcWechatUser record);

    int insertSelective(AmcWechatUser record);

    List<AmcWechatUser> selectByExampleWithRowbounds(AmcWechatUserExample example, RowBounds rowBounds);

    List<AmcWechatUser> selectByExample(AmcWechatUserExample example);

    AmcWechatUser selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AmcWechatUser record, @Param("example") AmcWechatUserExample example);

    int updateByExample(@Param("record") AmcWechatUser record, @Param("example") AmcWechatUserExample example);

    int updateByPrimaryKeySelective(AmcWechatUser record);

    int updateByPrimaryKey(AmcWechatUser record);
}