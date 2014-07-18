/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.types;

import dae.prefabs.parameters.DefaultSection;
import dae.prefabs.parameters.ParameterSection;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.Float3Parameter;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Koen
 */
public class ObjectType {

    private String label;
    private String category;
    private String objectClass;
    private String extraInfo;
    private ArrayList<ParameterSection> parameterSections =
            new ArrayList<ParameterSection>();
    private DefaultSection defaults = new DefaultSection();
    
    private boolean loadFromExtraInfo = false;

    /*
     * Creates a new objectype.
     * @param category the category of the object type.
     * @param label the label of the object.
     * @param objectClass the Java class for this object.
     * @param extraInfo the extra info for this object.
     * @param loadFromExtraInfo use a standard loader for this object.
     */
    public ObjectType(String category, String label, String objectClass, String extraInfo, boolean loadFromExtraInfo) {
        this.category = category;
        this.label = label;
        this.objectClass = objectClass;
        this.extraInfo = extraInfo;
        this.loadFromExtraInfo = loadFromExtraInfo;
    }
    
    /**
     * @return the objectToCreate
     */
    public String getObjectToCreate() {
        return objectClass;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String asset) {
        this.extraInfo = asset;
    }

    /**
     * Returns the category of the type to create.
     *
     * @return the category of the type to create.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the category of the type to create.
     *
     * @return the category of the type to create.
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param objectToCreate the objectToCreate to set
     */
    public void setObjectToCreate(String objectToCreate) {
        this.objectClass = objectToCreate;
    }

    public void addParameterSection(ParameterSection section) {
        parameterSections.add(section);
    }

    public void removeParameterSection(ParameterSection section) {
        parameterSections.remove(section);
    }

    public Iterable<ParameterSection> getParameterSections() {
        return parameterSections;
    }

    public void setDefaultSection(DefaultSection section) {
        this.defaults = section;
    }

    public Vector3f getDefaultVec3Parameter(String property) {
        Parameter p = defaults.getParameter(property);
        if (p instanceof Float3Parameter) {
            return ((Float3Parameter) p).getDefaultValue();
        } else {
            return new Vector3f();
        }

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

    public boolean usesDefaultLoader() {
        return this.loadFromExtraInfo;
    }
}
