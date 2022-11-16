/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APMain;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Pecherk
 */
public class ACStream extends ByteArrayOutputStream
{

    private Color color = Color.BLACK;

    public ACStream(Color color)
    {
        setColor(color);
    }

    @Override
    public void write(byte[] b)
    {
        write(new String(b));
    }

    @Override
    public void write(int b)
    {
        write(String.valueOf(b));
    }

    @Override
    public void write(byte[] b, int off, int len)
    {
        write(new String(b, off, len));
    }

    public void write(String message)
    {
        try
        {
            if (message.length() > 0)
            {
                EventQueue.invokeLater(()
                        -> 
                        {
                            APMain.console.append(message, getColor());
                });
            }
        }
        catch (Exception ex)
        {
            if (APMain.acxLog != null)
            {
                APMain.acxLog.logEvent(ex);
            }
        }
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public final void setColor(Color color)
    {
        this.color = color;
    }
}
