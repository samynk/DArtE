package dae.animation.trajectory;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.animation.skeleton.Handle;

/**
 *
 * @author Koen Samyn
 */
public class HandTargetCurve extends TargetCurve {

    private float height = 0.3f;

    /**
     * Creates an empty FootstepTargetCurve.
     */
    public HandTargetCurve() {
        CurveChannel cc = new CurveChannel("rightarm");
        this.addCurveChannel(cc);
    }

    /**
     * Creates a new TargetCurve
     *
     * @param start the start handle of the curve.
     * @param end the end handle of the curve.s
     * @param height the height of the footstep.
     */
    public HandTargetCurve(Handle start, Handle end) {
        super(start, end);
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
        middle.interpolateLocal(startLoc, endLoc, 0.5f);

        Quaternion startRotation = getStart().getLocalRotation();
        Quaternion endRotation = getEnd().getLocalRotation();

        Quaternion middleRot = new Quaternion();
        middleRot.slerp(startRotation, endRotation, 0.5f);

        this.createHandle("middle",middle, middleRot);

        CurveChannel rightarm = getCurveChannel("rightarm");
        rightarm.clearTargets();

        rightarm.addCurveTarget(new CurveTarget( "start", startLoc, startRotation));
        rightarm.addCurveTarget(new CurveTarget( "middle", middle, middleRot));
        rightarm.addCurveTarget(new CurveTarget( "end", endLoc, endRotation));
    }
}
