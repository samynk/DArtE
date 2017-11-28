/*
 * Digital Arts and Entertainment 
 */
package dae.prefabs.types;

import dae.prefabs.Prefab;

/**
 *
 * @author Koen.Samyn
 */
public interface ObjectTypePanel {
    /**
     * Set the prefab of this panel.
     * @param prefab the model for this panel.
     */
    public void setPrefab(Prefab prefab);
    /**
     * Clean up the panel
     */
    public void cleanUp();
}
