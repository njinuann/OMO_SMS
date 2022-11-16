package Ruby.acx;

import Ruby.APController;
import Ruby.APMain;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
/**
 *
 * @author Pecherk
 */
public class ACPane extends JTextPane implements DocumentListener
{

    SimpleAttributeSet attributes = new SimpleAttributeSet();
    StyledDocument doc = (StyledDocument) getDocument();
    Element root = doc.getDefaultRootElement();

    public ACPane()
    {
        addListener();
        setTabSize();
    }

    private void addListener()
    {
        doc.addDocumentListener(this);
        setSelectionColor(java.awt.Color.magenta);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK), "CTRL+L");
        getActionMap().put("CTRL+L", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setText("");
            }
        });
        if (doc instanceof AbstractDocument)
        {
            for (UndoableEditListener undoListener : ((AbstractDocument) doc).getUndoableEditListeners())
            {
                doc.removeUndoableEditListener(undoListener);
            }
        }
    }

    public void removeLines()
    {
        if (root.getElementCount() > APController.displayLines)
        {
            try
            {
                doc.remove(0, root.getElement(root.getElementCount() - (APController.displayLines + 1)).getEndOffset());
            }
            catch (Exception ble)
            {
                ble = null;
            }
        }
    }

    public void append(String str, Color color)
    {
        try
        {
            StyleConstants.setForeground(attributes, (str.contains("stacktrace") ? APMain.errorColor : color));
            doc.insertString(doc.getLength(), str, attributes);
        }
        catch (Exception ex)
        {
            ex = null;
        }
    }

    private void setTabSize()
    {
        StyleConstants.setTabSet(attributes, new TabSet(new TabStop[]
        {
            new TabStop(25, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(50, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(75, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(100, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(125, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(150, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(175, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(200, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(225, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(250, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(275, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
            new TabStop(300, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE),
        }));
        setParagraphAttributes(attributes, false);
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        EventQueue.invokeLater(this::removeLines);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }
}
