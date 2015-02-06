package dae.prefabs.parameters;

import dae.components.ComponentType;
import mlproject.fuzzy.FuzzySystem;

/**
 *
 * @author Koen Samyn
 */
public class FuzzyParameter extends Parameter {

    /**
     * Creates a new Fuzzy Parameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public FuzzyParameter(ComponentType componentType, String type, String id) {
        super( componentType, type, id);
    }

    /**
     * Gets the class type of this parameter.
     *
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return FuzzySystem.class;
    }
}
