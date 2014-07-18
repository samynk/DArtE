/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.math;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * Calculates the locations where two rays intersect
 *
 * @author Koen Samyn
 */
public class RayIntersect {

    public static boolean rayIntersect(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, Vector3f pa, Vector3f pb, Vector2f t) {
        Vector3f p13 = new Vector3f();
        Vector3f p43 = new Vector3f();
        Vector3f p21 = new Vector3f();
        float d1343, d4321, d1321, d4343, d2121;
        float numer, denom;

        p13.x = p1.x - p3.x;
        p13.y = p1.y - p3.y;
        p13.z = p1.z - p3.z;
        p43.x = p4.x - p3.x;
        p43.y = p4.y - p3.y;
        p43.z = p4.z - p3.z;
        if (FastMath.abs(p43.x) < FastMath.FLT_EPSILON && 
                FastMath.abs(p43.y) < FastMath.FLT_EPSILON && 
                FastMath.abs(p43.z) < FastMath.FLT_EPSILON) {
            return false;
        }
        p21.x = p2.x - p1.x;
        p21.y = p2.y - p1.y;
        p21.z = p2.z - p1.z;
        if (FastMath.abs(p21.x) <  FastMath.FLT_EPSILON  && 
                FastMath.abs(p21.y) <  FastMath.FLT_EPSILON  && 
                FastMath.abs(p21.z) <  FastMath.FLT_EPSILON ) {
            return false;
        }

        d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
        d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
        d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
        d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
        d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;

        denom = d2121 * d4343 - d4321 * d4321;
        if (FastMath.abs(denom) < FastMath.FLT_EPSILON) {
            return false;
        }
        numer = d1343 * d4321 - d1321 * d4343;

        float mua = numer / denom;
        float mub = (d1343 + d4321 * (  mua)) / d4343;
        
        t.x = mua;
        t.y = mub;

        pa.x = p1.x +  mua * p21.x;
        pa.y = p1.y +  mua * p21.y;
        pa.z = p1.z +  mua * p21.z;
        pb.x = p3.x +  mub * p43.x;
        pb.y = p3.y +  mub * p43.y;
        pb.z = p3.z +  mub * p43.z;
        return true;
    }
}
