/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author Koen
 */
public class ColorParameter extends Parameter {

    public ColorParameter(String label, String id) {
        super(label, id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return ColorRGBA.class;
    }
}
