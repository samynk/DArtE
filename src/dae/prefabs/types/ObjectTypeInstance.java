package dae.prefabs.types;

import com.jme3.asset.AssetManager;
import dae.GlobalObjects;
import dae.io.readers.PrefabTextImporter;
import dae.prefabs.Prefab;
import dae.prefabs.parameters.InstanceCreator;
import dae.prefabs.parameters.Parameter;
import java.util.ArrayList;

/**
 * Describes how to create a specific instance of an object
 * as part of a prefab.
 * @author Koen Samyn
 */
public class ObjectTypeInstance implements InstanceCreator{
    private String name;
    private String category;
    private String type;
    // the location in the scenegraph for the object type instance.
    // the convention for this property follows a directory style convention.
    // . : attach the instance to the parent prefab.
    // .. : attach the instance to the parent of the parent prefab.
    // / : attach the instance to the root of the level.
    private String location = ".";
    
    private ArrayList<ObjectTypeParameter> parametersToSet = new ArrayList<ObjectTypeParameter>();
    private PrefabTextImporter importer;
    
    /**
     * Creates a new ObjectTypeInstance
     * @param category the category of object to create.
     * @param type the type of object to create.
     */
    public ObjectTypeInstance(String name, String category, String type)
    {
        this.name = name;
        this.category = category;
        this.type = type;
    }
    
    /**
     * Returns the category of the object.
     * @return the category of the object to create.
     */
    public String getCategory(){
        return category;
    }
    
    /**
     * Returns the type of the object.
     * @return the type of the object.
     */
    public String getType(){
        return type;
    }
    
    /**
     * Sets the location of the prefab to create.
     * @param location the location of the prefab to create.
     */
    public void setLocation(String location){
        this.location = location;
    }
    
    /**
     * Returns the location of the prefab to create.
     * @return the location of the prefab to create.
     */
    public String getLocation(){
        return location;
    }
    
    /**
     * Adds a parameter to the list of parameters to set.
     * @param parameter the parameter to set. This parameter must have a default value
     * otherwise it is not really useful.
     */
    public void addParameter(ObjectTypeParameter parameter){
        parametersToSet.add(parameter); 
   }
    
    /**
     * Creates an instance of the prefab and sets the properties.
     * @param root the root node of the scene.
     * @return an instance of the prefab.
     */
    @Override
    public Object createInstance(){
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        ObjectType ot = types.getObjectType(category, type);
        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        System.out.println("Creating [" + category +"," + type +"]");
        Prefab prefab = ot.create(manager, name);
        for ( ObjectTypeParameter p: parametersToSet)
        {
            Parameter param = ot.findParameter(p.getId());
            String value = p.getValue();
            if ( param != null){
                importer.parseAndSetParameter(prefab, param.getId(), value);
            }
        }
        return prefab;
    }
    
    

    
    public void setValueConverter(PrefabTextImporter importer) {
        this.importer = importer;
    }
}
