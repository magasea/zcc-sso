package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.OauthRefreshToken;
import com.wensheng.sso.module.dao.mysql.auto.entity.OauthRefreshTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface OauthRefreshTokenMapper {
    long countByExample(OauthRefreshTokenExample example);

    int deleteByExample(OauthRefreshTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OauthRefreshToken record);

    int insertSelective(OauthRefreshToken record);

    List<OauthRefreshToken> selectByExampleWithBLOBsWithRowbounds(OauthRefreshTokenExample example, RowBounds rowBounds);

    List<OauthRefreshToken> selectByExampleWithBLOBs(OauthRefreshTokenExample example);

    List<OauthRefreshToken> selectByExampleWithRowbounds(OauthRefreshTokenExample example, RowBounds rowBounds);

    List<OauthRefreshToken> selectByExample(OauthRefreshTokenExample example);

    OauthRefreshToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OauthRefreshToken record, @Param("example") OauthRefreshTokenExample example);

    int updateByExampleWithBLOBs(@Param("record") OauthRefreshToken record, @Param("example") OauthRefreshTokenExample example);

    int updateByExample(@Param("record") OauthRefreshToken record, @Param("example") OauthRefreshTokenExample example);

    int updateByPrimaryKeySelective(OauthRefreshToken record);

    int updateByPrimaryKeyWithBLOBs(OauthRefreshToken record);

    int updateByPrimaryKey(OauthRefreshToken record);
}