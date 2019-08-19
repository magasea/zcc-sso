package com.wensheng.sso.module.common.dto;

import java.util.List;
import lombok.Data;

@Data
public
class AddressComponent {
  String country;
  String province;
  List<String> city;
  String citycode;
  String district;
  String adcode;
  String township;
  String towncode;
  StreetNumber streetNumber;
  List<BusinessArea> businessAreas;
  Building building;
  Neighborhood neighborhood;
}
