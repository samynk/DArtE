/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

/**
 *
 * @author Koen Samyn
 */
public class ZoomEvent {
    private ZoomEventType eventType;
    
    public ZoomEvent(ZoomEventType type)
    {
        this.eventType = type;
    }
    
    public ZoomEventType getEventType(){
        return eventType;
    }
}
