package dae.prefabs.types;

import dae.prefabs.parameters.DefaultSection;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.ParameterSection;
import java.util.ArrayList;

/**
 * Support for parameter sections.
 * @author Koen Samyn
 */
public class ParameterSupport {
    private ArrayList<ParameterSection> parameterSections =
            new ArrayList<ParameterSection>();
    private DefaultSection defaults = new DefaultSection();
    
    /**
     * Creates a new ParameterSupport object.
     */
    public ParameterSupport(){
        
    }
    /**
     * Adds a parameter section.
     * @param section the parameter section to add.
     */
    public void addParameterSection(ParameterSection section){
        parameterSections.add(section);
    }
    /**
     * Removes a parameter section.
     * @param section the parameter section to remove.
     */
    public void removeParameterSection(ParameterSection section){
        parameterSections.remove(section);
    }
    
    /**
     * Returns a list of parameter sections.
     * @return 
     */
    public Iterable<ParameterSection> getParameterSections(){
        return parameterSections;
    }

    public void setDefaultSection(DefaultSection section) {
        this.defaults = section;
    }

    public Parameter findParameter(String property) {
        for (ParameterSection ps : this.parameterSections) {
            Parameter p = ps.getParameter(property);
            if (p != null) {
                return p;
            }
        }
        return null;
    }
    
     /**
     * Returns all the parameters of this ParameterSupport object.
     * @return an iterable with all the parameters.
     */
    public Iterable<Parameter> getAllParameters() {
        ArrayList<Parameter> result = new ArrayList<Parameter>();
        
        for (ParameterSection ps: this.parameterSections){
            result.addAll(ps.getAllParameters());
        }
        return result;
    }
}
