package com.wensheng.sso.utils;

/**
 * @author chenwei on 1/11/19
 * @project zcc-backend
 */
public class ExceptionUtils {

  public static Exception getAmcException(AmcExceptions amcExceptions){
    return new Exception(String.format("%d:%s:%s",amcExceptions.code, amcExceptions.name, amcExceptions.reason));
  }

  public static Exception getAmcException(AmcExceptions amcExceptions, String additional){
    return new Exception(String.format("%d:%s:%s-%s",amcExceptions.code, amcExceptions.name, amcExceptions.reason,
        additional));
  }

  public static enum AmcExceptions{

    NO_AMCDEBTPACK_AVAILABLE(1001, "NO_AMCDEBTPACK_AVAILABLE", "no amcdebtpack available"),
    NO_AMCDEBT_AVAILABLE(1002, "NO_AMCDEBT_AVAILABLE", "no amcdebt available"),
    NO_AMCASSET_AVAILABLE(1003, "NO_AMCASSET_AVAILABLE", "no amcasset available"),

    INVALID_ACTION(1004, "INVALID_ACTION", "violate edit rule"),
    INVALID_ORIG_CREDITOR(1005, "INVALID_ORIG_CREDITOR", "没有该原始债权人"),
    MISSING_MUST_PARAM(1006, "Missing must parameters", "缺少必填参数"),

    INVALID_GRANTORTYPE(1007, "invalid grantor type", "不是合法的担保人类型"),
    NO_AMCGRANTOR_AVAILABLE(1008, "no grantor available", "担保人不存在"),
    INVALID_AMCCONTACTID(1009, "invalid amc contact ", "担保人id非法"),
    NO_AMCCONTACT_AVAILABLE(1010, "no amc contact available ", "amc聯係人不存在"),

    NO_CREDITOR(1011, "no creditor", "没有借款人"),
    INSERT_DB_ERROR(1012, "failed to insert db", "数据插入失败"),
    INVALID_LANDAREA_NUMBER(1013, "invalid landarea number", "非法的土地面积"),
    DIRECTORY_OPER_FAILED(1014, "directory failed to oper", "目录操作失败"),
    DUPLICATE_IMAGE_ERROR(1015, "duplicate image upload ", "重复上传图片文件, 已有同样的图片或文件"),
    INVALID_LANDAREA_UNIT(1016, "invalid landarea unit", "非法的土地面积单位"),
    FAILED_UPLOADFILE2OSS(1017, "failed to upload file to oss", "上传文件到OSS失败"),
    FAILED_UPLOADFILE2SERVER(1018, "failed to upload file to server", "上传文件到Server失败"),
    LIMTEXCEED_UPLOADFILENUMBER(1019, "exceed limit when upload files to server at the same time ", "同时上传文件的数量不能超过2"
        + "个，否则服务器要超时， 敬请谅解"),

    NO_SUCHUSER(1020, "No such user", "not fund such user"),
    NO_REQUIRED_PHONEINFO(1021, "No phone info", "没有手机信息"),
    INVALID_PASSWORD(1022, "Invalid Password", "密码不匹配或者为空"),
    INVALID_PHONE_VERCODE(1023, "Invalid vercode", "验证码不匹配或者验证码过期"),
    MISSING_COMPANYNAME(1024, "Missing Company Name", "缺少公司名称"),
    NOT_AUTHORIZED_FORTHISTASK(1025, "not authorized for this task", "用户的角色不允许做这个操作"),
    CANNOT_DEL(1026, "cannot del ", "该资源不能被删除"),
    INVALID_ENUM(1027, "Invalid Enum", "枚举值不正确"),
    INVALID_REGION_NAME(1028, "Invalid Region Name", "非法的地域名称"),
    INVALID_PHONE(1029, "Invalid PhoneNum", "非法的电话号码"),
    INVALID_AMOUNT_RANGE(1030, "Invalid Price Amount Range", "非法的价格区间"),
    INVALID_WECHAT_PARAMETER(1031, "Invalid Wechat Parameter", "非法的微信参数"),

    INVALID_USER_OPERATION(1032, "Invalid user operation", "不合规的用户操作"),

    NO_SUCHAMCAPP(1033, "No such amc app", "目前系统中没有该应用模块"),
    NO_SUCHAMCDEPT(1034, "No such amc dept", "目前系统中没有该部门"),
    NO_SUCHAMCLOCATION(1035, "No such amc location", "目前系统中没有该地区"),
    INVALID_PARAM(1036, "invalid param", "参数不对"),
    DUPLICATE_OBJECT(1037, "Duplicate object", "试图创建重复的对象"),
    ;
    int code;
    String name;
    String reason;
    AmcExceptions(int code, String name, String reason){
        this.code = code;
        this.name = name;
        this.reason = reason;
      }
  }

}
