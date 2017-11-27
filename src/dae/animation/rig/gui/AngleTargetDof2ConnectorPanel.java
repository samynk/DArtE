package dae.animation.rig.gui;

import com.jme3.scene.Spatial;
import dae.animation.rig.AngleTargetConnector;
import dae.animation.rig.AngleTargetDof2Connector;
import dae.animation.rig.InputConnector;
import dae.animation.rig.Rig;
import dae.animation.skeleton.AttachmentPoint;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Koen Samyn
 */
public class AngleTargetDof2ConnectorPanel extends javax.swing.JPanel implements InputCustomizer {

    private Rig rig;
    private AngleTargetConnector currentConnector;

    /**
     * Creates new form AngleTargetConnectorPanel
     */
    public AngleTargetDof2ConnectorPanel() {
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

        lblTargetName = new javax.swing.JLabel();
        cboTargetName = new javax.swing.JComboBox();
        lblAttachmentPoint = new javax.swing.JLabel();
        cboAttachmentPointName = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblTargetName.setText("Target :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 0);
        add(lblTargetName, gridBagConstraints);

        cboTargetName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTargetName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTargetNameItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 6, 10);
        add(cboTargetName, gridBagConstraints);

        lblAttachmentPoint.setText("Attachment Point :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        add(lblAttachmentPoint, gridBagConstraints);

        cboAttachmentPointName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboAttachmentPointName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboAttachmentPointNameItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 10);
        add(cboAttachmentPointName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 1.0;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cboTargetNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTargetNameItemStateChanged
        if (currentConnector != null) {
            String name = (String) evt.getItem();
            if (name != null) {
                currentConnector.setTargetName(name);
            }
        }
    }//GEN-LAST:event_cboTargetNameItemStateChanged

    private void cboAttachmentPointNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboAttachmentPointNameItemStateChanged
        if (currentConnector != null) {
            Object attachmentPoint = evt.getItem();
            if (attachmentPoint != null && attachmentPoint instanceof AttachmentPoint) {
                AttachmentPoint ap = (AttachmentPoint) attachmentPoint;
                currentConnector.setAttachmentName(ap.getName());
            }
        }
    }//GEN-LAST:event_cboAttachmentPointNameItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboAttachmentPointName;
    private javax.swing.JComboBox cboTargetName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblAttachmentPoint;
    private javax.swing.JLabel lblTargetName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setRig(Rig rig) {
        this.rig = rig;
        List<AttachmentPoint> attachmentPoints = rig.descendantMatches(AttachmentPoint.class);
        cboAttachmentPointName.setModel(new DefaultComboBoxModel(attachmentPoints.toArray()));
        String[] targets = new String[rig.getNrOfTargetKeys()];
        for (int i = 0; i < rig.getNrOfTargetKeys(); ++i) {
            targets[i] = rig.getTargetKeyAt(i);
        }
        cboTargetName.setModel(new DefaultComboBoxModel(targets));
    }

    @Override
    public InputConnector createConnector() {
        AngleTargetDof2Connector result = new AngleTargetDof2Connector();
        AttachmentPoint selected = (AttachmentPoint) cboAttachmentPointName.getSelectedItem();
        if (selected != null) {
            result.setAttachmentName(selected.getName());
        }
        Object sel = cboTargetName.getSelectedItem();
        if (sel != null) {
            String targetKey = sel.toString();
            result.setTargetName(targetKey);
        }
        return result;
    }

    @Override
    public void setInputConnector(InputConnector ic) {
        if (ic instanceof AngleTargetConnector) {
            AngleTargetConnector atc = (AngleTargetConnector) ic;
            this.currentConnector = atc;
            String attachmentName = atc.getAttachmentName();
            String targetKey = atc.getTargetName();

            Spatial child = rig.getChild(attachmentName);
            if (child instanceof AttachmentPoint) {
                cboAttachmentPointName.setSelectedItem(child);
            }
            cboTargetName.setSelectedItem(targetKey);
        }
    }
}
