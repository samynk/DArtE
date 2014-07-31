/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.collection;

import dae.prefabs.Prefab;
import dae.prefabs.parameters.DictionaryParameter;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A quick way to edit dictionary properties of a prefab. Also supports the
 * addition and removal of keys.
 *
 * @author Koen Samyn
 */
public class DictionaryTableModel implements TableModel {

    private Prefab prefab;
    private DictionaryParameter parameter;
    private ArrayList<TableModelListener> listeners =
            new ArrayList<TableModelListener>();
    

    public DictionaryTableModel(Prefab prefab, DictionaryParameter p) {
        this.prefab = prefab;
        this.parameter = p;
    }

    /**
     * @return the number of keys in the dictionary.
     */
    public int getRowCount() {
        return parameter.getNrOfKeys(prefab);
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Key";
            case 1:
                return "Value";
        }
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return parameter.getBaseType().getClassType();
        }
        return Object.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        String key = parameter.getKeyAt(prefab,rowIndex);
        switch(columnIndex){
            case 0 : return key;
            case 1: return parameter.getProperty(prefab, key);
        }
        return null;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ( columnIndex == 1)
        {
            String key = parameter.getKeyAt(prefab, rowIndex);
            parameter.setProperty(prefab, key, aValue);
        }
    }

    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void addPropertyKey(String key) {
        parameter.addKey(prefab, key);
        int indexOfKey = parameter.getIndexOfKey(prefab, key);
        TableModelEvent event = new TableModelEvent(this,indexOfKey, indexOfKey,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT);
        for( TableModelListener listener: listeners)
        {
            listener.tableChanged(event);
        }
    }

    void removePropertyKey(String key) {
        int indexOfKey = parameter.getIndexOfKey(prefab, key);
        parameter.removeKey(prefab, key);
        TableModelEvent event = new TableModelEvent(this,indexOfKey, indexOfKey,TableModelEvent.ALL_COLUMNS,TableModelEvent.DELETE);
        for( TableModelListener listener: listeners)
        {
            listener.tableChanged(event);
        }
    }

    public Prefab getPrefab() {
        return this.prefab;
    }
}
