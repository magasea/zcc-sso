package com.wensheng.sso.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wensheng.sso.dao.mysql.mapper.AmcRolePermissionMapper;
import com.wensheng.sso.dao.mysql.mapper.AmcUserMapper;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcDept;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRolePermission;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcRolePermissionExample;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUser;
import com.wensheng.sso.module.dao.mysql.auto.entity.AmcUserExample;
import com.wensheng.sso.module.helper.AmcCmpyEnum;
import com.wensheng.sso.module.helper.AmcDeptEnum;
import com.wensheng.sso.module.helper.AmcSSORolesEnum;
import com.wensheng.sso.module.helper.AmcSSOTitleEnum;
import com.wensheng.sso.module.helper.AmcUserValidEnum;
import com.wensheng.sso.module.vo.AmcDeptPermsVo;
import com.wensheng.sso.module.vo.DeptPermItem;
import com.wensheng.sso.service.AmcSsoService;
import com.wensheng.sso.service.AmcUserService;
import com.wensheng.sso.service.KafkaService;
import com.wensheng.sso.service.UserService;
import com.wensheng.sso.utils.AmcAppPermCheckUtil;
import io.swagger.models.auth.In;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenwei on 3/19/19
 * @project miniapp-backend
 */
@Service
@Slf4j
public class AmcSsoServiceImpl implements AmcSsoService {

  @Value("${weixin.loginUrl}")
  String loginUrl;

  @Value("${weixin.open.loginUrl}")
  String loginOpenUrl;

  @Value("${weixin.open.getUserInfoUrl}")
  String getUserInfoUrl;

  @Value("${spring.security.oauth2.client.registration.amc-client.client-id}")
  private String amcClientId;

  @Value("${spring.security.oauth2.client.registration.amc-client.secret}")
  private String amcSecret;


  @Value("${spring.security.oauth2.client.registration.amc-client.scopes}")
  private String amcScopes;

  @Value("${spring.security.oauth2.client.registration.amc-client.authorizedGrantTypes}")
  private String amcAuthorizedGrantTypes;

  @Value("${spring.security.oauth2.client.registration.amc-client.redirectUris}")
  private String amcRedirectUris;

  @Autowired
  AmcRolePermissionMapper amcRolePermissionMapper;

  @Value("${weixin.appId}")
  String appId;

  @Value("${weixin.appSecret}")
  String appSecret;

  @Autowired
  KafkaService kafkaService;

  private final int accessTokenValidSeconds = 365 * 24 * 60 * 60;


  private int refreshTokenValidSeconds = 2592000;

  private RestTemplate restTemplate = new RestTemplate();

  private Gson gson = new Gson();


  @Autowired
  AmcUserMapper amcUserMapper;

  @Autowired
  AmcUserService amcUserService;

  @Autowired
  UserService userService;

  @Autowired
  TokenStore tokenStore;

  @Autowired
  DefaultTokenServices tokenServices;

  @Autowired
  TokenEnhancer tokenEnhancer;

  @Autowired
  JwtAccessTokenConverter accessTokenConverter;


  private DataFormatter dataFormatter = new DataFormatter();


  @Value("${env.file-repo}")
  String fileDir;

  public static final int INIT_VECTOR_LENGTH = 16;

  public static String TITLE_CNAME = "姓名";
  public static String TITLE_ENAME = "帐号";
  public static String TITLE_STATUS = "帐号状态";
  public static String TITLE_TITLE = "岗位";
  public static String TITLE_DEPT = "组织";
  public static String TITLE_LOCATION = "地区";
  public static String TITLE_MOBILE = "手机号";
  public static String STATUS_NOMAL = "正常";


  private InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();

  @PostConstruct
  private void init() {
    GsonBuilder gson = new GsonBuilder();
    restTemplate.getMessageConverters().removeIf(item -> item instanceof MappingJackson2HttpMessageConverter);
    restTemplate.getMessageConverters().removeIf(item -> item instanceof MappingJackson2XmlHttpMessageConverter);
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));

    gsonHttpMessageConverter.setGson(gson.create());

    restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
    BaseClientDetails baseClientDetails = new BaseClientDetails();
    baseClientDetails.setClientId(amcClientId);
    baseClientDetails.setAccessTokenValiditySeconds(accessTokenValidSeconds);
    if (!StringUtils.isEmpty(amcAuthorizedGrantTypes)) {
      baseClientDetails
          .setAuthorizedGrantTypes(Arrays.stream(amcAuthorizedGrantTypes.split(",")).collect(Collectors.toList()));
    }
    baseClientDetails.setClientSecret(amcSecret);
    if (!StringUtils.isEmpty(amcScopes)) {
      baseClientDetails.setScope(Arrays.stream(amcScopes.split(",")).collect(Collectors.toList()));
    }
    Map<String, BaseClientDetails> clientParam = new HashMap<>();
    clientParam.put(amcClientId, baseClientDetails);
//    clientDetailsService.setClientDetailsStore(clientParam);
//    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, accessTokenConverter));

//    tokenWechatServices.setClientDetailsService(clientDetailsService);
  }


  private OAuth2AccessToken generateTokenByUserId(Long userId) {
    HashMap<String, String> authorizationParameters = new HashMap<String, String>();
    authorizationParameters.put("scope", amcScopes);
    UserDetails userDetails = userService.loadUserByUserId(userId);

    authorizationParameters.put("client_id", amcClientId);
    authorizationParameters.put("grant", amcAuthorizedGrantTypes);

    List<String> scopes = new ArrayList<>();
    if (!StringUtils.isEmpty(amcScopes)) {
      Arrays.stream(amcScopes.split(",")).forEach(item -> scopes.add(item.trim()));
    }
    Set<String> scopesSet = scopes.stream().collect(Collectors.toSet());

    OAuth2Request authorizationRequest = new OAuth2Request(authorizationParameters, amcClientId,
        userDetails.getAuthorities(), true,
        scopesSet,
        null,
        amcRedirectUris, null, null);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
        null, userDetails.getAuthorities());

    OAuth2Authentication auth2Authentication = new OAuth2Authentication(authorizationRequest, authenticationToken);

    OAuth2AccessToken token = tokenServices.createAccessToken(auth2Authentication);
//    token = accessTokenConverter.enhance(token, auth2Authentication);

    return token;
  }


  public String decodePhone(String encryptedData, String iv, String sessionKey) {
    try {
      byte[] sessionKeyBytes = Base64.decode(sessionKey);
      byte[] encryptedDataBytes = Base64.decode(encryptedData);
      byte[] ivBytes = Base64.decode(iv);

      Security.addProvider(new BouncyCastleProvider());

      SecretKeySpec skeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
      AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
      parameters.init(new IvParameterSpec(ivBytes, 0, INIT_VECTOR_LENGTH));
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(ivBytes, 0, INIT_VECTOR_LENGTH));

      byte[] decrypted = cipher.doFinal(encryptedDataBytes);
      if (null != decrypted && decrypted.length > 0) {
        String result = new String(decrypted, "UTF-8");
        return result;
      }
    } catch (Exception ex) {
      log.error("Failed to decode phoneNumber", ex);
      ex.printStackTrace();
    }

    return null;
  }

  @Override
  public OAuth2AccessToken generateToken(Long userId) {
    return generateTokenByUserId(userId);
  }

  @Override
  public UserDetails getUserDetailByUserId(Long userId) {
    UserDetails userDetails = userService.loadUserByUserId(userId);
    return userDetails;
  }

  @Override
  public boolean handleNameList(MultipartFile multipartFile) throws Exception {
    File targetFile =
        new File(fileDir + File.separatorChar + multipartFile.getOriginalFilename());
    multipartFile.transferTo(targetFile);
// Creating a Workbook from an Excel file (.xls or .xlsx)
    Workbook workbook = WorkbookFactory.create(targetFile);

    // Retrieving the number of sheets in the Workbook
    System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

     /*
           ==================================================================
           Iterating over all the rows and columns in a Sheet (Multiple ways)
           ==================================================================
        */

    // Getting the Sheet at index zero
    Sheet sheet = workbook.getSheetAt(0);
    XSSFFormulaEvaluator xssfFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);

    // Create a DataFormatter to format and get each cell's value as String

    // 1. You can obtain a rowIterator and columnIterator and iterate over them
    System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
    Iterator<Row> rowIterator = sheet.rowIterator();
    List<AmcUser> amcUsers = new ArrayList<>();
    int indexOfName = -1;
    int indexOfCName = -1;
    int indexOfTitle = -1;
    int indexOfDept = -1;
    int indexOfGrp = -1;
    int indexOfTel = -1;
    int indexOfLocation = -1;
    int indexOfMobile = -1;
    int indexOfStatus = -1;
    int indexOfRow = -1;
    while (rowIterator.hasNext()) {

      Row row = rowIterator.next();
      indexOfRow++;
      if (indexOfRow == 0) {
        log.info("now got first row, it is title row");
        // Now let's iterate over the columns of the current row
        Iterator<Cell> cellIterator = row.cellIterator();

        int idxOfCell = -1;
        while (cellIterator.hasNext()) {

          Cell cell = cellIterator.next();
          idxOfCell++;
          String cellValue = dataFormatter.formatCellValue(cell);
          System.out.print(cellValue + "\t");
          switch (cellValue) {
            case "姓名":
              indexOfCName = idxOfCell;
              break;
            case "组织":
              indexOfDept = idxOfCell;
              indexOfGrp = idxOfCell;
              break;
            case "帐号":
              indexOfName = idxOfCell;
              break;
            case "地区":
              indexOfLocation = idxOfCell;
              break;
            case "手机号":
              indexOfMobile = idxOfCell;
              break;
            case "帐号状态":
              indexOfStatus = idxOfCell;
              break;
            case "岗位":
              indexOfTitle = idxOfCell;
              break;
            default:
              System.out.println(String.format("failed to find match case for:%s", cellValue));

          }
        }
      } else {
        AmcUser amcUser = new AmcUser();
        if (indexOfCName < 0 ) {
          log.error("Failed find title row");
          return false;
        }else if(null == row.getCell(indexOfCName)){
          log.info("Empty row got");
          break;
        }
        amcUser.setUserCname(getCellStr(row, indexOfCName, xssfFormulaEvaluator));
        ;
        amcUser.setMobilePhone(getCellStr(row, indexOfMobile, xssfFormulaEvaluator));
        String status = getCellStr(row, indexOfStatus, xssfFormulaEvaluator);
        amcUser.setValid((status != null && status.equals(STATUS_NOMAL)) ? AmcUserValidEnum.VALID.getId() :
            AmcUserValidEnum.INVALID.getId());
        ;
        amcUser.setDeptId(Long.valueOf(AmcAppPermCheckUtil.getAmcDeptEnumFromExcelOfBusinessSys(getCellStr(row,
            indexOfDept, xssfFormulaEvaluator)).getId()));
        amcUser.setLocation(AmcAppPermCheckUtil.getAmcLocationEnumFromExcelOfBusinessSys(getCellStr(row,
            indexOfLocation, xssfFormulaEvaluator)).getId());


        amcUser.setLgroup(AmcAppPermCheckUtil.getAmcGroupFromExcelOfBusinessSys(getCellStr(row, indexOfGrp, xssfFormulaEvaluator)));
        amcUser.setTitle(AmcAppPermCheckUtil.getTitleFromExcelOfBusinessSys(getCellStr(row, indexOfTitle, xssfFormulaEvaluator)).getId());
        if(amcUser.getTitle() == AmcSSOTitleEnum.TITLE_MGR.getId()){
          int lgroupOfTitle = AmcAppPermCheckUtil.getTitleGroupFromExcelOfBusinessSys(getCellStr(row, indexOfTitle,
              xssfFormulaEvaluator));
          if( lgroupOfTitle > 0){
            log.info("Check group property for user:{} ", amcUser.getUserCname());
            amcUser.setLgroup(lgroupOfTitle);
          }
        }
        amcUser.setUserName(getCellStr(row, indexOfName, xssfFormulaEvaluator));
        ;
        amcUser.setMobilePhone(getCellStr(row, indexOfMobile, xssfFormulaEvaluator));
        amcUser.setCompanyId(Long.valueOf(AmcCmpyEnum.CMPY_WENSHENG.getId()));
        amcUser.setValid(AmcUserValidEnum.VALID.getId());
        amcUsers.add(amcUser);
      }


    }
    log.info("Got {} users", amcUsers.size());

//
//    // 2. Or you can use a for-each loop to iterate over the rows and columns
//    System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");
//    for (Row row: sheet) {
//      for(Cell cell: row) {
//        String cellValue = dataFormatter.formatCellValue(cell);
//        System.out.print(cellValue + "\t");
//      }
//      System.out.println();
//    }
//
//    // 3. Or you can use Java 8 forEach loop with lambda
//    System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
//    sheet.forEach(row -> {
//      row.forEach(cell -> {
//        String cellValue = dataFormatter.formatCellValue(cell);
//        System.out.print(cellValue + "\t");
//      });
//      System.out.println();
//    });

    // Closing the workbook

    Sheet sheet1 = workbook.getSheetAt(1);
    // 3. Or you can use Java 8 forEach loop with lambda
    System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
    sheet1.forEach(row -> {
      row.forEach(cell -> {
        String cellValue = dataFormatter.formatCellValue(cell);
        System.out.print(cellValue + "\t");
      });
      System.out.println();
    });
    workbook.close();

    //save users
    saveUsers(amcUsers);

    return true;
  }

  @Override
  public boolean handleJDList(MultipartFile multipartFile) throws Exception {
    File targetFile =
        new File(fileDir + File.separatorChar + multipartFile.getOriginalFilename());
    multipartFile.transferTo(targetFile);
    BufferedReader reader;
    List<JDUser> jdUsers = new ArrayList<>();
    try{
      reader = new BufferedReader(new FileReader(targetFile));
      String line = reader.readLine();
      while(line != null){
        JDUser jdUser = processLine(line);
        jdUsers.add(jdUser);
        line = reader.readLine();
      }
    }catch (IOException e){
      log.error("Failed to handle file:", e);
      return false;
    }
    saveJDUsers(jdUsers);
    return true;
  }

  @Override
  public Map<Integer, List<Long>> getAmcRolePerm() {
    AmcRolePermissionExample amcRolePermissionExample = new AmcRolePermissionExample();
    List<AmcRolePermission> amcRolePermissions = amcRolePermissionMapper.selectByExample(amcRolePermissionExample);
    Map<Integer, List<Long>> result  = new HashMap<>();

    for(AmcDeptEnum amcDeptEnum :AmcDeptEnum.values()){
      if(amcDeptEnum.isUsed()){
        result.put(amcDeptEnum.getId(), new ArrayList<>());
      }
    }

    if(CollectionUtils.isEmpty(amcRolePermissions)){
      log.error("DB table amcRolePermissions is empty");
      return result;
    }
    Map<Integer, Set<Long>> tempResult = new HashMap<>();
    for(AmcRolePermission amcRolePermission: amcRolePermissions){
      if(!tempResult.containsKey(amcRolePermission.getDeptId())){
        tempResult.put(amcRolePermission.getDeptId(), new HashSet<>());
      }
      tempResult.get(amcRolePermission.getDeptId()).add(amcRolePermission.getPermissionId());
    }

    for(Entry<Integer, Set<Long>> item: tempResult.entrySet()){

      if(CollectionUtils.isEmpty(item.getValue())){
        log.error("Failed to find perms for deptId {}", item.getKey());
        continue;
      }
      if(result.containsKey(item.getKey())){
        result.get(item.getKey()).addAll(item.getValue());
      }


    }
   return result;
  }

  @Override
 @Transactional(rollbackFor = Exception.class)
  public boolean updateAmcRolePerm(Map<Integer, List<Long>> amcDeptPerm) {
    AmcRolePermissionExample amcRolePermissionExample = new AmcRolePermissionExample();
    amcRolePermissionMapper.deleteByExample(amcRolePermissionExample);

    if(CollectionUtils.isEmpty(amcDeptPerm)){
      log.error("There is empty deptPermItems:{}", amcDeptPerm);
      return false;
    }
    AmcRolePermission amcRolePermission = new AmcRolePermission();
    for(Entry<Integer, List<Long>> deptPermItem: amcDeptPerm.entrySet()){
      for(int permIdx = 0; permIdx < deptPermItem.getValue().size() ; permIdx ++){
        amcRolePermission.setDeptId(deptPermItem.getKey());
        amcRolePermission.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_MGR.getId()));
        amcRolePermission.setPermissionId(deptPermItem.getValue().get(permIdx));
        amcRolePermissionMapper.insertSelective(amcRolePermission);
        amcRolePermission.setDeptId(deptPermItem.getKey());
        amcRolePermission.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_STAFF.getId()));
        amcRolePermission.setPermissionId(deptPermItem.getValue().get(permIdx));
        amcRolePermissionMapper.insertSelective(amcRolePermission);
        amcRolePermission.setDeptId(deptPermItem.getKey());
        amcRolePermission.setRoleId(Long.valueOf(AmcSSORolesEnum.ROLE_SSO_LDR.getId()));
        amcRolePermission.setPermissionId(deptPermItem.getValue().get(permIdx));
        amcRolePermissionMapper.insertSelective(amcRolePermission);

      }

    }
    return true;
  }

  private void saveJDUsers(List<JDUser> jdUsers) {
    AmcUserExample amcUserExample = new AmcUserExample();
    for (JDUser jdUser : jdUsers) {
      if(jdUser.getMobileNumber() == null ){
        continue;
      }
      amcUserExample = new AmcUserExample();
      amcUserExample.createCriteria().andMobilePhoneEqualTo(jdUser.getMobileNumber());
      List<AmcUser> amcUsers = amcUserMapper.selectByExample(amcUserExample);
      if(!CollectionUtils.isEmpty(amcUsers)){
        amcUsers.get(0).setPassword(jdUser.getPassword());
        amcUserMapper.updateByPrimaryKeySelective(amcUsers.get(0));
      }
    }
  }

  private JDUser processLine(String line) {
    JDUser jdUser = gson.fromJson(line, JDUser.class);
    return jdUser;

  }

  private void saveUsers(List<AmcUser> amcUsers) {
    AmcUserExample amcUserExample = new AmcUserExample();
    Long userId = -1L;
    for (AmcUser amcUser : amcUsers) {
      amcUserExample = new AmcUserExample();
      amcUserExample.createCriteria().andMobilePhoneEqualTo(amcUser.getMobilePhone());
      List<AmcUser> amcUserQuery = amcUserMapper.selectByExample(amcUserExample);
      userId = -1L;
      if (CollectionUtils.isEmpty(amcUserQuery)) {
        amcUserMapper.insertSelective(amcUser);
        userId = amcUser.getId();
      } else {
        amcUserMapper.updateByExampleSelective(amcUser, amcUserExample);
        userId = amcUserQuery.get(0).getId();
      }
      try{
        amcUser.setId(userId);
        amcUserService.updateUserRole(amcUser);
      }catch (Exception ex){
        log.error("Failed to update role for user:{}", amcUser.getId(), ex);
      }
      amcUserExample = null;

    }

  }

  @Data
  private class PhoneObject {

    String phoneNumber;
    String countryCode;

  }
  @Data
  private class JDUser {

    String username;
    String mobileNumber;
    String password;
  }

  private String getCellStr(Row row, int cellIdx, XSSFFormulaEvaluator xssfFormulaEvaluator) {
    Cell cell = row.getCell(cellIdx);

    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        return String.format("%s", cell.getNumericCellValue());
      case FORMULA:
        return dataFormatter.formatCellValue(row.getCell(cellIdx), xssfFormulaEvaluator);
      default:
        log.error("Failed to handle cellTYpe:{}", cell.getCellType());
        return null;
    }
  }
}