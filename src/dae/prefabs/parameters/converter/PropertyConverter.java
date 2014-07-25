/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.parameters.converter;

/**
 * A converter is necessary when an object uses a property that is not
 * necessarily easy to change in the user interface.
 * An example of such a property is the rotation property of a mesh. The rotation
 * can be defined as a Matrix3f or a Quaternion, but those values are not
 * useful in a user interface. The solution is to register a converter that
 * will convert for example a Quaternion to an euler angle, and an euler angle
 * back to a Quaternion.
 * @author Koen Samyn
 */
public interface PropertyConverter {

    /**
     * Converts the given value to a value that can be used in a UI.
     * @param value the value to convert.
     * @return the converted value.
     */
    public Object convertFromObjectToUI(Object value);
    /**
     * Converts the given value from a ui value to a value that can be used by
     * the object.
     * @param value the value to convert.
     * @return the converted value.
     */
    public Object convertFromUIToObject(Object value);
}
