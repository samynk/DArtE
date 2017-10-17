package dae.prefabs.ui;

import com.jme3.math.Vector3f;
import dae.prefabs.Prefab;
import dae.prefabs.ReflectionManager;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.converter.PropertyConverter;

import dae.prefabs.standard.UpdateObject;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Koen
 */
public class Float3ParameterUI extends javax.swing.JPanel implements ParameterUI, ChangeListener {

    private Parameter parameter;
    private Prefab currentNode;
    private boolean disregardEvent = false;
    private PropertyConverter converter;

    /**
     * Creates new form Float3ParameterUI
     */
    public Float3ParameterUI() {
        initComponents();

    }

    public void setFloat3(Vector3f value) {
        disregardEvent = true;
        xSpinner.setValue(value.x);
        ySpinner.setValue(value.y);
        zSpinner.setValue(value.z);
        disregardEvent = false;
    }

    public void setFloat3(float x, float y, float z) {
        disregardEvent = true;
        xSpinner.setValue(x);
        ySpinner.setValue(y);
        zSpinner.setValue(z);
        disregardEvent = false;
    }

    public Vector3f getFloat3() {
        float x = (Float) xSpinner.getValue();
        float y = (Float) ySpinner.getValue();
        float z = (Float) zSpinner.getValue();
        return new Vector3f(x, y, z);
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

        lblBracket = new javax.swing.JLabel();
        xSpinner = new javax.swing.JSpinner();
        ySpinner = new javax.swing.JSpinner();
        lblComma1 = new javax.swing.JLabel();
        lblComma2 = new javax.swing.JLabel();
        zSpinner = new javax.swing.JSpinner();
        lblCloseBracket = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lblBracket.setText("[");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 0);
        add(lblBracket, gridBagConstraints);

        xSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.5f)));
        xSpinner.setMinimumSize(new java.awt.Dimension(20, 20));
        xSpinner.setPreferredSize(new java.awt.Dimension(20, 20));
        xSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                xSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        add(xSpinner, gridBagConstraints);

        ySpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.5f)));
        ySpinner.setMinimumSize(new java.awt.Dimension(20, 20));
        ySpinner.setPreferredSize(new java.awt.Dimension(20, 20));
        ySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ySpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        add(ySpinner, gridBagConstraints);

        lblComma1.setText(",");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 1, 0, 0);
        add(lblComma1, gridBagConstraints);

        lblComma2.setText(",");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 1, 0, 0);
        add(lblComma2, gridBagConstraints);

        zSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(0.5f)));
        zSpinner.setAlignmentX(0.0F);
        zSpinner.setMinimumSize(null);
        zSpinner.setPreferredSize(new java.awt.Dimension(20, 20));
        zSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        add(zSpinner, gridBagConstraints);

        lblCloseBracket.setText("]");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 1, 0, 0);
        add(lblCloseBracket, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void xSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_xSpinnerStateChanged
        updateNode();
    }//GEN-LAST:event_xSpinnerStateChanged

    private void ySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ySpinnerStateChanged
        updateNode();
    }//GEN-LAST:event_ySpinnerStateChanged

    private void zSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zSpinnerStateChanged
        updateNode();
    }//GEN-LAST:event_zSpinnerStateChanged

    /**
     * Sets the parameter this float3 ui is bound to.
     *
     * @param p the bound parameter.
     */
    @Override
    public void setParameter(Parameter p) {
        parameter = p;
        converter = p.getConverter();
        parameter.addChangeListener(this);
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void setNode(Prefab currentSelectedNode) {

        currentNode = currentSelectedNode;
        if (currentNode == null) {
            return;
        }

        Object value = ReflectionManager.getInstance().invokeGetMethod(currentSelectedNode, parameter);
        if (converter != null) {
            value = converter.convertFromObjectToUI(value);
        }
        if (value != null && value instanceof Vector3f) {
            this.setFloat3((Vector3f) value);
        }
    }

    /**
     * Checks if a label should be created for the UI.
     *
     * @return true if a label should be created, false othwerise.
     */
    public boolean needsLabel() {
        return true;
    }

    /**
     * Sends the correct event to set the new value for the object.
     */
    private void updateNode() {
        if (disregardEvent) {
            return;
        }
        //System.out.println("updating node:"+ parameter.getId());
        Object value = getFloat3();
        if (converter != null) {
            value = converter.convertFromUIToObject(value);
        }
        if (currentNode != null) {
            //System.out.println("Setting " + property + " to " + value);
            currentNode.addUpdateObject(new UpdateObject(parameter, value, true));
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblBracket;
    private javax.swing.JLabel lblCloseBracket;
    private javax.swing.JLabel lblComma1;
    private javax.swing.JLabel lblComma2;
    private javax.swing.JSpinner xSpinner;
    private javax.swing.JSpinner ySpinner;
    private javax.swing.JSpinner zSpinner;
    // End of variables declaration//GEN-END:variables

    /**
     * Called when a property of the component is changed.
     *
     * @param evt the property change event.
     */
    public void stateChanged(final ChangeEvent e) {
        if (e.getSource() != currentNode) {
            return;
        }
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //System.out.println("state changed from outside event thread:" + parameter.getId() + e.getSource().toString());
                    stateChanged();
                }
            });
        } else {
            //System.out.println("state changed from withing event thread:"+ parameter.getId());
            stateChanged();
        }
    }

    private void stateChanged() {
        if (currentNode != null) {
            disregardEvent = true;
            Object value = ReflectionManager.getInstance().invokeGetMethod(currentNode, parameter);
            if (converter != null) {
                value = converter.convertFromObjectToUI(value);
            }
            //System.out.println("New value for " + currentNode.getName() + ":"+ parameter.getId());
            if (value != null && value instanceof Vector3f) {
                this.setFloat3((Vector3f) value);
            }
            disregardEvent = false;
        }
    }
}
