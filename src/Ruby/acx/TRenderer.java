/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Pecherk
 */
public class TRenderer extends DefaultTreeCellRenderer
{
    Icon starIcon = new ImageIcon(getClass().getResource("/Ruby/ximg/star.png"));
    Icon listIcon = new ImageIcon(getClass().getResource("/Ruby/ximg/list.png"));
    Icon baloonIcon = new ImageIcon(getClass().getResource("/Ruby/ximg/baloon.png"));
    Icon crossIcon = new ImageIcon(getClass().getResource("/Ruby/ximg/cross.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        setFont(new Font(getFont().getName(), (node.isLeaf() ? Font.PLAIN : Font.BOLD), getFont().getSize()));
        setIcon(node.isRoot() ? starIcon : (node.getUserObject() instanceof AXNode ? baloonIcon : (node.isLeaf() ? crossIcon : listIcon)));
        return this;
    }
}
