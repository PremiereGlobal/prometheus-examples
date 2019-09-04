package net.pgicloud.prometheus.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
  
  public static String SHAString(String ... s)  {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      for(String ss: s) {
        byte[] ba = ss.getBytes();
        digest.update(ba);
      }
      return bytesToHex(digest.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String bytesToHex(byte[] hash) {
    StringBuffer hs = new StringBuffer();
    for(byte b: hash) {
      hs.append(String.format("%02x", b));
    }
    return hs.toString();
  }
}
