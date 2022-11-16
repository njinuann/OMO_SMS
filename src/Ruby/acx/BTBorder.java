/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import static javax.swing.border.TitledBorder.ABOVE_BOTTOM;
import static javax.swing.border.TitledBorder.ABOVE_TOP;
import static javax.swing.border.TitledBorder.BELOW_BOTTOM;
import static javax.swing.border.TitledBorder.BELOW_TOP;
import static javax.swing.border.TitledBorder.BOTTOM;
import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;
import static javax.swing.border.TitledBorder.LEFT;
import static javax.swing.border.TitledBorder.RIGHT;
import static javax.swing.border.TitledBorder.TOP;

/**
 *
 * @author Pecherk
 */
public class BTBorder extends TitledBorder
{
    protected JComponent component;

    public BTBorder(JComponent component)
    {
        this(null, component, LEFT, TOP);
    }

    public BTBorder(Border border)
    {
        this(border, null, LEFT, TOP);
    }

    public BTBorder(Border border, JComponent component)
    {
        this(border, component, LEFT, TOP);
    }

    public BTBorder(Border border, JComponent component, int titleJustification, int titlePosition)
    {
        super(border, null, titleJustification, titlePosition, new java.awt.Font("Tahoma", 1, 11), java.awt.Color.black);
        this.component = component;
        if (border == null)
        {
            this.border = super.getBorder();
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        Rectangle borderR = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING, width - (EDGE_SPACING * 2), height - (EDGE_SPACING * 2));
        Insets borderInsets = (border != null) ? border.getBorderInsets(c) : new Insets(0, 0, 0, 0);

        Rectangle rect = new Rectangle(x, y, width, height);
        Insets insets = getBorderInsets(c);
        Rectangle compR = getComponentRect(rect, insets);
        int diff;
        switch (titlePosition)
        {
            case ABOVE_TOP:
                diff = compR.height + TEXT_SPACING;
                borderR.y += diff;
                borderR.height -= diff;
                break;
            case TOP:
            case DEFAULT_POSITION:
                diff = insets.top / 2 - borderInsets.top - EDGE_SPACING;
                borderR.y += diff;
                borderR.height -= diff;
                break;
            case BELOW_TOP:
            case ABOVE_BOTTOM:
                break;
            case BOTTOM:
                diff = insets.bottom / 2 - borderInsets.bottom - EDGE_SPACING;
                borderR.height -= diff;
                break;
            case BELOW_BOTTOM:
                diff = compR.height + TEXT_SPACING;
                borderR.height -= diff;
                break;
        }
        border.paintBorder(c, g, borderR.x, borderR.y, borderR.width, borderR.height);
        Color color = g.getColor();
        g.setColor(c.getBackground());
        g.fillRect(compR.x, compR.y, compR.width, compR.height);
        g.setColor(color);
        component.repaint();
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        Insets borderInsets;
        if (border != null)
        {
            borderInsets = border.getBorderInsets(c);
        }
        else
        {
            borderInsets = new Insets(0, 0, 0, 0);
        }
        insets.top = EDGE_SPACING + TEXT_SPACING + borderInsets.top;
        insets.right = EDGE_SPACING + TEXT_SPACING + borderInsets.right;
        insets.bottom = EDGE_SPACING + TEXT_SPACING + borderInsets.bottom;
        insets.left = EDGE_SPACING + TEXT_SPACING + borderInsets.left;

        if (c == null || component == null)
        {
            return insets;
        }

        int compHeight = 0;
        if (component != null)
        {
            compHeight = component.getPreferredSize().height;
        }

        switch (titlePosition)
        {
            case ABOVE_TOP:
                insets.top += compHeight + TEXT_SPACING;
                break;
            case TOP:
            case DEFAULT_POSITION:
                insets.top += Math.max(compHeight, borderInsets.top)
                    - borderInsets.top;
                break;
            case BELOW_TOP:
                insets.top += compHeight + TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                insets.bottom += compHeight + TEXT_SPACING;
                break;
            case BOTTOM:
                insets.bottom += Math.max(compHeight, borderInsets.bottom)
                    - borderInsets.bottom;
                break;
            case BELOW_BOTTOM:
                insets.bottom += compHeight + TEXT_SPACING;
                break;
        }
        return insets;
    }

    public JComponent getTitleComponent()
    {
        return component;
    }

    public void setTitleComponent(JComponent component)
    {
        this.component = component;
    }

    public Rectangle getComponentRect(Rectangle rect, Insets borderInsets)
    {
        Dimension compD = component.getPreferredSize();
        Rectangle compR = new Rectangle(0, 0, compD.width, compD.height);
        switch (titlePosition)
        {
            case ABOVE_TOP:
                compR.y = EDGE_SPACING;
                break;
            case TOP:
            case DEFAULT_POSITION:
                compR.y = EDGE_SPACING
                    + (borderInsets.top - EDGE_SPACING - TEXT_SPACING - compD.height)
                    / 2;
                break;
            case BELOW_TOP:
                compR.y = borderInsets.top - compD.height - TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                compR.y = rect.height - borderInsets.bottom + TEXT_SPACING;
                break;
            case BOTTOM:
                compR.y = rect.height
                    - borderInsets.bottom
                    + TEXT_SPACING
                    + (borderInsets.bottom - EDGE_SPACING - TEXT_SPACING - compD.height)
                    / 2;
                break;
            case BELOW_BOTTOM:
                compR.y = rect.height - compD.height - EDGE_SPACING;
                break;
        }
        switch (titleJustification)
        {
            case LEFT:
            case DEFAULT_JUSTIFICATION:
                compR.x = TEXT_INSET_H + borderInsets.left;
                break;
            case RIGHT:
                compR.x = rect.width - borderInsets.right - TEXT_INSET_H
                    - compR.width;
                break;
            case CENTER:
                compR.x = (rect.width - compR.width) / 2;
                break;
        }
        return compR;
    }
}
