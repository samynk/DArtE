package dae.prefabs.ui;

import dae.prefabs.Prefab;
import dae.prefabs.ReflectionManager;
import dae.prefabs.parameters.IntParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.standard.UpdateObject;

/**
 *
 * @author Koen
 */
public class IntParameterUI extends javax.swing.JPanel implements ParameterUI {

    private IntParameter parameter;
    private boolean disregardEvent;
    private Prefab currentNode;

    /**
     * Creates new form FloatParameterUI
     */
    public IntParameterUI() {
        initComponents();
    }

    public int getValue() {
        return (Integer) spnIntegerValue.getValue();
    }

    public void setValue(int value) {
        disregardEvent = true;
        spnIntegerValue.setValue(value);
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

        spnIntegerValue = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        spnIntegerValue.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        spnIntegerValue.setMinimumSize(new java.awt.Dimension(50, 22));
        spnIntegerValue.setPreferredSize(new java.awt.Dimension(50, 22));
        spnIntegerValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnIntegerValueStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(spnIntegerValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void spnIntegerValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnIntegerValueStateChanged
        if (disregardEvent) {
            return;
        }
        if (currentNode != null) {
            currentNode.addUpdateObject(new UpdateObject(parameter, (Integer) spnIntegerValue.getValue(), true));
        }
    }//GEN-LAST:event_spnIntegerValueStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner spnIntegerValue;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setParameter(Parameter p) {
        parameter = (IntParameter) p;
    }
    
    public Parameter getParameter(){
        return parameter;
    }

    @Override
    public void setNode(Prefab currentSelectedNode) {
        currentNode = currentSelectedNode;
        Object value = ReflectionManager.getInstance().invokeGetMethod(currentSelectedNode,parameter); 
        if ( value != null ){
            spnIntegerValue.setValue(value);
        }
    }
    
    /**
     * Checks if a label should be created for the UI.
     * @return true if a label should be created, false othwerise.
     */
    public boolean needsLabel(){
        return true;
    }
}
