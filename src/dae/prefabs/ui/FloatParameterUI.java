package dae.prefabs.ui;

import dae.prefabs.Prefab;
import dae.prefabs.ReflectionManager;
import dae.prefabs.parameters.FloatParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.standard.UpdateObject;

/**
 *
 * @author Koen
 */
public class FloatParameterUI extends javax.swing.JPanel implements ParameterUI {

    private FloatParameter parameter;
    private boolean disregardEvent;
    private Prefab currentNode;

    /**
     * Creates new form FloatParameterUI
     */
    public FloatParameterUI() {
        initComponents();
    }

    public float getValue() {
        return (Float) spnFloatValue.getValue();
    }

    public void setValue(float value) {
        disregardEvent = true;
        spnFloatValue.setValue(value);
        disregardEvent = false;
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

        spnFloatValue = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        spnFloatValue.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
        spnFloatValue.setMinimumSize(null);
        spnFloatValue.setPreferredSize(null);
        spnFloatValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnFloatValueStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(spnFloatValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void spnFloatValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnFloatValueStateChanged
        if (disregardEvent) {
            return;
        }
        
        if (currentNode != null) {
            currentNode.addUpdateObject(new UpdateObject(parameter, (Float) spnFloatValue.getValue(), true));
        }
    }//GEN-LAST:event_spnFloatValueStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner spnFloatValue;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setParameter(Parameter p) {
        parameter = (FloatParameter) p;
    }
    
    @Override
    public Parameter getParameter(){
        return parameter;
    }

    @Override
    public void setNode(Prefab prefab) {
        currentNode = prefab;
        Object value = ReflectionManager.getInstance().invokeGetMethod(prefab,parameter); 
        if ( value != null ){
            spnFloatValue.setValue(value);
        }
        
    }
    
    /**
     * Checks if a label should be created for the UI.
     * @return true if a label should be created, false othwerise.
     */
    @Override
    public boolean needsLabel(){
        return true;
    }
}
