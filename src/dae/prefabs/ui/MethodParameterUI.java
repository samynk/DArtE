package dae.prefabs.ui;

import dae.prefabs.Prefab;
import dae.prefabs.parameters.Parameter;

/**
 *
 * @author Koen Samyn
 */
public class MethodParameterUI extends javax.swing.JPanel implements ParameterUI{
    private Parameter p;
    private Prefab prefab;
    
    /**
     * Creates new form MethodParameterUI
     */
    public MethodParameterUI() {
        initComponents();
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

        btnMethod = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        btnMethod.setText("Method to call");
        btnMethod.setMaximumSize(null);
        btnMethod.setMinimumSize(null);
        btnMethod.setPreferredSize(null);
        btnMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMethodActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(btnMethod, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMethodActionPerformed
        prefab.call(p);
    }//GEN-LAST:event_btnMethodActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMethod;
    // End of variables declaration//GEN-END:variables

    public void setParameter(Parameter p) {
        this.p = p;
        this.btnMethod.setText(p.getLabel());
    }
    
    public Parameter getParameter(){
        return p;
    }

    public void setNode(Prefab currentSelectedNode) {
        this.prefab = currentSelectedNode;
    }
    
    /**
     * Checks if a label should be created for the UI.
     * @return true if a label should be created, false othwerise.
     */
    public boolean needsLabel(){
        return false;
    }
}
