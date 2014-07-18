/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Stores the result of the path calculation, so different responses can be
 * compared.
 *
 * @author Koen
 */
public class PathingResult {

    public float length;
    public Vector3f c1;
    public Vector3f c2;
    public Vector3f t1;
    public Vector3f t2;
    public float startAngle1;
    public float endAngle1;
    public boolean cw1;
    public float startAngle2;
    public float endAngle2;
    public boolean cw2;

    public PathingResult() {
        length = Float.MAX_VALUE;

        c1 = new Vector3f();
        c2 = new Vector3f();
        t1 = new Vector3f();
        t2 = new Vector3f();
    }

    public void printAngles() {
        System.out.println("Angles 1 : " + startAngle1 * FastMath.RAD_TO_DEG + "," + endAngle1 * FastMath.RAD_TO_DEG);
        System.out.println("Angles 2 : " + startAngle2 * FastMath.RAD_TO_DEG + "," + endAngle2 * FastMath.RAD_TO_DEG);
    }
}