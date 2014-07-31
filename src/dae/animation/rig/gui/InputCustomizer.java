/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig.gui;

import dae.animation.rig.InputConnector;
import dae.animation.rig.Rig;

/**
 *
 * @author Koen Samyn
 */
public interface InputCustomizer {
    /**
     * Set the rig to be used.
     * @param rig the rig.
     */
    public void setRig(Rig rig);
    /**
     * Create a new input connector,based on the properties of the panel.
     * @return a new InputConnector.
     */
    public InputConnector createConnector();
    /**
     * Sets the input connector on the correct panel.
     * @param currentInputConnector the input connector to display.
     */
    public void setInputConnector(InputConnector currentInputConnector);
}
