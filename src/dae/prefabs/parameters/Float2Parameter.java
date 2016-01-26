package dae.prefabs.parameters;

import com.jme3.math.Vector2f;
import dae.components.ComponentType;

/**
 * Describes a float2 parameter.
 * @author Koen Samyn
 */
public class Float2Parameter extends Parameter {

    private Vector2f defaultValue = new Vector2f(0, 0);

    /**
     * Creates a new Float3Parameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public Float2Parameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Creates a new Float3Parameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public Float2Parameter(ComponentType componentType, String label, String id, Vector2f defaultValue) {
        super(componentType, label, id);
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value for this float3 parameter.
     * @return the default value for this parameter.
     */
    public Vector2f getDefaultValue() {
        return defaultValue;
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return Vector2f.class;
    }
}
