package com.wensheng.sso.utils;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample.Criteria;
import com.wensheng.sso.service.util.QueryParam;
import java.util.Map;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class SQLUtils {

  public static AmcUserExample getAmcUserExample(QueryParam queryParam) {
    AmcUserExample amcUserExample = new AmcUserExample();
    Criteria criteria = amcUserExample.createCriteria();
    Criteria criteriaOr = amcUserExample.or();
    boolean needControllOr = false;
    if(!StringUtils.isEmpty(queryParam.getName())){
      StringBuilder sb = new StringBuilder().append("%").append(queryParam.getName()).append("%");
      criteria.andUserCnameLike(sb.toString());
      criteriaOr.andNickNameLike(sb.toString());
      needControllOr = true;
    }
    if( queryParam.getDeptId() > 0 ){
      criteria.andDeptIdEqualTo( Long.valueOf(queryParam.getDeptId()));
    }
    if(queryParam.getLocation() > 0){
      criteria.andLocationEqualTo(queryParam.getLocation());
    }
    if(!StringUtils.isEmpty(queryParam.getMobilePhone())){
      criteria.andMobilePhoneEqualTo(queryParam.getMobilePhone());
    }
    if(queryParam.getTitle() > 0){
      criteria.andTitleEqualTo(queryParam.getTitle());
    }
    if(needControllOr){
      if( queryParam.getDeptId() > 0 ){
        criteriaOr.andDeptIdEqualTo( Long.valueOf(queryParam.getDeptId()));
      }
      if(queryParam.getLocation() > 0){
        criteriaOr.andLocationEqualTo(queryParam.getLocation());
      }
      if(!StringUtils.isEmpty(queryParam.getMobilePhone())){
        criteriaOr.andMobilePhoneEqualTo(queryParam.getMobilePhone());
      }
      if(queryParam.getTitle() > 0){
        criteriaOr.andTitleEqualTo(queryParam.getTitle());
      }
    }
    return amcUserExample;
  }
  public static String getOrderBy(Map<String, Direction> orderByParam, RowBounds rowBounds) throws Exception {
    if(CollectionUtils.isEmpty(orderByParam)){
      throw new Exception("empty orderByParam");
    }
    StringBuilder sb = new StringBuilder();
    for(Map.Entry<String, Sort.Direction> item : orderByParam.entrySet()){
      sb.append(item.getKey()).append(" ").append(item.getValue().name()).append(",");
    }
    if(rowBounds != null && rowBounds.getOffset() >= 0 && rowBounds.getLimit() > 0){
      sb.deleteCharAt(sb.length() -1).append(" LIMIT ").append(rowBounds.getOffset()).append(
          " , ").append(rowBounds.getLimit());
    }
    return sb.toString();
  }

}
