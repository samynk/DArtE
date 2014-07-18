/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.types;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class ObjectTypeCollection {

    private String name;
    private ArrayList<ObjectType> objectTypes =
            new ArrayList<ObjectType>();
    private HashMap<String, ObjectType> objectTypeMap =
            new HashMap<String, ObjectType>();

    public ObjectTypeCollection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addObjectType(ObjectType type) {
        objectTypes.add(type);
        objectTypeMap.put(type.getLabel(), type);
    }

    public void removeObjectType(ObjectType type) {
        objectTypes.remove(type);
        objectTypeMap.remove(type.getLabel());
    }

    @Override
    public String toString() {
        return name;
    }

    public Iterable<ObjectType> getObjectTypes() {
        return objectTypes;
    }

    public ObjectType find(String label) {
        return objectTypeMap.get(label);
    }
}
