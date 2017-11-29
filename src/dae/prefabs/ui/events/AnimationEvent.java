/*
 * Digital Arts and Entertainment 
 */
package dae.prefabs.ui.events;

import dae.animation.rig.Rig;

/**
 * Event to use when an animation key has been created or deleted.
 * @author Koen.Samyn
 */
public class AnimationEvent {
    private final AnimationEventType type;
    private final Rig rig;
    
    public AnimationEvent(Rig rig, AnimationEventType type){
        this.rig = rig;
        this.type = type;
    }
    
    public Rig getRig(){
        return rig;
    }
    
    public AnimationEventType getType(){
        return type;
    }
}
