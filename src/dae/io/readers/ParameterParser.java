package dae.io.readers;

/**
 * 
 * @author Koen Samyn
 */
public interface ParameterParser {
    /**
     * Converts a string to a value.
     * @param value the value to parse.
     * @return 
     */
    Object parseParameter(String value);
}
