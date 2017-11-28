package dae.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import mlproject.fuzzy.FuzzySystem;

/**
 *
 * @author Koen Samyn
 * 
 */
public class EditFuzzySystemDialog extends javax.swing.JDialog implements WindowListener {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    
    private FuzzySystem system;

    /**
     * Creates new form EdiitFuzzySystemDialog
     * @param parent
     * @param modal
     */
    public EditFuzzySystemDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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
        addWindowListener(this);
    }
    
    public void setFuzzySystem(FuzzySystem fuzzySystem){
        system = fuzzySystem;
        
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

        fuzzySystemUI1 = new mlproject.fuzzy.gui.FuzzySystemUI();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        tabSystem = new javax.swing.JTabbedPane();
        fuzzyInputVariablePanel1 = new dae.gui.fuzzy.FuzzyInputVariablePanel();
        ruleBlockPanel1 = new dae.gui.fuzzy.RuleBlockPanel();
        fuzzyOutputVariablePanel1 = new dae.gui.fuzzy.FuzzyOutputVariablePanel();
        lblFiller = new javax.swing.JLabel();

        setTitle("Fuzzysystem editor");
        setPreferredSize(new java.awt.Dimension(900, 350));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        okButton.setText("Save");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
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
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        getContentPane().add(cancelButton, gridBagConstraints);

        tabSystem.addTab("Fuzzy Inputs", fuzzyInputVariablePanel1);
        tabSystem.addTab("Rules", ruleBlockPanel1);
        tabSystem.addTab("Fuzzy Outputs", fuzzyOutputVariablePanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        getContentPane().add(tabSystem, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(lblFiller, gridBagConstraints);

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
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditFuzzySystemDialog dialog = new EditFuzzySystemDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private dae.gui.fuzzy.FuzzyInputVariablePanel fuzzyInputVariablePanel1;
    private dae.gui.fuzzy.FuzzyOutputVariablePanel fuzzyOutputVariablePanel1;
    private mlproject.fuzzy.gui.FuzzySystemUI fuzzySystemUI1;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JButton okButton;
    private dae.gui.fuzzy.RuleBlockPanel ruleBlockPanel1;
    private javax.swing.JTabbedPane tabSystem;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;

    public void windowOpened(WindowEvent e) {
        fuzzyInputVariablePanel1.setFuzzySystem(system);
        ruleBlockPanel1.setFuzzySystem(system);
        fuzzyOutputVariablePanel1.setFuzzySystem(system);
        tabSystem.setSelectedIndex(0);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
       
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
       
    }

    @Override
    public void windowActivated(WindowEvent e) {
        fuzzyInputVariablePanel1.setFuzzySystem(system);
        ruleBlockPanel1.setFuzzySystem(system);
        fuzzyOutputVariablePanel1.setFuzzySystem(system);
        tabSystem.setSelectedIndex(0);
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }
}
