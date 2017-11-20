package dae.io.readers;

import com.jme3.scene.Node;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import dae.prefabs.UnresolvedReferencePrefab;
import dae.prefabs.parameters.BaseTypeParameter;
import dae.prefabs.parameters.BooleanParameter;
import dae.prefabs.parameters.ChoiceParameter;
import dae.prefabs.parameters.ColorParameter;
import dae.prefabs.parameters.EnumListParameter;
import dae.prefabs.parameters.FileParameter;
import dae.prefabs.parameters.Float2Parameter;
import dae.prefabs.parameters.Float3Parameter;
import dae.prefabs.parameters.FloatParameter;
import dae.prefabs.parameters.IntParameter;
import dae.prefabs.parameters.ListParameter;
import dae.prefabs.parameters.ObjectParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.RangeParameter;
import dae.prefabs.parameters.TextParameter;
import dae.prefabs.types.ObjectTypeInstance;
import java.util.HashMap;

/**
 * @author Koen Samyn
 */
public class DefaultPrefabImporter implements PrefabTextImporter {

    private Node levelNode;
    private HashMap<Class, ParameterParser> parameterMap =
            new HashMap<Class, ParameterParser>();

    /**
     * Creates a new DefaultPrefabImporter object.
     */
    public DefaultPrefabImporter() {

        parameterMap.put(FloatParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return Float.parseFloat(value);
            }
        });
        parameterMap.put(BooleanParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return Boolean.parseBoolean(value);
            }
        });
        parameterMap.put(RangeParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return Float.parseFloat(value);
            }
        });
        parameterMap.put(IntParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return Integer.parseInt(value);
            }
        });
        parameterMap.put(TextParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return value;
            }
        });
        parameterMap.put(Float2Parameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return XMLUtils.parseFloat2(value);
            }
        });
        parameterMap.put(Float3Parameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return XMLUtils.parseFloat3(value);
            }
        });
        parameterMap.put(FileParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return value;
            }
        });
        parameterMap.put(ChoiceParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                return value;
            }
        });
        parameterMap.put(EnumListParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String value) {
                EnumListParameter elp = (EnumListParameter) p;
                return elp.getEnum(value);
            }
        });
        parameterMap.put(BaseTypeParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String id) {
                // find the object in the scene with the given id.
                return levelNode.getChild(id);
            }
        });
        parameterMap.put(ObjectParameter.class, new ParameterParser() {
            public Object parseParameter(Object parent, Parameter p, String id) {

                Object prefab = levelNode.getChild(id);
                if (prefab == null) {
                    UnresolvedReferencePrefab ur = new UnresolvedReferencePrefab();
                    ur.setReference(parent, p, id);
                    prefab = ur;
                }
                return prefab;
            }
        });
        parameterMap.put(ColorParameter.class,new ParameterParser(){
             public Object parseParameter(Object parent, Parameter p, String id) {
                // find the object in the scene with the given id.
                 return XMLUtils.parseColor(id);
            }
        });
    }

    /**
     * Sets the root node for this importer. Can be used to find objects in the
     * game.
     *
     * @param rootNode the rootnode to find.
     */
    public void setRootNode(Node rootNode) {
        this.levelNode = rootNode;
    }

    /**
     * Parses a parameter and sets the value on the correct property of the
     * component.
     *
     * @param prefab the prefab to set the property on.
     * @param p the prefab component to set the property on.
     * @param ct the component type of the PrefabComponent.
     * @param id the id of the parameter.
     * @param value the value of the parameter.
     */
    @Override
    public void parseAndSetParameter(PrefabComponent p, ComponentType ct, String id, String value) {
        Parameter parameter = ct.findParameter(id);
        if (parameter == null || value == null) {
            return;
        }
        ParameterParser pp = parameterMap.get(parameter.getClass());
        if (pp != null) {
            Object oValue = pp.parseParameter(p, parameter, value);
            if (oValue != null) {
                PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(p.getClass());
                pr.invokeSetMethod(p, id, parameter.convertToObject(oValue));
            }
        }
    }

    /**
     * Parses a parameter and sets the value on the correct property of the
     * component.
     *
     * @param p the prefab component to set the property on.
     * @param ct the component type of the PrefabComponent.
     * @param id the id of the parameter.
     * @param value the value of the parameter.
     */
    @Override
    public void parseAndSetParameter(Prefab p, String id, String value) {
        Parameter parameter = p.getObjectType().findParameter(id);
        if (parameter == null || value == null) {
            return;
        }
        ParameterParser pp = parameterMap.get(parameter.getClass());
        if (pp != null) {
            Object oValue = pp.parseParameter(p, parameter, value);
            if (oValue != null) {
                parameter.invokeSet(p, oValue, false);
            }
        }
    }

    /**
     * Parses a parameter and sets the value on the correct property of the
     * component.
     *
     * @param p the prefab component to set the property on.
     * @param ct the component type of the PrefabComponent.
     * @param id the id of the parameter.
     * @param value the value of the parameter.
     */
    public void parseAndSetListParameter(Prefab prefab, String id, String value) {
        Parameter parameter = prefab.getObjectType().findParameter(id);
        if (parameter == null || value == null || !(parameter instanceof ListParameter)) {
            return;
        }
        ListParameter lp = (ListParameter) parameter;

        ParameterParser pp = parameterMap.get(lp.getBaseType().getClass());
        if (pp != null) {
            Object oValue = pp.parseParameter(prefab, parameter, value);
            if (oValue != null) {
                lp.addListItem(prefab, oValue);
            }
        }
    }

    /**
     * Parses the defaultValue parameter and returns the result.
     *
     * @param p the parameter that has the information about the value to parse.
     * @param defaultValue the value to parse.
     * @return the parsed object.
     */
    public Object parseParameter(Parameter p, String defaultValue) {
        ParameterParser pp = parameterMap.get(p.getClass());
        return pp.parseParameter(null, p, defaultValue);
    }
}
