/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.beans.ConstructorProperties;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicHTML;

/**
 *
 * @author Pecherk
 */
@SuppressWarnings("serial")
public final class CTBorder extends AbstractBorder
{
    protected Border border;
    protected int titlePosition;
    protected int justification;
    private JComponent header = new JLabel("Header");
    private Font titleFont;
    private Color titleColor;

    /**
     * Use the default vertical orientation for the title text.
     */
    static public final int DEFAULT_POSITION = 0;
    /**
     * Position the title above the border's top line.
     */
    static public final int ABOVE_TOP = 1;
    /**
     * Position the title in the middle of the border's top line.
     */
    static public final int TOP = 2;
    /**
     * Position the title below the border's top line.
     */
    static public final int BELOW_TOP = 3;
    /**
     * Position the title above the border's bottom line.
     */
    static public final int ABOVE_BOTTOM = 4;
    /**
     * Position the title in the middle of the border's bottom line.
     */
    static public final int BOTTOM = 5;
    /**
     * Position the title below the border's bottom line.
     */
    static public final int BELOW_BOTTOM = 6;

    /**
     * Use the default justification for the title text.
     */
    static public final int DEFAULT_JUSTIFICATION = 0;
    /**
     * Position title text at the left side of the border line.
     */
    static public final int LEFT = 1;
    /**
     * Position title text in the center of the border line.
     */
    static public final int CENTER = 2;
    /**
     * Position title text at the right side of the border line.
     */
    static public final int RIGHT = 3;
    /**
     * Position title text at the left side of the border line for left to right
     * orientation, at the right side of the border line for right to left
     * orientation.
     */
    static public final int LEADING = 4;
    /**
     * Position title text at the right side of the border line for left to
     * right orientation, at the left side of the border line for right to left
     * orientation.
     */
    static public final int TRAILING = 5;

    // Space between the border and the component's edge
    static protected final int EDGE_SPACING = 2;

    // Space between the border and text
    static protected final int TEXT_SPACING = 2;

    // Horizontal inset of text that is left or right justified
    static protected final int TEXT_INSET_H = 5;

    /**
     * Creates a CTBorder instance.
     *
     */
    public CTBorder()
    {
        this(null, null, LEADING, DEFAULT_POSITION);
    }

    /**
     * Creates a CTBorder instance.
     *
     * @param header the header
     */
    public CTBorder(JComponent header)
    {
        this(header, null, LEADING, DEFAULT_POSITION);
    }

    /**
     * Creates a CTBorder instance with the specified border and an empty title.
     *
     * @param header the header
     * @param border the border
     */
    public CTBorder(JComponent header, Border border)
    {
        this(header, border, LEADING, DEFAULT_POSITION);
    }

    /**
     * Creates a CTBorder instance with the specified border and an empty title.
     *
     * @param header the header
     * @param justification
     * @param titlePosition
     */
    public CTBorder(JComponent header, int justification, int titlePosition)
    {
        this(header, null, justification, titlePosition);
    }

    /**
     * Creates a CTBorder instance with the specified border, title,
     * title-justification, title-position, title-font, and title-color.
     *
     * @param header the header
     * @param border the border
     * @param justification the justification for the title
     * @param titlePosition the position for the title
     */
    @ConstructorProperties(
            {
                "header", "border", "justification", "titlePosition"
            })
    public CTBorder(JComponent header, Border border, int justification, int titlePosition)
    {
        setHeader(header);
        setBorder(border);
        setJustification(justification);
        setTitlePosition(titlePosition);
        getHeader().putClientProperty(BasicHTML.propertyKey, null);
    }

    /**
     * Paints the border for the specified component with the specified position
     * and size.
     *
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        Border border = getBorder();
        JComponent header = getHeader(c);
        int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;

        Dimension size = header.getPreferredSize();
        Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));
        int borderX = x + edge;

        int borderY = y + edge;
        int borderW = width - edge - edge;
        int borderH = height - edge - edge;

        int headerY = y;
        int headerH = size.height;
        int position = getPosition();

        switch (position)
        {
            case ABOVE_TOP:
                insets.left = 0;
                insets.right = 0;
                borderY += headerH - edge;
                borderH -= headerH - edge;
                break;
            case TOP:
                insets.top = edge + insets.top / 2 - headerH / 2;
                if (insets.top < edge)
                {
                    borderY -= insets.top;
                    borderH += insets.top;
                }
                else
                {
                    headerY += insets.top;
                }
                break;
            case BELOW_TOP:
                headerY += insets.top + edge;
                break;
            case ABOVE_BOTTOM:
                headerY += height - headerH - insets.bottom - edge;
                break;
            case BOTTOM:
                headerY += height - headerH;
                insets.bottom = edge + (insets.bottom - headerH) / 2;
                if (insets.bottom < edge)
                {
                    borderH += insets.bottom;
                }
                else
                {
                    headerY -= insets.bottom;
                }
                break;
            case BELOW_BOTTOM:
                insets.left = 0;
                insets.right = 0;
                headerY += height - headerH;
                borderH -= headerH - edge;
                break;
        }
        insets.left += edge + TEXT_INSET_H;
        insets.right += edge + TEXT_INSET_H;

        int headerX = x;
        int headerW = width - insets.left - insets.right;
        if (headerW > size.width)
        {
            headerW = size.width;
        }
        switch (CTBorder.this.getJustification(c))
        {
            case LEFT:
                headerX += insets.left;
                break;
            case RIGHT:
                headerX += width - insets.right - headerW;
                break;
            case CENTER:
                headerX += (width - headerW) / 2;
                break;
        }

        if (border != null)
        {
            if ((position != TOP) && (position != BOTTOM))
            {
                border.paintBorder(c, g, borderX, borderY, borderW, borderH);
            }
            else
            {
                Graphics g2 = g.create();
                if (g2 instanceof Graphics2D)
                {
                    Graphics2D g2d = (Graphics2D) g2;
                    Path2D path = new Path2D.Float();
                    path.append(new Rectangle(borderX, borderY, borderW, headerY - borderY), false);

                    path.append(new Rectangle(borderX, headerY, headerX - borderX - TEXT_SPACING, headerH), false);
                    path.append(new Rectangle(headerX + headerW + TEXT_SPACING, headerY, borderX - headerX + borderW - headerW - TEXT_SPACING, headerH), false);
                    path.append(new Rectangle(borderX, headerY + headerH, borderW, borderY - headerY + borderH - headerH), false);
                    g2d.clip(path);
                }
                border.paintBorder(c, g2, borderX, borderY, borderW, borderH);
                g2.dispose();
            }
        }

        header.setSize(headerW, headerH);
        header.setBounds(headerX, headerY, headerW, headerH);

        g.translate(headerX, headerY);
        header.paint(g);
        g.translate(-headerX, -headerY);
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     *
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return insets
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        Border border = getBorder();
        insets = getBorderInsets(border, c, insets);
        Dimension size = getHeader(c).getPreferredSize();
        int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;

        switch (getPosition())
        {
            case ABOVE_TOP:
                insets.top += size.height - edge;
                break;
            case TOP:
            {
                if (insets.top < size.height)
                {
                    insets.top = size.height - edge;
                }
                break;
            }
            case BELOW_TOP:
                insets.top += size.height;
                break;
            case ABOVE_BOTTOM:
                insets.bottom += size.height;
                break;
            case BOTTOM:
            {
                if (insets.bottom < size.height)
                {
                    insets.bottom = size.height - edge;
                }
                break;
            }
            case BELOW_BOTTOM:
                insets.bottom += size.height - edge;
                break;
        }
        insets.top += edge + TEXT_SPACING;
        insets.left += edge + TEXT_SPACING;
        insets.right += edge + TEXT_SPACING;
        insets.bottom += edge + TEXT_SPACING;
        return insets;
    }

    /**
     * Sets the title-position of the titled border.
     *
     * @param titlePosition the position for the border
     */
    public void setTitlePosition(int titlePosition)
    {
        switch (titlePosition)
        {
            case ABOVE_TOP:
            case TOP:
            case BELOW_TOP:
            case ABOVE_BOTTOM:
            case BOTTOM:
            case BELOW_BOTTOM:
            case DEFAULT_POSITION:
                this.titlePosition = titlePosition;
                break;
            default:
                throw new IllegalArgumentException(titlePosition + " is not a valid title position.");
        }
    }

    /**
     * Sets the title-justification of the titled border.
     *
     * @param justification the justification for the border
     */
    public void setJustification(int justification)
    {
        switch (justification)
        {
            case DEFAULT_JUSTIFICATION:
            case LEFT:
            case CENTER:
            case RIGHT:
            case LEADING:
            case TRAILING:
                this.justification = justification;
                break;
            default:
                throw new IllegalArgumentException(justification + " is not a valid title justification.");
        }
    }

    /**
     * Returns the minimum dimensions this border requires in order to fully
     * display the border and title.
     *
     * @param c the component where this border will be drawn
     * @return the {@code Dimension} object
     */
    public Dimension getMinimumSize(Component c)
    {
        Insets insets = getBorderInsets(c);
        Dimension minSize = new Dimension(insets.right + insets.left, insets.top + insets.bottom);
        Dimension size = getHeader(c).getPreferredSize();

        int position = getPosition();
        if ((position != ABOVE_TOP) && (position != BELOW_BOTTOM))
        {
            minSize.width += size.width;
        }
        else if (minSize.width < size.width)
        {
            minSize.width += size.width;
        }
        return minSize;
    }

    /**
     * @return Returns the baseline.
     *
     * @throws NullPointerException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @see javax.swing.JComponent#getBaseline(int, int)
     * @since 1.6
     */
    @Override
    public int getBaseline(Component c, int width, int height)
    {
        if (c == null)
        {
            throw new NullPointerException("Must supply non-null component");
        }
        if (width < 0)
        {
            throw new IllegalArgumentException("Width must be >= 0");
        }
        if (height < 0)
        {
            throw new IllegalArgumentException("Height must be >= 0");
        }
        
        Border border = getBorder();
        JComponent header = getHeader(c);
        int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;

        Dimension size = header.getPreferredSize();
        Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

        int baseline = header.getBaseline(size.width, size.height);
        switch (getPosition())
        {
            case ABOVE_TOP:
                return baseline;
            case TOP:
                insets.top = edge + (insets.top - size.height) / 2;
                return (insets.top < edge) ? baseline : baseline + insets.top;
            case BELOW_TOP:
                return baseline + insets.top + edge;
            case ABOVE_BOTTOM:
                return baseline + height - size.height - insets.bottom - edge;
            case BOTTOM:
                insets.bottom = edge + (insets.bottom - size.height) / 2;
                return (insets.bottom < edge) ? baseline + height - size.height : baseline + height - size.height + insets.bottom;
            case BELOW_BOTTOM:
                return baseline + height - size.height;
        }
        return -1;
    }

    /**
     * @return Returns an enum indicating how the baseline of the border changes
     * as the size changes.
     *
     * @throws NullPointerException {@inheritDoc}
     * @see javax.swing.JComponent#getBaseline(int, int)
     * @since 1.6
     */
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component c)
    {
        super.getBaselineResizeBehavior(c);
        switch (getPosition())
        {
            case CTBorder.ABOVE_TOP:
            case CTBorder.TOP:
            case CTBorder.BELOW_TOP:
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            case CTBorder.ABOVE_BOTTOM:
            case CTBorder.BOTTOM:
            case CTBorder.BELOW_BOTTOM:
                return JComponent.BaselineResizeBehavior.CONSTANT_DESCENT;
        }
        return Component.BaselineResizeBehavior.OTHER;
    }

    private int getPosition()
    {
        int position = getTitlePosition();
        if (position != DEFAULT_POSITION)
        {
            return position;
        }
        Object value = UIManager.get("TitledBorder.position");
        if (value instanceof Integer)
        {
            int i = (Integer) value;
            if ((0 < i) && (i <= 6))
            {
                return i;
            }
        }
        else if (value instanceof String)
        {
            String s = (String) value;
            if (s.equalsIgnoreCase("ABOVE_TOP"))
            {
                return ABOVE_TOP;
            }
            if (s.equalsIgnoreCase("TOP"))
            {
                return TOP;
            }
            if (s.equalsIgnoreCase("BELOW_TOP"))
            {
                return BELOW_TOP;
            }
            if (s.equalsIgnoreCase("ABOVE_BOTTOM"))
            {
                return ABOVE_BOTTOM;
            }
            if (s.equalsIgnoreCase("BOTTOM"))
            {
                return BOTTOM;
            }
            if (s.equalsIgnoreCase("BELOW_BOTTOM"))
            {
                return BELOW_BOTTOM;
            }
        }
        return TOP;
    }

    private int getJustification(Component c)
    {
        int justification = getJustification();
        if ((justification == LEADING) || (justification == DEFAULT_JUSTIFICATION))
        {
            return c.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
        }
        if (justification == TRAILING)
        {
            return c.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
        }
        return justification;
    }

    private static Insets getBorderInsets(Border border, Component c, Insets insets)
    {
        if (border == null)
        {
            insets.set(0, 0, 0, 0);
        }
        else if (border instanceof AbstractBorder)
        {
            insets = ((AbstractBorder) border).getBorderInsets(c, insets);
        }
        else
        {
            Insets i = border.getBorderInsets(c);
            insets.set(i.top, i.left, i.bottom, i.right);
        }
        return insets;
    }

    protected Font getFont(Component c)
    {
        Font font = getTitleFont();
        if (font != null)
        {
            return font;
        }
        if (c != null)
        {
            font = c.getFont();
            if (font != null)
            {
                return font;
            }
        }
        return new Font(Font.DIALOG, Font.PLAIN, 12);
    }

    private Color getColor(Component c)
    {
        Color color = getTitleColor();
        if (color != null)
        {
            return color;
        }
        return (c != null) ? c.getForeground() : null;
    }

    /**
     * @return Returns whether or not the border is opaque.
     */
    @Override
    public boolean isBorderOpaque()
    {
        return false;
    }

    /**
     * Returns the border of the titled border.
     *
     * @return the border of the titled border
     */
    public Border getBorder()
    {
        return border != null ? border : UIManager.getBorder("TitledBorder.border");
    }

    /**
     * Returns the title-position of the titled border.
     *
     * @return the title-position of the titled border
     */
    public int getTitlePosition()
    {
        return titlePosition;
    }

    /**
     * Returns the title-justification of the titled border.
     *
     * @return the title-justification of the titled border
     */
    public int getJustification()
    {
        return justification;
    }

    /**
     * Sets the border of the titled border.
     *
     * @param border the border
     */
    public void setBorder(Border border)
    {
        this.border = border;
    }

    /**
     * @return the header
     */
    public JComponent getHeader()
    {
        if (header != null)
        {
            header.setFont(getFont(header));
            header.setForeground(getColor(header));

        }
        return header != null ? header : new JLabel("Component Header");
    }

    /**
     * @param c
     * @return the header
     */
    public JComponent getHeader(Component c)
    {
        if (header != null)
        {
            header.setFont(getFont(c));
            header.setForeground(getColor(c));
            header.setComponentOrientation(c.getComponentOrientation());
            header.setEnabled(c.isEnabled());
            for (Component x : header.getComponents())
            {
                x.setFont(getFont(c));
                x.setForeground(getColor(c));
                x.setComponentOrientation(c.getComponentOrientation());
                x.setEnabled(c.isEnabled());
            }
        }
        return header != null ? header : new JLabel("Component Header");
    }

    /**
     * @param header the header to set
     */
    public void setHeader(JComponent header)
    {
        this.header = header;
    }

    /**
     * Returns the title-font of the titled border.
     *
     * @return the title-font of the titled border
     */
    public Font getTitleFont()
    {
        return titleFont == null ? UIManager.getFont("TitledBorder.font") : titleFont;
    }

    /**
     * @param titleFont the titleFont to set
     */
    public void setTitleFont(Font titleFont)
    {
        this.titleFont = titleFont;
    }

    /**
     * Returns the title-color of the titled border.
     *
     * @return the title-color of the titled border
     */
    public Color getTitleColor()
    {
        return titleColor == null ? UIManager.getColor("TitledBorder.titleColor") : titleColor;
    }

    /**
     * @param titleColor the titleColor to set
     */
    public void setTitleColor(Color titleColor)
    {
        this.titleColor = titleColor;
    }
}
