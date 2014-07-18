/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.trajectory;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.animation.skeleton.Handle;

/**
 * The footstep target curve creates a target curve for a footstep.
 *
 * @author Koen Samyn
 */
public class FootstepTargetCurve extends TargetCurve {

    private float height = 0.3f;

    /**
     * Creates an empty FootstepTargetCurve.
     */
    public FootstepTargetCurve() {
    }

    /**
     * Creates a new TargetCurve
     *
     * @param start the start handle of the curve.
     * @param end the end handle of the curve.s
     * @param height the height of the footstep.
     */
    public FootstepTargetCurve(Handle start, Handle end, float height) {
        super(start, end);
        this.height = height;
    }

    /**
     * Returns the height of the footstep.
     *
     * @return the height of the footstep.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of the footstep.
     *
     * @param height the new height of the footstep.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Recreates the target curve for the current footstep.
     */
    @Override
    public void recreateTargetCurve() {
        // clears the visible helpers.
        clearDebugNode();
        Vector3f startLoc = getStart().getLocalTranslation();
        Vector3f endLoc = getEnd().getLocalTranslation();

        Vector3f middle = new Vector3f();
        middle.interpolate(startLoc,endLoc, 0.5f);
        middle.y = endLoc.y + height;
        System.out.println("Middle : " + middle);
        
        Quaternion startRotation = getStart().getLocalRotation();
        Quaternion endRotation = getEnd().getLocalRotation();

        Quaternion middleRot = new Quaternion();
        middleRot.slerp(startRotation, endRotation, 0.5f);

        this.createHandle("middle",middle, middleRot);
        
    }
}
