package dae.gui;

import dae.prefabs.ui.classpath.FileNode;
import dae.project.Project;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author Koen Samyn
 */
public class SelectAssetDialog extends javax.swing.JDialog implements TreeSelectionListener {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    /**
     * The selected FileNode
     */
    private FileNode selected;

    /**
     * Creates new form SelectAssetDialog
     */
    public SelectAssetDialog(java.awt.Window parent, boolean modal) {
        super(parent,modal? ModalityType.TOOLKIT_MODAL:ModalityType.MODELESS);
        initComponents();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });

        assetPanel1.addTreeSelectionListener(this);
        // not possible to change the filter.
        assetPanel1.setToggleButtonsEnabled(false);
    }
    
    public FileNode getSelectedFileNode(){
        return selected;
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        assetPanel1 = new dae.gui.AssetPanel();
        lblFiller = new javax.swing.JLabel();
        fileInformationPanel2 = new dae.gui.components.FileInformationPanel();

        setTitle("Select Asset");
        setPreferredSize(new java.awt.Dimension(600, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(okButton, gridBagConstraints);
        getRootPane().setDefaultButton(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 2);
        getContentPane().add(cancelButton, gridBagConstraints);

        assetPanel1.setMinimumSize(null);
        assetPanel1.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(assetPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(lblFiller, gridBagConstraints);

        fileInformationPanel2.setMinimumSize(null);
        fileInformationPanel2.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 12, 0);
        getContentPane().add(fileInformationPanel2, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        fileInformationPanel2.showEmptyPreview();
        fileInformationPanel2.startTimer();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        fileInformationPanel2.stopTimer();
    }//GEN-LAST:event_formWindowDeactivated

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dae.gui.AssetPanel assetPanel1;
    private javax.swing.JButton cancelButton;
    private dae.gui.components.FileInformationPanel fileInformationPanel2;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    /**
     * Checks if the selected node is a leaf or not. If it is a leaf, then the
     * ok button can be pressed.
     *
     * @param e the tree selection event.
     */
    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getPath().getLastPathComponent();
        if (o instanceof FileNode) {
            FileNode fn = (FileNode) o;
            okButton.setEnabled(fn.isFile());
            if ( fn.isFile()){
                this.selected = fn;
                
            }else{
                this.selected = null;
            }
            this.fileInformationPanel2.setFileNode(fn);
        }
    }

    public void setExtensions(String extensions) {
        
        assetPanel1.adjustToggleButtonsToFilePattern(extensions);
        assetPanel1.setFilePattern(".*\\.(?:"+extensions+")");
    }

    public void setProject(Project currentProject) {
        assetPanel1.setProject(currentProject);
        fileInformationPanel2.setProject(currentProject);
    }
}
