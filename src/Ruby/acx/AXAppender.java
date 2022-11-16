/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APMain;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Pecherk
 */
public class AXAppender extends AppenderSkeleton
{
    public AXAppender(Priority priority)
    {
        setThreshold(priority);
    }

    @Override
    protected void append(LoggingEvent event)
    {
        if (event.getLevel().isGreaterOrEqual(Level.INFO))
        {
            APMain.acxLog.logEvent(event.getMessage());
        }
        else
        {
            APMain.acxLog.logDebug(event.getMessage());
        }
    }

    @Override
    public boolean requiresLayout()
    {
        return false;
    }

    @Override
    public void close()
    {
    }
}
