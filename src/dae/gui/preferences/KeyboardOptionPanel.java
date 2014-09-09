/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.preferences;

import com.jme3.input.InputManager;
import dae.GlobalObjects;

/**
 *
 * @author Koen Samyn
 */
public class KeyboardOptionPanel extends javax.swing.JPanel {

    /**
     * Creates new form KeyboardOptionPanel
     */
    public KeyboardOptionPanel() {
        initComponents();

//        "FLYCAM_Left",
//            "FLYCAM_Right",
//            "FLYCAM_Up",
//            "FLYCAM_Down",
//
//            "FLYCAM_StrafeLeft",
//            "FLYCAM_StrafeRight",
//            "FLYCAM_Forward",
//            "FLYCAM_Backward",
        
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        keyLeft = new dae.gui.components.KeySelectionField();
        keyRight = new dae.gui.components.KeySelectionField();
        keyForward = new dae.gui.components.KeySelectionField();
        keyBack = new dae.gui.components.KeySelectionField();
        lblFiller = new javax.swing.JLabel();
        lblCamera = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        keyLeft.setLabel("Left : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(keyLeft, gridBagConstraints);

        keyRight.setLabel("Right : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(keyRight, gridBagConstraints);

        keyForward.setLabel("Forward : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(keyForward, gridBagConstraints);

        keyBack.setLabel("Back : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(keyBack, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        add(lblFiller, gridBagConstraints);

        lblCamera.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCamera.setText("Camera keys :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        add(lblCamera, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dae.gui.components.KeySelectionField keyBack;
    private dae.gui.components.KeySelectionField keyForward;
    private dae.gui.components.KeySelectionField keyLeft;
    private dae.gui.components.KeySelectionField keyRight;
    private javax.swing.JLabel lblCamera;
    private javax.swing.JLabel lblFiller;
    // End of variables declaration//GEN-END:variables
}
