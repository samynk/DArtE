package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 *
 * @author Koen Samyn
 */
public class TextParameter extends Parameter {

    /**
     * Creates a new TextParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     *
     */
    public TextParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Gets the class type of this parameter.
     *
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return String.class;
    }
}
