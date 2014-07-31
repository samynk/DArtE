/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig.gui;

import dae.animation.rig.AnimationController;
import dae.animation.rig.AnimationListControl;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Koen Samyn
 */
public class AnimationControllerListModel implements ListModel {

    private AnimationListControl model;
    private ArrayList<ListDataListener> listeners =
            new ArrayList<ListDataListener>();

    public AnimationControllerListModel(AnimationListControl alc) {
        this.model = alc;
    }

    public int getSize() {
        return model.getNrOfAnimationControllers();
    }

    public Object getElementAt(int index) {
        return model.getAnimationControllerAt(index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void add(AnimationController c) {
        int index = model.addAnimationController(c);
        if (index > -1) {
            ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
            for (ListDataListener ldl : listeners) {
                ldl.intervalAdded(lde);
            }
        }
    }

    public void remove(AnimationController c) {
        int index = model.removeAnimationController(c);
        if (index > -1) {
            ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
            for (ListDataListener ldl : listeners) {
                ldl.intervalAdded(lde);
            }
        }
    }
}
