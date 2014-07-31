/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.collection.editor;

import com.google.common.eventbus.Subscribe;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.ui.collection.DictionaryTableModel;
import dae.prefabs.ui.events.GizmoEvent;
import dae.prefabs.ui.events.GizmoType;
import dae.prefabs.ui.events.PickEvent;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Koen Samyn
 */
public class PrefabCellEditor extends javax.swing.JPanel implements TableCellEditor {

    private Object currentValue;
    private Prefab parentPrefab;
    private Object pickedObject;
    private ArrayList<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

    /**
     * Creates new form PrefabCellEditor
     */
    public PrefabCellEditor() {
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

        lblPrefabName = new javax.swing.JLabel();
        btnTogglePick = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridBagLayout());

        lblPrefabName.setText("jLabel1");
        lblPrefabName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 289;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(lblPrefabName, gridBagConstraints);

        btnTogglePick.setText("+");
        btnTogglePick.setMargin(new java.awt.Insets(0, 14, 0, 14));
        btnTogglePick.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnTogglePickItemStateChanged(evt);
            }
        });
        add(btnTogglePick, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void btnTogglePickItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnTogglePickItemStateChanged
        // TODO add your handling code here:
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GizmoEvent ge = new GizmoEvent(this, GizmoType.PICK);
            ge.setPickProperty(parentPrefab.getName());
            GlobalObjects.getInstance().postEvent(ge);
        } else {
            GizmoEvent ge = new GizmoEvent(this, GizmoType.TRANSLATE);
            GlobalObjects.getInstance().postEvent(ge);
        }
    }//GEN-LAST:event_btnTogglePickItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnTogglePick;
    private javax.swing.JLabel lblPrefabName;
    // End of variables declaration//GEN-END:variables

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value != null) {
            lblPrefabName.setText(value.toString());
        } else {
            lblPrefabName.setText("");
        }
        btnTogglePick.setSelected(false);
        DictionaryTableModel model = (DictionaryTableModel) table.getModel();
        parentPrefab = model.getPrefab();
        return this;
    }

    public Object getCellEditorValue() {
        return pickedObject;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        fireEditingStopped();
        GizmoEvent ge = new GizmoEvent(this, GizmoType.TRANSLATE);
        GlobalObjects.getInstance().postEvent(ge);
        return true;
    }

    public void cancelCellEditing() {
        fireEditingCanceled();
        GizmoEvent ge = new GizmoEvent(this, GizmoType.TRANSLATE);
        GlobalObjects.getInstance().postEvent(ge);
    }

    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }

    protected void fireEditingCanceled() {
        pickedObject = currentValue;
        ChangeEvent ce = new ChangeEvent(this);
        CellEditorListener[] copy = new CellEditorListener[listeners.size()];
        listeners.toArray(copy);
        for (CellEditorListener listener : copy) {
            listener.editingCanceled(ce);
        }
    }

    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);
        CellEditorListener[] copy = new CellEditorListener[listeners.size()];
        listeners.toArray(copy);
        for (CellEditorListener listener : copy) {
            listener.editingStopped(ce);
        }
    }

    @Subscribe
    public void objectPicked(PickEvent pe) {
        //System.out.println("Object picked");

        if (!this.parentPrefab.getName().equals(pe.getPickProperty())) {
            return;
        }
        pickedObject = pe.getSelectedNode();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireEditingStopped();
            }
        });
    }
}