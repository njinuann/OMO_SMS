/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.sms;

import Ruby.APController;
import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.APLog;
import Ruby.acx.ATBox;
import Ruby.acx.AXFile;
import Ruby.acx.AXWorker;
import Ruby.acx.TRItem;
import Ruby.model.ALHeader;
import Ruby.model.AXSetting;
import Ruby.model.CNBranch;
import Ruby.model.USRole;
import com.mashape.unirest.http.Unirest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.SwingUtilities;

/**
 *
 * @author Pecherk
 */
public class ALController
{

    public static String restricted = "Yes";
    public static final String channel = "SMS";
    public static final Long channelId = 9L, systemUserId = -99L;

    public static ArrayList<String> recipients = new ArrayList<>();
    private static TreeMap<String, AXSetting> settings = new TreeMap<>();
    public static String chargeScheme = "S01";

    public static String senderId = "WAKULIMASAC", user = "Wakulima Sacco ", clientId = "1092";
    public static String password = "f5fb2487f3546093894d44a68e743a9b62407b3531aa9f6d761b52f86afef3969a09f558dfc748f4793df9d7a66e26be31679f48f5b63354bb73afb0cf0bff38";
    public static String apiKey = "iv0akGjyFWNDUltdqeh2oQucBYLCOsbP8ZJHmXxn31VzfK4gr97Ip6wT5SMEAR";
    private static ArrayList<ALHeader> headers = new ArrayList();

    private static ArrayList<TRItem> treeLog = new ArrayList();
    private static final ATBox box = new ATBox(getLog());
    public static final Integer retryCount = 2;

    public static String sendSmsURL = "https://eclecticsgateway.ekenya.co.ke:8095/ServiceLayer/pgsms/send";
    public static Long creditLimitFieldId = 65L;
    public static Long debitLimitFieldId = 66L;

    private static CNBranch branch = new CNBranch();
    private static USRole systemRole = new USRole();

    static
    {
        setHeaders();
    }

    public static void initialize()
    {
        if (configure())
        {
            setSystemRole(getdClient().queryUserRoles(systemUserId, channelId).get(0));
            setBranch(getdClient().queryChannelBranch(channelId));
            getdClient().synchroniseDates();
        }
    }

    public static boolean configure()
    {
        setSettings(getWorker().configure(ALController.class, channel));
        return !getSettings().isEmpty();
    }

    private static void setHeaders()
    {
        getHeaders().add(ALHeader.AlertId);
        getHeaders().add(ALHeader.CreateDt);
        getHeaders().add(ALHeader.AlertCode);

        getHeaders().add(ALHeader.AlertType);
        getHeaders().add(ALHeader.Msisdn);

        getHeaders().add(ALHeader.TxnId);
        getHeaders().add(ALHeader.CustId);
        getHeaders().add(ALHeader.CustName);

        getHeaders().add(ALHeader.Account);
        getHeaders().add(ALHeader.Contra);
        getHeaders().add(ALHeader.ChargeAcct);

        getHeaders().add(ALHeader.TxnDate);
        getHeaders().add(ALHeader.Currency);
        getHeaders().add(ALHeader.Amount);

        getHeaders().add(ALHeader.Description);
        getHeaders().add(ALHeader.Charge);
        getHeaders().add(ALHeader.ChgId);

        getHeaders().add(ALHeader.SchemeId);
        getHeaders().add(ALHeader.AccessCode);
        getHeaders().add(ALHeader.Balance);

        getHeaders().add(ALHeader.Message);
        getHeaders().add(ALHeader.Result);
        getHeaders().add(ALHeader.RecSt);
    }

    public static void readTreeLog()
    {
        try
        {
            File arc = new File("sms/arc/tree.arc");
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
            getLog().logEvent("<exception realm=\"sms\">unable to load sms tree log ~ [" + ex.getMessage() + "]</exception>");
        }
    }

    public static void addTreeItem(TRItem item)
    {
        // getTreeLog().add(item);
        if (APMain.apFrame != null)
        {
            SwingUtilities.invokeLater(()
                    ->
            {
                APMain.apFrame.insertTreeItem(item, channel);
            });
        }
    }

    public static void saveTreeLog()
    {
        try
        {
            File arc = new File("sms/arc/tree.arc");
            getxFile().createDirectory(arc.getParent());
            if (getTreeLog() != null)
            {
                try ( ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(arc)))
                {
                    os.writeObject(getTreeLog());
                    os.flush();
                }
                catch (Exception e)
                {
                    e = null;
                }
            }
        }
        catch (Throwable ex)
        {
            getLog().logEvent(ex);
        }
    }

    public static void expireOldAlerts()
    {
        try
        {
            getdClient().expireOldAlerts();
        }
        catch (Throwable ex)
        {
            getLog().logEvent(ex);
        }
    }

    public static void shutdown()
    {
        try
        {
            Unirest.shutdown();
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        getBox().dispose();
        saveTreeLog();
    }

    public static AXFile getxFile()
    {
        return getBox().getxFile();
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

    /**
     * @return the dclient
     */
    public static DBClient getdClient()
    {
        return getBox().getdClient();
    }

    public static APLog getLog()
    {
        return APMain.smsLog;
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
     * @return the branch
     */
    public static CNBranch getBranch()
    {
        return branch;
    }

    /**
     * @param aBranch the branch to set
     */
    public static void setBranch(CNBranch aBranch)
    {
        branch = aBranch;
    }

    /**
     * @return the systemRole
     */
    public static USRole getSystemRole()
    {
        return systemRole;
    }

    /**
     * @param aSystemRole the systemRole to set
     */
    public static void setSystemRole(USRole aSystemRole)
    {
        systemRole = aSystemRole;
    }

    public static boolean isSuspended()
    {
        return getWorker().isYes(APController.suspendAlert);
    }
}
