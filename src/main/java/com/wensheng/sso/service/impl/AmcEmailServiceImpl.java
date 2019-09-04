package com.wensheng.sso.service.impl;

import com.wensheng.sso.dao.mysql.mapper.AmcPwdrstTokenMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPwdrstToken;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPwdrstTokenExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.service.AmcEMailService;
import com.wensheng.sso.utils.AmcDateUtils;
import com.wensheng.sso.utils.ExceptionUtils;
import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class AmcEmailServiceImpl implements AmcEMailService {

  @Autowired
  private MessageSource messages;

  @Autowired
  private JavaMailSender mailSender;

  private Long validSeconds = 2*3600L;

  @Autowired
  private AmcUserMapper amcUserMapper;

  @Autowired
  private AmcPwdrstTokenMapper amcPwdrstTokenMapper;

  private String mailUrl = "10.20.100.235:8090/resetPwdByEmail?token=%s";


  @Override
  public void confirmReset(String emailAdr) throws Exception {

    AmcUserExample amcUserExample = new AmcUserExample();
    amcUserExample.createCriteria().andEmailEqualTo( emailAdr);
    List<AmcUser> amcUsers =  amcUserMapper.selectByExample(amcUserExample);
    if(CollectionUtils.isEmpty(amcUsers)){
      throw ExceptionUtils.getAmcException(AmcExceptions.NO_SUCHUSER, String.format(" with email:%s", emailAdr));
    }
    final String token = UUID.randomUUID().toString();
    AmcPwdrstToken amcPwdrstToken = new AmcPwdrstToken();
    amcPwdrstToken.setCreateDate(Date.from(Instant.now()));
    amcPwdrstToken.setToken(token);
    amcPwdrstToken.setUserId(amcUsers.get(0).getId());
    amcPwdrstToken.setExpireDate(AmcDateUtils.getDateSecondsDiff(validSeconds.intValue()));
    AmcPwdrstTokenExample amcPwdrstTokenExample = new AmcPwdrstTokenExample();
    amcPwdrstTokenExample.createCriteria().andUserIdEqualTo(amcUsers.get(0).getId());
    List<AmcPwdrstToken> amcPwdrstTokens =  amcPwdrstTokenMapper.selectByExample(amcPwdrstTokenExample);
    if(CollectionUtils.isEmpty(amcPwdrstTokens)){
      amcPwdrstToken.setId(amcPwdrstTokens.get(0).getId());
      amcPwdrstTokenMapper.updateByPrimaryKeySelective(amcPwdrstToken);
    }else{
      amcPwdrstTokenMapper.insert(amcPwdrstToken);
    }
    final SimpleMailMessage email = constructEmailMessage( amcUsers.get(0), token);
    mailSender.send(email);
  }

  //

  private final SimpleMailMessage constructEmailMessage( final AmcUser user, final String token) {
    final String recipientAddress = user.getEmail();
    final String subject = "Registration Confirmation";
    final String confirmationUrl = String.format(mailUrl, token);
    final String message = "passwd reset, 密码重置";
    final SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message + " \r\n" + confirmationUrl);
    email.setFrom(user.getEmail());
    return email;
  }


}
