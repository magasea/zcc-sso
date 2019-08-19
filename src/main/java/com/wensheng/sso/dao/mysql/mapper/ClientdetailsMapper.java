package com.wensheng.sso.dao.mysql.mapper;

import com.wensheng.sso.module.dao.mysql.auto.entity.Clientdetails;
import com.wensheng.sso.module.dao.mysql.auto.entity.ClientdetailsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ClientdetailsMapper {
    long countByExample(ClientdetailsExample example);

    int deleteByExample(ClientdetailsExample example);

    int deleteByPrimaryKey(String appid);

    int insert(Clientdetails record);

    int insertSelective(Clientdetails record);

    List<Clientdetails> selectByExampleWithRowbounds(ClientdetailsExample example, RowBounds rowBounds);

    List<Clientdetails> selectByExample(ClientdetailsExample example);

    Clientdetails selectByPrimaryKey(String appid);

    int updateByExampleSelective(@Param("record") Clientdetails record, @Param("example") ClientdetailsExample example);

    int updateByExample(@Param("record") Clientdetails record, @Param("example") ClientdetailsExample example);

    int updateByPrimaryKeySelective(Clientdetails record);

    int updateByPrimaryKey(Clientdetails record);
}