/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.prefabs.parameters.converter.PropertyConverter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class Parameter {

    private String type;
    private String id;
    private String label;
    private PropertyConverter converter;

    public Parameter(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the converter class to use for this parameter.
     *
     * @param converter the converter class to use.
     */
    public void setConverter(String converterClass) {
        if (converterClass == null || converterClass.length()==0)
            return;
        Class clazz;
        try {
            clazz = Class.forName(converterClass);
            if (PropertyConverter.class.isAssignableFrom(clazz)) {
                converter = (PropertyConverter) clazz.newInstance();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns the property converter for this parameter.
     * @return the property converter object.
     */
    public PropertyConverter getConverter(){
        return converter;
    }
}
