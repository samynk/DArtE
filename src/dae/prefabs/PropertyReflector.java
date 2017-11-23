package dae.prefabs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class PropertyReflector {

    private final Class clazz;
    private final HashMap<String, Method> getMethods =
            new HashMap<>();
    private final HashMap<String, Method> setMethods =
            new HashMap<>();

    /**
     * Creates a new PropertyReflector object for the given class.
     *
     * @param c the propertyreflector for the class.
     */
    public PropertyReflector(Class c) {
        clazz = c;
    }

    /**
     * Gets the set method for a property.
     *
     * @param property the string value of the property.
     * @param parameterType the class of the parameter type.
     * @return the Method to apply the property to.
     */
    public Method getSetMethod(String property, Class parameterType) {
        Method m = setMethods.get(property);
        if (m == null) {
            // get declared method
            Method[] methods = clazz.getMethods();
            String p = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
            for (int i = 0; i < methods.length; ++i) {

                if (methods[i].getName().equals(p) && methods[i].getParameterTypes().length == 1) {
                    Class pc = methods[i].getParameterTypes()[0];
                    Class vc = getPrimitiveType(parameterType);
                    if (pc.equals(vc) || pc.isAssignableFrom(vc)) {
                        m = methods[i];
                        setMethods.put(property, m);
                        break;
                    }
                }
            }
        }
        return m;
    }

    /**
     * Invokes the set method of the property with the given value.
     *
     * @param component the object to invoke the method on.
     * @param property the property to change.
     * @param value the new value.
     * @return true if the property has changed, false otherwise.
     */
    public boolean invokeSetMethod(Object component, String property, Object value) {
        Method m = getSetMethod(property, value.getClass());
        if (m == null) {
            return false;
        }
        try {
            m.invoke(component, value);
            return true;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, "Problem setting {0}, on prefab{1}:{2}", new Object[]{property, component, value});
            Logger.getLogger("DArtE").log(Level.SEVERE, "type of value is : {0}", value.getClass().getName());
            Logger.getLogger("DArtE").log(Level.SEVERE, "Method is : {0}", m.getName());
            Logger.getLogger("DArtE").log(Level.SEVERE, "Type is :{0}", m.getParameterTypes()[0].getName());
        }
        return false;
    }

    /**
     * Gets the primitive type of the given clazz object. (e.g Boolean.class
     * returns boolean.class)
     *
     * @param clazz the class object to transform to a primitive type.
     * @return the primitive class object( e.g. boolean.class)
     */
    public Class getPrimitiveType(Class clazz) {
        if (clazz.equals(Boolean.class)) {
            return boolean.class;
        } else if (clazz.equals(Float.class)) {
            return float.class;
        } else if (clazz.equals(Double.class)) {
            return double.class;
        } else if (clazz.equals(Integer.class)) {
            return int.class;
        } else if (clazz.equals(Short.class)) {
            return short.class;
        } else if (clazz.equals(Long.class)) {
            return long.class;
        } else if (clazz.equals(Byte.class)) {
            return byte.class;
        } else if (clazz.equals(Character.class)) {
            return char.class;
        } else {
            return clazz;
        }
    }

    /**
     * Returns the value of the given property.
     *
     * @param property the property to get the value for.
     * @param component the object to get the property from.
     * @return the value of the property.
     */
    public Object invokeGetMethod(Object component, String property) {
        Method m = getMethods.get(property);
        if (m == null) {
            try {
                String p = Character.toUpperCase(property.charAt(0)) + property.substring(1);
                m = clazz.getMethod("get" + p);
                getMethods.put(property, m);
            } catch (NoSuchMethodException | SecurityException ex) {
                //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        if (m != null) {
            try {
                return m.invoke(component);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, "Problem getting {0}, on prefab{1}", new Object[]{property, component.toString()});
                Logger.getLogger("DArtE").log(Level.SEVERE, "type of return value is : {0}", m.getReturnType().getName());
                Logger.getLogger("DArtE").log(Level.SEVERE, "Method is : {0}", m.getName());
            }
        }
        return null;

    }

    /**
     * Clones a value if that value is cloneable, otherwise return the object
     * itself.
     *
     * @param toClone the value to clone.
     * @return a clone of the value or the object itself if the value is not
     * cloneable.
     */
    public static Object clone(Object toClone) {
        if (toClone instanceof Cloneable) {
            try {
                Method clone = toClone.getClass().getMethod("clone");
                if (clone != null && Modifier.isPublic(clone.getModifiers())) {
                    return clone.invoke(toClone);
                } else {
                    return toClone;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return toClone;
    }

    public void invokeMethod(Object base, String methodName) {
        try {
            Method m = clazz.getMethod(methodName);
            if (m != null) {
                m.invoke(base);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Invokes the list size method for the given property. The list size method
     * should be of the form : get<Property>ListSize without arguments and should
     * return an integer.
     * This method will return 0 if the list is empty or if the proper method was not implemented.
     * @param prefab the prefab to get the list size for.
     * @param property the list property.
     * @return the current size of the list.
     */
    public int invokeListSizeMethod(Prefab prefab, String property) {
        String p = Character.toUpperCase(property.charAt(0)) + property.substring(1);
        try {
            Method m = clazz.getMethod("get" + p + "ListSize");
            Object result = m.invoke(prefab);
            if ( result instanceof Integer){
                return (Integer)result;
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PropertyReflector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Object invokeListElementAt(Prefab prefab, String property, int index) {
        String p = Character.toUpperCase(property.charAt(0)) + property.substring(1);
        try {
            Method m = clazz.getMethod("get" + p + "At",getPrimitiveType(Integer.class));
            Object result = m.invoke(prefab,index);
            return result;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PropertyReflector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void invokeListAddItem(Prefab prefab, String property, Object object) {
        String p = Character.toUpperCase(property.charAt(0)) + property.substring(1);
        try {
            Method m = clazz.getMethod("add" + p ,object.getClass());
            m.invoke(prefab,object);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PropertyReflector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
