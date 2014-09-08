/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.gizmos.events;

import com.jme3.math.Vector3f;
import dae.prefabs.AxisEnum;

/**
 *
 * @author Koen Samyn
 */
public class AutoGridEvent {
    private boolean autoGridEnabled;
    private AxisEnum mainAxis;
    
    /**
     * Creates a new AutoGridEvent.
     * @param autoGridEnabled if true, the auto grid is enabled.
     * @param mainAxis the main axis that is used to align the object.
     */
    public AutoGridEvent(boolean autoGridEnabled, AxisEnum mainAxis)
    {
        this.autoGridEnabled = autoGridEnabled;
        this.mainAxis = mainAxis;
    }
    
    /**
     * Returns the state of the autogrid.
     * @return true if the autogrid is enabled, false otherwise.
     */
    public boolean isAutoGridEnabled(){
        return autoGridEnabled;
    }
    
    /**
     * Gets the main axis that is used to align the object.
     * @return the main axis.
     */
    public AxisEnum getMainAxis(){
        return mainAxis;
    }
}
