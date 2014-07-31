/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class RangeParameter extends Parameter {

    private float min;
    private float max;
    private float step;
    private float defaultValue;
    
    public RangeParameter(String label, String id) {
        super(label, id);
        this.defaultValue = 0.0f;
    }

    public RangeParameter(String label, String id, float defaultValue) {
        super(label, id);
        this.defaultValue = defaultValue;
    }

    /**
     * @return the min
     */
    public float getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(float min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public float getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(float max) {
        this.max = max;
    }

    /**
     * @return the step
     */
    public float getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(float step) {
        this.step = step;
    }

    /**
     * @return the defaultValue
     */
    public float getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }
    
     /**
     * Gets the class type of this parameter.
     * @return the class type of the parameter.
     */
    @Override
    public Class getClassType() {
        return float.class;
    }
}