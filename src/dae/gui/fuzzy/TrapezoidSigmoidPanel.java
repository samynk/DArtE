/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.fuzzy;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mlproject.fuzzy.TrapezoidMemberShip;

/**
 *
 * @author Koen Samyn
 */
public class TrapezoidSigmoidPanel extends javax.swing.JPanel implements DocumentListener {

    private TrapezoidMemberShip membership;
    private Color errorColor;
    private Color normalColor;

    /**
     * Creates new form NewJPanel
     */
    public TrapezoidSigmoidPanel() {
        initComponents();
        for (Component c : this.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).putClientProperty("JComponent.sizeVariant", "small");
            }
        }
        txtMembershipName.getDocument().addDocumentListener(this);
        errorColor = UIManager.getDefaults().getColor("nimbusRed");
        if (errorColor == null) {
            errorColor = Color.RED;
        }
        normalColor = UIManager.getDefaults().getColor("text");
        if (normalColor == null) {
            normalColor = Color.BLACK;
        }
    }

    public void setMemberShip(TrapezoidMemberShip memberShip) {
        this.membership = memberShip;
        txtMembershipName.setText(memberShip.getName());
        spnLeft.setValue(memberShip.getLeft());
        spnCenterLeft.setValue(memberShip.getCenterLeft());
        spnCenterRight.setValue(memberShip.getCenterRight());
        spnRight.setValue(memberShip.getRight());
    }

    public void insertUpdate(DocumentEvent e) {
        updateMemberShipName();
    }

    public void removeUpdate(DocumentEvent e) {
        updateMemberShipName();
    }

    public void changedUpdate(DocumentEvent e) {
        updateMemberShipName();
    }

    private void updateMemberShipName() {
        String newName = txtMembershipName.getText();
        this.membership.setName(newName);

        if (!membership.getName().equals(newName)) {
            txtMembershipName.setForeground(errorColor);
        } else {
            txtMembershipName.setForeground(normalColor);
        }
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

        lblName = new javax.swing.JLabel();
        spnCenterLeft = new javax.swing.JSpinner();
        spnCenterRight = new javax.swing.JSpinner();
        txtMembershipName = new javax.swing.JTextField();
        lblCenterLeft = new javax.swing.JLabel();
        lblCenterRight = new javax.swing.JLabel();
        lblLeft = new javax.swing.JLabel();
        spnLeft = new javax.swing.JSpinner();
        lblRight = new javax.swing.JLabel();
        spnRight = new javax.swing.JSpinner();
        lblFiller = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblName.setText("Name :");
        lblName.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(lblName, gridBagConstraints);

        spnCenterLeft.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        spnCenterLeft.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCenterLeftStateChanged(evt);
            }
        });
        spnCenterLeft.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(spnCenterLeft, gridBagConstraints);

        spnCenterRight.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        spnCenterRight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCenterRightStateChanged(evt);
            }
        });
        spnCenterRight.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(spnCenterRight, gridBagConstraints);
        txtMembershipName.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(txtMembershipName, gridBagConstraints);

        lblCenterLeft.setText("Center left : ");
        lblCenterLeft.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(lblCenterLeft, gridBagConstraints);

        lblCenterRight.setText("Center right : ");
        lblCenterRight.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(lblCenterRight, gridBagConstraints);

        lblLeft.setText("Left :");
        lblLeft.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(lblLeft, gridBagConstraints);

        spnLeft.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        spnLeft.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLeftStateChanged(evt);
            }
        });
        spnLeft.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(spnLeft, gridBagConstraints);

        lblRight.setText("Right :");
        lblRight.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(lblRight, gridBagConstraints);

        spnRight.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.1f)));
        spnRight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnRightStateChanged(evt);
            }
        });
        spnRight.putClientProperty("JComponent.sizeVariant", "mini");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        add(spnRight, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0;
        add(lblFiller, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void spnCenterLeftStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCenterLeftStateChanged
        // TODO add your handling code here:
        membership.setCenterLeft((Float) spnCenterLeft.getValue());
    }//GEN-LAST:event_spnCenterLeftStateChanged

    private void spnCenterRightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCenterRightStateChanged
        // TODO add your handling code here:
        membership.setCenterRight((Float) spnCenterRight.getValue());
    }//GEN-LAST:event_spnCenterRightStateChanged

    private void spnLeftStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLeftStateChanged
        // TODO add your handling code here:
        membership.setLeft((Float) spnLeft.getValue());
    }//GEN-LAST:event_spnLeftStateChanged

    private void spnRightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnRightStateChanged
        // TODO add your handling code here:
        membership.setRight((Float) spnRight.getValue());
    }//GEN-LAST:event_spnRightStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCenterLeft;
    private javax.swing.JLabel lblCenterRight;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRight;
    private javax.swing.JSpinner spnCenterLeft;
    private javax.swing.JSpinner spnCenterRight;
    private javax.swing.JSpinner spnLeft;
    private javax.swing.JSpinner spnRight;
    private javax.swing.JTextField txtMembershipName;
    // End of variables declaration//GEN-END:variables
}