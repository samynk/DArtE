/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class BooleanParameter extends Parameter {

    private boolean defaultValue;

    public BooleanParameter(String label, String id) {
        super(label, id);
    }

    public BooleanParameter(String label, String id, boolean defaultValue) {
        super(label, id);
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return boolean.class;
    }
    
    
}
