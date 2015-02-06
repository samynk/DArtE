/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import dae.prefabs.parameters.converter.PropertyConverter;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
import dae.prefabs.ui.Float3ParameterUI;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Koen
 */
public class Parameter {

    private String type;
    private String property;
    private String id;
    private String label;
    private PropertyConverter converter;
    /**
     * The Component type that this Parameter belongs to. set to null if the
     * Parameter belongs to the prefab itself.
     */
    private ComponentType componentType;
    /**
     * An list of listener that listen to change of this specific parameter.
     */
    private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

    /**
     * Creates a new Parameter object.
     *
     * @param componentType the component of the parameter object.
     * @param type the type of the parameter object.
     * @param id the id of the parameter object.
     */
    public Parameter(ComponentType componentType, String type, String id) {
        this.componentType = componentType;
        this.type = type;
        this.id = id;
        this.property = Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    /**
     * Gets the class of the object.
     *
     * @return the class of the object.
     */
    public Class getClassType() {
        return Object.class;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the type of the component.
     *
     * @param type the type of the component.
     */
    public void setComponentType(ComponentType type) {
        this.componentType = type;
    }

    /**
     * Returns the component type.
     *
     * @return the component type.
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * Checks if the parameter has a component type.
     *
     * @return true if this Parameter has a component type, false otherwise.
     */
    public boolean hasComponentType() {
        return componentType != null;
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
     * Gets the id as a property name.
     */
    public String getProperty() {
        return property;
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
        if (converterClass == null || converterClass.length() == 0) {
            return;
        }
        Class clazz;
        try {
            clazz = Class.forName(converterClass);
            if (PropertyConverter.class.isAssignableFrom(clazz)) {
                converter = (PropertyConverter) clazz.newInstance();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the property converter for this parameter.
     *
     * @return the property converter object.
     */
    public PropertyConverter getConverter() {
        return converter;
    }

    /**
     * Invokes the get method of this parameter on the prefab.The ComponentType
     * will be taken into account to invoke this get method on the correct
     * component.
     *
     * @param prefab the prefab to invoke the get method on.
     * @return the current value of the property.
     */
    public Object invokeGet(Prefab prefab) {
        Object base = prefab;
        if (this.componentType != ComponentType.PREFAB) {
            base = prefab.getComponent(componentType.getId());
        }
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(base.getClass());
        return pr.invokeGetMethod(base, property);
    }

    /**
     * Invokes the set of the property of this object. The ComponentType will be
     * taken into account to invoke the set method on the correct component.
     *
     * @param prefab the prefab to invoke the set method on.
     * @param value the value to set.
     */
    public void invokeSet(Prefab prefab, Object value, boolean undoableEdit) {
        Object base = prefab;
        if (this.componentType != ComponentType.PREFAB) {
            base = prefab.getComponent(componentType.getId());
        }
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(base.getClass());

        Object oldValue = invokeGet(prefab);
        oldValue = pr.clone(oldValue);
        boolean changed = true;
        if (oldValue != null) {
            changed = !oldValue.equals(value);
        }
        if (changed) {
            changed = pr.invokeSetMethod(base, property, value);
            if (undoableEdit && changed) {
                GlobalObjects go = GlobalObjects.getInstance();
                go.addEdit(new UndoPrefabPropertyEdit(prefab, this, oldValue, value));

                for (ChangeListener cl : listeners) {
                    cl.stateChanged(new ChangeEvent(prefab));
                }
            }
        }
    }

    /**
     * Notifies the listeners of the parameter that the value of the parameter
     * has changed.
     */
    public void notifyChangeInValue(Prefab prefab) {
        for (ChangeListener cl : listeners) {
            cl.stateChanged(new ChangeEvent(prefab));
        }
    }

    /**
     *
     * @return
     */
    public String getComponentId() {
        if (this.componentType != null) {
            return componentType.getId();
        } else {
            return null;
        }
    }

    /**
     * Adds a change listener to this parameter object.
     *
     * @param cl the change listener to add.
     */
    public void addChangeListener(ChangeListener cl) {
        listeners.add(cl);
    }

    /**
     * Converts a value from the user interface representation to the
     * object representation. If no converter exists the value itself
     * will be returned.
     * @param oValue the value to converted.
     * @return the converted value or oValue if no converter is configured.
     */
    public Object convertToObject(Object oValue) {
        if ( converter != null){
            return converter.convertFromUIToObject(oValue);
        }else{
            return oValue;
        }
    }
}
