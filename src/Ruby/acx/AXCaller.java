/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import Ruby.model.LGItem;
import com.neptunesoftware.supernova.ws.common.XAPIException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Pecherk
 */
public final class AXCaller extends APLog implements Serializable
{
    private String tag = "exec";
    private final String indent = "\t";
    private final HashMap<String, LGItem> calls = new HashMap<>();

    public AXCaller()
    {
        setTag(tag);
    }

    public AXCaller(String tag)
    {
        setTag(tag);
    }

    @Override
    public void logEvent(Object event)
    {
        logEvent(null, event);
    }

    @Override
    public void logDebug(Object event)
    {
        if (APController.isDebugMode())
        {
            setCall("debug", event);
        }
    }

    @Override
    public void logEvent(String input, Object event)
    {
        if (!isBlank(input))
        {
            setCall("input", event);
        }
        if (event instanceof Throwable)
        {
            logError((Throwable) event);
        }
        else
        {
            setCall("info", event);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder("<" + getTag() + ">");
        for (String key : sortArray(getCalls().keySet().toArray(new String[getCalls().size()]), true))
        {
            LGItem logItem = getCalls().get(key);
            Object payload = logItem.getPayload();
            if (payload instanceof Throwable)
            {
                if (payload instanceof XAPIException)
                {
                    buffer.append("\r\n").append(getIndent()).append("<exception>").append(convertToString(payload)).append("</exception>");
                }
                else
                {
                    buffer.append("\r\n").append(getIndent()).append("<exception>");
                    buffer.append("\r\n").append(getIndent()).append(getIndent()).append("<class>").append(((Throwable) payload).getClass().getSimpleName()).append("</class>");
                    String emsg = (((Throwable) payload).getMessage() == null) ? "" : ((Throwable) payload).getMessage();

                    if (emsg.contains("\r\n"))
                    {
                        buffer.append("\r\n").append(getIndent()).append(getIndent()).append("<message>");
                        buffer.append("\r\n").append(getIndent()).append(getIndent()).append(getIndent()).append(((Throwable) payload).getMessage().replaceAll("\r\n", "\r\n" + getIndent() + getIndent() + getIndent()));
                        buffer.append("\r\n").append(getIndent()).append(getIndent()).append("</message>");
                    }
                    else
                    {
                        buffer.append("\r\n").append(getIndent()).append(getIndent()).append("<message>").append(((Throwable) payload).getMessage()).append("</message>");
                    }
                    buffer.append("\r\n").append(getIndent()).append(getIndent()).append("<stacktrace>");
                    for (StackTraceElement s : ((Throwable) payload).getStackTrace())
                    {
                        buffer.append("\r\n").append(getIndent()).append(getIndent()).append(getIndent()).append("at ").append(s.toString());
                    }
                    buffer.append("\r\n").append(getIndent()).append(getIndent()).append("</stacktrace>");
                    buffer.append("\r\n").append(getIndent()).append("</exception>");
                }
            }
            else
            {
                buffer.append("\r\n").append(getIndent()).append("<").append(key.replaceAll("\\d", "")).append(">").append(checkBlank(logItem.getPrefix(), "")).append(cleanText(convertToString(payload))).append("</").append(key.replaceAll("\\d", "")).append(">");
            }
        }

        buffer.append("\r\n").append("</").append(getTag()).append(">");
        return buffer.toString();
    }

    public String indentAllLines(String text, String indent)
    {
        String line = "", buffer = "";
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
        {
            while (line != null)
            {
                buffer += indent + line + "\r\n";
                line = bis.readLine();
            }
            return indent + buffer.trim();
        }
        catch (Exception ex)
        {
            return indent + buffer.trim();
        }
    }

    public String cleanText(String text)
    {
        String line;
        StringBuilder buffer = new StringBuilder();
        if (!isBlank(text))
        {
            try (BufferedReader bis = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes()))))
            {
                while ((line = bis.readLine()) != null)
                {
                    buffer.append(line.trim());
                }
            }
            catch (Exception ex)
            {
                ex = null;
            }
        }
        return buffer.toString().replaceAll(">\\s+<", "><");
    }

    public <T> T[] sortArray(T[] array, boolean ascending)
    {
        Arrays.sort(array, ascending ? ((Comparator<T>) (T o1, T o2) -> ((Comparable) o1).compareTo(o2)) : ((Comparator<T>) (T o1, T o2) -> ((Comparable) o2).compareTo(o1)));
        return array;
    }

    private boolean callExists(String callKey)
    {
        return getCalls().keySet().stream().anyMatch((key) -> (callKey.equalsIgnoreCase(key.replaceAll("\\d", ""))));
    }

    public void setReference(String reference)
    {
        setCall("reference", checkBlank(reference), false);
    }

    public void setTxnType(String txnType)
    {
        if (!callExists("txntype"))
        {
            setCall("txntype", checkBlank(txnType), false);
        }
    }

    public void setAlertCode(String alertCode)
    {
        setCall("alertcode", checkBlank(alertCode));
    }

    public void setScheme(String scheme)
    {
        setCall("scheme", checkBlank(scheme));
    }

    public void setAccount(String account)
    {
        setCall("account", checkBlank(account), false);
    }

    public void setNarration(String narration)
    {
        setCall("narration", checkBlank(narration), false);
    }

    public void setOurTerminal(boolean ourTerminal)
    {
        setCall("ourterminal", yesNo(ourTerminal));
    }

    public void setOurCustomer(boolean ourCustomer)
    {
        setCall("ourcustomer", yesNo(ourCustomer));
    }

    public void setReversal(boolean isReversal)
    {
        setCall("isreversal", yesNo(isReversal));
    }

    public void setRequest(Object request)
    {
        setCall("request", request, false);
    }

    public void setResponse(Object response)
    {
        setCall("response", response);
    }

    public void setResult(String result)
    {
        setCall("result", checkBlank(result));
    }

    public void setDuration(String duration)
    {
        setCall("duration", checkBlank(duration));
    }

    /**
     * @return the indent
     */
    public String getIndent()
    {
        return indent;
    }

    /**
     * @return the calls
     */
    public HashMap<String, LGItem> getCalls()
    {
        return calls;
    }

    @Override
    public void logError(Throwable ex)
    {
        setCall("error", ex);
    }

    @Override
    public void setCall(String callRef, Object callObject)
    {
        setCall(callRef, callObject, false);
    }

    @Override
    public void setCall(String callRef, Object callObject, String prefix)
    {
        setCall(callRef, callObject, false, prefix);
    }

    @Override
    public void setCall(String callRef, Object callObject, boolean replace)
    {
        setCall(callRef, callObject, replace, null);
    }

    public void setCall(String callRef, Object callObject, boolean replace, String prefix)
    {
        getCalls().put(String.format("%02d", (replace ? getCalls().size() - 1 : getCalls().size())) + callRef.toLowerCase(), new LGItem(prefix, callObject));
    }
     
     @Override
    public void setCall(String callRef, Object callObject, long position)
    {
        setCall(callRef, callObject, false, position, null);
    }
     
    public void setCall(String callRef, Object callObject, boolean replace, long position, String prefix)
    {
        if (!isBlank(callObject))
        {
            getCalls().put(String.format("%02d", (position <= 0 ? (replace ? getCalls().size() - 1 : getCalls().size()) : position)) + (callRef = callRef.toLowerCase()), new LGItem(callRef,  callObject));
        }
    }

    public void dump(PrintStream p, String indent)
    {
        p.print(indentAllLines(toString(), indent));
    }

    /**
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    
}
