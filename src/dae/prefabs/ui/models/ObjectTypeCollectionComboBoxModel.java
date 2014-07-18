/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.models;

import dae.prefabs.types.ObjectTypeCollection;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Koen
 */
public class ObjectTypeCollectionComboBoxModel implements ComboBoxModel {

    private ObjectTypeCollection selection;
    private ArrayList<ObjectTypeCollection> objects;
    private ArrayList<ListDataListener> listeners =
            new ArrayList<ListDataListener>();

    public ObjectTypeCollectionComboBoxModel(ArrayList<ObjectTypeCollection> objects) {
        this.objects = objects;
    }

    @Override
    public void setSelectedItem(Object item) {
        selection = (ObjectTypeCollection) item;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }

    @Override
    public int getSize() {
        return objects.size();
    }

    @Override
    public Object getElementAt(int index) {
        return objects.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
