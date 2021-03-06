package dae.animation.rig.gui;

import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import dae.animation.rig.AnimationController;
import dae.animation.rig.ConnectorType;
import dae.animation.rig.Joint;
import dae.animation.rig.OutputConnector;
import dae.animation.rig.Rig;
import dae.animation.skeleton.RevoluteJoint;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

/**
 *
 * @author Koen Samyn
 */
public class OutputConnectorPanel extends javax.swing.JPanel {

    private Rig rig;
    private AnimationController currentAnimationController;
    private OutputConnector currentOutputConnector;
    private String currentPanelId;

    /**
     * Creates new form InputConnectorPanel
     */
    public OutputConnectorPanel() {
        initComponents();
    }

    public void setRig(Rig rig) {
        this.rig = rig;
        List<Joint> joints = new ArrayList<>();
        rig.breadthFirstTraversal((Spatial spatial) -> {
            if ( spatial instanceof Joint){
                joints.add((Joint)spatial);
            }
        });
        
        cboJointNames.setModel(new DefaultComboBoxModel(joints.toArray()));
        if ( joints.size() > 0 )
        {
            cboJointNames.setSelectedIndex(0);
            updateJoint(joints.get(0));
        }

        setRigOnCustomizerPanel(rig);
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

        lblJoint = new javax.swing.JLabel();
        cboJointNames = new javax.swing.JComboBox();
        lblType = new javax.swing.JLabel();
        cboTypes = new javax.swing.JComboBox();
        pnlConnectorTypes = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));
        setLayout(new java.awt.GridBagLayout());

        lblJoint.setText("Joint : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lblJoint, gridBagConstraints);

        cboJointNames.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJointNamesItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 5);
        add(cboJointNames, gridBagConstraints);

        lblType.setText("Type :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lblType, gridBagConstraints);

        cboTypes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTypesItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 5);
        add(cboTypes, gridBagConstraints);

        pnlConnectorTypes.setLayout(new java.awt.CardLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlConnectorTypes, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cboTypesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTypesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            ConnectorType ict = (ConnectorType) evt.getItem();
            updateCustomizerPanel(ict);
        }
    }//GEN-LAST:event_cboTypesItemStateChanged

    private void cboJointNamesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJointNamesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Object selected = evt.getItem();
            if (selected instanceof Joint) {
                Joint j= (Joint) selected;
                updateJoint(j);
                if (currentOutputConnector != null) {
                    currentOutputConnector.setJointName(j.getName());
                }
            }
        }
    }//GEN-LAST:event_cboJointNamesItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboJointNames;
    private javax.swing.JComboBox cboTypes;
    private javax.swing.JLabel lblJoint;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel pnlConnectorTypes;
    // End of variables declaration//GEN-END:variables

    private void updateCustomizerPanel(ConnectorType ict) {

        CardLayout cl = (CardLayout) pnlConnectorTypes.getLayout();
        // check if a panel with the correct class type is allready present.
        Component[] c = pnlConnectorTypes.getComponents();
        try {
            Class panelClass = Class.forName(ict.getCustomizerPanelClass());
            for (int i = 0; i < c.length; ++i) {

                if (panelClass.equals(c[i].getClass())) {
                    if (!ict.getId().equals(currentPanelId)) {
                        currentPanelId = ict.getId();
                        // select the correct card.
                        cl.show(pnlConnectorTypes, ict.getId());
                        currentOutputConnector = createNewOutputConnector();
                        currentAnimationController.setOutput(currentOutputConnector);
                    }
                    setOutputConnectorOnCustomizerPanel();
                    return;
                }
            }
            // the card was not found.
            OutputCustomizer panel = (OutputCustomizer) panelClass.newInstance();
            panel.setRig(rig);
            pnlConnectorTypes.add((JComponent) panel, ict.getId());
            currentPanelId = ict.getId();
            cl.show(pnlConnectorTypes, ict.getId());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OutputConnectorPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(OutputConnectorPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OutputConnectorPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateJoint(Joint selected) {
        List<ConnectorType> types = selected.getOutputConnectorTypes();
        cboTypes.setModel(new DefaultComboBoxModel(types.toArray()));
        cboTypes.setSelectedIndex(0);
        updateCustomizerPanel(types.get(0));
    }

    public void setAnimationController(AnimationController ac) {
    currentAnimationController = ac;
        if (ac.getOutput() != null) {
            currentOutputConnector = ac.getOutput();
            currentOutputConnector.initialize(rig);
            String jointName = currentOutputConnector.getJointName();
            if (jointName != null) {
                Spatial child = rig.getChild(jointName);
                if (child instanceof Joint) {
                    cboJointNames.setSelectedItem(child);
                }
            }
            setOutputConnectorOnCustomizerPanel();
        } else {
            OutputConnector oc = createNewOutputConnector();
            if (oc != null) {
                currentOutputConnector = oc;
                Object joint = cboJointNames.getSelectedItem();
                if (joint != null && joint instanceof Joint) {
                    oc.setJointName(((Joint) joint).getName());
                }
            }

            ac.setOutput(oc);
        }
    }

    private void setRigOnCustomizerPanel(Rig rig) {
        for (Component c : pnlConnectorTypes.getComponents()) {
            if (c.isVisible() && c instanceof OutputCustomizer) {
                OutputCustomizer oc = (OutputCustomizer) c;
                oc.setRig(rig);
            }
        }
    }

    private OutputConnector createNewOutputConnector() {
        for (Component c : pnlConnectorTypes.getComponents()) {
            if (c.isVisible() && c instanceof OutputCustomizer) {
                OutputCustomizer oc = (OutputCustomizer) c;
                return oc.createConnector();
            }
        }
        return null;
    }

    private void setOutputConnectorOnCustomizerPanel() {
        for (Component c : pnlConnectorTypes.getComponents()) {
            if (c.isVisible() && c instanceof OutputCustomizer) {
                OutputCustomizer oc = (OutputCustomizer) c;
                oc.setOutputConnector(currentOutputConnector);
            }
        }
    }
}
