/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import Ruby.APMain;
import Ruby.DBClient;
import Ruby.model.AXRequest;
import Ruby.model.AXResponse;
import Ruby.model.GPSplit;
import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class CSWorker
{
    public static boolean wait = false;
    private static ATBox box = new ATBox(new AXCaller());

    public static void process()
    {
        try
        {
            processChargeSplits();
            getBox().dispose();
        }
        catch (Throwable ex)
        {
            APMain.acxLog.logEvent(ex);
        }
    }

    private static void processChargeSplits()
    {
        ArrayList<GPSplit> pendingSplits = getdClient().queryGroupSplits();
        try
        {
            for (GPSplit split : pendingSplits)
            {
                wait = true;
                AXLogger log = APMain.smsLog;
                try
                {
                    getBox().setLog(new AXCaller("split"));
                    Long startTime = System.currentTimeMillis();

                    try
                    {
                        AXRequest aXRequest = new AXRequest();
                        aXRequest.setReference(APController.generateKey());
                        aXRequest.setMsisdn(split.getChannel());

                        aXRequest.setNarration(split.getDescription());
                        aXRequest.setAmount(split.getAmount());
                        getCaller().setReference(aXRequest.getReference());

                        getCaller().setAccount(split.getDebitAccount());
                        getCaller().setNarration(split.getDescription());
                        getCaller().setReversal(getWorker().isYes(split.getReversal()));

                        getCaller().setRequest(aXRequest);
                        if (getBox().getXclient().postSplit(aXRequest, split))
                        {
                            getCaller().setResponse(new AXResponse(AXResult.SUCCESS));
                            getdClient().updateGroupSplit(split, "S");
                        }
                        else
                        {
                            getCaller().setResponse(new AXResponse(AXResult.FAILED));
                        }
                    }
                    catch (Exception ex)
                    {
                        getCaller().logError(ex);
                    }
                    getCaller().setCall("duration", (System.currentTimeMillis() - startTime) + " Ms");
                    log.logEvent(getCaller());
                }
                catch (Exception ex)
                {
                    log.logEvent(ex);
                }
                wait = false;
                if (APMain.exit)
                {
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            APMain.acxLog.logEvent(ex);
        }
        pendingSplits.clear();
    }

    private static AXCaller getCaller()
    {
        return getBox().getLog(AXCaller.class);
    }

    private static DBClient getdClient()
    {
        return getBox().getdClient();
    }

    private static AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the box
     */
    public static ATBox getBox()
    {
        return box;
    }
}
