package dae.components;

import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class ComponentList {
    private ArrayList<PrefabComponent> components= new ArrayList<PrefabComponent>();
    
    /**
     * Creates a new ComponentList object.
     */
    public ComponentList(){
        
    }
    
    public void addComponent(PrefabComponent c){
        components.add(c);
    }
    
    public Iterable<PrefabComponent> getComponents(){
        return components;
    }
}
