/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.types;

import dae.components.ComponentType;
import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class ObjectTypeCategory {

    private HashMap<String, ObjectTypeCollection> categories = new HashMap<String, ObjectTypeCollection>();
    private HashMap<String, ComponentType> components = new HashMap<String, ComponentType>();

    public ObjectTypeCategory() {
    }

    public void addObjectTypeCollection(ObjectTypeCollection collection) {
        categories.put(collection.getName(), collection);
    }

    public ObjectType getObjectType(String categoryName, String typeName) {
        ObjectTypeCollection otc = categories.get(categoryName);
        if (otc != null) {
            return otc.find(typeName);
        } else {
            return null;
        }
    }

    public Iterable<ObjectTypeCollection> getObjectTypes() {
        return categories.values();
    }

    public ObjectType find(String label) {
        for (ObjectTypeCollection otc : categories.values()) {
            ObjectType ot = otc.find(label);
            if (ot != null) {
                return ot;
            }
        }
        return null;
    }

    /**
     * Adds a component to the list of components.
     * @param comp the component to add.
     */
    public void addComponent(ComponentType comp) {
        components.put(comp.getId(),comp);
    }
    
    /**
     * Gets the component with the specified id.
     * @param id the id of the component.
     * @return the base component object.
     */
    public ComponentType getComponent(String id){
        return components.get(id);
    }
}
