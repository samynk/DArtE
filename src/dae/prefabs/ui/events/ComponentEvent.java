package dae.prefabs.ui.events;

/**
 *
 * @author Koen Samyn
 */
public class ComponentEvent {
    private String componentId;
    
    /**
     * Creates a new component event.
     * @param componentId 
     */
    public ComponentEvent(String componentId){
        this.componentId = componentId;
    }
    
    /**
     * Returns the id of the component that needs to be created.
     * @return the component id.
     */
    public String getComponentId(){
        return componentId;
    }
}
