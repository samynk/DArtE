package dae.components;

import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.types.ParameterSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The component type describes how to create a component and how to set and get
 * parameters from the component.
 *
 * @author Koen Samyn
 */
public class ComponentType extends ParameterSupport {

    private String id;
    private String className;
    private int order;
    private int cid;
    public static ComponentType PREFAB = new ComponentType("__PREFAB");

    /**
     * Creates a new component.
     */
    public ComponentType() {
    }

    /**
     * Creates a new component with a given id.
     *
     * @param id the id of the component type.
     */
    public ComponentType(String id) {
        this.id = id;
    }

    /**
     * Returns the name of the component.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the component.
     *
     * @param id the new id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Returns the class id of this component.
     * @return the class id.
     */
    public int getCID(){
        return cid;
    }
    
    /**
     * Sets the class id of this component.
     * @param cidi the class id of this component.
     */
    public void setCID(int cidi) {
        this.cid = cidi;
    }

    /**
     * Sets the className of the component.
     *
     * @param className the classname of the component.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the class name of the component.
     *
     * @return the classname.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Creates a new empty component on the basis of the class name.
     *
     * @return the new component, or null if the creation failed.
     */
    public PrefabComponent create() {
        PrefabComponent result = null;
        try {
            Class c = Class.forName(className);
            Object component = c.newInstance();
            if (component instanceof PrefabComponent) {
                result = (PrefabComponent) component;
                result.setId(this.id);
                result.setOrder(this.order);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ComponentType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ComponentType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ComponentType.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return result;
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    /**
     * Copies the information of this prefabcomponent into the target component.
     *
     * @param source the source to copy.
     * @param target the target for the copy operation.
     */
    public void copy(Prefab source, Prefab target) {
        for (Parameter p : this.getAllParameters()) {
            Object value = p.invokeGet(source);
            
            if (value != null) {
                Object cloned = PropertyReflector.clone(value);
                p.invokeSet(target, cloned, false);
            }
        }
    }


}