package com.wensheng.sso.service;

import com.wensheng.sso.module.dao.mysql.auto.entity.AmcCompany;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDept;
import com.wensheng.sso.module.vo.AmcCmpyDeptVo;
import java.util.List;

/**
 * @author chenwei on 3/15/19
 * @project zcc-backend
 */
public interface AmcBasicService {

  AmcCompany createCompany(AmcCompany amcCompany);
  List<AmcCompany> queryCompany();

  AmcDept createDept(AmcDept amcDept);
  List<AmcDept> queryDept(Long amcId);
  List<AmcDept> queryDept();

  AmcCmpyDeptVo createModifyCmpyDept(AmcCmpyDeptVo amcCmpyDeptVo) throws Exception;

  AmcCompany queryCompany(Long companyId);
}
