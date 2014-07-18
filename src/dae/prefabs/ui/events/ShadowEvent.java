/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

import dae.prefabs.lights.ShadowCastSupport;

/**
 * This event type signals that a light has enabled or disabled
 * shadows. 
 * @author Koen Samyn
 */
public class ShadowEvent {
    private ShadowCastSupport source;
    
    public ShadowEvent(ShadowCastSupport source){
        this.source = source;
    }
    
    public ShadowCastSupport getSource(){
        return source;
    }
}
