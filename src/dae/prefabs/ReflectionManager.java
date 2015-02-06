package dae.prefabs;

import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.prefabs.parameters.Parameter;
import java.util.HashMap;

/**
 * This class offers support for the reflection needed to edit
 * prefabs and components. The ReflectionSupport caches the lookup
 * of methods.
 * @author Koen Samyn
 */
public class ReflectionManager {

   
    public HashMap<Class,PropertyReflector> reflectionClasses;
    private static ReflectionManager instance = new ReflectionManager();

    /**
     * Creates a new ReflectionManager object.
     */
    private ReflectionManager() {
       reflectionClasses = new HashMap<Class,PropertyReflector>();
    }
    
    /**
     * 
     * @return the reflection manager instance.
     */
    public static ReflectionManager getInstance() {
        return instance;
    }
    
    /**
     * Adds a PropertyReflector for the given class.
     * @param c the class for which to reflect.
     * @param reflector the object that provides support for properties.
     */
    public void addPropertyReflector(Class c , PropertyReflector reflector){
        reflectionClasses.put(c, reflector);
    }
    
    /**
     * Checks if the given class has a property reflector.
     * @param c the class to check.
     * @return true if the class has a property reflector , false otherwise.
     */
    public boolean hasPropertyReflector(Class c){
        return reflectionClasses.containsKey(c);
    }
    
    /**
     * Returns the property reflector for the given class..
     * @param c the class. If the Property reflector does not exist,
     * one will be created.
     * @return a PropertyReflector object for the given class.
     */
    public PropertyReflector getPropertyReflector(Class c){
        PropertyReflector reflector = reflectionClasses.get(c);
        if ( reflector == null){
            reflector = new PropertyReflector(c);
        }
        return reflector;
    }
    
    /**
     * Convenience method that gets a value from the given object and given
     * property.
     * @param object the object to get the property from.
     * @param property the property of the object.
     * @return the value of the property or null if the property does not exist
     * for the object.
     * 
     */
    public Object invokeGetMethod(Prefab object, Parameter p){
        Object base = object;
        if ( p.getComponentType() != ComponentType.PREFAB){
            base = object.getComponent(p.getComponentType().getId());
        }
        
        PropertyReflector pr = getPropertyReflector(base.getClass());
        Object value = pr.invokeGetMethod(base, p.getId());
        return value;
    }
    
    /**
     * Convenience method that gets a value from the given object and given
     * property.
     * @param object the object to get the property from.
     * @param property the property of the object.
     * @return the value of the property or null if the property does not exist
     * for the object.
     * 
     */
    public Object invokeGetMethod(Prefab object, ComponentType ct, String property){
        Object base = object;
        if ( ct != ComponentType.PREFAB){
            base = object.getComponent(ct.getId());
        }
        
        PropertyReflector pr = getPropertyReflector(base.getClass());
        Object value = pr.invokeGetMethod(base, property);
        return value;
    }
}
