/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby;

import static Ruby.APMain.consoleDialog;
import Ruby.acx.APLog;
import Ruby.acx.ATBox;
import Ruby.acx.AXConstant;
import Ruby.acx.AXCrypt;
import Ruby.acx.AXProperties;
import Ruby.acx.AXWorker;
import Ruby.model.CBNode;
import Ruby.model.CLItem;
import Ruby.model.CNCurrency;
import Ruby.sms.ALController;
import static Ruby.sms.ALController.channel;
import com.neptunesoftware.supernova.ws.client.security.BasicHTTPAuthenticator;
import com.neptunesoftware.supernova.ws.common.XAPIErrorCodes;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Authenticator;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Pecherk
 */
public class APController
{

    private static Integer keySequence = 0;
    private static final String confDir = "acx/conf";
    public static final String application = "Ruby SMS";

    public static Long languageFieldId = 62L, languageListId = 72L;
    private static final AXProperties settings = new AXProperties();
    private static final AXProperties xcodes = new AXProperties();

    private static final AXProperties rcodes = new AXProperties();
    private static final ArrayList<CBNode> xapiNodes = new ArrayList<>();
    private static final AXProperties fields = new AXProperties();

    public static String defaultLanguage = "EN", xapiUser = "biKsgfG84Onp9zM0zF2huA==";
    public static String coreSchemaName = "WAKULIMALIVE", xapiPassword = "ACdFgyoXow8uEp5NQ6xqXQ==";
    public static String xapiWsContext = "http://192.168.1.55:7001/supernovaws/|http://192.168.1.55:7001/supernovaws/";

    public static Integer yearsToKeepLogs = 2, countryCode = 263;
    public static Integer displayLines = 1000, treeLogSize = 1000;
    public static String databaseUrl = "jdbc:oracle:thin:@//192.168.1.56:1521/WKLLVPDB", cmSchemaName = "CHANNELMANAGER";

    public static String lockShutdown = "No", cmSchemaPassword = "k6MjR8pvTyRrwjLHPRk5FA==";
    public static String enableDebug = "No", displayConsole = "Yes", suspendMailer = "No", suspendAlert = "No";
    private static HashMap<Long, CLItem> languages = new HashMap<>();

    public static String databaseDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final ATBox box = new ATBox(getLog());
    private static CNCurrency currency = new CNCurrency();

    public static void initialize()
    {
        if (configure())
        {
            loadLibraries();
            checkLicense();
            setLists();
        }
    }

    public static void loadLibraries()
    {
        try
        {
            Class.forName(APController.databaseDriverName);
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        try
        {
            Authenticator.setDefault(new BasicHTTPAuthenticator(xapiUser, xapiPassword));
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    public static boolean configure()
    {
        try ( InputStream sin = new FileInputStream(new File(confDir, "settings.prp")))
        {
            getSettings().loadFromXML(sin);
            try
            {
                boolean updated = false;
                for (Field field : APController.class.getDeclaredFields())
                {
                    if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()))
                    {
                        try
                        {
                            if (!getSettings().containsKey(field.getName()))
                            {
                                getSettings().setProperty(getWorker().capitalize(field.getName(), false), String.valueOf(field.get(null)));
                                updated = true;
                            }
                            else
                            {
                                field.set(null, getWorker().getSetting(getSettings(), field.getName(), field.getType()));
                            }
                        }
                        catch (Exception ex)
                        {
                            getLog().logEvent(field.getName(), ex);
                        }
                    }
                }
                if (updated)
                {
                    updateSettings();
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
            setXapiNodes();
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }

        EventQueue.invokeLater(()
                ->
        {
            consoleDialog.setVisible(getWorker().isYes(displayConsole));
        });

        getWorker().loadProperties(new File(confDir, "fields.prp"), getFields());
        getWorker().loadProperties(new File(confDir, "xcodes.prp"), getXcodes());
        getWorker().loadProperties(new File(confDir, "rcodes.prp"), getXcodes());
        return !getSettings().isEmpty();
    }

    public static void updateSettings()
    {
        try
        {
            if (getSettings() != null)
            {
                getSettings().storeToXML(new FileOutputStream(new File(confDir, "settings.prp")), "Ruby Properties");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    public static void updateSetting(String name, String value)
    {
        getSettings().setProperty(name, value);
        updateSettings();
    }

    private static void updateXapiCodes()
    {
        getdClient().updateXapiErrors();
        for (Field field : XAPIErrorCodes.class.getDeclaredFields())
        {
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()))
            {
                try
                {
                    String code = String.valueOf(field.get(null));
                    if (!getXcodes().containsKey(code))
                    {
                        getXcodes().put(code, getWorker().capitalize(field.getName().replaceAll("_", " ")));
                    }
                }
                catch (Exception ex)
                {
                    getLog().logEvent(field.getName(), ex);
                }
            }
        }
        APController.saveXapiCodes();
    }

    public static void saveXapiCodes()
    {
        try
        {
            if (getXcodes() != null)
            {
                getXcodes().storeToXML(new FileOutputStream(new File(confDir, "xcodes.prp")), "Xapi Codes");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    public static boolean checkLicense()
    {
        try ( ResultSet rs = getdClient().executeQueryToResultSet("SELECT TO_NUMBER(TO_DATE(DISPLAY_VALUE,'DD/MM/YYYY')-(SELECT TO_DATE(DISPLAY_VALUE,'DD/MM/YYYY') FROM " + coreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD='S02')) AS DAYS FROM " + coreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD='S769'"))
        {
            if (rs.next())
            {
                if (rs.getDouble(1) < 0)
                {
                    JOptionPane.showMessageDialog(null, "Software license has expired. Login will be denied.", "License Expired", JOptionPane.ERROR_MESSAGE);
                    APMain.shutdown(false);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        setCurrency(getdClient().queryCurrency(getdClient().queryParameter("S17", Long.class)));
        return true;
    }

    public static void configure(String channel)
    {
        switch (channel)
        {
            case ALController.channel:
                ALController.configure();
                break;
        }
    }

    public static void refresh()
    {
        try
        {
            if (Integer.parseInt(new SimpleDateFormat("HH").format(new Date())) == 3)
            {
                APMain.acxLog.logEvent("---<<<~~~amrs~~~>>>---");
                APMain.shutdown(true);
            }
        }
        catch (Throwable ex)
        {
            APMain.acxLog.logEvent(ex);
        }
    }

    private static void setXapiNodes()
    {
        for (String url : xapiWsContext.split("[|]"))
        {
            getXapiNodes().add(new CBNode(url));
        }
    }

    private static void setLists()
    {
        // setLanguages(getdClient().queryCustomListItems(languageListId));
        HashMap<Long, CLItem> itemsList = new HashMap<>();
        CLItem cLItem = new CLItem(1, 1L, "EN", "English");
        itemsList.put(cLItem.getItemId(), cLItem);
        setLanguages(itemsList);
        updateXapiCodes();
    }

    public static void deleteXapiHistory()
    {
        try
        {
            getdClient().deleteXapiHistory();
        }
        catch (Throwable ex)
        {
            APMain.acxLog.logEvent(ex);
        }
    }

    public static void gc()
    {
        try
        {
            System.gc();
        }
        catch (Throwable ex)
        {
            APMain.acxLog.logEvent(ex);
        }
    }

    public static void purgeTreeItems()
    {
        try
        {
            if (APMain.apFrame != null)
            {
                SwingUtilities.invokeLater(()
                        ->
                {
                    APMain.apFrame.purgeTreeItems();
                });
            }
        }
        catch (Throwable ex)
        {
            ex = null;
        }
    }

    public static String getXapiMessage(String xapiCode)
    {
        return xapiCode != null ? getXcodes().getProperty(xapiCode, xapiCode) : xapiCode;
    }

    public static String generateKey()
    {
        return "SC" + AXConstant.keyDateFormat.format(new Date()) + String.format("%03d", (keySequence = ((keySequence + 1) % 1000) + 1));
    }

    public static boolean isDebugMode()
    {
        return getWorker().isYes(APController.enableDebug);
    }

    public static CNCurrency queryCurrency(String codeOrId)
    {
        return getdClient().queryCurrency(codeOrId);
    }

    /**
     * @return the dclient
     */
    public static DBClient getdClient()
    {
        return getBox().getdClient();
    }

    public static APLog getLog()
    {
        return APMain.acxLog;
    }

    /**
     * @return the xcodes
     */
    public static AXProperties getXcodes()
    {
        return xcodes;
    }

    /**
     * @return the worker
     */
    public static AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the currency
     */
    public static CNCurrency getCurrency()
    {
        return currency;
    }

    /**
     * @param pCurrency the currency to set
     */
    public static void setCurrency(CNCurrency pCurrency)
    {
        currency = pCurrency;
    }

    /**
     * @return the languages
     */
    public static HashMap<Long, CLItem> getLanguages()
    {
        return languages;
    }

    /**
     * @param aLanguages the languages to set
     */
    public static void setLanguages(HashMap<Long, CLItem> aLanguages)
    {
        languages = aLanguages;
    }

    /**
     * @return the xapiNodes
     */
    public static ArrayList<CBNode> getXapiNodes()
    {
        return xapiNodes;
    }

    /**
     * @return the settings
     */
    public static AXProperties getSettings()
    {
        return settings;
    }

    /**
     * @return the crypt
     */
    public static AXCrypt getCrypt()
    {
        return getBox().getXcrypt();
    }

    /**
     * @return the box
     */
    public static ATBox getBox()
    {
        return box;
    }

    /**
     * @return the rcodes
     */
    public static AXProperties getRcodes()
    {
        return rcodes;
    }

    /**
     * @return the fields
     */
    public static AXProperties getFields()
    {
        return fields;
    }
}
