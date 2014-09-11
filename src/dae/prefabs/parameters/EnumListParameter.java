/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.parameters;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class EnumListParameter extends Parameter{
    private Object[] choices;
    /**
     * Creates a new EnumListParameter object.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     */
    public EnumListParameter(String type, String id){
        super(type,id);
    }
    
    /**
     * Sets the enum class of this enum list parameter.
     * @param className the name of the enum class.
     */
    public void setEnumClass(String className){
        try {
            Class<?> enumClass = Class.forName(className);
            choices = enumClass.getEnumConstants();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EnumListParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object[] getChoices() {
        return choices;
    }
}
