package com.wensheng.sso.module.common.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public
class GaoDeReGeoResult {
  Integer status;
  String info;
  @SerializedName("infocode")
  String infoCode;
  @SerializedName("regeocode")
  ReGeoCode reGeoCode;

}
