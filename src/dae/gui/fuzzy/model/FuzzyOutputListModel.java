/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.fuzzy.model;

import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;

/**
 *
 * @author Koen Samyn
 */
public class FuzzyOutputListModel implements ListModel{
    private FuzzySystem system;
    private ArrayList<ListDataListener> listeners = 
            new ArrayList<ListDataListener>();
    
    public FuzzyOutputListModel(FuzzySystem system){
        this.system = system;
    }

    /**
     * Returns the number of inputs.
     * @return the number of inputs.
     */
    public int getSize() {
        return system.getNrOfOutputs();
    }

    /**
     * Gets the element at the given index.
     * @param index the index of the element.
     * @return the FuzzyVariable at the given index.
     */
    public Object getElementAt(int index) {
        return system.getFuzzyOutputAt(index);
    }

    /**
     * Add a ListDataListener object.
     * @param l the list data listener object.
     */
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

     /**
     * Remove a ListDataListener object.
     * @param l the list data listener object.
     */
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    /**
     * Adds a fuzzy variable to the list.
     * @param fv the fuzzy variable to add.
     */
    public int addFuzzyVariable(FuzzyVariable fv) {
        system.addFuzzyOutput(fv);
        int index = system.getOutputIndex(fv);
        ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,index,index);
        for(ListDataListener ldl: listeners){
            ldl.intervalAdded(lde);
        }
        return index;
    }

    public void removeFuzzyVariable(int selectedIndex) {
        FuzzyVariable output = system.getFuzzyOutputAt(selectedIndex);
        system.removeFuzzyOutput(output);
        ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED,selectedIndex,selectedIndex);
        for(ListDataListener ldl: listeners){
            ldl.intervalRemoved(lde);
        }
    }
}
