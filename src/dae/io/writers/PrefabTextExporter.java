package dae.io.writers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.prefabs.parameters.Float3Parameter;
import dae.prefabs.parameters.FloatParameter;
import dae.prefabs.parameters.IntParameter;
import dae.prefabs.parameters.RangeParameter;
import dae.prefabs.parameters.TextParameter;
import java.io.IOException;
import java.io.Writer;

/**
 * An interface that writes a single prefab to a text file. The PrefabExporter
 * interfaced defines methods for each type of parameter (text, float, int, ...)
 * that need to be exported. Implementers need only to define these methods to
 * start exporting in a different format.
 *
 * @author Koen Samyn
 */
public interface PrefabTextExporter {

    /**
     * Writes a float to the output writer.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeFloat(Writer w, FloatParameter parameter, float value, int depth) throws IOException;

    /**
     * Writes a float to the output writer.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeFloat(Writer w, RangeParameter parameter, float value, int depth) throws IOException;
    
    /**
     * Writes an int to the output writer.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeInt(Writer w, IntParameter parameter, int value, int depth) throws IOException;

    /**
     * Writes a vector3f to the output writer.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeVector3f(Writer w, Float3Parameter parameter, Vector3f value, int depth) throws IOException;

    /**
     * Writes a quaternion to the output writer. The parameter for this value is
     * a Float3Parameter because the rotation is presented to the user as euler
     * angles.
     *
     * @param w the writer object.
     * @param parameter he parameter with extra information about the context of
     * the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeQuaternion(Writer w, Float3Parameter parameter, Quaternion value, int depth) throws IOException;

    /**
     * Writes a string to the output writer.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeString(Writer w, TextParameter parameter, String value, int depth) throws IOException;
    
    /**
     * Writes a component to the writer.
     *
     * @param w the writer object.
     * @param component the prefab component to write.
     * @param ct the ComponentType of the component. This determines the
     * parameters that will be exported.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeComponent(Writer w, PrefabComponent pc, ComponentType ct, int depth) throws IOException;

    /**
     * Writes the start of a component to the output writer.
     *
     * @param w the writer object.
     * @param component the prefab component to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeComponentStart(Writer w, PrefabComponent pc, int depth) throws IOException;

    /**
     * Writes the end of a component to the output writer.
     *
     * @param w the writer object.
     * @param component the prefab component to write.
     *
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeComponentEnd(Writer w, PrefabComponent pc, int depth) throws IOException;
}
