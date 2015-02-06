package dae.io.writers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

/**
 * Implementation of the PrefabTextExporter interface that writes the prefab to
 * an xml file.
 *
 * @author Koen Samyn
 */
public class DefaultPrefabExporter implements PrefabTextExporter {

    private HashMap<Class, ParameterExporter> parameterMap =
            new HashMap<Class, ParameterExporter>();

    /**
     * Creates a new DefaultPrefabExporter object.
     */
    public DefaultPrefabExporter() {
        parameterMap.put(FloatParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeFloat(w, (FloatParameter) p, (Float) value, depth);
            }
        });
        parameterMap.put(RangeParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeFloat(w, (RangeParameter) p, (Float) value, depth);
            }
        });

        parameterMap.put(IntParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeInt(w, (IntParameter) p, (Integer) value, depth);
            }
        });
        parameterMap.put(Float3Parameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                if (p.getConverter() != null) {
                    value = p.getConverter().convertFromObjectToUI(value);
                }
                writeVector3f(w, (Float3Parameter) p, (Vector3f) value, depth);
            }
        });
        parameterMap.put(TextParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeString(w, (TextParameter) p, value != null ? value.toString() : "", depth);
            }
        });

        parameterMap.put(ChoiceParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeString(w, (ChoiceParameter) p, value != null ? value.toString() : "", depth);
            }
        });

        parameterMap.put(FileParameter.class, new ParameterExporter() {
            public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException {
                writeFile(w, (FileParameter) p, value.toString(), depth);
            }
        });
    }

    /**
     * Writes a float to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeFloat(Writer w, FloatParameter parameter, float value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes a float to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeFloat(Writer w, RangeParameter parameter, float value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes an int to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeInt(Writer w, IntParameter parameter, int value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes a vector3f to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeVector3f(Writer w, Float3Parameter parameter, Vector3f value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes a quaternion to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeQuaternion(Writer w, Float3Parameter parameter, Quaternion value, int depth) throws IOException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Writes a string to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeString(Writer w, TextParameter parameter, String value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes a string to the output object
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     * @throws IOException
     */
    public void writeString(Writer w, ChoiceParameter parameter, String value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        XMLUtils.writeAttribute(w, "value", value);
        w.write(" />\n");
    }

    /**
     * Writes a file to the output of the component. The file will be embedded
     * in a CDATA section in the written file to guard against illegal xml
     * characters in the file name.
     *
     * @param w the writer to write to.
     * @param pc the prefab component to write.
     * @param ct the ComponentType to export.
     * @param depth the depth to write the component.
     */
    private void writeFile(Writer w, FileParameter parameter, String value, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<parameter ");
        XMLUtils.writeAttribute(w, "id", parameter.getId());
        w.write("><![CDATA[");
        w.write(value);
        w.write("]]></parameter>");
    }

    /**
     *
     * @param w the writer to write to.
     * @param pc the prefab component to write.
     * @param ct the ComponentType to export.
     * @param depth the depth to write the component.
     * @throws IOException
     */
    public void writeComponent(Writer w, PrefabComponent pc, ComponentType ct, int depth) throws IOException {
        writeComponentStart(w, pc, depth);
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(pc.getClass());

        for (Parameter p : ct.getAllParameters()) {
            ParameterExporter pe = this.parameterMap.get(p.getClass());
            if (pe != null) {
                Object value = pr.invokeGetMethod(pc, p.getId());
                pe.writeParameter(w, p, value, depth + 1);
            }
        }
        writeComponentEnd(w, pc, depth);
    }

    /**
     * Writes the start of the component.
     *
     * @param w the writer to write to.
     * @param pc the prefab component to write.
     * @param depth the depth to write the component.
     * @throws IOException
     */
    public void writeComponentStart(Writer w, PrefabComponent pc, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("<component ");
        XMLUtils.writeAttribute(w, "id", pc.getId());
        w.write(" >\n");
    }

    /**
     * Writes the end of the component.
     *
     * @param w the writer to write to.
     * @param pc the prefab component to write.
     * @param depth the depth to write the component.
     * @throws IOException
     */
    public void writeComponentEnd(Writer w, PrefabComponent pc, int depth) throws IOException {
        XMLUtils.writeTabs(w, depth);
        w.write("</component>\n");
    }
}
