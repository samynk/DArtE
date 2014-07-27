/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig;

/**
 * This interface defines the methods that a connector must have to set
 * a calculated as a property of some controllable joint in the rig.
 * @author Koen Samyn
 */
public interface OutputConnector {
    /**
     * Connects an output value to the correct property of the object.
     * @param value the value to set.
     */
    public void setValue(float value);
}
