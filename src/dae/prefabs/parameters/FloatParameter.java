/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class FloatParameter extends Parameter {

    private float defaultValue;

    public FloatParameter(String label, String id) {
        super(label, id);
    }

    public FloatParameter(String label, String id, float defaultValue) {
        super(label, id);
        this.defaultValue = defaultValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return float.class;
    }
}
