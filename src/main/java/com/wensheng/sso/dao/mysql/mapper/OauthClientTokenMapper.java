package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.OauthClientToken;
import com.wensheng.sso.module.dao.mysql.auto.entity.OauthClientTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthClientTokenMapper {
    long countByExample(OauthClientTokenExample example);

    int deleteByExample(OauthClientTokenExample example);

    int insert(OauthClientToken record);

    int insertSelective(OauthClientToken record);

    List<OauthClientToken> selectByExampleWithBLOBsWithRowbounds(OauthClientTokenExample example, RowBounds rowBounds);

    List<OauthClientToken> selectByExampleWithBLOBs(OauthClientTokenExample example);

    List<OauthClientToken> selectByExampleWithRowbounds(OauthClientTokenExample example, RowBounds rowBounds);

    List<OauthClientToken> selectByExample(OauthClientTokenExample example);

    int updateByExampleSelective(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);

    int updateByExampleWithBLOBs(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);

    int updateByExample(@Param("record") OauthClientToken record, @Param("example") OauthClientTokenExample example);
}