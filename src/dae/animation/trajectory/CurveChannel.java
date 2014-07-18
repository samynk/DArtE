/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.trajectory;

import java.util.ArrayList;

/**
 * A curve channel contains the CurveTarget objects for an attachment point on
 * the skeleton.
 *
 * @author Koen Samyn
 */
public class CurveChannel {

    private String attachmentPointName;
    private ArrayList<CurveTarget> curveTargets = new ArrayList<CurveTarget>();
    private int currentTarget = 0;

    /**
     * Creates a new CurveChannel object with
     *
     * @param attachmentPointName
     */
    public CurveChannel(String attachmentPointName) {
        this.attachmentPointName = attachmentPointName;
    }

    /**
     * Returns the name of the attachment point that is targeted by this
     * CurveChannel.
     *
     * @return the attachmentpoint name.
     */
    public String getAttachmentPointName() {
        return attachmentPointName;
    }

    /**
     * Adds a CurveTarget to this channel.
     *
     * @param target the target to add to this channel.
     */
    public void addCurveTarget(CurveTarget target) {
        curveTargets.add(target);
    }

    /**
     * Checks if the channel has more targets.
     *
     * @return true if the channel has more targets, false otherwise.
     */
    public boolean hasNextTarget() {
        return currentTarget != curveTargets.size();
    }

    /**
     * Returns the next target in the list.
     *
     * @return the next target in the list.
     */
    public void nextTarget() {
        if (currentTarget + 1 != curveTargets.size()) {
            currentTarget++;
        }
    }

    /**
     * Get the current target in the list.
     */
    public CurveTarget getCurrentTarget() {
        return curveTargets.get(currentTarget);
    }

    /**
     * Resets the curve target to the start of the channel.
     */
    public void reset() {
        currentTarget = 0;
    }

    /**
     * Removes all the targets of the channel.
     */
    void clearTargets() {
        currentTarget = 0;
        this.curveTargets.clear();
    }
}
