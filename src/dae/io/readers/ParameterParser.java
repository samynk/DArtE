package dae.io.readers;

import dae.prefabs.parameters.Parameter;

/**
 * 
 * @author Koen Samyn
 */
public interface ParameterParser {
    /**
     * Converts a string to a value.   
     * @param parent the object that is the parent of this object.
     * @param parameter the parameter that is associated with the value.
     * @param value the value to parse.
     * @return the parsed value.
     */
    Object parseParameter(Object parent, Parameter parameter, String value);
}
