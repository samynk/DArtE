/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io.writers;

import dae.prefabs.parameters.Parameter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen Samyn
 */
public interface ParameterExporter {

    /**
     * Writes a parameter to the writer object.
     *
     * @param w the writer object.
     * @param parameter the parameter with extra information about the context
     * of the value.
     * @param value the value to write.
     * @param depth the index into the tree structure. (provided for formatting
     * purposes).
     */
    public void writeParameter(Writer w, Parameter p, Object value, int depth) throws IOException;
}
