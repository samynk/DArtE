/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 *
 * @author Koen
 */
public class BooleanParameter extends Parameter {

    private boolean defaultValue;

    /**
     * Creates a new action parameter.
     * @param componentType the component type of the action parameter.
     * @param type the type of the parameter (string, int, float, color, ...)
     * @param id the id of the property of the prefab or component.
     */
    public BooleanParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Returns the default value for the parameter.
     * @return the default value for the parameter.
     */
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
