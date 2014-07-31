/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig.gui;

import dae.animation.rig.OutputConnector;
import dae.animation.rig.Rig;

/**
 *
 * @author Koen Samyn
 */
public interface OutputCustomizer {
    public void setRig(Rig rig);
    public OutputConnector createConnector();

    public void setOutputConnector(OutputConnector currentOutputConnector);
}
