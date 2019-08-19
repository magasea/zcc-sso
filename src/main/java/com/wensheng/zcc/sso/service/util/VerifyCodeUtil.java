package com.wensheng.zcc.sso.service.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * @author chenwei on 2/21/19
 * @project zcc-backend
 */
public class VerifyCodeUtil {
  public static final int AUTHCODE_LENGTH = 6; // length of verification code
  public static final int SINGLECODE_WIDTH = 10; // width of one digit or character in the image
  public static final int SINGLECODE_HEIGHT = 25; // height of one digit or character in the image
  public static final int SINGLECODE_GAP = 5; // margin of a digit or character
  public static final int PADDING_TOP_BOT = 10;// padding of top and bottom
  public static final int PADDING_LEFT_RIGHT = 10; //padding of left and right
  public static final int IMG_WIDTH = AUTHCODE_LENGTH * (SINGLECODE_WIDTH + SINGLECODE_GAP) + PADDING_LEFT_RIGHT;
  public static final int IMG_HEIGHT = SINGLECODE_HEIGHT+PADDING_TOP_BOT;
  public static final char[] CHARS = {'0','1', '2', '3', '4', '5', '6', '7', '8', '9' };
  static Random random = new Random();


  public static String getAuthCode() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < 6; i++) {// generate 6 digit and character
      buffer.append(CHARS[random.nextInt(CHARS.length)]);
    }
    return buffer.toString();
  }


  public static BufferedImage getAuthImg(String authCode) {
    // Set img width height type
    // RGB：red、green、blue
    BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,
        BufferedImage.TYPE_INT_BGR);

    Graphics g = img.getGraphics();
    // Set the img background
    g.setColor(new Color(255, 255, 230));
    // draw a rectangle
    g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
    // color of verification code
    g.setColor(Color.BLACK);
    // Set font details
    g.setFont(new Font("Arial", Font.PLAIN, SINGLECODE_HEIGHT + 5));
    //draw the code in the image
    char c;
    for (int i = 0; i < authCode.toCharArray().length; i++) {

      c = authCode.charAt(i);

      g.drawString(c + "", i * (SINGLECODE_WIDTH + SINGLECODE_GAP)
          + SINGLECODE_GAP / 2 +PADDING_LEFT_RIGHT/2, IMG_HEIGHT-PADDING_TOP_BOT/2);
    }
    Random random = new Random();
    // add interferential elements - draw some random lines
    for (int i = 0; i < 10; i++) {
      int x = random.nextInt(IMG_WIDTH);
      int y = random.nextInt(IMG_HEIGHT);
      int x2 = random.nextInt(IMG_WIDTH);
      int y2 = random.nextInt(IMG_HEIGHT);
      g.drawLine(x, y, x + x2, y + y2);
    }
    return img;
  }

  public static void outputImage(OutputStream os, String verifyCode) throws IOException {

    ImageIO.write(getAuthImg(verifyCode),"jpg", os);
  }
  public static byte[] outputImage(String verifyCode) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    ImageIO.write(getAuthImg(verifyCode),"jpg", outputStream);
    return outputStream.toByteArray();
  }


}
