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
     * This method is called to allow the OutputConnector to determine then
     * necessary objects to connect to.
     * @param rig the rig with the necessary inputs.
     */
    public void initialize(Rig rig);
    /**
     * Connects an output value to the correct property of the object.
     * @param value the value to set.
     */
    public void setValue(float value);
    /**
     * Checks if the output is properly initialized.
     * @return true if the connector is properly initialized, false otherwise.
     */
    public boolean isInitialized();
     /**
     * Returs a copy (non initialized) of this output connector.
     */
    public OutputConnector cloneConnector();
    /**
     * Sets the joint name of this input connector.
     * @param jointName the joint name.
     */
    public void setJointName(String jointName);
    /**
     * Returns the joint name of this input connector.
     * @return the joint name.
     */
    public String getJointName();
}