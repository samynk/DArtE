/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

/**
 *
 * @author Koen
 */
public class RotationRange {

    private float minimum;
    private float maximum;
    private float step;
    private float currentValue;
    private boolean bound = false;

    public RotationRange(float minimum, float maximum, float step, float value) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
        this.currentValue = value;

        if (value < minimum) {
            currentValue = minimum;
        }

        if (value > maximum) {
            currentValue = maximum;
        }
        bound = true;
    }

    public RotationRange(float step, float value) {
        this.minimum = 0;
        this.maximum = 0;
        this.step = step;
        this.currentValue = value;
        bound = false;
    }

    public float nextUp() {
        currentValue += step;
        if (bound) {
            if (currentValue > maximum) {
                currentValue = maximum;
            }
        }
        return currentValue;
    }

    public float nextDown() {
        currentValue -= step;
        if (bound) {
            if (currentValue > maximum) {
                currentValue = maximum;
            }
        }
        return currentValue;
    }

    public void setCurrentValue(float rotation) {
        currentValue = Math.round(rotation / step) * step;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setStep(float snap) {
        this.step = snap;
    }

    public float getStep() {
        return step;
    }
}
