/*
 * To change this license header, choose License Headers in Project Properties. 
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package Ruby;

import static Ruby.APController.getWorker;
import Ruby.acx.AXLogger;
import Ruby.acx.ACPane;
import Ruby.acx.ACPanel;
import Ruby.acx.ACStream;
import Ruby.acx.AXAppender;
import Ruby.acx.CSWorker;
import Ruby.est.ESController;
import Ruby.est.ESProcessor;
import Ruby.sms.ALController;
import Ruby.sms.ALProcessor;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.swing.JDialog;
import javax.swing.UIManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Pecherk
 */
public class APMain
{

    public static APFrame apFrame;
    public static AXLogger acxLog;
    public static AXLogger smsLog;
    public static AXLogger estLog;

    public static boolean exit = false;
    public static Color infoColor = new Color(0, 0, 128);
    public static Color errorColor = new Color(192, 0, 0);

    public static final ACPane console = new ACPane();
    public static final JDialog consoleDialog = new JDialog();
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        setLookAndFeel();
        new APMain().execute();
    }

    private void execute()
    {
        initialize();
        displayWindow();
        while (apFrame == null || !apFrame.isVisible())
        {
            pauseThread(2000);
        }
        startTasks();
    }

    public void initialize()
    {
        setConsole();
        setLoggers();
        setControllers();
        setAppenders();
    }

    private static void setLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(new PlasticLookAndFeel());
        }
        catch (Exception ex)
        {
            ex = null;
        }
    }

    public void setConsole()
    {
        ACPanel panel = new ACPanel();
        consoleDialog.setTitle(APController.application);
        consoleDialog.setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Ruby/ximg/icon.png")));

        javax.swing.GroupLayout consoleLayout = new javax.swing.GroupLayout(consoleDialog.getContentPane());
        consoleDialog.getContentPane().setLayout(consoleLayout);

        consoleLayout.setHorizontalGroup(
                consoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        consoleLayout.setVerticalGroup(
                consoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        consoleDialog.setSize(500, 500);
        consoleDialog.setUndecorated(true);
        consoleDialog.setLocationRelativeTo(null);

        System.setOut(new PrintStream(new ACStream(infoColor), true));
        System.setErr(new PrintStream(new ACStream(errorColor), true));
    }

    private void setLoggers()
    {
        acxLog = new AXLogger("acx", "acx" + File.separator + "logs");
        smsLog = new AXLogger("sms", "sms" + File.separator + "logs");
        estLog = new AXLogger("est", "est" + File.separator + "logs");
        acxLog.logEvent("==========<" + APController.application + ">==========");
    }

    private void displayWindow()
    {
        EventQueue.invokeLater(()
                ->
        {
            apFrame = new APFrame();
            consoleDialog.setVisible(false);
            for (Window window : APFrame.getWindows())
            {
                getWorker().prepareAllScrollers(window);
            }
            apFrame.setLocationRelativeTo(null);
            apFrame.setVisible(true);
            consoleDialog.dispose();
        });
    }

    public static void shutdown(boolean restart)
    {
        exit = true;
        ALController.shutdown();
        stopTasks();
        if (restart)
        {
            try
            {
                Runtime.getRuntime().exec("Ruby_SMS.exe");
            }
            catch (Exception ex)
            {
                acxLog.logEvent(ex);
            }
        }
        System.exit(0);
    }

    private void pauseThread(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception ie)
        {
            acxLog.logEvent(ie);
        }
    }

    private void setControllers()
    {
        APController.initialize();
        ALController.initialize();
        ESController.initialize();
    }

    public static void startTasks()
    {
        scheduler.scheduleWithFixedDelay(ALProcessor::process, 0, 10, SECONDS);
        scheduler.scheduleAtFixedRate(ESProcessor::process, 0, 10, SECONDS);
        
        scheduler.scheduleAtFixedRate(CSWorker::process, 0, 1, DAYS);
        scheduler.scheduleWithFixedDelay(ALController::expireOldAlerts, 0, 1, DAYS);
        
        scheduler.scheduleWithFixedDelay(APController::deleteXapiHistory, 0, 1, DAYS);
        scheduler.scheduleAtFixedRate(ALController::saveTreeLog, 2, 2, HOURS);
        
        scheduler.scheduleAtFixedRate(APController::purgeTreeItems, 1, 1, HOURS);
        scheduler.scheduleAtFixedRate(APController::refresh, 1, 1, HOURS);
        
        scheduler.scheduleAtFixedRate(APController::gc, 1, 1, HOURS);
    }

    public static void stopTasks()
    {
        try
        {
            scheduler.shutdownNow();
        }
        catch (Exception ex)
        {
            acxLog.logEvent(ex);
        }
    }

    private void setAppenders()
    {
        Logger.getRootLogger().addAppender(new AXAppender(APController.isDebugMode() ? Level.DEBUG : Level.INFO));
    }

    public static Image getIconImage()
    {
        return Toolkit.getDefaultToolkit().createImage(APMain.class.getResource("/Ruby/ximg/icon.png"));
    }
}
