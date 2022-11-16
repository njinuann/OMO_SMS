/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Pecherk
 */
public class TLFilter extends PlainDocument
{
    private int maxChars = 10;

    public TLFilter(int length)
    {
        maxChars = length;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        if (str != null && (getLength() + str.length() <= maxChars))
        {
            super.insertString(offs, str, a);
        }
    }
}
