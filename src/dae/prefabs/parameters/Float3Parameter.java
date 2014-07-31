/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import com.jme3.math.Vector3f;

/**
 *
 * @author Koen
 */
public class Float3Parameter extends Parameter {

    private Vector3f defaultValue = new Vector3f(0, 0, 0);

    public Float3Parameter(String label, String id) {
        super(label, id);
    }

    public Float3Parameter(String label, String id, Vector3f defaultValue) {
        super(label, id);
        this.defaultValue = defaultValue;
    }

    public Vector3f getDefaultValue() {
        return defaultValue;
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return Vector3f.class;
    }
}
