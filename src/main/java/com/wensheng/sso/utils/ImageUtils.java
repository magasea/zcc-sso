package com.wensheng.sso.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author chenwei on 1/4/19
 * @project zcc-backend
 */
public class ImageUtils {

  public static final String UNKNOWNTYPE = "unknownType";

  private static MessageDigest md = null;

  public synchronized static String getImageType(String filePath){

    try{
      File fileLocal = new File(filePath);

      ImageInputStream iis = ImageIO.createImageInputStream(fileLocal);
      // get all currently registered readers that recognize the image format

      Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

      if (!iter.hasNext()) {

        throw new RuntimeException("No readers found!");

      }

      // get the first reader

      ImageReader reader = iter.next();


      System.out.println("Format: " + reader.getFormatName());
      String ext = reader.getFormatName().toLowerCase();
      iis.close();
      return ext;

    }catch (Exception ex){
      ex.printStackTrace();
      return UNKNOWNTYPE;
    }
  }



  public static synchronized MessageDigest getMD() throws NoSuchAlgorithmException {

      if(md == null){
        md = MessageDigest.getInstance("SHA-1");
      }
      md.reset();
      return md;

  }

  public static String getCheckSum(String filePath) throws NoSuchAlgorithmException, IOException {
    MessageDigest digest = getMD();
    return getFileChecksum(digest, new File(filePath));

//    // file hashing with DigestInputStream
//    try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), digest)) {
//      while (dis.read() != -1) ; //empty loop to clear the data
//      digest = dis.getMessageDigest();
//    }
//
//    // bytes to hex
//    StringBuilder result = new StringBuilder();
//    for (byte b : digest.digest()) {
//      result.append(String.format("%02x", b));
//    }
//    return result.toString();

  }

  private static String getFileChecksum(MessageDigest digest, File file) throws IOException
  {
    //Get file input stream for reading the file content
    FileInputStream fis = new FileInputStream(file);

    //Create byte array to read data in chunks
    byte[] byteArray = new byte[1024];
    int bytesCount = 0;

    //Read file data and update in message digest
    while ((bytesCount = fis.read(byteArray)) != -1) {
      digest.update(byteArray, 0, bytesCount);
    };

    //close the stream; We don't need it now.
    fis.close();

    //Get the hash's bytes
    byte[] bytes = digest.digest();

    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for(int i=0; i< bytes.length ;i++)
    {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    //return complete hash
    return sb.toString();
  }

}
