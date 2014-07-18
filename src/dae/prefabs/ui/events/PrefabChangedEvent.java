/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import dae.prefabs.Prefab;

/**
 *
 * @author samyn_000
 */
public class PrefabChangedEvent {

    private Prefab node;
    private PrefabChangedEventType type;

    public PrefabChangedEvent(Prefab node, PrefabChangedEventType type) {
        this.node = node;
        this.type = type;
    }

    public Prefab getChangedNode() {
        return node;
    }
    
    public PrefabChangedEventType getType(){
        return type;
    }
}
