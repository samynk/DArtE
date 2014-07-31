/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig;

/**
 * The input connector interface connects a calculated value (for example
 * an angle)  with the input of a controller.
 * @author Koen Samyn
 */
public interface InputConnector {
    /**
     * This method is called to allow the InputConnector to determine then
     * necessary inputs.
     * @param rig the rig with the necessary inputs.
     */
    public void initialize(Rig rig);
    /**
     * Checks if the output is properly initialized.
     * @return true if the connector is properly initialized, false otherwise.
     */
    public boolean isInitialized();
    /**
     * Returns the calculated value.
     * @return the calculated value.
     */
    public float getValue();
    /**
     * Returs a copy (non initialized) of this input connector.
     */
    public InputConnector cloneConnector();
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
