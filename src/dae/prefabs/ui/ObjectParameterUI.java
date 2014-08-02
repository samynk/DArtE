/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui;

import com.google.common.eventbus.Subscribe;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.ui.events.GizmoEvent;
import dae.prefabs.ui.events.GizmoType;
import dae.prefabs.ui.events.PickEvent;
import javax.swing.SwingUtilities;

/**
 * The object parameter ui allows the user to select an object of a give type
 * from the level by name.
 *
 * @author Koen Samyn
 */
public class ObjectParameterUI extends javax.swing.JPanel implements ParameterUI {

    private String objectName;
    private String classFilter;
    private Prefab currentNode;
    private Prefab pickedObject;
    private Parameter parameter;

    /**
     * Creates new form ObjectParameterUI
     */
    public ObjectParameterUI() {
        initComponents();
        GlobalObjects.getInstance().registerListener(this);
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
        txtObjectName = new javax.swing.JTextField();
        btnPick = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblLabel.setText("Label :");
        lblLabel.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(lblLabel, gridBagConstraints);

        txtObjectName.setEditable(false);
        txtObjectName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(txtObjectName, gridBagConstraints);

        btnPick.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/select.png"))); // NOI18N
        btnPick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPickActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(btnPick, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPickActionPerformed
        // TODO add your handling code here:
        GizmoEvent ge = new GizmoEvent(this, GizmoType.PICK);
        ge.setPickProperty(parameter.getId());
        GlobalObjects.getInstance().postEvent(ge);
    }//GEN-LAST:event_btnPickActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPick;
    private javax.swing.JLabel lblLabel;
    private javax.swing.JTextField txtObjectName;
    // End of variables declaration//GEN-END:variables

    public void setParameter(Parameter p) {
        this.parameter = p;
        lblLabel.setText(parameter.getLabel());
    }

    public void setNode(Prefab currentSelectedNode) {
        this.currentNode = currentSelectedNode;
        Object pValue = currentNode.getParameter(this.parameter.getId());
        if (pValue instanceof Prefab) {
            pickedObject = (Prefab) pValue;
            if (pickedObject != null) {
                txtObjectName.setText(pickedObject.getName());
            } else {
                txtObjectName.setText("");
            }
        }else{
            pickedObject = null;
            txtObjectName.setText(pValue!=null?pValue.toString():"");
        }
    }

    /**
     * @return the classFilter
     */
    public String getClassFilter() {
        return classFilter;
    }

    /**
     * @param classFilter the classFilter to set
     */
    public void setClassFilter(String classFilter) {
        this.classFilter = classFilter;
    }

    @Subscribe
    public void objectPicked(PickEvent pe) {
        //System.out.println("Object picked");

        if (!this.parameter.getId().equals(pe.getPickProperty())) {
            return;
        }
        pickedObject = pe.getSelectedNode();
        if (currentNode != null) {
            currentNode.setParameter(this.parameter.getId(), pickedObject, false);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtObjectName.setText(pickedObject.getName());
            }
        });
    }
}
