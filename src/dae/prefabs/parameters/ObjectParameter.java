package dae.prefabs.parameters;

import dae.components.ComponentType;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class ObjectParameter extends Parameter {

    /**
     * Creates a new ObjectParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     *
     */
    public ObjectParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Gets the class type of this parameter.
     *
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return Prefab.class;
    }
}
