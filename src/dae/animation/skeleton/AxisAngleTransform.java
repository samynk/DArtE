/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Koen
 */
public class AxisAngleTransform {

    public static Quaternion createAxisAngleTransform(Vector3f axis, float angle) {
        float newangle = angle / 2.0f;
        float qx = (float) (axis.x * Math.sin(newangle));
        float qy = (float) (axis.y * Math.sin(newangle));
        float qz = (float) (axis.z * Math.sin(newangle));
        float qw = (float) Math.cos(newangle);

        return new Quaternion(qx, qy, qz, qw);
    }
}
