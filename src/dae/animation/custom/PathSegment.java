/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.math.Vector3f;

/**
 * An interface that offers the basic functionality of parametric splines.
 *
 * @author Koen
 */
public interface PathSegment {

    /**
     * Interpolates the spline at the specified parameter t.
     *
     * @param t the parameter t to interpolate the spline with.
     * @param result the Vector3f object that will contain the result.
     */
    public void interpolate(float t, Vector3f result);

    /**
     * Calculates a segment with a desired length.
     *
     * @param t the start value for the segment.
     * @param desiredLength the desired length of the segment.
     */
    public float getSegment(float t, float desiredLength);

    /**
     * Gets the tangent vector of the spline, this is important to orient
     * characters with.
     *
     * @param t the parameter t for interpolation.
     * @param tangent the Vector3f object that will contain the tangent vector.
     */
    public void getTangent(float t, Vector3f tangent);
    
    /**
     * Returns the total length of the segment.
     * @return the total length of the segment.
     */
    public float getTotalLength();
    
    
}
