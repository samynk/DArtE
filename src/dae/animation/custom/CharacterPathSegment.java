/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.math.Vector3f;

/**
 * This is a single bezier spline that can be used as a part of the walking path
 * of a character.
 *
 * @author Koen
 */
public class CharacterPathSegment implements PathSegment {

    private Vector3f start;
    private Vector3f cp1;
    private Vector3f cp2;
    private Vector3f end;

    public CharacterPathSegment(Vector3f start, Vector3f cp1, Vector3f cp2, Vector3f end) {
        this.start = new Vector3f(start);
        this.cp1 = new Vector3f(cp1);
        this.cp2 = new Vector3f(cp2);
        this.end = new Vector3f(end);
        end.y = start.y;
        cp1.y = start.y;
        cp2.y = start.y;
    }

    public void interpolate(float t, Vector3f result) {
        float oneMinusT = (1.0f - t);
        float oneMinusT2 = oneMinusT * oneMinusT;
        float oneMinusT3 = oneMinusT2 * oneMinusT;

        float t2 = t * t;
        float t3 = t2 * t;

        float x = start.x * oneMinusT3 + 3.0f * cp1.x * oneMinusT2 * t + 3.0f * cp2.x * oneMinusT * t2 + end.x * t3;
        float y = start.y * oneMinusT3 + 3.0f * cp1.y * oneMinusT2 * t + 3.0f * cp2.y * oneMinusT * t2 + end.y * t3;
        float z = start.z * oneMinusT3 + 3.0f * cp1.z * oneMinusT2 * t + 3.0f * cp2.z * oneMinusT * t2 + end.z * t3;

        result.set(x, y, z);
    }

    public float getSegment(float t, float desiredLength) {
        Vector3f lastPoint = new Vector3f();
        Vector3f currentPoint = new Vector3f();
        interpolate(t, lastPoint);
        currentPoint.set(lastPoint);
        float currentLength = 0;
        while (currentLength < desiredLength && t < 1.0f) {
            t += 1e-3f;
            interpolate(t, currentPoint);
            currentLength += currentPoint.distance(lastPoint);
            lastPoint.set(currentPoint);
        }
        if (t > 1.0f) {
            t = 1.0f;
        }
        return t;
    }

    public void getTangent(float t, Vector3f tangent) {
        float oneMinusT = (1.0f - t);
        float oneMinusT2 = oneMinusT * oneMinusT;
        float t2 = t * t;

        float a = -3 * oneMinusT2;
        float b = -2 * oneMinusT * t + oneMinusT2;
        float c = -t2 + 2 * t * oneMinusT;
        float d = 3 * t2;

        float x = start.x * a + 3.0f * cp1.x * b + 3.0f * cp2.x * c + end.x * d;
        float y = start.y * a + 3.0f * cp1.y * b + 3.0f * cp2.y * c + end.y * d;
        float z = start.z * a + 3.0f * cp1.z * b + 3.0f * cp2.z * c + end.z * d;

        tangent.set(x, y, z);
    }
    
    public float getTotalLength(){
        return start.distance(end);
    }
}
