package dae.prefabs.parameters;

import dae.components.ComponentType;
import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;

/**
 * Describes an action parameter, this is an action (method) that can
 * be called on a prefab or on a component of a prefab.
 * @author Koen Samyn
 */
public class ActionParameter extends Parameter {

    private String methodName;

    /**
     * Creates a new action parameter.
     * @param componentType the component type of the action parameter.
     * @param type the type of the parameter (string, int, float, color, ...)
     * @param id the id of the property of the prefab or component.
     */
    public ActionParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Set the method name of the action parameter.
     * @param methodName the method name of the parameter.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the method name of the parameter.
     * @return the method name of the parameter.
     */
    public String getMethodName() {
        return methodName;
    }

     /**
     * Invokes the set of the property of this object.
     * The ComponentType will be taken into account to invoke
     * the set method on the correct component.
     * @param prefab the prefab to invoke the set method on.
     * @param value the value to set.
     */
    @Override
    public void invokeSet(Prefab prefab, Object value, boolean undoableEdit) {
        Object base = prefab;
        if ( getComponentType() != ComponentType.PREFAB)
        {
           base = prefab.getComponent(getComponentType().getId());
        }
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(base.getClass());
        pr.invokeMethod(base,methodName);
            
    }
    
    
}
