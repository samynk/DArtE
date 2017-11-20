package dae.util;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dae.components.TransformComponent;
import dae.prefabs.AxisEnum;

/**
 * This class offers a couple of useful functions , mostly related to difficult
 * rotation transformations.
 *
 * @author Koen Samyn
 */
public class MathUtil {

    /**
     * Creates a new reference frame starting from a single normal.
     *
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
     *
     * @param from the start point of the rotation.
     * @param to the point to rotate the from point to over the two axis.
     * @param axis1 the first degree of freedom.
     * @param axis2 the second degree of freedom.
     * @param q the quaternion to store the result in. If null, a new quaternion
     * will be created.
     * @return The quaternion with the DOF2 rotation.
     */
    public static Quaternion createDof2Rotation(Vector3f from, Vector3f to, Vector3f axis1, Vector3f axis2, Quaternion q, Vector2f angleOut) {
        if (q == null) {
            q = new Quaternion();
        }

        float phi = calculateDof1Rotation(from, to, axis1);
        q.fromAngleAxis(phi, axis1);
        Vector3f p2 = q.mult(from);
        Vector3f axis2r = q.mult(axis2);
        float theta = calculateDof1Rotation(p2, to, axis2r);
        System.out.println("phi : " + FastMath.RAD_TO_DEG * phi);
        System.out.println("theta : " + FastMath.RAD_TO_DEG * theta);
        angleOut.x = phi;
        angleOut.y = theta;
        q.fromAngles(0, phi, theta);
        return q;
    }

    /**
     * Calculates the angle of rotation to rotate the from point to the to
     * point.
     *
     * @param from the point to rotate.
     * @param to the point to rotate to.
     * @param axis the axis over which to rotate.
     * @return the angle of the rotation.
     */
    public static float calculateDof1Rotation(Vector3f from, Vector3f to, Vector3f axis) {
        float dot1 = axis.dot(from);
        Vector3f pFrom = from.subtract(axis.mult(dot1));
        pFrom.normalizeLocal();
        float dot2 = axis.dot(to);
        Vector3f pTo = to.subtract(axis.mult(dot2));
        pTo.normalizeLocal();
        float angle = FastMath.acos(pFrom.dot(pTo));

        Vector3f dir = pFrom.cross(pTo);
        angle = dir.dot(axis) > 0 ? angle : -angle;
        return angle;
    }

    /**
     * Projects a vector onto the plane going through point [0,0,0] and with a
     * normal defined by the normal parameter.
     *
     * @param toProject the point to project.
     * @param normal the normal of the plane.
     */
    public static void project(Vector3f toProject, Vector3f normal) {
        float dot = normal.dot(toProject);
        toProject.x = toProject.x - dot * normal.x;
        toProject.y = toProject.y - dot * normal.y;
        toProject.z = toProject.z - dot * normal.z;
    }

    /**
     * Changes the parent of a prefab while keeping the world transformation of
     * the prefab.
     * @param toChange the prefab to change.
     * @param newParent the new parent of the prefab.
     * @return the local transformation of the child, relative to the new parent.
     */
    public static Matrix4f changeParent(Node toChange, Node newParent) {
        Matrix4f parentMatrix = new Matrix4f();
        parentMatrix.setTranslation(newParent.getWorldTranslation());
        parentMatrix.setRotationQuaternion(newParent.getWorldRotation());
        parentMatrix.setScale(newParent.getWorldScale());
        parentMatrix.invertLocal();

        Vector3f wtrans = toChange.getWorldTranslation().clone();
        Quaternion wrot = toChange.getWorldRotation().clone();
        Vector3f wscale = toChange.getWorldScale().clone();

        Matrix4f childMatrix = new Matrix4f();
        childMatrix.setTranslation(wtrans);
        childMatrix.setRotationQuaternion(wrot);
        childMatrix.setScale(wscale);

        Matrix4f local = parentMatrix.mult(childMatrix);
        return local;
    }
}
