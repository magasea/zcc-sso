package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.OauthAccessToken;
import com.wensheng.sso.module.dao.mysql.auto.entity.OauthAccessTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthAccessTokenMapper {
    long countByExample(OauthAccessTokenExample example);

    int deleteByExample(OauthAccessTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OauthAccessToken record);

    int insertSelective(OauthAccessToken record);

    List<OauthAccessToken> selectByExampleWithBLOBsWithRowbounds(OauthAccessTokenExample example, RowBounds rowBounds);

    List<OauthAccessToken> selectByExampleWithBLOBs(OauthAccessTokenExample example);

    List<OauthAccessToken> selectByExampleWithRowbounds(OauthAccessTokenExample example, RowBounds rowBounds);

    List<OauthAccessToken> selectByExample(OauthAccessTokenExample example);

    OauthAccessToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OauthAccessToken record, @Param("example") OauthAccessTokenExample example);

    int updateByExampleWithBLOBs(@Param("record") OauthAccessToken record, @Param("example") OauthAccessTokenExample example);

    int updateByExample(@Param("record") OauthAccessToken record, @Param("example") OauthAccessTokenExample example);

    int updateByPrimaryKeySelective(OauthAccessToken record);

    int updateByPrimaryKeyWithBLOBs(OauthAccessToken record);

    int updateByPrimaryKey(OauthAccessToken record);
}