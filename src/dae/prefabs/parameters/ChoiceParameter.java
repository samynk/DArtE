/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class ChoiceParameter extends Parameter {

    private ArrayList<String> choices = new ArrayList<String>();

    public ChoiceParameter(String label, String id) {
        super(label, id);
    }

    public void addChoice(String choice) {
        choices.add(choice);
    }

    public ArrayList<String> getChoices() {
        return choices;
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
