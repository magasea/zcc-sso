package com.wensheng.sso.module.common.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public
class ReGeoCode {
  @SerializedName("formatted_address")
  String formattedAddress;
  AddressComponent addressComponent;
}
