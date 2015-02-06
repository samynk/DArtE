/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.types;

import com.google.common.io.Files;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.ModelKey;
import dae.components.ComponentList;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.prefabs.magnets.FillerParameter;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.parameters.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class ObjectType extends ParameterSupport {

    private String label;
    private String category;
    private String objectClass;
    private String extraInfo;
    private boolean loadFromExtraInfo = false;
    private ArrayList<ComponentType> componentTypes = new ArrayList<ComponentType>();
    private HashMap<String,ComponentType> componentMap = new HashMap<String,ComponentType>();

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

    public boolean usesDefaultLoader() {
        return this.loadFromExtraInfo;
    }

    /**
     * Adds a component type to the object type definition.
     *
     * @param ct the component type to add.
     */
    public void addComponentType(ComponentType ct) {
        this.componentTypes.add(ct);
        componentMap.put(ct.getId(), ct);
    }

    /**
     * Returns the list of component types.
     *
     * @return the list of component types.
     */
    public Iterable<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    /**
     * Creates a Prefab with the info in this object type.
     *
     * @param AssetManager manager the AssetManager to use to create the object.
     * @param name the name of the new object.
     */
    public Prefab create(AssetManager manager, String name) {
        Prefab p = null;
        if (usesDefaultLoader()) {
            ModelKey mk = new ModelKey(getExtraInfo());
            p = (Prefab) manager.loadAsset(mk);
            loadComponents(manager, p, mk);
            p.setName(name);
        } else {
            try {
                p = (Prefab) Class.forName(getObjectToCreate()).newInstance();
                p.create(name, manager, this, getExtraInfo());

                p.setType(getLabel());
                p.setCategory(getCategory());
                p.notifyLoaded();


                MagnetParameter mp = (MagnetParameter) findParameter("magnets");
                p.setMagnets(mp);

                FillerParameter fp = (FillerParameter) findParameter("filler");
                p.setFillers(fp);
            } catch (InstantiationException ex) {
                Logger.getLogger(ObjectType.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ObjectType.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ObjectType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if ( p != null ){
            for (ComponentType c: this.componentTypes)
            {
                PrefabComponent pc = c.create();
                if ( pc != null )
                {
                    p.addPrefabComponent(pc);
                }
            }
        }
        
        return p;

    }

    /**
     * Loads the components if a components file with the same name exists.
     *
     * @param manager the manager object.
     * @param p the prefab to load the component file from.
     * @param mk the modelkey of the original object.
     */
    private void loadComponents(AssetManager manager, Prefab p, ModelKey mk) {
        String folder = mk.getFolder();
        String name = mk.getName();
        String baseName = Files.getNameWithoutExtension(name);
        String components = folder + baseName + ".components";

        try {
            Object result = manager.loadAsset(components);
            if (result instanceof ComponentList) {
                ComponentList cl = (ComponentList) result;
                p.addComponents(cl);
            }
        } catch (AssetNotFoundException ex) {
            // not an error, it is possible that there is no components file.
        }
    }

    /**
     * Returns the component type with the given id.
     * @param id the id for the component type.
     * @return the ComponentType or null if no ComponentType with the given id
     * is found.
     */
    public ComponentType getComponentType(String id) {
        return this.componentMap.get(id);
    }
    
    /**
     * Finds the parameter that is bound to the given component for the
     * given property.
     * @param transformComponent
     * @param property
     * @return the Parameter object or null if the parameter is not found.
     */
    public Parameter findParameter(String componentId, String property) {
        ComponentType ct = componentMap.get(componentId);
        if ( ct != null ){
            return ct.findParameter(property);
        }else{
            return null;
        }
    }
}