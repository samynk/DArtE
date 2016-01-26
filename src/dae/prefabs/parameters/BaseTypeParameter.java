package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 *
 * @author Koen Samyn
 */
public class BaseTypeParameter extends Parameter{

    /**
     * Defines a base type parameter. The type corresponds to a 
     * object type as defined in the objecttype.types file.
     * @param ct The ComponentType this parameter belongs to.
     * @param type the actual type to create.
     * @param id the id of the parameter.
     */
    public BaseTypeParameter(ComponentType ct, String type, String id){
        super(ct,type,id);
    }
}
