/*
 * Digital Arts and Entertainment 
 */
package dae.prefabs;

/**
 * This interface indicates that an implementator can be tagged
 * as part of a layer.
 * An object can be part of multiple layers.
 * @author Koen.Samyn
 */
public interface LayerTaggable {
    /**
     * Tags the object as part of a layer. 
     * @param layer the layer to add the object to.
     */
    public void addToLayer(String layer);
    /**
     * Removes the object from a layer.
     * @param layer the layer to remove the object from.
     */
    public void removeFromLayer(String layer);
    /**
     * Sets the visibility of the object.
     * @param visible true if the object should be visible, false otherwise.
     */
    public void setVisible(boolean visible);
    /**
     * Checks the visibility of the object.
     * @return true if the object is visible, false otherwise.
     */
    public boolean isVisible();
    
}
