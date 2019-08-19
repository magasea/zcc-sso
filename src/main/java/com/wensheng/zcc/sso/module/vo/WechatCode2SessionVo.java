package com.wensheng.zcc.sso.module.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author chenwei on 3/20/19
 * @project miniapp-backend
 */
@Data
public class WechatCode2SessionVo {
  @SerializedName("session_key")
  String sessionKey;
  String openid;
  String unionid;
  @SerializedName("access_token")
  String accessToken;
  @SerializedName("expires_in")
  Long expiresIn;
  @SerializedName("refresh_token")
  String refreshToken;
  String scope;
  String errcode;
  String errmsg;
}
