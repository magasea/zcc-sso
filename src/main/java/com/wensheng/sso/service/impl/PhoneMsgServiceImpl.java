package com.wensheng.sso.service.impl;

import com.wensheng.sso.dao.mysql.mapper.AmcPhoneMsgMapper;
import com.wensheng.sso.service.util.VerifyCodeUtil;
import com.wensheng.sso.utils.AmcBeanUtils;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPhoneMsg;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcPhoneMsgExample;
import com.wensheng.sso.service.PhoneMsgService;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenwei on 2/21/19
 * @project zcc-backend
 */
@Service
@Slf4j
public class PhoneMsgServiceImpl implements PhoneMsgService {

  @Autowired
  AmcPhoneMsgMapper amcPhoneMsgMapper;

  private static final Long timeOut = 1200L;


  @Value("${message.commond}")
  String commond;
  @Value("${message.multiCommond}")
  String multiCommond;
  @Value("${message.spid}")
  String spid;
  @Value("${message.sppassword}")
  String sppassword;
  @Value("${message.url}")
  String url;

  private RestTemplate restTemplate = new RestTemplate();

  @Override
  public String generateVerificationCodeToPhone(String phoneNum) {
    String vcode = VerifyCodeUtil.getAuthCode();
    String da = String.format("86%s",phoneNum);
    String dc = "15";//GBK
    String orgMessage ="验证码为：{0}，{1}分钟内有效。严防诈骗，请勿泄露验证码。";
    String msg = orgMessage.replace("{0}",vcode).replace("{1}",""+timeOut/60);
//        println msg
    String message = new String(Hex.encodeHex(msg.getBytes(Charset.forName("GBK"))));
    StringBuilder smsBuilder = new StringBuilder();
    smsBuilder.append(url)
        .append("?command=").append(commond)
        .append("&spid=").append(spid)
        .append("&sppassword=").append(sppassword)
        .append("&da=").append(da)
        .append("&dc=").append(dc)
        .append("&sm=").append(message);
//        println smsBuilder.toString()
    String res = null;
    ResponseEntity<String> resp = restTemplate.getForEntity(smsBuilder.toString(), String.class);
    if(resp.getBody().contains("errorcode=0")){
      return vcode;
    }else{
      log.error(String.format("call message service failed with:%s", resp.getBody()));
      return resp.getBody();
    }
  }

  @Override
  public boolean verifyPhoneAndVerifyCode(String phoneNum, String vcode) {
    AmcPhoneMsgExample amcPhoneMsgExample = new AmcPhoneMsgExample();
    amcPhoneMsgExample.createCriteria().andPhoneNumEqualTo(phoneNum).andCheckCodeEqualTo(vcode);
    List<AmcPhoneMsg> amcPhoneMsgs = amcPhoneMsgMapper.selectByExample(amcPhoneMsgExample);
    if(CollectionUtils.isEmpty(amcPhoneMsgs)){
      return false;
    }else{
      AmcPhoneMsg histryMsg = amcPhoneMsgs.get(0);
      long currentTimeInSeconds = System.currentTimeMillis()/1000;
      Instant now = Instant.now();
      long executime = now.getEpochSecond() - histryMsg.getCreateDate().getTime()/1000;
      if(executime - timeOut > 10){
        log.error(String.format("the message is outof time, create-time:%s current time:%s",
            histryMsg.getCreateDate().getTime(), currentTimeInSeconds));
        return false;
      }else{
        return true;
      }
    }

  }

  @Override
  public void saveVerificationCode(String phoneNum, String vcode) {
    AmcPhoneMsg amcPhoneMsg = new AmcPhoneMsg();
    amcPhoneMsg.setCheckCode(vcode);
    amcPhoneMsg.setPhoneNum(phoneNum);
    AmcPhoneMsgExample amcPhoneMsgExample = new AmcPhoneMsgExample();
    amcPhoneMsgExample.createCriteria().andPhoneNumEqualTo(phoneNum);
    List<AmcPhoneMsg> amcPhoneMsgList = amcPhoneMsgMapper.selectByExample(amcPhoneMsgExample);
    if(CollectionUtils.isEmpty(amcPhoneMsgList)){
      amcPhoneMsgMapper.insertSelective(amcPhoneMsg);
    }else{
      amcPhoneMsg.setCreateDate(Date.from(Instant.now()));
      AmcBeanUtils.copyProperties(amcPhoneMsg, amcPhoneMsgList.get(0));
      amcPhoneMsgMapper.updateByPrimaryKeySelective(amcPhoneMsgList.get(0));
    }

  }


}
