/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * CSPanel.java
 *
 * Created on Feb 22, 2012, 12:21:01 AM
 */
package Ruby.acx;

import Ruby.APMain;
import Ruby.DBClient;
import Ruby.model.TCScheme;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Pecherk
 */
public final class CSPanel extends javax.swing.JPanel
{
    private String channel;
    private TCScheme scheme = new TCScheme();
    private TreeMap<String, TCScheme> schemes = new TreeMap<>();
    private static final ATBox box = new ATBox(APMain.acxLog);

    /**
     * Creates new form POSSchemes
     */
    public CSPanel()
    {
        initComponents();
        initDialog();
    }

    public void initDialog()
    {
        schemeDialog.setIconImage(APMain.getIconImage());
        schemeDialog.setContentPane(this);
        schemeDialog.pack();
        schemeDialog.setResizable(false);
        schemeDialog.setLocationRelativeTo(APMain.apFrame);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        schemeDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        descriptionField = new javax.swing.JTextField();
        schemeIdField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        statusBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        sysUserField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        sysDateField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        channelBox = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        saveButton = new javax.swing.JButton();
        schemeTreeScroller = new javax.swing.JScrollPane();
        schemeTree = new javax.swing.JTree();

        schemeDialog.setIconImage(APMain.getIconImage());
        schemeDialog.setModal(true);
        schemeDialog.setName("schemeDialog"); // NOI18N

        javax.swing.GroupLayout schemeDialogLayout = new javax.swing.GroupLayout(schemeDialog.getContentPane());
        schemeDialog.getContentPane().setLayout(schemeDialogLayout);
        schemeDialogLayout.setHorizontalGroup(
            schemeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        schemeDialogLayout.setVerticalGroup(
            schemeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray));

        jLabel1.setText("Description");

        descriptionField.setToolTipText("Scheme Description");

        schemeIdField.setEditable(false);
        schemeIdField.setToolTipText("Scheme ID");

        jLabel7.setText("Scheme Status");

        statusBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A~Active", "C~Closed" }));
        statusBox.setToolTipText("Scheme Status");

        jLabel8.setText("Modified By");

        sysUserField.setEditable(false);
        sysUserField.setForeground(new java.awt.Color(51, 102, 255));
        sysUserField.setToolTipText("Modified By");

        jLabel10.setText("Scheme ID");

        jLabel11.setText("Date Modified");

        sysDateField.setEditable(false);
        sysDateField.setForeground(new java.awt.Color(51, 102, 255));
        sysDateField.setToolTipText("Date Modified");

        jLabel2.setText("Channel");

        channelBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ATM", "MOBILE", "POS", "SMS", "TACH", "EST" }));
        channelBox.setToolTipText("Scheme Channel");
        channelBox.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(schemeIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(channelBox, 0, 186, Short.MAX_VALUE))
                    .addComponent(descriptionField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sysUserField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sysDateField, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel2)
                            .addComponent(channelBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(schemeIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(statusBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sysUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(sysDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton.setText("Close");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveButtonActionPerformed(evt);
            }
        });

        schemeTreeScroller.setBorder(null);
        schemeTreeScroller.setBorder(null);

        schemeTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Charge Schemes");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Add Scheme");
        treeNode1.add(treeNode2);
        schemeTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        schemeTree.setCellRenderer(new TRenderer());
        schemeTree.setRootVisible(false);
        schemeTree.setShowsRootHandles(true);
        schemeTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
        {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt)
            {
                schemeTreeValueChanged(evt);
            }
        });
        schemeTreeScroller.setViewportView(schemeTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(schemeTreeScroller, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(schemeTreeScroller, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator2)
                            .addComponent(cancelButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void schemeTreeValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_schemeTreeValueChanged
    {//GEN-HEADEREND:event_schemeTreeValueChanged
        // TODO add your handling code here:
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) schemeTree.getLastSelectedPathComponent();
        if (selectedNode != null)
        {
            if (selectedNode.getUserObject() instanceof TCScheme)
            {
                displayScheme((TCScheme) selectedNode.getUserObject());
            }
            else
            {
                acceptScheme();
            }
        }
}//GEN-LAST:event_schemeTreeValueChanged

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        SwingUtilities.invokeLater(()
                -> 
                {
                    schemeDialog.setVisible(false);
        });
}//GEN-LAST:event_cancelButtonActionPerformed

    public void setSchemeTree()
    {
        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) schemeTree.getModel().getRoot());
        setSchemes(getdClient().querySchemes(getChannel()));

        rootNode.removeAllChildren();
        rootNode.add(new DefaultMutableTreeNode("Add Scheme"));
        for (Object code : getWorker().sortArray(getSchemes().keySet().toArray(), true))
        {
            rootNode.add(new DefaultMutableTreeNode(getSchemes().get(code)));
        }

        getWorker().expandAllNodes(schemeTree, TCScheme.class);
    }

    private void acceptScheme()
    {
        setScheme(new TCScheme());
        getWorker().resetAllFields(this);
        getScheme().setCode(getWorker().nextCode(getSchemes(), getChannel().substring(0, 1)));
        schemeIdField.setText(getScheme().getCode());
        saveButton.setText("Save");
    }

    public void displayScheme(TCScheme scheme)
    {
        setScheme(scheme);
        schemeIdField.setText(getScheme().getCode());
        descriptionField.setText(getScheme().getDescription());
        getWorker().selectBoxValue(statusBox, getScheme().getStatus());
        sysUserField.setText(getScheme().getSysUser());
        sysDateField.setText(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a").format(getScheme().getSysDate()));
        saveButton.setText("Update");
    }

    public void showDialog(String channel)
    {
        setChannel(channel);
        setSchemeTree();
        getWorker().selectBoxValue(channelBox, getChannel());
        schemeDialog.setTitle((getChannel().length() > 3 ? getWorker().capitalize(getChannel()) : getChannel()) + " Schemes");
        SwingUtilities.invokeLater(()
                -> 
                {
                    schemeDialog.setVisible(true);
        });
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
    {//GEN-HEADEREND:event_saveButtonActionPerformed
        // TODO add your handling code here:
        if (getWorker().validateFields(schemeDialog, this))
        {
            getScheme().setCode(schemeIdField.getText().trim());
            getScheme().setDescription(descriptionField.getText().trim());
            getScheme().setChannel(getWorker().getBoxValue(channelBox));

            getScheme().setStatus(getWorker().getBoxValue(statusBox));
            getScheme().setSysUser(ULPanel.getUser().getStaffName());
            getScheme().setSysDate(new Date());

            boolean proceed = getSchemes().containsKey(getScheme().getCode())
                    ? (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(schemeDialog, "Are you sure you want to save changes to this scheme?", "Update Scheme?", JOptionPane.YES_NO_OPTION))
                    : (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(schemeDialog, "Are you sure you want to save this new scheme?", "Save Scheme?", JOptionPane.YES_NO_OPTION));
            if (proceed)
            {
                getSchemes().put(getScheme().getCode(), getScheme());
                if (getdClient().upsertScheme(getScheme()))
                {
                    JOptionPane.showMessageDialog(schemeDialog, "Scheme saved successfully.");
                    setSchemeTree();
                }
                else
                {
                    JOptionPane.showMessageDialog(schemeDialog, "Unable to save scheme. Please refer to log for more details.", "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
}//GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> channelBox;
    private javax.swing.JTextField descriptionField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton saveButton;
    public javax.swing.JDialog schemeDialog;
    private javax.swing.JTextField schemeIdField;
    private javax.swing.JTree schemeTree;
    private javax.swing.JScrollPane schemeTreeScroller;
    private javax.swing.JComboBox statusBox;
    private javax.swing.JTextField sysDateField;
    private javax.swing.JTextField sysUserField;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the channel
     */
    public String getChannel()
    {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    /**
     * @return the schemes
     */
    public TreeMap<String, TCScheme> getSchemes()
    {
        return schemes;
    }

    /**
     * @param schemes the schemes to set
     */
    public void setSchemes(TreeMap<String, TCScheme> schemes)
    {
        this.schemes = schemes;
    }

    /**
     * @return the scheme
     */
    public TCScheme getScheme()
    {
        return scheme;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setScheme(TCScheme scheme)
    {
        this.scheme = scheme;
    }

    /**
     * @return the box
     */
    public static ATBox getBox()
    {
        return box;
    }

    private AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    private DBClient getdClient()
    {
        return getBox().getdClient();
    }
}
