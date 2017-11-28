package dae.prefabs.ui;

import javax.swing.DefaultComboBoxModel;
import dae.prefabs.parameters.ChoiceParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.Prefab;
import dae.prefabs.ReflectionManager;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Koen
 */
public class ChoiceParameterUI extends javax.swing.JPanel implements ParameterUI, ChangeListener {

    private ChoiceParameter parameter;
    private Parameter choiceProvider;
    private Prefab prefab;

    /**
     * Creates new form ChoiceParameterUI
     */
    public ChoiceParameterUI() {
        initComponents();
    }

    public ChoiceParameterUI(Parameter p) {
        initComponents();
        setParameter(p);
    }

    public void setChoices(String[] choice) {
        cboChoice.setModel(new DefaultComboBoxModel(choice));
    }

    public String getSelectedChoice() {
        return cboChoice.getSelectedItem().toString();
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

        cboChoice = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        cboChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboChoice.setMaximumSize(new java.awt.Dimension(100, 20));
        cboChoice.setMinimumSize(null);
        cboChoice.setPreferredSize(null);
        cboChoice.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboChoiceItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(cboChoice, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cboChoiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboChoiceItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Object newValue = evt.getItem();
            parameter.invokeSet(prefab, newValue, true);
        }
    }//GEN-LAST:event_cboChoiceItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboChoice;
    // End of variables declaration//GEN-END:variables

    @Override
    public final void setParameter(Parameter p) {
        if (p instanceof ChoiceParameter) {
            ChoiceParameter cp = (ChoiceParameter) p;
            this.parameter = cp;
            if (cp.isBoundToProperty()) {
                String parameterNameForValues = cp.getListenTo();
                choiceProvider = p.getComponentType().findParameter(parameterNameForValues);
                cboChoice.setModel(new DefaultComboBoxModel());
                choiceProvider.addChangeListener(this);
            } else {
                cboChoice.setModel(new DefaultComboBoxModel(cp.getChoices().toArray()));
            }
        }
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void setNode(Prefab prefab) {
        this.prefab = prefab;
        if (parameter.isBoundToProperty()) {
            String property = parameter.getValuesProvider();
            Object[] values = (Object[]) ReflectionManager.getInstance().invokeGetMethod(prefab, parameter.getComponentType(), property);
            if ( values != null){
                cboChoice.setModel(new DefaultComboBoxModel(values));
            }else{
                cboChoice.setModel(new DefaultComboBoxModel()); 
            }
        }

        Object value = ReflectionManager.getInstance().invokeGetMethod(prefab, parameter);
        if (value != null) {
            cboChoice.setSelectedItem(value);
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

    public void stateChanged(ChangeEvent e) {
        String property = parameter.getValuesProvider();
        Object[] values = (Object[]) ReflectionManager.getInstance().invokeGetMethod(prefab, parameter.getComponentType(), property);
        cboChoice.setModel(new DefaultComboBoxModel(values));
    }
}
