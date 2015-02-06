package dae.prefabs.parameters;

import com.jme3.math.Vector3f;
import dae.components.ComponentType;

/**
 * Describes a float3 parameter.
 * @author Koen Samyn
 */
public class Float3Parameter extends Parameter {

    private Vector3f defaultValue = new Vector3f(0, 0, 0);

    /**
     * Creates a new Float3Parameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public Float3Parameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Creates a new Float3Parameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public Float3Parameter(ComponentType componentType, String label, String id, Vector3f defaultValue) {
        super(componentType, label, id);
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value for this float3 parameter.
     * @return the default value for this parameter.
     */
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
