/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class SelectionEvent {

    private Prefab node;
    private Object source;

    public SelectionEvent(Prefab node) {
        this.node = node;
    }
    
    public SelectionEvent(Prefab node, Object source){
        this.node = node;
        this.source = source;
    }

    public Prefab getSelectedNode() {
        return node;
    }
    
    public Object getSource(){
        return source;
    }
}
