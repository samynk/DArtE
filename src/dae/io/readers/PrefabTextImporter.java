package dae.io.readers;

import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * 
 * @author Koen Samyn
 */
public interface PrefabTextImporter {
    /**
     * Parses a parameter and sets the value on the correct
     * property of the component.
     * @param p the prefab component to set the property on.
     * @param ct the component type of the PrefabComponent.
     * @param id the id of the parameter.
     * @param value the value of the parameter.
     */
    public void parseAndSetParameter( PrefabComponent p, ComponentType ct, String id, String value);
    /**
     * Parses a parameter and sets the value on the correct
     * property of the component.
     * @param p the prefab  to set the property on.
     * @param id the id of the parameter.
     * @param value the value of the parameter.
     */
    public void parseAndSetParameter(Prefab p, String id, String value);
}
