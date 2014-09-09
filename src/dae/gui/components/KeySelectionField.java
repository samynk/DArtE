/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.components;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.KeyStroke;

/**
 *
 * @author Koen Samyn
 */
public class KeySelectionField extends javax.swing.JPanel implements KeyListener {

    private String label;
    private KeyStroke currentStroke;
    private int keyCode;

    /**
     * Creates new form KeySelectionField
     */
    public KeySelectionField() {
        initComponents();
        txtKey.addKeyListener(this);
    }

    /**
     * Gets the label to show.
     *
     * @return the label of the keyselectionfield.
     */
    public String getLabel() {
        return lblLabel.getText();
    }

    /**
     * Sets the label of the key selection field.
     *
     * @param label the new label for the selection field.
     */
    public void setLabel(String label) {
        lblLabel.setText(label);
    }

    /**
     * Sets the keystroke to show on this field.
     *
     * @param stroke the keystroke.
     */
    public void setKeyStroke(KeyStroke stroke) {
        this.currentStroke = stroke;
        adaptTextField();

    }

    /**
     * Adapt the text field to the contents of the stroke.
     */
    private void adaptTextField() {
        if (currentStroke != null) {
            int modifiers = currentStroke.getModifiers();
            StringBuilder text = new StringBuilder();
            if ((modifiers & InputEvent.CTRL_DOWN_MASK) > 0) {
                text.append("CTRL + ");
            }
            if ((modifiers & InputEvent.ALT_DOWN_MASK) > 0) {
                text.append("ALT + ");
            }
            if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) > 0) {
                text.append("ALT GR + ");
            }
            if ((modifiers & InputEvent.META_DOWN_MASK) > 0) {
                text.append("META + ");
            }
            if ((modifiers & InputEvent.SHIFT_DOWN_MASK) > 0) {
                text.append("SHIFT + ");
            }
            text.append( KeyEvent.getKeyText(currentStroke.getKeyCode()));
            txtKey.setText(text.toString());
        }
    }

    /**
     * Gets the current keystroke.
     *
     * @return the current keystroke.
     */
    public KeyStroke getKeyStroke() {
        return currentStroke;
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

        lblLabel = new javax.swing.JLabel();
        txtKey = new javax.swing.JTextField();
        btnChange = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblLabel.setText("Label");
        lblLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        lblLabel.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLabel, gridBagConstraints);

        txtKey.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtKey, gridBagConstraints);

        btnChange.setText("Change");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(btnChange, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        txtKey.setEnabled(true);
        txtKey.setText("");
        txtKey.requestFocus();
    }//GEN-LAST:event_btnChangeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChange;
    private javax.swing.JLabel lblLabel;
    private javax.swing.JTextField txtKey;
    // End of variables declaration//GEN-END:variables

    public void keyTyped(KeyEvent e) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, e.getModifiers());
        txtKey.setEnabled(false);
        setKeyStroke(stroke);
    }

    public void keyPressed(KeyEvent e) {
        keyCode = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) {
    }
}
