/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.sms;

import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.ATBox;
import Ruby.model.MXAlert;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pecherk
 */
public class ALProcessor
{

    private static final ATBox box = new ATBox(APMain.smsLog);
    public static final ConcurrentHashMap<String, Date> runningAlerts = new ConcurrentHashMap<>();

    public static void process()
    {
        if (!APMain.exit && !ALController.isSuspended())
        {
            try
            {
                APMain.smsLog.logDebug("---<process>---");
                processPendingAlerts();
                getdClient().dispose();
            }
            catch (Throwable ex)
            {
                APMain.smsLog.logEvent(ex);
            }
        }
    }

    private static ArrayList<MXAlert> getPendingAlerts()
    {
        Date currentDate = getdClient().getProcessingDate();
        ArrayList<MXAlert> pendingAlerts = new ArrayList<>();
        TreeMap<String, MXAlert> alerts = getdClient().queryAlerts();
        alerts.values().stream().map((mXAlert)
                ->
        {
            if (currentDate.after(mXAlert.getExpiryDate()))
            {
                mXAlert.setStatus("E");
            }
            if (mXAlert.getNextDate().before(getdClient().getSystemDate()))
            {
                if (mXAlert.getStatus().equals("R"))
                {
                    mXAlert.setStatus("A");
                }
            }
            return mXAlert;
        }).filter((mXAlert) -> ("A".equals(mXAlert.getStatus()) && !"Triggered".equalsIgnoreCase(mXAlert.getFrequency()) && isTimeRight(mXAlert) && (currentDate.after(mXAlert.getNextDate()) || currentDate.equals(mXAlert.getNextDate()) || "Real-Time".equalsIgnoreCase(mXAlert.getFrequency())))).forEach((mXAlert)
                ->
        {
            pendingAlerts.add(mXAlert);
        });
        alerts.clear();
        return pendingAlerts;
    }

    private static void processPendingAlerts()
    {
        ArrayList<MXAlert> pendingAlerts = getPendingAlerts();
        pendingAlerts.stream().filter((mXAlert) -> (!runningAlerts.containsKey(mXAlert.getAlertCode()))).forEach((mXAlert)
                ->
        {

            new ALWorker(mXAlert).start();
        });
        pendingAlerts.clear();
    }

    private static boolean isTimeRight(MXAlert mXAlert)
    {
        int time = Integer.parseInt(new SimpleDateFormat("HH").format(getdClient().getSystemDate()));
        switch (mXAlert.getRunTime())
        {
            case "Morning":
                return time >= 7 && time < 14;
            case "Afternoon":
                return time >= 14 && time < 19;
            case "Evening":
                return time >= 19 && time <= 23;
            case "Night":
                return time >= 0 && time < 7;
            default:
                return true;
        }
    }

    /**
     * @return the tDClient
     */
    public static DBClient getdClient()
    {
        return getBox().getdClient();
    }

    /**
     * @return the box
     */
    public static ATBox getBox()
    {
        return box;
    }
}
