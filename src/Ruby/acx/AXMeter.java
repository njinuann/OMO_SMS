/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2010 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Ruby.acx;

import Ruby.model.AXActivity;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.BasicBalloonTipPositioner;
import net.java.balloontip.positioners.RightBelowPositioner;
import net.java.balloontip.styles.EdgedBalloonStyle;

/**
 * ISOMsgPanel Swing based GUI to ISOMsg
 *
 * @author apr@cs.com.uy
 * @author Kris Leite <kleite at imcsoftware.com>
 * @see org.jpos.iso.ISOMsg
 */
public final class AXMeter extends JComponent implements Runnable
{
    final static int width = 180, height = 67;
    final static int MAX_VALUE = 1000;
    public final static int mass = height / 2;
    int continueScroll, yPoints[], xPoints[];

    String upperText = "", lowerText = "";
    int refreshPanel = 50, lastUpper, lastLower;
    private int position = 0;
    private String upperCounter = "", lowerCounter = "";

    Timer ti;
    private boolean showActivity = false;
    Graphics img;
    private String caption = "", status = "OPEN";

    Font fontBig, fontSmall, capFont;
    Image im, imb;
    private Thread repaintThread;
    private boolean connected = false, scroll = true;

    Color color = new Color(255, 255, 255);
    JTable activityTable = new JTable(new DefaultTableModel(new String[]
    {
        "Txn ID", "Channel", "Identity", "Name", "Activity"
    }, 0));

    private int sessions = 0;
    private BalloonTip balloonTip = null;
    private boolean showBalloon = false, updated = false;
    private ArrayList<AXActivity> activities = new ArrayList<>();

    public AXMeter(String caption, int position)
    {
        super();
        fontBig = new Font("Helvetica", Font.ITALIC, mass * 3 / 5);
        fontSmall = new Font("Helvetica", Font.PLAIN, 10);
        capFont = new Font("Helvetica", Font.BOLD, 10);

        yPoints = new int[width];
        xPoints = new int[width];
        for (int i = 0; i < width; i++)
        {
            xPoints[i] = i;
            yPoints[i] = mass;
        }

        setConnected(true, "ONLINE");
        setCaption(caption.toUpperCase());
        setPosition(position);
        upperCounter = "";
        if (isShowActivity())
        {
            prepareActivityTable();
        }
    }

    private void prepareActivityTable()
    {
        activityTable.getTableHeader().setFont(new Font(activityTable.getTableHeader().getFont().getName(), Font.BOLD, activityTable.getTableHeader().getFont().getSize()));
        activityTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        activityTable.setAutoCreateRowSorter(true);
        for (int i = 0; i < activityTable.getColumnCount(); i++)
        {
            final boolean firstColumn = i == 0;
            activityTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
                    if (firstColumn)
                    {
                        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
                        label.setBackground(new java.awt.Color(0, 204, 204));
                    }
                    label.setHorizontalAlignment((value instanceof Integer || value instanceof BigDecimal || value instanceof Number) ? RIGHT : LEFT);
                    return label;
                }
            });
        }
        activityTable.getColumnModel().getColumn(0).setMinWidth(100);
        activityTable.getColumnModel().getColumn(1).setMinWidth(125);
        activityTable.getColumnModel().getColumn(2).setMinWidth(150);
        activityTable.getColumnModel().getColumn(3).setMinWidth(150);
        activityTable.getColumnModel().getColumn(4).setMinWidth(150);

        JScrollPane sessionsScroller = new JScrollPane(activityTable);
        sessionsScroller.setPreferredSize(new Dimension(675, 100));
        BasicBalloonTipPositioner balloonTipPositioner = new RightBelowPositioner(15, ((height - 10) + (getPosition() * 110)));

        balloonTipPositioner.enableFixedAttachLocation(true);
        balloonTipPositioner.setAttachLocation(0.92f, 0.2f);

        EdgedBalloonStyle balloonStyle = new EdgedBalloonStyle(new Color(204, 255, 204), Color.CYAN);
        balloonTip = new BalloonTip(this, sessionsScroller, balloonStyle, balloonTipPositioner, null);
        balloonTip.setVisible(getSessions() > 0 && showBalloon);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                balloonTip.setVisible(getSessions() > 0 && !showBalloon);
                showBalloon = !showBalloon;
            }
        });
    }

    private void showActivity()
    {
        DefaultTableModel tableModel = (DefaultTableModel) activityTable.getModel();
        tableModel.setRowCount(0);
        if (getSessions() > 0)
        {
            getActivities().stream().forEach((activity)
                    -> 
                    {
                        tableModel.addRow(new String[]
                        {
                            activity.getTxnId(), activity.getChannel(), activity.getIdentity(), activity.getName(), activity.getActivity()
                        });
            });
            activityTable.updateUI();
        }
        balloonTip.setVisible(getSessions() > 0);
        showBalloon = getSessions() > 0;
    }

    public String capitalize(String text)
    {
        if (text != null)
        {
            try
            {
                StringBuilder builder = new StringBuilder();
                for (String word : text.toLowerCase().split("\\s"))
                {
                    builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
                }
                return builder.toString();
            }
            catch (Exception ex)
            {
                return text;
            }
        }
        return text;
    }

    public synchronized void start()
    {
        if (repaintThread == null)
        {
            repaintThread = new Thread(this, "ISOMeter");
            repaintThread.setPriority(Thread.MIN_PRIORITY);
            repaintThread.start();
        }
    }

    public void setValue(int val)
    {
        int y = mass - ((val % 1000) * height / 2000);
        yPoints[width - 1] = y;
        continueScroll = width;
        scroll();
    }

    public void setScroll(boolean scroll)
    {
        this.scroll = scroll;
    }

    public void setRefresh(int refreshPanel)
    {
        if (refreshPanel > 0)
        {
            this.refreshPanel = refreshPanel;
        }
    }

    public void setConnected(boolean connected, String status)
    {
        if (this.isConnected() != connected || !status.equals(getStatus()))
        {
            if (!scroll)
            {
                continueScroll = connected ? width : 1;
            }
            setStatus(connected ? "ONLINE" : status);
            this.setConnected(connected);
            repaint();
        }
    }

    public void setUpperCounter(int s)
    {
        upperCounter = s <= 0 ? "" : String.format("%03d", s);
        setSessions(s);
        if (isShowActivity() && balloonTip != null)
        {
            EventQueue.invokeLater(()
                    -> 
                    {
                        showActivity();
            });
        }
    }

    public void showSignal(AXActivity activity, String txn, boolean inwards)
    {
        if (inwards)
        {
            getActivities().add(activity);
        }
        else
        {
            getActivities().remove(activity);
        }
        setUpperCounter(getActivities().size());
        setValue(inwards ? -400 : 400, txn);
    }

    public void incrementUpperCounter(int s)
    {
        setUpperCounter(getSessions() + 1);
    }

    public void decrementUpperCounter(int s)
    {
        setUpperCounter(getSessions() - 1);
    }

    public void setLowerCounter(int s)
    {
        lowerCounter = (s <= 0 ? "" : String.format("%03d", s));
    }

    public void setValue(int val, String text)
    {
        setValue(val);

        if (val >= 0)
        {
            upperText = (text != null) ? text : "";
            lastUpper = 0;
        }
        else
        {
            lowerText = (text != null) ? text : "";
            lastLower = 0;
        }
    }

    @Override
    public void paint(Graphics g)
    {
        if (repaintThread == null)
        {
            start();
        }
        plot();
        g.drawImage(im, 0, 0, null);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }

    private void scroll()
    {
        System.arraycopy(yPoints, 1, yPoints, 0, width - 1);
        if (continueScroll > 0)
        {
            continueScroll--;
        }
        yPoints[width - 1] = mass;
    }

    public void plot()
    {
        if (im == null)
        {
            im = createImage(width, height);
            img = im.getGraphics();
            img.setColor(Color.black);

            img.fillRect(0, 0, width, height);
            img.clipRect(0, 0, width, height);
            plotGrid();

            /*
             * save a copy of the image
             */
            imb = createImage(width, height);
            Graphics imbCopy = imb.getGraphics();
            imbCopy.drawImage(im, 0, 0, this);
        }
        img.drawImage(imb, 0, 0, this);
        if (continueScroll > 0)
        {
            scroll();
        }

        plotText(upperText, lastUpper++, 3, mass - 10);
        plotText(lowerText, lastLower++, 3, height - 10);
        plotCounters(getUpperCounter(), getLowerCounter());

        img.setColor(isConnected() ? Color.green : Color.red);
        img.drawPolyline(xPoints, yPoints, width);
        plotCaption();
        plotStatus();
    }

    private void plotGrid()
    {
        img.setColor(Color.blue);
        for (int i = 0; i < width; i++)
        {
            if (i % 20 == 0)
            {
                img.drawLine(i, 0, i, height);
            }
        }
        for (int i = -1000; i < 1000; i += 200)
        {
            int y = mass + (i * height / 2000);
            img.drawLine(0, y, width, y);
        }
    }

    private void plotText(String t, int l, int x, int y)
    {
        if (t != null && continueScroll > 0)
        {
            img.setColor(Color.lightGray);
            img.setFont(fontBig);
            img.drawString(t, x, y);
        }
    }

    public void plotCaption()
    {
        img.setFont(capFont);
        int capLen = Math.round(getCaption().length() * 6.4f);
        int x = (width - capLen) / 2;
        img.setColor(isConnected() ? Color.green : Color.red);
        img.drawString(getCaption(), x, 8);
    }

    public void plotStatus()
    {
        img.setFont(capFont);
        int capLen = Math.round(getStatus().length() * 6.4f);
        int x = (width - capLen) / 2;
        img.setColor(isConnected() ? Color.green : Color.red);
        img.drawString(getStatus(), x, height - 1);
    }

    private void plotCounters(String p, String n)
    {
        img.setColor(Color.lightGray);
        img.setFont(fontSmall);
        img.drawString(p, width - 25, 13);
        img.drawString(n, width - 25, height - 3);
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (continueScroll > 0 || !upperText.equals("") || !lowerText.equals("") || isUpdated())
            {
                setUpdated(false);
                try
                {
                    repaint();
                }
                catch (Exception ex)
                {
                    ex = null;
                }
                scroll();
            }
            try
            {
                Thread.sleep(refreshPanel);
            }
            catch (InterruptedException e)
            {
                // OK to ignore
            }
        }
    }

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    /**
     * @return the caption
     */
    public String getCaption()
    {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status.toUpperCase();
    }

    /**
     * @return the sessions
     */
    public int getSessions()
    {
        return sessions;
    }

    /**
     * @param sessions the sessions to set
     */
    public void setSessions(int sessions)
    {
        this.sessions = sessions;
    }

    /**
     * @return the connected
     */
    public boolean isConnected()
    {
        return connected;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected)
    {
        this.connected = connected;
        setUpdated(true);
    }

    /**
     * @return the updated
     */
    public boolean isUpdated()
    {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }

    /**
     * @return the upperCounter
     */
    public String getUpperCounter()
    {
        return upperCounter;
    }

    /**
     * @return the lowerCounter
     */
    public String getLowerCounter()
    {
        return lowerCounter;
    }

    /**
     * @return the activities
     */
    public ArrayList<AXActivity> getActivities()
    {
        return activities;
    }

    /**
     * @param activities the activities to set
     */
    public void setActivities(ArrayList<AXActivity> activities)
    {
        this.activities = activities;
    }

    /**
     * @return the showActivity
     */
    public boolean isShowActivity()
    {
        return showActivity;
    }

    /**
     * @param showActivity the showActivity to set
     */
    public void setShowActivity(boolean showActivity)
    {
        this.showActivity = showActivity;
    }

    /**
     * @return the position
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position)
    {
        this.position = position;
    }
}
