package dae.prefabs.events;

import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * Interface to receive component events for a prefab. The event can be that an
 * a component was removed, or that a component was added.
 * @author Koen Samyn
 */
public interface ComponentListener {
    /**
     * Called when a PrefabComponent was added to a prefab.
     * @param prefab the prefab where a component was added.
     * @param added the added PrefabComponent.
     */
    public void componentAdded(Prefab prefab, PrefabComponent added);
     /**
     * Called when a PrefabComponent was removed from the prefab.
     * @param prefab the prefab where a component was added.
     * @param removed the PrefabComponent that was removed.
     */
    public void componentRemoved(Prefab prefab, PrefabComponent removed);
}
