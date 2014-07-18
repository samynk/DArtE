/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.gizmos.events;

import dae.prefabs.gizmos.TranslateGizmoSpace;

/**
 *
 * @author Koen Samyn
 */
public class GizmoSpaceChangedEvent {
    private TranslateGizmoSpace space;
    
    public GizmoSpaceChangedEvent(TranslateGizmoSpace space){
        this.space = space;
    }
    
    public TranslateGizmoSpace getSpace(){
        return space;
    }
}
