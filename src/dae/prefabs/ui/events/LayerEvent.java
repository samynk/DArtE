/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

import dae.project.Layer;

/**
 *
 * @author Koen Samyn
 */
public class LayerEvent {
    private Layer layer;
    public LayerEventType eventType = LayerEventType.SELECTED;
    
    public enum LayerEventType{SELECTED,DELETED,CREATED};
    
    /**
     * Creates a new LayerEvent.
     * @param layer the layer that was selected.
     */
    public LayerEvent(Layer layer){
        this.layer = layer;
    }
    
    /**
     * Create a new LayerEvent.
     * @param layer the layer that was selected, deleted or created.
     * @param type the type of the event (SELECTED,DELETED,CREATED).
     */
    public LayerEvent(Layer layer, LayerEventType type){
        this.layer = layer;
        this.eventType = type;
    }
    
    /**
     * Returns the layer 
     * @return the layer object.
     */
    public Layer getLayer(){
        return layer;
    }
    
    /**
     * Returns the event type.
     * @return the event type of this event.
     */
    public LayerEventType getEventType(){
        return eventType;
    }
}
