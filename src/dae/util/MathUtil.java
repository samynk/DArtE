package dae.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.prefabs.AxisEnum;

/**
 * This class offers a couple of useful functions , mostly related to difficult
 * rotation transformations.
 * @author Koen Samyn
 */
public class MathUtil {

    /**
     * Creates a new reference frame starting from a single normal.
     * @param normal the normal to create a new rotation from.
     * @param mainAxis the axis of the object to align the normal with.
     * @return a new Quaternion.
     */
    public static Quaternion createRotationFromNormal(Vector3f normal, AxisEnum mainAxis) {
        Vector3f x;
        Vector3f y;
        Quaternion result = new Quaternion();
        if (normal.x > normal.y && normal.x > normal.z) {
            x = normal.cross(Vector3f.UNIT_Y);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();
        } else if (normal.y > normal.x && normal.y > normal.z) {
            x = normal.cross(Vector3f.UNIT_Z);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();
        } else {
            x = normal.cross(Vector3f.UNIT_X);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();
        }
        switch (mainAxis) {
            case X:
                result.fromAxes(normal, x, y);
                break;
            case Y:
                result.fromAxes(y, normal, x);
                break;
            case Z:
                result.fromAxes(x, y, normal);
                break;
        }
        return result;
    }
}
