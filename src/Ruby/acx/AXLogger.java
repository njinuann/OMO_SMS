/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class AXLogger extends APLog
{
    private String indent = "\t";
    private final Long maxSize = 10485760L;
    private final AXFile brFile = new AXFile();
    public String realm = "acx", path = "acx" + File.separator + "logs";

    public AXLogger(String realm, String path)
    {
        this.realm = realm;
        this.path = path;
    }

    @Override
    public void logDebug(Object event)
    {
        if (APController.isDebugMode())
        {
            logEvent(null, null, null, event);
        }
    }

    @Override
    public void logDebug(String key, Object event)
    {
        if (APController.isDebugMode())
        {
            logEvent(key, null, null, event);
        }
    }

    @Override
    public void logEvent(Object event)
    {
        logEvent(null, null, null, event);
    }

    @Override
    public void logEvent(String input, Object event)
    {
        logEvent(null, input, null, event);
    }

    public void logEvent(String message, Object input, Object event)
    {
        logEvent(null, input, message, event);
    }

    public void logEvent(String key, Object input, String message, Object event)
    {
        try
        {
            StringBuilder logEvent = new StringBuilder("<event realm=\"" + (key != null ? key.toLowerCase() : realm) + "\" datetime=\"" + new Date() + "\">");
            if (event instanceof Throwable)
            {
                logEvent.append("\r\n").append(getIndent()).append("<error>");
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<class>").append(((Throwable) event).getClass().getSimpleName()).append("</class>");
                if (input != null)
                {
                    logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<input>").append(String.valueOf(input)).append("</input>");
                }
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<message>").append(message == null ? "" : message).append("[ ").append(cleanText(((Throwable) event).getMessage())).append(" ]").append("</message>");
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("<stacktrace>");
                for (StackTraceElement s : ((Throwable) event).getStackTrace())
                {
                    logEvent.append("\r\n").append(getIndent()).append(getIndent()).append(getIndent()).append("at ").append(s.toString());
                }
                logEvent.append("\r\n").append(getIndent()).append(getIndent()).append("</stacktrace>");
                logEvent.append("\r\n").append(getIndent()).append("</error>");
                logEvent.append("\r\n").append("</event>\r\n");
            }
            else if (String.valueOf(event).trim().startsWith("<") && String.valueOf(event).trim().endsWith(">"))
            {
                logEvent.append("\r\n").append(indentAllLines(String.valueOf(event))).append("\r\n");
                logEvent.append("</event>\r\n");
            }
            else
            {
                logEvent.append("\r\n").append(getIndent()).append("<info>").append(String.valueOf(event)).append("</info>");
                logEvent.append("\r\n").append("</event>\r\n");
            }
            new Thread(()
                    -> 
                    {
                        writeToLog(logEvent.toString(), event instanceof Throwable);
            }).start();
            if (event instanceof AXCaller)
            {
                ((AXCaller) event).getCalls().clear();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String indentAllLines(String text)
    {
        String line = "", buffer = "";
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
        {
            while (line != null)
            {
                buffer += getIndent() + line + "\r\n";
                line = bis.readLine();
            }
        }
        catch (IOException ex)
        {
            return buffer;
        }

        return getIndent() + buffer.trim();
    }

    private String cleanText(String text)
    {
        String line, buffer = "";
        InputStream is = new ByteArrayInputStream(String.valueOf(text).getBytes());
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(is, "UTF-8")))
        {
            while ((line = bis.readLine()) != null)
            {
                buffer += line;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return buffer;
    }

    private void archiveOldLog(String lastDate, File logs, File logFile)
    {
        rotateExistingLogs(lastDate, logs);
        try
        {
            brFile.appendToFile(logFile, "</logger>");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        File oldLog = new File(logs, "events-" + lastDate + "-0.log");
        logFile.renameTo(oldLog);
        try
        {
            brFile.compressFileToGzip(oldLog);
            brFile.deleteFile(oldLog);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        purgeOldLogs();
    }

    private void rotateExistingLogs(String lastDate, File logs)
    {
        int count = 99;
        while (count >= 0)
        {
            try
            {
                File prev = new File(logs, "events-" + lastDate + "-" + count + ".log.gz");
                if (prev.exists())
                {
                    if (count >= 99)
                    {
                        brFile.deleteFile(prev);
                    }
                    else
                    {
                        prev.renameTo(new File(logs, "events-" + lastDate + "-" + (count + 1) + ".log.gz"));
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            count--;
        }
    }

    private File getNewLog(File logsDir, File logFile)
    {
        logFile = new File(logsDir, "events.log");
        try
        {
            logFile.createNewFile();
            brFile.appendToFile(logFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            brFile.appendToFile(logFile, "<logger class=\"" + AXLogger.class.getName() + "\" datetime=\"" + new Date() + "\">");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return logFile;
    }

    private synchronized void writeToLog(String logEvent, boolean error)
    {
        try
        {
            (error ? System.err : System.out).print(logEvent);
            brFile.appendToFile(getLog(), logEvent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private File getLog()
    {
        File logs = new File(path);
        if (!logs.exists())
        {
            logs.mkdirs();
        }
        File logFile = new File(path, "events.log");
        if (logFile.exists())
        {
            String lastDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(logFile.lastModified()));
            if (!lastDate.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) || logFile.length() >= maxSize)
            {
                archiveOldLog(lastDate, logs, logFile);
                return getNewLog(logs, logFile);
            }
            return logFile;
        }
        return getNewLog(logs, logFile);
    }

    private void purgeOldLogs()
    {

        File logs = new File(path);
        for (File log : logs.listFiles())
        {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date(log.lastModified()));
            if (Calendar.getInstance().get(Calendar.MONTH) - c1.get(Calendar.MONTH) >= 0 && Calendar.getInstance().get(Calendar.YEAR) - c1.get(Calendar.YEAR) >= APController.yearsToKeepLogs)
            {
                try
                {
                    brFile.deleteFile(log);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void logError(String message, Throwable ex)
    {
        logEvent(message, null, ex);
    }

    @Override
    public void logError(Throwable ex)
    {
        logEvent(ex);
    }

    @Override
    public void setCall(String callRef, Object callObject)
    {
        setCall(callRef, callObject, false);
    }

    @Override
    public void setCall(String callRef, Object callObject, boolean replace)
    {
        logEvent("<" + callRef + ">" + convertToString(callObject) + "</" + callRef + ">");
    }

    @Override
    public void setCall(String callRef, Object callObject, String prefix)
    {
        logEvent("<" + callRef + ">" + checkBlank(prefix, "") + convertToString(callObject) + "</" + callRef + ">");
    }

    /**
     * @return the indent
     */
    public String getIndent()
    {
        return indent;
    }

    /**
     * @param indent the indent to set
     */
    public void setIndent(String indent)
    {
        this.indent = indent;
    }
}
