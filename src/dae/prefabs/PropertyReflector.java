package dae.prefabs;

import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
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

    private Class clazz;
    private HashMap<String, Method> getMethods =
            new HashMap<String, Method>();
    private HashMap<String, Method> setMethods =
            new HashMap<String, Method>();

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
        } catch (IllegalAccessException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
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
            } catch (NoSuchMethodException ex) {
                //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (m != null) {
            try {
                return m.invoke(component);
            } catch (IllegalAccessException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
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
    public Object clone(Object toClone) {
        if (toClone instanceof Cloneable) {
            try {
                Method clone = toClone.getClass().getMethod("clone");
                if (clone != null && Modifier.isPublic(clone.getModifiers())) {
                    return clone.invoke(toClone);
                } else {
                    return toClone;
                }
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
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
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
