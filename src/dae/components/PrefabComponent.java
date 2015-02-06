package dae.components;

import dae.prefabs.Prefab;

/**
 * Defines a component that can be added to a prefab.
 * @author Koen Samyn
 */
public abstract class PrefabComponent implements Comparable<PrefabComponent>{
    /**
     * The id of this component, must be unique.
     */
    private String id;
    /**
     * Is this component enabled in design mode or not.
     */
    private boolean enabled;
    /**
     * The order in the user interface and for construction
     * purposes.
     */
    private int order;
    
    
    /**
     * Creates a new PrefabComponent.
     */
    public PrefabComponent(){
    }
    
    /**
     * Gets the id from the prefab component.
     * @return the id of the component.
     */
    public String getId(){
        return id;
    }
    
    /**
     * Sets the id for the prefab component.
     * @param id the id of the prefab component.
     */
    void setId(String id) {
        this.id = id;
    }
    
    /**
     * Installs the component in the prefab parent.
     * @param parent 
     */
    public abstract void install(Prefab parent); 
    
    /**
     * This method is called every tick.
     * @param deltaTime the time between ticks.
     */
    public void update(float deltaTime){
        
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the order of the prefab component.
     * @param order the order of precedence.
     */
    public  void setOrder(int order) {
        this.order = order;
    }

    /**
     * Neccessary for sorting.
     * @param other the other prefab component to compare this one to.
     * @return a positive value if this prefabcomponent is behind the other one, zero
     * if they are equal and negative it this prefabcomponent comes before the other one.
     */
    public int compareTo(PrefabComponent o) {
        int diff = o.order - order;
        return diff < 0? 1 : o.order > 0 ? -1 : 0 ;
    }
}
