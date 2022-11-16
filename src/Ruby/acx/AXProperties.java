/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 *
 * @author Pecherk
 */
public class AXProperties extends TreeMap<String, String>
{
    private final AXFile aXFile = new AXFile();
    private final AXCrypt crypt = new AXCrypt();
    private final Properties properties = new Properties();
    private static final HashMap<String, String> escapeXters = new HashMap<>();
    private static final HashMap<String, String> unescapeXters = new HashMap<>();

    static
    {
        escapeXters.put("&", "&amp;");
        unescapeXters.put("&lt;", "<");
        unescapeXters.put("&gt;", ">");
        unescapeXters.put("&amp;", "&");
        unescapeXters.put("&apos;", "'");
        unescapeXters.put("&quot;", "\"");
    }

    public AXProperties()
    {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    public void loadFromXML(InputStream is) throws IOException, InvalidPropertiesFormatException
    {
        properties.loadFromXML(aXFile.bytesToStream(escapeXmlXters(aXFile.readInputStream(is), true).trim().getBytes()));
        properties.stringPropertyNames().stream().forEach((key)
                -> 
                {
                    properties.setProperty(key, escapeXmlXters(properties.getProperty(key), false));
        });
        putAll((Map) properties);
    }

    private String escapeXmlXters(String xml, boolean escape)
    {
        HashMap<String, String> xters = escape ? escapeXters : unescapeXters;
        for (String key : xters.keySet())
        {
            xml = xml.replaceAll(key, xters.get(key));
        }
        return xml.replaceAll("&amp;amp;", "&amp;");
    }

    public void storeToXML(OutputStream os, String comment) throws IOException
    {
        properties.putAll(this);
        ByteArrayOutputStream baos;
        properties.storeToXML((baos = new ByteArrayOutputStream()), comment);
        aXFile.writeToStream(os, baos.toString().replaceAll("&amp;", "&"));
    }

    public String getProperty(String key, String defaultValue)
    {
        return check(getOrDefault(key, defaultValue));
    }

    public String setProperty(String key, String value)
    {
        return put(key, value);
    }

    private String check(String value)
    {
        return getCrypt().isEncrypted(value) ? getCrypt().decrypt(value) : value;
    }

    public String getProperty(String key)
    {
        return check(get(key));
    }

    /**
     * @return the crypt
     */
    public AXCrypt getCrypt()
    {
        return crypt;
    }
}
