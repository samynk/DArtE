/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters.converter;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * This class converts a Quaternion to an euler angles value.
 *
 * @author Koen Samyn
 */
public class QuaternionConverter implements PropertyConverter {

    float angles[] = new float[3];

    /**
     * Converts the given value to a value that can be used in a UI.
     *
     * @param value the value to convert.
     * @return the converted value.
     */
    public Object convertFromObjectToUI(Object value) {
        if (value instanceof Quaternion) {
            Quaternion q = (Quaternion) value;
            Vector3f eulerAngles = new Vector3f();
            q.toAngles(angles);
            eulerAngles.set(angles[0],angles[1],angles[2]);
            eulerAngles.multLocal(FastMath.RAD_TO_DEG);
            return eulerAngles;
        }else{
            return Vector3f.ZERO;
        }
    }

    /**
     * Converts the given value from a ui value to a value that can be used by
     * the object.
     *
     * @param value the value to convert.
     * @return the converted value.
     */
    public Object convertFromUIToObject(Object value) {
        if ( value instanceof Vector3f)
        {
            Vector3f vangles = (Vector3f)value;
            vangles = vangles.mult(FastMath.DEG_TO_RAD);
            Quaternion q = new Quaternion();
            q.fromAngles(vangles.x, vangles.y, vangles.z);
            return q;
        }else{
            return new Quaternion();
        }
    }
}
