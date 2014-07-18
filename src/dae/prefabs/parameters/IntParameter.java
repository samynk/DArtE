/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class IntParameter extends Parameter {

    private int defaultValue;

    public IntParameter(String label, String id) {
        super(label, id);
    }

    public IntParameter(String label, String id, int defaultValue) {
        super(label, id);
        this.defaultValue = defaultValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}
