/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.types;

import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class ObjectTypeCategory {

    private HashMap<String, ObjectTypeCollection> categories = new HashMap<String, ObjectTypeCollection>();

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
}
