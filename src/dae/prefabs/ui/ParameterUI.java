/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui;

import dae.prefabs.Prefab;
import dae.prefabs.parameters.Parameter;

/**
 *
 * @author Koen
 */
public interface ParameterUI {

    public void setParameter(Parameter p);

    public void setNode(Prefab currentSelectedNode);
}
