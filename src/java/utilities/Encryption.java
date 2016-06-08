
package utilities;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author cjones
 */
public class Encryption {

    private static SecretKey key = null;
    
    public static void initialize(){
        initialize(null);
    }
    
    public static void initialize(String filename){
        if(filename == null){
            key = generateKey();
            return;
        }
        key = readKey(new File(filename));
    }
    
    /**
    * Encrypts the given string using the stored secret key. 
    * If no key is stored, one is generated. 
    *
   
    * @param str The string to keyEncrypt.
    * @return A string that represents the encrypted string or the original string if 
    * no encryption key is available.
    */
   public static String keyEncrypt(String str) {
      if(key==null) key = generateKey();
      return keyEncrypt( key,  str);
      
   }
    
   /**
     * Decrypts the given string with the stored secret key. If no key is stored,
     * the original string is returned.
     *
     * @param str The string to keyDecrypt.
     * @return A string that represents the decrypted string.
     */
    public static String keyDecrypt(String str) {
        if(key==null) return str; 
        return keyDecrypt(key,str);
    }
   
    /**
    * Encrypts the given string using the secret key.
    *
    * @param key The key.
    * @param str The string to keyEncrypt.
    * @return A string that represents the encrypted string.
    */
   public static String keyEncrypt(SecretKey key, String str) {
        try {
             Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (IllegalBlockSizeException ex) {
            ErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        } catch (InvalidKeyException ex) {
            ErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            ErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            ErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            ErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
            ErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                      +"trying to encrypt the string. " + str, ex);
        }
        return null;
    }

    /**
     * Decrypts the given string with the secret key.
     *
     * @param key The key.
     * @param str The string to keyDecrypt.
     * @return A string that represents the decrypted string.
     */
    public static String keyDecrypt(SecretKey key, String str) {
        try {
             Cipher dcipher = Cipher.getInstance("DES");
             dcipher.init(Cipher.DECRYPT_MODE, key);
             // Decode base64 to get bytes
             byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

             // Decrypt
             byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (InvalidKeyException ex) {
            ErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            ErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            ErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            ErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        } catch (IllegalBlockSizeException ex) {
            ErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
           ErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                      +"trying to decrypt the string. " + str, ex);
        }
        return null;
    }

  /**
   * Generates a secret DES encryption/decryption key.
   *
   * @return The secret key.
   */
  public static SecretKey generateKey(){
      KeyGenerator keygenerator = null;
      try {
          // Get a key generator for Triple DES
          keygenerator = KeyGenerator.getInstance("DES");

      } catch (NoSuchAlgorithmException ex) {
         ErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    +"trying to generate a secret key.", ex);
      }
      if(keygenerator==null) return null;
      else return keygenerator.generateKey();
  }

  /**
   * Saves the given SecretKey to the given file.
   *
   * @param key The secret key.
   * @param f The file.
   */
  public static void writeKey(SecretKey key, File f){
      FileOutputStream out = null;
      try {
           SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
           DESKeySpec keyspec = (DESKeySpec) keyfactory.getKeySpec(key,
           DESKeySpec.class);
           byte[] bytes = keyspec.getKey();
           out = new FileOutputStream(f);
           out.write(bytes);
           out.close();
      } catch (IOException ex) {
           ErrorLogger.log(Level.SEVERE, "IOException is thrown while "+
                "trying to write the secret key to the file: "+f+".", ex);
      } catch (NoSuchAlgorithmException ex) {
          ErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "+
                 "while trying to write the secret key to the file: "+f+".", ex);
      } catch (InvalidKeySpecException ex) {
          ErrorLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "+
                 "while trying to write the secret key to the file: "+f+".", ex);
      } finally {
           try {
                out.close();
           } catch (IOException ex) {
                ErrorLogger.log(Level.SEVERE, "IOException is thrown while "+
                     "trying to close the file output stream.", ex);
           }
      }
  }

  /**
   * Reads a DES secret key from the file.
   *
   * @param f The file.
   * @return A SecretKey that was read from the file.
   */
  public static SecretKey readKey(File f) {
      SecretKey key = null;
        try {
            // Read the bytes from the keyfile
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            byte[] bytes = new byte[(int) f.length()];
            in.readFully(bytes);
            in.close();
            // Convert the bytes to a secret key
            DESKeySpec keyspec = new DESKeySpec(bytes);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            key = keyfactory.generateSecret(keyspec);

        } catch (InvalidKeySpecException ex) {
            ErrorLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "+
                        "while trying to read the file: "+f+".", ex);

        } catch (NoSuchAlgorithmException ex) {
            ErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "+
                        "while trying to read the file: "+f+".", ex);

        } catch (InvalidKeyException ex) {
            ErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown "+
                        "while trying to read the file: "+f+".", ex);

        } catch (IOException ex) {
            ErrorLogger.log(Level.SEVERE, "IOException is thrown "+
                        "while trying to read the file: "+f+".", ex);
        }
      return key;
  }

  
   /**
     * Encrypts a string in hexadecimal format using the SHA-1 
     * hash algorithm. If SHA-1 does not exist, then the original string is returned. 
     * This is a cryptographic hash function, designed not to be decrypted. 
     * Use this concept to store passwords in a database
     *
     * @param orig The original string.
     * @return The encrypted string.
     */
    public static String encryptSHA1(String orig) {
        String alg = "SHA-1";
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(alg).digest(orig.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption algorithm "+ alg 
                         + " is not available or does not exist.";
            ErrorLogger.log(Level.SEVERE, msg, e);
            return orig;
        }

        // translates bytes to hex string
        StringBuilder hexStrBuf = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xff);
            hexStrBuf.append(str.length() == 1 ? "0" : "").append(str);
        }

        return hexStrBuf.toString();
    }

  
  // * Please keep the main method.  Do not delete.
  
    public static void main(String[] args) {
        
        SecretKey key = generateKey();
        File file = new File("web\\WEB-INF\\config\\DoNotDeleteFile.txt");
        writeKey(key, file);
       // SecretKey key = readKey(file);
        // Encrypt
        String encrypted = keyEncrypt(key, "SE2015");
        System.out.println("Encrypted MySQLUserName: " + encrypted);
        String decrypted = keyDecrypt(key, encrypted);
        System.out.println("Decrypted MySQLUserName: " + decrypted);
        String encrypted2 = keyEncrypt(key, "Soft3ng2015");
        System.out.println("Encrypted password: " + encrypted2);
        String decrypted2 = keyDecrypt(key, encrypted2);
        System.out.println("Decrypted password: " + decrypted2);
        String databaseURL = "hermes.bloomu.edu";
        String encrypted3 = keyEncrypt(key, databaseURL);
        System.out.println("Encrypted host: " + encrypted3);
        String decrypted3 = keyDecrypt(key, encrypted3);
        System.out.println("Decrypted host: " + decrypted3);
        
        String encrypted4 = keyEncrypt(key, "ISISTools");
        System.out.println("Encrypted db name: " + encrypted4);
        String decrypted4 = keyDecrypt(key, encrypted4);
        System.out.println("Decrypted db name: " + decrypted4);
        
        String encrypted5 = keyEncrypt(key, "buweatherproject@gmail.com");
        System.out.println("Encrypted email: " + encrypted5);
        String decrypted5 = keyDecrypt(key, encrypted5);
        System.out.println("Decrypted email: " + decrypted5);
        
        String encrypted6 = keyEncrypt(key, "SoftwareEngineering2014");
        System.out.println("Encrypted email password: " + encrypted6);
        String decrypted6 = keyDecrypt(key, encrypted6);
        System.out.println("Decrypted email password: " + decrypted6);
        System.out.println("encryptSHA1(Soft3ng2015) is " + encryptSHA1("yourpassword"));
    }
    
}
