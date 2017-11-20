package dae.animation.rig;

import dae.prefabs.Prefab;
import dae.prefabs.standard.PrefabPlaceHolder;

/**
 * Defines a call back for PrefabPlaceHolder objects.
 * @author Koen Samyn
 */
public interface PrefabPlaceHolderCallback {
    /**
     * Called when the actual prefab has been found.
     * @param actualPrefab the prefab that was found.
     * @param placeHolder the placeholder object that held the place of the actual prefab.
     */
    public void prefabFound(Prefab actualPrefab, PrefabPlaceHolder placeHolder);
}
