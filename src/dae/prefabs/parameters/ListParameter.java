package dae.prefabs.parameters;

import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.prefabs.CreateListItemTask;
import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import dae.prefabs.types.ObjectType;

/**
 * An indexed parameter defines an indexed property, in other words a property
 * that is backed by an arraylist.
 *
 * If the id of the parameter is "property" and the type of the property is Type,
 * then the following methods must be present.
 *
 * public Type getProperty(int index); 
 * public void setProperty(int index, Type value); 
 * public int getPropertySize();
 *
 * @author Koen Samyn
 */
public class ListParameter extends Parameter {

    private Parameter baseType;

    /**
     * Creates a new IndexedParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     * @param baseType the base type of the parameter.
     */
    public ListParameter(ComponentType componentType, String type, String id, Parameter baseType) {
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
     * Creates a new empty list item.
     *
     * @param prefab the prefab to create a list item for.
     * @return the newly created object.
     */
    public void createListItem(Prefab prefab) {
        String toCreate = baseType.getType();
        String[] cs = toCreate.split("\\.");
        if (cs.length == 2) {
            String category = cs[0];
            String type = cs[1];
            ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType(category, type);
            prefab.addTask(new CreateListItemTask(prefab,this, ot));
        }
    }
    
    /**
     * Invokes the get method of this parameter on the prefab.The ComponentType
     * will be taken into account to invoke this get method on the correct
     * component.
     *
     * @param prefab the prefab to invoke the get method on.
     * @return the current value of the property.
     */
    public Object invokeGet(Prefab prefab) {
        Object base = prefab;
        if (getComponentType() != ComponentType.PREFAB) {
            base = prefab.getComponent(getComponentType().getId());
        }
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(base.getClass());
        return pr.invokeGetMethod(base, getProperty() + "s");
    }

    /**
     * Gets the size of the backing list.
     *
     * @return
     */
    public int getListSize(Prefab prefab) {
        ReflectionManager rm = ReflectionManager.getInstance();
        PropertyReflector pr = rm.getPropertyReflector(prefab.getClass());
        return pr.invokeListSizeMethod(prefab, getProperty());
    }

    public Object getListElementAt(Prefab prefab, int index) {
        ReflectionManager rm = ReflectionManager.getInstance();
        PropertyReflector pr = rm.getPropertyReflector(prefab.getClass());
        return pr.invokeListElementAt(prefab, getProperty(), index);
    }

    public void addListItem(Prefab prefab, Object object) {
        ReflectionManager rm = ReflectionManager.getInstance();
        PropertyReflector pr = rm.getPropertyReflector(prefab.getClass());
        pr.invokeListAddItem(prefab, getProperty(), object);
    }
}
