package com.wensheng.sso.service.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author chenwei on 1/10/19
 * @project zcc-backend
 */
public class PageReqRepHelper {
  public static Map<String, Direction> getOrderParam(PageInfo pageable){
    HashMap<String, Direction> orderByParam = new LinkedHashMap<>();
    if(pageable.getSort() != null && pageable.getSort().length > 0){

      for(String orderBy : pageable.getSort()){

        orderByParam.put( orderBy, pageable.direction());
      }
    }
    return orderByParam;
  }

  public static int getOffset(PageInfo pageable){
    return pageable.getOffset() > 0 ? pageable.getOffset(): pageable.getPage() > 0?
        (pageable.getPage()-1)*pageable.getSize(): 0;
  }



  public static <T> AmcPage <T> getAmcPage(List<T> queryResults, Long total){
    AmcPage<T> amcPage = new AmcPage<>();
    amcPage.setContent(queryResults);
    amcPage.setTotal(total);
    return amcPage;
  }
}
