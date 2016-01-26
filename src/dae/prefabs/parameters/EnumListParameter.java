package dae.prefabs.parameters;

import dae.components.ComponentType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class EnumListParameter extends Parameter{
    private Object[] choices;
    private Class enumClass;
    /**
     * Creates a new EnumListParameter object.
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public EnumListParameter(ComponentType componentType, String type, String id){
        super(componentType, type, id);
    }
    
    /**
     * Sets the enum class of this enum list parameter.
     * @param className the name of the enum class.
     */
    public void setEnumClass(String className){
        try {
            enumClass = Class.forName(className);
            choices = enumClass.getEnumConstants();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Converts the string to the correct enum value.
     * @param value
     * @return 
     */
    public Object getEnum(String value){
        return Enum.valueOf(enumClass, value);
    }

    /**
     * Gets the list of choices from this EnumList parameter.
     * @return the list of choices as an array.
     */
    public Object[] getChoices() {
        return choices;
    }
}
