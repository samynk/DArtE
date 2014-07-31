/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.parameters;

import mlproject.fuzzy.FuzzySystem;

/**
 *
 * @author Koen Samyn
 */
public class FuzzyParameter extends Parameter{

    /**
     * Creates a new Fuzzy Parameter UI.
     * @param id 
     */
    public FuzzyParameter(String type,String id){
        super(type,id);
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return FuzzySystem.class;
    }
}
