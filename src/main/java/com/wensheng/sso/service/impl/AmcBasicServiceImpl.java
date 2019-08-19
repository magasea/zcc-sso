package com.wensheng.sso.service.impl;

import com.wensheng.sso.dao.mysql.mapper.AmcCompanyMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcDeptMapper;
import com.wensheng.sso.utils.ExceptionUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcCompany;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcCompanyExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDept;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDeptExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDeptExample.Criteria;
import com.wensheng.sso.module.vo.AmcCmpyDeptVo;
import com.wensheng.sso.service.AmcBasicService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author chenwei on 3/15/19
 * @project zcc-backend
 */
@Service
@Slf4j
public class AmcBasicServiceImpl implements AmcBasicService {
  @Autowired
  AmcCompanyMapper amcCompanyMapper;

  @Autowired
  AmcDeptMapper amcDeptMapper;

  @Override
  public AmcCompany createCompany(AmcCompany amcCompany) {
    int count = amcCompanyMapper.insertSelective(amcCompany);
    return amcCompany;
  }

  @Override
  public List<AmcCompany> queryCompany() {
    List<AmcCompany> amcCompanies = amcCompanyMapper.selectByExample(null);
    return amcCompanies;
  }

  @Override
  public AmcDept createDept(AmcDept amcDept) {
    int count = amcDeptMapper.insertSelective(amcDept);
    return amcDept;
  }

  @Override
  public List<AmcDept> queryDept(Long amcId) {
    AmcDeptExample amcDeptExample = new AmcDeptExample();
    amcDeptExample.createCriteria().andCmpyIdEqualTo(amcId);
    List<AmcDept> amcDepts = amcDeptMapper.selectByExample(amcDeptExample);
    return amcDepts;
  }

  @Override
  public List<AmcDept> queryDept() {
    List<AmcDept> amcDepts = amcDeptMapper.selectByExample(null);
    return amcDepts;
  }

  @Override
  public AmcCmpyDeptVo createModifyCmpyDept(AmcCmpyDeptVo amcCmpyDeptVo) throws Exception {
    AmcCompanyExample amcCompanyExample = new AmcCompanyExample();
    if(StringUtils.isEmpty(amcCmpyDeptVo.getAmcCompany().getName())){
      log.error("company name cannot be empty");
      throw ExceptionUtils.getAmcException(AmcExceptions.MISSING_COMPANYNAME);
    }
    amcCompanyExample.createCriteria().andNameEqualTo(amcCmpyDeptVo.getAmcCompany().getName());
    List<AmcCompany> amcCompanys = amcCompanyMapper.selectByExample(amcCompanyExample);
    if(CollectionUtils.isEmpty(amcCompanys)){
      amcCmpyDeptVo.getAmcCompany().setId(null);
      amcCompanyMapper.insertSelective(amcCmpyDeptVo.getAmcCompany());
    }
    if(CollectionUtils.isEmpty(amcCmpyDeptVo.getAmcDeptList())){
      return amcCmpyDeptVo;
    }
    AmcDeptExample amcDeptExample = new AmcDeptExample();
    List<AmcDept> amcDeptsResult = new ArrayList<>();

    for(AmcDept amcDept : amcCmpyDeptVo.getAmcDeptList()){
      Criteria criteria = amcDeptExample.createCriteria();
      criteria.andNameEqualTo(amcDept.getName()).andCmpyIdEqualTo(amcCmpyDeptVo.getAmcCompany().getId());
      List<AmcDept> amcDepts = amcDeptMapper.selectByExample(amcDeptExample);
      if(CollectionUtils.isEmpty(amcDepts)){
        amcDeptMapper.insertSelective(amcDept);
        amcDeptsResult.add(amcDept);
      }else{
        log.error(String.format(" the dept with name:%s already exists",amcDept.getName()));
        amcDeptsResult.add(amcDepts.get(0));
      }
    }
    amcCmpyDeptVo.setAmcDeptList(amcDeptsResult);

    return amcCmpyDeptVo;
  }

  @Override
  public AmcCompany queryCompany(Long companyId) {
    return amcCompanyMapper.selectByPrimaryKey(companyId);
  }
}
