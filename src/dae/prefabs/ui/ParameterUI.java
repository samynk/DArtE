package dae.prefabs.ui;

import dae.prefabs.Prefab;
import dae.prefabs.parameters.Parameter;

/**
 *
 * @author Koen
 */
public interface ParameterUI {

   

    public void setNode(Prefab currentSelectedNode);

    /**
     * Sets the parameter for the parameter ui.
     * @param p the parameter to set.
     */
     public void setParameter(Parameter p);
     
    /**
     * Gets the parameter for the parameter ui.
     * @return the parameter.
     */ 
    public Parameter getParameter();
    
    /**
     * Checks if a label should be created for the UI.
     * @return true if a label should be created, false othwerise.
     */
    public boolean needsLabel();
}
