package dae.util;

import com.jme3.math.FastMath;
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
    
    /**
     * Creates a two degree freedom rotation from the provided normal.
     * @param from the start point of the rotation.
     * @param to the point to rotate the from point to over the two axis.
     * @param axis1 the first degree of freedom.
     * @param axis2 the second degree of freedom.
     * @param q the quaternion to store the result in. If null, a new quaternion
     * will be created.
     * @return The quaternion with the DOF2 rotation.
     */
    public static Quaternion createDof2Rotation(Vector3f from, Vector3f to, Vector3f axis1, Vector3f axis2, Quaternion q)
    {
        if ( q == null ){
            q = new Quaternion();
        }
        
       float phi = calculateDof1Rotation(from, to, axis1);
       float theta = calculateDof1Rotation(from,to, axis2);
        System.out.println("phi : " + FastMath.RAD_TO_DEG * phi);
        System.out.println("theta : " + FastMath.RAD_TO_DEG * theta);
       
        q.fromAngles(0,phi,theta);
        return q;
    }
    
    /**
     * Calculates the angle of rotation to rotate the from point to the to point.
     * @param from the point to rotate. 
     * @param to the point to rotate to.
     * @param axis the axis over which to rotate.
     * @return the angle of the rotation.
     */
    public static float calculateDof1Rotation(Vector3f from, Vector3f to, Vector3f axis)
    {
        float dot1 = axis.dot(from);
        Vector3f pFrom = from.subtract(axis.mult(dot1));
        pFrom.normalizeLocal();
        float dot2 = axis.dot(to);
        Vector3f pTo = to.subtract(axis.mult(dot2));
        pTo.normalizeLocal();
        float angle = FastMath.acos(pFrom.dot(pTo));
        
        Vector3f dir = pFrom.cross(pTo);
        angle = dir.dot(axis)>0?angle:-angle;
        return angle;
    }
}
