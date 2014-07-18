/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;


/**
 *
 * @author Koen Samyn
 */
public class GizmoEvent {
    private GizmoType type;
    private Object source;
    
    private String pickProperty;
    
    public GizmoEvent(Object source,GizmoType type){
        this.source = source;
        this.type = type;
    }
    
    public Object getSource(){
        return source;
    }
    
    public GizmoType getType(){
        return type;
    }

    public void setPickProperty(String id) {
        this.pickProperty = id;
    }
    
    public String getPickProperty(){
        return pickProperty;
    }
}
