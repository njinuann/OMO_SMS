/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import Ruby.APMain;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 *
 * @author Pecherk
 */
public final class AXCrypt
{
    private Cipher encipher, decipher;
    private String seedCode = "This is supernova project !";

    private static final byte salt[] =
    {
        -87, -101, -56, 50, 86, 53, -29, 3
    };

    public AXCrypt()
    {
        this(null);
    }

    public AXCrypt(String code)
    {
        if (code != null)
        {
            seedCode = code;
        }
        initialize();
    }

    public final void initialize()
    {
        try
        {
            String seed = (String.valueOf(seedCode) + "~%!*/6~#^1&0*%3&^*&$@5$!%&^").substring(0, 27);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(seed.toCharArray()));
            java.security.spec.AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 19);

            encipher = Cipher.getInstance(key.getAlgorithm());
            decipher = Cipher.getInstance(key.getAlgorithm());
            encipher.init(1, key, paramSpec);
            decipher.init(2, key, paramSpec);
        }
        catch (Exception ex)
        {
            APMain.acxLog.logEvent(ex);
        }
    }

    public String encrypt(String plainStr)
    {
        try
        {
            if (plainStr != null)
            {
                return (new BASE64Encoder()).encode(encipher.doFinal(plainStr.getBytes(StandardCharsets.UTF_8)));
            }
        }
        catch (Exception ex)
        {
            APMain.acxLog.logEvent(ex);
            initialize();
        }
        return plainStr;
    }

    public String decrypt(String encryptedStr)
    {
        try
        {
            if (encryptedStr != null)
            {
                return new String(decipher.doFinal((new BASE64Decoder()).decodeBuffer(encryptedStr)), StandardCharsets.UTF_8);
            }
        }
        catch (Exception ex)
        {
            APMain.acxLog.logEvent(ex);
            initialize();
        }
        return encryptedStr;
    }

    public boolean isEncrypted(String text)
    {
        try
        {
            if (text != null)
            {
                if (text.length() >= 3)
                {
                    return !text.equals(new String(decipher.doFinal((new BASE64Decoder()).decodeBuffer(text)), StandardCharsets.UTF_8));
                }
            }
        }
        catch (Exception ex)
        {
            initialize();
        }
        return false;
    }

    public boolean isEqEncrypted(String password)
    {
        try
        {
            eqDecrypt(password);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public String checkCrypt(String password)
    {
        try
        {
            return encrypt(eqDecrypt(password));
        }
        catch (Exception ex)
        {
            return password;
        }
    }

    public String eqEncrypt(String srcString)
    {
        if (srcString == null)
        {
            return null;
        }

        String rsDestText = "";
        String psSourceText;
        String sKey = generateKey();
        psSourceText = reverse(srcString);

        for (int nTextLength = psSourceText.length(); nTextLength > 0; nTextLength = psSourceText.length())
        {
            String sCurrentChar = String.valueOf(psSourceText.charAt(0));
            psSourceText = psSourceText.substring(1);

            int i = sCurrentChar.charAt(0);
            String sHold = sKey.substring(nTextLength + 1, nTextLength + 2);

            long j = Long.valueOf(sHold);
            long nCurrentVal = (long) i + j;

            sCurrentChar = String.valueOf(nCurrentVal).trim();
            if (sCurrentChar.length() == 1)
            {
                sCurrentChar = "00".concat(String.valueOf(String.valueOf(sCurrentChar)));
            }
            else if (sCurrentChar.length() == 2)
            {
                sCurrentChar = "0".concat(String.valueOf(String.valueOf(sCurrentChar)));
            }
            rsDestText = String.valueOf(rsDestText) + String.valueOf(sCurrentChar);
        }

        rsDestText = intSkip(rsDestText);
        return reverse(rsDestText);
    }

    public String eqDecrypt(String srcString)
    {
        if (srcString == null)
        {
            return null;
        }

        String rsDestText = "";
        String psSourceText;
        String sKey = generateKey();
        psSourceText = reverse(srcString);

        psSourceText = intSkip(psSourceText);
        for (int nTextLength = psSourceText.length() / 3; nTextLength > 0; nTextLength = psSourceText.length() / 3)
        {
            String sCurrentChar = psSourceText.substring(0, 3);
            psSourceText = psSourceText.substring(3);

            int i = Integer.valueOf(sCurrentChar);
            String sHold = sKey.substring(nTextLength + 1, nTextLength + 2);

            long j = Long.valueOf(sHold);
            long nCurrentVal = (long) i - j;

            sCurrentChar = String.valueOf((char) (int) nCurrentVal);
            rsDestText = String.valueOf(rsDestText) + String.valueOf(sCurrentChar);
        }

        return reverse(rsDestText);
    }

    private String reverse(String Inputstr)
    {
        String oString = "";
        int j = Inputstr.length() - 1;

        for (int i = j; i >= 0; i--)
        {
            oString = String.valueOf(oString) + String.valueOf(Inputstr.charAt(i));
        }

        return oString;
    }

    private String generateKey()
    {
        double pi = 3.14159265358979D;
        double log2 = 0.69314718055993996D;

        String sTemp1 = String.valueOf(pi);
        String sKey = sTemp1.substring(sTemp1.length() - 12);

        String sTemp2 = String.valueOf(log2);
        sTemp1 = sTemp2.substring(sTemp2.length() - 12);

        sKey = String.valueOf(sKey) + String.valueOf(sTemp1);
        sTemp1 = sKey;

        sKey = repeat(sTemp1, 10);
        return sKey;
    }

    private String repeat(String sStr, int nTimes)
    {
        String destr = sStr;
        for (int i = 1; i < nTimes; i++)
        {
            destr = String.valueOf(destr) + String.valueOf(sStr);
        }

        return destr;
    }

    private String intSkip(String psSourceText)
    {
        String DestStr = "";
        String sTempChunk = "";

        String sTemp1;
        String saTextChunks[] = new String[psSourceText.length()];

        int nBytes;
        if ((double) psSourceText.length() % 2D == (double) 0)
        {
            nBytes = 2;
        }
        else if ((double) psSourceText.length() % 3D == (double) 0)
        {
            nBytes = 3;
        }
        else
        {
            nBytes = 1;
        }

        int nCount;
        for (nCount = 2; psSourceText.length() != 0; nCount++)
        {
            sTemp1 = psSourceText.substring(0, nBytes);
            saTextChunks[nCount] = sTemp1;
            psSourceText = psSourceText.substring(nBytes);
        }

        int nMax = nCount;
        nCount = 2;
        int nChunkCount = 0;
        while (nCount <= nMax)
        {
            if (nChunkCount < 2)
            {
                if (nCount % 2 == 0)
                {
                    if (nCount < nMax)
                    {
                        sTempChunk = String.valueOf(sTempChunk) + String.valueOf(saTextChunks[nCount]);
                    }
                }
                else if (nCount != nMax)
                {
                    sTemp1 = saTextChunks[nCount];
                    sTemp1 = String.valueOf(sTemp1) + String.valueOf(sTempChunk);
                    sTempChunk = sTemp1;
                }
                nChunkCount++;
                nCount++;
            }
            else
            {
                DestStr = String.valueOf(DestStr) + String.valueOf(sTempChunk);
                sTempChunk = "";
                nChunkCount = 0;
            }
        }
        DestStr = String.valueOf(DestStr) + String.valueOf(sTempChunk);
        return DestStr;
    }
}
