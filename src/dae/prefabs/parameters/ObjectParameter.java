/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class ObjectParameter extends Parameter {

    public ObjectParameter(String label, String id) {
        super(label, id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return Prefab.class;
    }
}
