package com.wensheng.sso.utils;

import com.wensheng.sso.utils.ExceptionUtils.AmcExceptions;
import java.io.File;

public class SystemUtils {
  public static boolean checkAndMakeDir(String path) throws Exception {
    File directory = new File(path);
    try{
      if (! directory.exists()){
        directory.mkdirs();
      }
    }catch (Exception ex){

      throw ExceptionUtils.getAmcException(AmcExceptions.DIRECTORY_OPER_FAILED);
    }
    return true;
  }


  public static  void displayIt(File node){

    System.out.println(node.getAbsoluteFile());

    if(node.isDirectory()){
      String[] subNote = node.list();
      for(String filename : subNote){
        displayIt(new File(node, filename));
      }
    }

  }
}
