/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.est;

import Ruby.APController;
import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.APLog;
import Ruby.acx.ATBox;
import Ruby.acx.AXFile;
import Ruby.acx.AXWorker;
import Ruby.acx.TRItem;
import Ruby.enu.ALHeader;
import Ruby.model.AXChannel;
import Ruby.model.AXSetting;
import Ruby.model.USRole;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Pecherk
 */
public class ESController
{
    public static final String module = "EST";
    public static Long channelId = 15L, systemUserId = -99L;
    public static TreeMap<String, AXSetting> settings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static final String confDir = "est/conf", logsDir = "est/logs";
    public static Long cycleFieldId = 59L, sentDateFieldId = 66L, addressFieldId = 75L;
    public static Long startDateFieldId = 60L, endDateFieldId = 65L;

    public static String emailSubject = "Nawiri Account Statement", chargeScheme = "E01";
    private static ArrayList<ALHeader> headers = new ArrayList();
    private static ArrayList<TRItem> treeLog = new ArrayList();

    public static String statementFileName = "Nawiri Account Statement", sslTrust = "*";
    public static String smtpUsername, smtpPassword, smtpAuth, starttlsEnable;
    public static ArrayList<String> whiteList = new ArrayList<>();

    public static String senderAlias, smtpHost, sslEnable = "true";
    public static String sslProtocols = "TLSv1.2", transportProtocol = "smtps";
    public static Long nowFieldId = 58L, yesValueId = 154L, noValueId = 153L;

    private static final ATBox box = new ATBox(APMain.estLog);
    private static AXChannel channel = new AXChannel();
    private static USRole role = new USRole();

    static
    {
        setHeaders();
    }

    public static void initialize()
    {
        if (configure())
        {
            setRole(getClient().queryUserRoles(systemUserId, channelId).get(0));
            setChannel(getClient().queryChannel(channelId));
            readTreeLog();
        }
    }

    public static boolean configure()
    {
        setSettings(getWorker().configure(ESController.class, module));
        return !getSettings().isEmpty();
    }

    private static void setHeaders()
    {
        getHeaders().add(ALHeader.RecId);
        getHeaders().add(ALHeader.CreateDt);
        getHeaders().add(ALHeader.Task);

        getHeaders().add(ALHeader.Account);
        getHeaders().add(ALHeader.Address);
        getHeaders().add(ALHeader.StartDt);

        getHeaders().add(ALHeader.EndDt);
        getHeaders().add(ALHeader.Charge);
        getHeaders().add(ALHeader.ChgId);

        getHeaders().add(ALHeader.Result);
        getHeaders().add(ALHeader.RecSt);
    }

    public static void readTreeLog()
    {
        try
        {
            File arc = new File("est/arc/tree.arc");
            if (arc.exists())
            {
                Object log = new ObjectInputStream(new FileInputStream(arc)).readObject();
                if (log instanceof ArrayList)
                {
                    setTreeLog((ArrayList) log);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent("<exception realm=\"est\">unable to load statement tree log ~ [" + ex.getMessage() + "]</exception>");
        }
    }

    public static void saveTreeLog()
    {
        if (getTreeLog() != null)
        {
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(getFile().checkParent(new File("est/arc/tree.arc")))))
            {
                os.writeObject(getTreeLog());
                os.flush();
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
    }

    public static boolean isWhiteListed(String accountNumber)
    {
        return getWhiteList().isEmpty() || getWhiteList().contains("*") || getWhiteList().contains(accountNumber);
    }

    public static boolean isSuspended()
    {
        return getWorker().isYes(APController.suspendMailer);
    }

    /**
     * @return the settings
     */
    public static TreeMap<String, AXSetting> getSettings()
    {
        return settings;
    }

    /**
     * @param aSettings the settings to set
     */
    public static void setSettings(TreeMap<String, AXSetting> aSettings)
    {
        settings = aSettings;
    }

    /**
     * @return the worker
     */
    public static AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    public static AXFile getFile()
    {
        return getBox().getxFile();
    }

    public static DBClient getClient()
    {
        return getBox().getdClient();
    }

    /**
     * @return the role
     */
    public static USRole getRole()
    {
        return role;
    }

    /**
     * @param aSystemRole the role to set
     */
    public static void setRole(USRole aSystemRole)
    {
        role = aSystemRole;
    }

    /**
     * @return the box
     */
    public static ATBox getBox()
    {
        return box;
    }

    /**
     * @return the headers
     */
    public static ArrayList<ALHeader> getHeaders()
    {
        return headers;
    }

    /**
     * @param aHeaders the headers to set
     */
    public static void setHeaders(ArrayList<ALHeader> aHeaders)
    {
        headers = aHeaders;
    }

    /**
     * @return the treeLog
     */
    public static ArrayList<TRItem> getTreeLog()
    {
        return treeLog;
    }

    /**
     * @param aTreeLog the treeLog to set
     */
    public static void setTreeLog(ArrayList<TRItem> aTreeLog)
    {
        treeLog = aTreeLog;
    }

    /**
     * @return the whiteList
     */
    public static ArrayList<String> getWhiteList()
    {
        return whiteList;
    }

    /**
     * @param aWhiteList the whiteList to set
     */
    public static void setWhiteList(ArrayList<String> aWhiteList)
    {
        whiteList = aWhiteList;
    }

    public static APLog getLog()
    {
        return getBox().getLog();
    }

    /**
     * @return the channel
     */
    public static AXChannel getChannel()
    {
        return channel;
    }

    /**
     * @param aChannel the channel to set
     */
    public static void setChannel(AXChannel aChannel)
    {
        channel = aChannel;
    }
}
