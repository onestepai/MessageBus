package com.onestep.os.messagebusservice.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecurityUtils {

  private final static String ENCRYPT_MODE = "RSA";
  public static String encrypt(String buffer, String encryptKey) {
    try {

      final byte[] keyBytes = Base64.getMimeDecoder().decode(encryptKey);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory kf = KeyFactory.getInstance(ENCRYPT_MODE);
      PrivateKey privateKey = kf.generatePrivate(spec);
      Cipher rsa;
      rsa = Cipher.getInstance(ENCRYPT_MODE);
      rsa.init(Cipher.ENCRYPT_MODE, privateKey);
      return Base64.getMimeEncoder().encodeToString(rsa.doFinal(buffer.getBytes()));

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String decrypt(String buffer, String decryptKey) {
    try {
      final byte[] keyBytes = Base64.getMimeDecoder().decode(decryptKey);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
      KeyFactory kf = KeyFactory.getInstance(ENCRYPT_MODE);
      PublicKey publicKey = kf.generatePublic(spec);
      Cipher rsa;
      rsa = Cipher.getInstance(ENCRYPT_MODE);
      rsa.init(Cipher.DECRYPT_MODE, publicKey);
      return new String(rsa.doFinal(Base64.getMimeDecoder().decode(buffer)), StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
