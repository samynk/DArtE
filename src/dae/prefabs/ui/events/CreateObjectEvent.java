package dae.prefabs.ui.events;

import dae.components.PrefabComponent;
import dae.prefabs.types.ObjectType;
import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class CreateObjectEvent {

    public String objectToCreate;
    private ArrayList<PrefabComponent> components;
    private ObjectType objectType;

    public CreateObjectEvent(String className, String extraInfo, ObjectType ot) {
        objectToCreate = className;
        this.objectType = ot;
    }

    public String getClassName() {
        return objectToCreate;
    }

    /**
     * Adds a prefab component to the list of default components to make.
     * @param pc the prefab component to create.
     */
    public void addPrefabComponent(PrefabComponent pc)
    {
        if ( components == null ){
            components = new ArrayList<PrefabComponent>();
        }
        components.add(pc);
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Iterable<PrefabComponent> getComponents() {
        return components;
    }

    public boolean hasComponents() {
        return components!=null && components.size()>0;
    }
}
