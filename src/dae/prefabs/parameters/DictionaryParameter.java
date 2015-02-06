package dae.prefabs.parameters;

import dae.components.ComponentType;
import dae.prefabs.Prefab;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a dictionary type parameter. In order for this parameter to
 * work the object needs to have the following methods:
 *
 * void addPropertyKey( String key ); 
 * void removePropertyKey( String key );
 *
 * int getNrOfPropertyKeys();
 * int getIndexOfPropertyKey(String key);
 * String getPropertyKeyAt(int index);
 *
 * void setProperty( String key, T value);
 * T getProperty( String key );
 *
 * with Property the name of the property, and T the type of the property.
 *
 * @author Koen Samyn
 */
public class DictionaryParameter extends Parameter {

    private Parameter baseType;

    /**
     * Creates a new IndexedParameter object.
     *
     * @param componentType the componentType of the parameter.
     * @param id the id of the parameter.
     * @param baseType the base type of the parameter.
     */
    public DictionaryParameter(ComponentType componentType, String type, String id, Parameter baseType) {
        super(componentType, type, id);
        this.baseType = baseType;
    }

    /**
     * Returns the base type of this ListParameter.
     *
     * @return the base type of this list parameter.
     */
    public Parameter getBaseType() {
        return baseType;
    }

    /**
     * Gets the method to retrieve the number of values in this dictionary.
     *
     * @param p the prefab to get this method for.
     * @return the Method that can invoke the method.
     */
    public Method getNrOfKeysMethod(Prefab p) {
        try {
            return p.getClass().getMethod("getNrOf" + getProperty() + "Keys");
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the actual number of keys in the prefab.
     *
     * @param prefab the prefab to get the keys for.
     */
    public int getNrOfKeys(Prefab prefab) {
        try {
            Method method = getNrOfKeysMethod(prefab);
            if (method != null) {
                return (Integer) method.invoke(prefab);
            } else {
                return 0;
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    /**
     * Returns the method to get the index of a key.
     * @param p the prefab to get the key index of.
     * @return the index of the key.
     */
    public Method getIndexOfKeyMethod( Prefab p){
        try {
            return p.getClass().getMethod("getIndexOf"+getProperty()+"Key" , String.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Returns the actual index of the key for this property of the Prefab.
     * @param p the prefab that contains the property with the key.
     * @param key the key to get the index for.
     * @return the index of the given key.
     */
    public int getIndexOfKey(Prefab p, String key){
        Method m = getIndexOfKeyMethod(p);
        if ( m!= null){
            try {
               return (Integer) m.invoke(p, key);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        }
        return -1;
    }
    
    /**
     * Gets the method to return the key list.
     * @param p the prefab object to return the key list for.
     * @return the Method that can get the list of keys.
     */
    public Method getKeyAtMethod(Prefab p){
        try {
            return p.getClass().getMethod("get" +getProperty()+"KeyAt", int.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Get the actual key at the given index.
     * @param p the prefab to get the key from.
     * @param index the index of the key.
     * @return the actual string.
     */
    public String getKeyAt(Prefab p, int index){
        Method getKeyAtMethod = getKeyAtMethod(p);
        try {
            return (String)getKeyAtMethod.invoke(p, (Integer)index);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the method that can add a key to the list of keys in this
     * dicationary.
     *
     * @param p the prefab to get this method for.
     * @return the Method object that can invoke the method.
     */
    public Method getAddKeyMethod(Prefab p) {
        try {
            return p.getClass().getMethod("add" + getProperty() + "Key", String.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Adds an actual key for this property on the prefab.
     *
     * @param prefab the prefab to add the key to.
     * @param key the key to add.
     */
    public void addKey(Prefab prefab, String key) {
        Method addKeyMethod = getAddKeyMethod(prefab);
        if (addKeyMethod != null) {
            try {
                addKeyMethod.invoke(prefab, key);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the method that can remove a key from the list of keys.
     *
     * @param p the prefab to get this method for.
     * @return the Method object that can invoke the method.
     */
    public Method getRemoveKeyMethod(Prefab p) {
        try {
            return p.getClass().getMethod("remove" + getProperty() + "Key", String.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Removes an actual key for this property on the prefab.
     *
     * @param prefab the prefab to remove the key to.
     * @param key the key to remove.
     */
    public void removeKey(Prefab prefab, String key) {
        Method removeKeyMethod = getRemoveKeyMethod(prefab);
        if (removeKeyMethod != null) {
            try {
                removeKeyMethod.invoke(prefab, key);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns a method that can get the value of the property.
     *
     * @param p the prefab to get this method for.
     * @return the Method that
     */
    public Method getGetPropertyMethod(Prefab p) {
        try {
            return p.getClass().getMethod("get" + getProperty(), String.class);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the value for the property for a given key.
     *
     * @param p the prefab to get the value for.
     * @param key the key that is associated with the value.
     * @return the value for the property for a given key.
     */
    public Object getProperty(Prefab p, String key) {
        Method getMethod = getGetPropertyMethod(p);
        if ( getMethod != null){
            try {
                return getMethod.invoke(p, key);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }else{
            return null;
        }
    }

    /**
     * Returns a method that can get the value of the property.
     *
     * @param p the prefab to get this method for.
     * @return the Method that
     */
    public Method getSetPropertyMethod(Prefab p) {
        try {
            return p.getClass().getMethod("set" + getProperty(), String.class, baseType.getClassType());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Sets the value for the property for a given key.
     *
     * @param p the prefab to set the value for.
     * @param key the key that is associated with the value.
     */
    public void setProperty(Prefab p, String key, Object value) {
        Method setMethod = getSetPropertyMethod(p);
        if ( setMethod != null){
            try {
                setMethod.invoke(p, key, value);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DictionaryParameter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
