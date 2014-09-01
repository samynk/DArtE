/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.fuzzy.model;

import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import mlproject.fuzzy.FuzzyRuleBlock;
import mlproject.fuzzy.FuzzySystem;

/**
 *
 * @author Koen Samyn
 */
public class FuzzyRuleBlockListModel implements ListModel {

    private FuzzySystem system;
    private ArrayList<ListDataListener> listeners;

    public FuzzyRuleBlockListModel(FuzzySystem system) {
        this.system = system;
    }

    public int getSize() {
        return system.getNrOfRuleBlocks();
    }

    public Object getElementAt(int index) {
        return system.getFuzzyRuleBlock(index);
    }

    public void addListDataListener(ListDataListener l) {
        if (listeners == null) {
            listeners = new ArrayList<ListDataListener>();
        }
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        if (listeners == null) {
            return;
        }
        listeners.remove(l);
    }

    public int addRuleBlock(FuzzyRuleBlock rb) {
        system.addFuzzyRuleBlock(rb);
        int index = system.getIndexOfFuzzyRuleBlock(rb);
        ListDataEvent lde = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,index,index);
        if (listeners != null) {
            for (ListDataListener l : listeners) {
                l.intervalAdded(lde);
            }
        }
        return index;
    }
}
