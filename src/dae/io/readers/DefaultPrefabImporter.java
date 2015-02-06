package dae.io.readers;

import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.io.XMLUtils;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import dae.prefabs.parameters.ChoiceParameter;
import dae.prefabs.parameters.FileParameter;
import dae.prefabs.parameters.Float3Parameter;
import dae.prefabs.parameters.FloatParameter;
import dae.prefabs.parameters.IntParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.RangeParameter;
import dae.prefabs.parameters.TextParameter;
import java.util.HashMap;

/**
 * @author Koen Samyn
 */
public class DefaultPrefabImporter implements PrefabTextImporter {

    private HashMap<Class, ParameterParser> parameterMap =
            new HashMap<Class, ParameterParser>();

    /**
     * Creates a new DefaultPrefabImporter object.
     */
    public DefaultPrefabImporter() {
        parameterMap.put(FloatParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return Float.parseFloat(value);
            }
        });
        parameterMap.put(RangeParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return Float.parseFloat(value);
            }
        });
        parameterMap.put(IntParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return Float.parseFloat(value);
            }
        });
        parameterMap.put(TextParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return value;
            }
        });
        parameterMap.put(Float3Parameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return XMLUtils.parseFloat3(value);
            }
        });
        parameterMap.put(FileParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return value;
            }
        });
        parameterMap.put(ChoiceParameter.class, new ParameterParser() {
            public Object parseParameter(String value) {
                return value;
            }
        });
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
    public void parseAndSetParameter(PrefabComponent p, ComponentType ct, String id, String value) {
        Parameter parameter = ct.findParameter(id);
        ParameterParser pp = parameterMap.get(parameter.getClass());
        if ( pp != null ){
            Object oValue = pp.parseParameter(value);
            if ( oValue != null){
                PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(p.getClass());
                pr.invokeSetMethod(p, id, parameter.convertToObject(oValue));
            }
        }
    }
}
