package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 *
 * @author Koen
 */
public class IntParameter extends Parameter {

    private int defaultValue;

    /**
     * Creates a new IntParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public IntParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Creates a new IntParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     * @param defaultValue the defaultValue for this IntParameter object.
     */
    public IntParameter(ComponentType componentType, String type, String id, int defaultValue) {
         super(componentType, type, id);
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value for this IntParameter object.
     * @return the default value.
     */
    public int getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the class type of this parameter.
     *
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return int.class;
    }
}
