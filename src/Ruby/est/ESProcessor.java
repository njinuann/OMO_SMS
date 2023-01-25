/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.est;

import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.ATBox;
import Ruby.acx.AXWorker;
import Ruby.model.ESTask;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pecherk
 */
public class ESProcessor
{
    private static String mailBody = null;
    private static final ATBox box = new ATBox(APMain.estLog);
    public static final ConcurrentHashMap<String, Long> wip = new ConcurrentHashMap<>();

    public static void process()
    {
        try
        {
            if (!APMain.exit && !ESController.isSuspended())
            {
                if (getWorker().isBlank(mailBody))
                {
                    mailBody = readMailBody();
                }
                processPendingTasks();
            }
        }
        catch (Throwable ex)
        {
            APMain.estLog.logEvent(ex);
        }
    }

    private static void processPendingTasks()
    {
        Date currentDate = getClient().getSystemDate();
        getClient().queryTasks().values().stream().map((eSTask)
                -> 
                {
                    if (currentDate.after(eSTask.getExpiryDate()))
                    {
                        eSTask.setStatus("E");
                        getClient().upsertTask(eSTask);
                    }
                    return eSTask;
        }).filter((eSTask) -> ("A".equals(eSTask.getStatus()) && isTimeRight(eSTask) && (currentDate.after(eSTask.getNextDate()) || currentDate.equals(eSTask.getNextDate())))).forEach((eSTask)
                -> 
                {
                    Long time = wip.getOrDefault(eSTask.getCode(), 1L);
                   if (time != 0L && System.currentTimeMillis() - time > 300000)
                    {
                        new ESWorker(eSTask, mailBody).start();
                    }
        });
    }

    private static boolean isTimeRight(ESTask eSTask)
    {
        int time = Integer.parseInt(new SimpleDateFormat("HH").format(getClient().getSystemDate()));
        switch (eSTask.getRunTime())
        {
            case "M":
                return time >= 7 && time < 14;
            case "F":
                return time >= 14 && time < 19;
            case "E":
                return time >= 19 && time <= 23;
            case "N":
                return time >= 0 && time < 7;
            default:
                return true;
        }
    }

    private static String readMailBody()
    {
        try
        {
            return getBox().getxFile().readTextFile("est/conf/email.html");
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
        return null;
    }

    private static AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the tDClient
     */
    public static DBClient getClient()
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
