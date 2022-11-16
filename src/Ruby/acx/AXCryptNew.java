/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.io.PrintStream;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AXCryptNew
{

    private Cipher cipher;
    private final byte[] salt =
    {
        -87, -101, -56, 50, 86, 53, -29, 3
    };
    private static final int ITERATION_COUNT = 1024;
    private static final int KEY_STRENGTH = 128;
    private static SecretKey key;
    private static final byte[] iv =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static final String DEFAULT_NEW_PASS_PHRASE = "Rubikon Universal Core Banking System";

    public AXCryptNew()
    {
        try
        {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            KeySpec spec = new PBEKeySpec(DEFAULT_NEW_PASS_PHRASE.toCharArray(), this.salt, 1024, 128);

            SecretKey tmp = factory.generateSecret(spec);

            key = new SecretKeySpec(tmp.getEncoded(), "AES");

            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public AXCryptNew(String passPhrase)
    {
        try
        {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), this.salt, 1024, 128);

            SecretKey tmp = factory.generateSecret(spec);

            key = new SecretKeySpec(tmp.getEncoded(), "AES");

            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String encrypt(String plainStr)
    {
        String base64EncryptedData = "";
        try
        {
            if (plainStr == null)
            {
                throw new IllegalArgumentException("The argument 'plainStr' is null");
            }

            IvParameterSpec ivspec = new IvParameterSpec(iv);

            this.cipher.init(1, key, ivspec);

            byte[] utf8EncryptedData = this.cipher.doFinal(plainStr.getBytes());

            base64EncryptedData = Base64.getEncoder().encodeToString(utf8EncryptedData);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return base64EncryptedData;
    }

    public String decrypt(String encryptedStr)

    {
        String decrypted = "";
        try
        {
            if (encryptedStr == null)
            {
                throw new IllegalArgumentException("The argument 'encryptedStr' is null");
            }

            IvParameterSpec ivspec = new IvParameterSpec(iv);

            this.cipher.init(2, key, ivspec);

            byte[] decryptedData = Base64.getDecoder().decode(encryptedStr);

            byte[] utf8 = this.cipher.doFinal(decryptedData);
            decrypted = new String(utf8, "UTF8");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return decrypted;
    }

    public static void main(String[] args)
    {
        try
        {
            System.err.println(".. " + new AXCryptNew(DEFAULT_NEW_PASS_PHRASE).decrypt("NfWrkFM2Kh4dlbV5+fVycQ=="));
            System.err.println(".. " + new AXCryptNew(DEFAULT_NEW_PASS_PHRASE).encrypt("training"));
            //   System.err.println(".. "+new BRCryptNew(DEFAULT_NEW_PASS_PHRASE).encrypt("proxy_password"));

        }
        catch (Exception ex)
        {
            Logger.getLogger(AXCryptNew.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
