/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class PickEvent {

    private Prefab node;
    private Object source;
    
    private String pickProperty;

    public PickEvent(Prefab node, String pickProperty) {
        this.node = node;
        this.pickProperty = pickProperty;
    }

    public PickEvent(Prefab node, Object source, String pickProperty) {
        this.node = node;
        this.source = source;
        this.pickProperty = pickProperty;
    }

    public Prefab getSelectedNode() {
        return node;
    }

    public Object getSource() {
        return source;
    }
    
    public String getPickProperty(){
        return pickProperty;
    }
}
