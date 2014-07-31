/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class TextParameter extends Parameter {

    public TextParameter(String label, String id) {
        super(label, id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return String.class;
    }
}
