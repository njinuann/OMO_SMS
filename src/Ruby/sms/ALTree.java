/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.sms;

import Ruby.APMain;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Pecherk
 */
public final class ALTree extends JTree
{
    private ALPanel alPanel;
    HashMap<TreePath, CheckNode> checkNodesMap = new HashMap<>();
    HashSet<TreePath> checkedPaths = new HashSet<>();
    protected EventListenerList lsnrList = new EventListenerList();

    public ALTree(ALPanel mXPanel)
    {
        super();
        setAlPanel(mXPanel);
        prepare();
    }

    private void prepare()
    {
        // Disabling toggling by double-click
        setToggleClickCount(0);
        // Overriding cell renderer by new one defined above
        setCellRenderer(new CheckBoxCellRenderer());

        // Overriding selection model by an empty one
        DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel()
        {
            // Totally disabling the selection mechanism
            @Override
            public void setSelectionPath(TreePath path)
            {
            }

            @Override
            public void addSelectionPath(TreePath path)
            {
            }

            @Override
            public void removeSelectionPath(TreePath path)
            {
            }

            @Override
            public void setSelectionPaths(TreePath[] pPaths)
            {
            }
        };
        // Calling checking mechanism on mouse click
        addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                TreePath treePath = ALTree.this.getPathForLocation(arg0.getX(), arg0.getY());
                if (treePath != null)
                {
                    checkSubTree(treePath, !checkNodesMap.get(treePath).isSelected);
                    updateParentNodes(treePath);
                    // Firing the check change event
                    fireCheckChangeEvent(new CheckChangeEvent(new Object()));
                    // Repainting tree after the data structures were updated
                    ALTree.this.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent arg0)
            {
            }

            @Override
            public void mouseExited(MouseEvent arg0)
            {
            }

            @Override
            public void mousePressed(MouseEvent arg0)
            {
            }

            @Override
            public void mouseReleased(MouseEvent arg0)
            {
            }
        });

        setSelectionModel(dtsm);

        addCheckChangeEventListener((ALTree.CheckChangeEvent event)
            -> 
            {
                getAlPanel().selectedNodesArea.setText("");
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) ALTree.this.getModel().getRoot();
                Enumeration<DefaultMutableTreeNode> enu = (Enumeration<DefaultMutableTreeNode>) root.children();
                while (enu.hasMoreElements())
                {
                    DefaultMutableTreeNode node = enu.nextElement();
                    if (checkNodesMap.get(new TreePath(node.getPath())).isSelected)
                    {
                        getAlPanel().selectedNodesArea.append(node.getUserObject().toString().trim() + "\r\n");
                    }
                }
                getAlPanel().selectedNodesAreaScroller.getHorizontalScrollBar().setValue(0);
                getAlPanel().selectedNodesAreaScroller.getVerticalScrollBar().setValue(0);
                getAlPanel().selectedNodesArea.updateUI();
        });

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void setFilterTree(Object[][] filterItems, String[] selectItems, String filterHeader)
    {
        try
        {
            javax.swing.tree.DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) getModel().getRoot());
            rootNode.setUserObject(filterHeader);
            rootNode.removeAllChildren();
            for (Object[] filterItem : filterItems)
            {
                rootNode.add(new javax.swing.tree.DefaultMutableTreeNode(String.valueOf(filterItem[0]) + "~" + String.valueOf(filterItem[1]).trim()));
            }
            setPreselectedItems(selectItems, rootNode);
        }
        catch (Exception ex)
        {
            APMain.smsLog.logEvent(ex);
        }
    }

    public void setPreselectedItems(String[] selectItems, DefaultMutableTreeNode rootNode)
    {
        resetNodesState();
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(selectItems));
        Enumeration e = rootNode.children();
        while (e.hasMoreElements())
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (arrayList.contains(String.valueOf(node.getUserObject()).split("~")[0]))
            {
                TreePath path = new TreePath(node.getPath());
                checkNodesMap.put(path, new CheckNode(true, node.getChildCount() > 0, false));
                getAlPanel().selectedNodesArea.append(node.getUserObject().toString() + "\r\n");
                checkSubTree(path, true);
                updateParentNodes(path);
            }
        }
        updateUI();
    }

    // Defining data structure that will enable to fast check-indicate the state of each node
    // It totally replaces the "selection" mechanism of the JTree
    private class CheckNode
    {
        boolean isSelected, hasChildren, allChildrenSelected;

        public CheckNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_)
        {
            isSelected = isSelected_;
            hasChildren = hasChildren_;
            allChildrenSelected = allChildrenSelected_;
        }
    }

    public class CheckChangeEvent extends EventObject
    {
        public CheckChangeEvent(Object source)
        {
            super(source);
        }
    }

    public interface CheckChangeEventListener extends EventListener
    {
        public void checkStateChanged(CheckChangeEvent event);
    }

    public void addCheckChangeEventListener(CheckChangeEventListener listener)
    {
        lsnrList.add(CheckChangeEventListener.class, listener);
    }

    public void removeCheckChangeEventListener(CheckChangeEventListener listener)
    {
        lsnrList.remove(CheckChangeEventListener.class, listener);
    }

    void fireCheckChangeEvent(CheckChangeEvent evt)
    {
        Object[] listeners = lsnrList.getListenerList();
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] == CheckChangeEventListener.class)
            {
                ((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
            }
        }
    }

    @Override
    public void setModel(TreeModel newModel)
    {
        super.setModel(newModel);
        resetNodesState();
    }

    // New method that returns only the checked paths (totally ignores original "selection" mechanism)
    public TreePath[] getCheckedPaths()
    {
        return checkedPaths.toArray(new TreePath[checkedPaths.size()]);
    }

    // Returns true in case that the node is selected, has children but not all of them are selected
    public boolean isSelectedPartially(TreePath path)
    {
        CheckNode cn = checkNodesMap.get(path);
        return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
    }

    private void resetNodesState()
    {
        checkedPaths = new HashSet<>();
        checkNodesMap = new HashMap<>();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
        if (node != null)
        {
            trackNodeChecking(node);
        }
        if (getAlPanel() != null)
        {
            getAlPanel().selectedNodesArea.setText("");
        }
    }

    // Creating data structure of the current model for the checking mechanism
    private void trackNodeChecking(DefaultMutableTreeNode node)
    {
        TreePath treePath = new TreePath(node.getPath());
        checkNodesMap.put(treePath, new CheckNode(false, node.getChildCount() > 0, false));
        for (int i = 0; i < node.getChildCount(); i++)
        {
            trackNodeChecking((DefaultMutableTreeNode) treePath.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
        }
    }

    // Overriding cell renderer by a class that ignores the original "selection" mechanism
    // It decides how to show the nodes due to the checking-mechanism
    private class CheckBoxCellRenderer implements TreeCellRenderer
    {
        boolean init = false;
        JCheckBox checkBox = new JCheckBox();

        public CheckBoxCellRenderer()
        {
            super();
            checkBox.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Ruby/ximg/partial.png"))));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            checkBox.setText(node.getUserObject().toString());
            checkBox.setOpaque(false);
            checkBox.setIcon(null);
            CheckNode cn = checkNodesMap.get(new TreePath(node.getPath()));
            if (cn != null)
            {
                checkBox.setSelected(cn.isSelected);
                if (cn.isSelected && cn.hasChildren && !cn.allChildrenSelected)
                {
                    checkBox.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Ruby/ximg/partial.png"))));
                }
            }
            checkBox.setFont(new Font(getFont().getName(), (node.isLeaf() ? Font.PLAIN : Font.BOLD), getFont().getSize()));
            return checkBox;
        }
    }

    // When a node is checked/unchecked, updating the states of the predecessors
    protected void updateParentNodes(TreePath tp)
    {
        TreePath parentPath = tp.getParentPath();
        // If it is the root, stop the recursive calls and return
        if (parentPath != null)
        {
            CheckNode parentCheckedNode = checkNodesMap.get(parentPath);
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
            parentCheckedNode.allChildrenSelected = true;
            parentCheckedNode.isSelected = false;
            for (int i = 0; i < parentNode.getChildCount(); i++)
            {
                CheckNode childCheckedNode = checkNodesMap.get(parentPath.pathByAddingChild(parentNode.getChildAt(i)));
                // It is enough that even one subtree is not fully selected
                // to determine that the parent is not fully selected
                if (!childCheckedNode.allChildrenSelected)
                {
                    parentCheckedNode.allChildrenSelected = false;
                }
                // If at least one child is selected, selecting also the parent
                if (childCheckedNode.isSelected)
                {
                    parentCheckedNode.isSelected = true;
                }
            }
            if (parentCheckedNode.isSelected)
            {
                checkedPaths.add(parentPath);
            }
            else
            {
                checkedPaths.remove(parentPath);
            }
            // Go to upper predecessor
            updateParentNodes(parentPath);
        }
    }

    // Recursively checks/unchecks a subtree
    protected void checkSubTree(TreePath treePath, boolean check)
    {
        CheckNode checkNode = checkNodesMap.get(treePath);
        checkNode.isSelected = check;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        for (int i = 0; i < node.getChildCount(); i++)
        {
            checkSubTree(treePath.pathByAddingChild(node.getChildAt(i)), check);
        }
        checkNode.allChildrenSelected = check;
        if (check)
        {
            checkedPaths.add(treePath);
        }
        else
        {
            checkedPaths.remove(treePath);
        }
    }

    /**
     * @return the alPanel
     */
    public ALPanel getAlPanel()
    {
        return alPanel;
    }

    /**
     * @param alPanel the alPanel to set
     */
    public void setAlPanel(ALPanel alPanel)
    {
        this.alPanel = alPanel;
    }
}
