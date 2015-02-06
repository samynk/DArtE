package dae.prefabs.parameters;

import com.jme3.math.ColorRGBA;
import dae.components.ComponentType;

/**
 *
 * @author Koen Samyn
 */
public class ColorParameter extends Parameter {

    /**
     * Creates a new Parameter object.
     * @param componentType the component of the parameter object.
     * @param type the type of the parameter object.
     * @param id the id of the parameter object.
     */
    public ColorParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return ColorRGBA.class;
    }
}
