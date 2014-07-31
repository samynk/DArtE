/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.parameters;

/**
 *
 * @author Koen Samyn
 */
public class ConnectorParameter extends Parameter{

    /**
     * Creates a new Fuzzy Parameter UI.
     * @param id 
     */
    public ConnectorParameter(String type,String id){
        super(type,id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return Object.class;
    }
}
