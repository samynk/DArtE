/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.event;

import dae.prefabs.Prefab;

/**
 * Interface that enables an object to listen to transformation
 * changes in a Prefab object.
 * @author samyn_000
 */
public interface TransformListener {
    /**
     * Method that is called when the transformation of a prefab has changed.
     * @param source the prefab object that is the source.
     * @param type the transform type.
     */
    public void transformChanged(Prefab source, TransformType type);
}
