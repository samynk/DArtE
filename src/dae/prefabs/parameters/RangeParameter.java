package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 *
 * @author Koen
 */
public class RangeParameter extends Parameter {

    private float min;
    private float max;
    private float step;
    private float defaultValue;
    
    /**
     * Creates a new RangeParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     *
     */
    public RangeParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
        this.defaultValue = 0.0f;
    }

    /**
     * Creates a new RangeParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     * @param defaultValue the default value for this range parameter.
     */
    public RangeParameter(ComponentType componentType, String type, String id, float defaultValue) {
        super(componentType, type, id);
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