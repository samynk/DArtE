package dae.prefabs.parameters;

import dae.components.ComponentType;
import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class ChoiceParameter extends Parameter {

    private ArrayList<String> choices = new ArrayList<String>();
    private String listenTo;
    private String valuesProvider;

    /**
     * Creates a new Parameter object.
     * @param componentType the component of the parameter object.
     * @param type the type of the parameter object.
     * @param id the id of the parameter object.
     */
    public ChoiceParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Adds a choice to the list of choices.
     * @param choice the choice to add.
     */
    public void addChoice(String choice) {
        choices.add(choice);
    }

    /**
     * Returns the list of choices from this ChoiceParameter.
     * @return the list of choices.
     */
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

    /**
     * Sets the property name that will provide the values for this parameter.
     * @param values 
     */
    public void setListenTo(String values) {
        this.listenTo = values;
    }
    
    public String getListenTo(){
        return listenTo;
    }
    
    public void setValuesProvider(String provider){
        this.valuesProvider = provider;
    }
    
    public String getValuesProvider(){
        return valuesProvider;
    }

    public boolean isBoundToProperty() {
        return listenTo != null;
    }
}
