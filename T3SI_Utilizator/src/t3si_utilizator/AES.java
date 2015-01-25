/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package t3si_utilizator;

/**
 *
 * @author Ervin
 */
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AES {
    public static String generareCheieRandom(int lungime) {
        char[] caractere = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lungime; i++) {
            char c = caractere[random.nextInt(caractere.length)];
            sb.append(c);
        }
        
        return sb.toString();
    }
    
    public static String generareK(int lungime){
        char[] caractere = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lungime-2; i++) {
            char c = caractere[random.nextInt(caractere.length)];
            sb.append(c);
        }
        sb.append("==");
        return sb.toString();
    }
    public static String symmetricEncrypt(String text, String secretKey) {
            byte[] raw;
            String encryptedString;
            SecretKeySpec skeySpec;
            byte[] encryptText = text.getBytes();
            Cipher cipher;
            try {
                raw = Base64.decodeBase64(secretKey);
                skeySpec = new SecretKeySpec(raw, "AES");
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
            } 
            catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
            return encryptedString;
        }

        public static String symmetricDecrypt(String text, String secretKey) {
            Cipher cipher;
            String encryptedString;
            byte[] encryptText = null;
            byte[] raw;
            SecretKeySpec skeySpec;
            try {
                raw = Base64.decodeBase64(secretKey);
                skeySpec = new SecretKeySpec(raw, "AES");
                encryptText = Base64.decodeBase64(text);
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                encryptedString = new String(cipher.doFinal(encryptText));
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
            return encryptedString;
        }
        
        
    public static void print() throws NoSuchAlgorithmException{
        int length = Cipher.getMaxAllowedKeyLength("AES");
        System.out.println(length);//prints the max key length
    }
}

