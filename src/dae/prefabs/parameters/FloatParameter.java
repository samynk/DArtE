package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 * Describes a float parameter.
 * @author Koen
 */
public class FloatParameter extends Parameter {

    private float defaultValue;

    /**
     * Creates a new FloatParameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public FloatParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Creates a new FloatParameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public FloatParameter(ComponentType componentType, String type, String id, float defaultValue) {
        super(componentType, type, id);
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of this float parameter object.
     * @return the default value.
     */
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
