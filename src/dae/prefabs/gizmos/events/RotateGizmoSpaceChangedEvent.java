/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.gizmos.events;

import dae.prefabs.gizmos.RotateGizmoSpace;

/**
 *
 * @author Koen Samyn
 */
public class RotateGizmoSpaceChangedEvent {
    private RotateGizmoSpace newSpace;
    
    public RotateGizmoSpaceChangedEvent(RotateGizmoSpace space){
        this.newSpace = space;
    }
    
    public RotateGizmoSpace getSpace(){
        return newSpace;
    }
}
