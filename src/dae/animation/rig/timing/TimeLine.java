/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 * Animates an object The keys for the animation are specified as frame numbers.
 * The rotation keys are always defined in local space.
 *
 * @author Koen.Samyn
 */
public class TimeLine {

    private final Spatial target;
    private final ArrayList<Quaternion> rotationFrames
            = new ArrayList<>();

    /**
     * Creates a new TimeLine object.
     *
     * @param target the target for the time line object.
     */
    public TimeLine(Spatial target) {
        this.target = target;
    }

    /**
     * Returns the target of this time line.
     *
     * @return the target object.
     */
    public Spatial getTarget() {
        return target;
    }

    /**
     * Adds a rotation the list of key frames.
     *
     * @param frame the frame to add the rotation to.
     * @param rotation the rotation key.
     */
    public void addRotation(int frame, Quaternion rotation) {
        // copy the quaternion, avoid sharing quaternion rotations.

        Quaternion copy = new Quaternion(rotation);
        if (frame + 1 > rotationFrames.size()) {
            int numElements = frame + 1 - rotationFrames.size();
            rotationFrames.ensureCapacity(frame + 1);
            for (int i = 0; i < numElements; ++i) {
                rotationFrames.add(null);
            }
        }

        rotationFrames.set(frame, copy);
    }

    public Quaternion getRotation(int f) {
        return rotationFrames.get(f);
    }

    /**
     * Returns the maximum frame number in this timeline.
     *
     * @return the maximum frame number.
     */
    public int getMaxFrameNumber() {
        return rotationFrames.size() - 1;
    }

    /**
     * Checks if an animation key is set in this time line.
     *
     * @param i the animation key.
     * @return true if the animation key is set, false otherwise.
     */
    public boolean containsKey(int i) {
        if (i < rotationFrames.size()) {
            return rotationFrames.get(i) != null;
        }
        return false;
    }

    public int getStartKey(float frame) {
        for (int f = (int) frame; f >= 0; f--) {
            if (containsKey(f)) {
                return f;
            }
        }
        return -1;
    }

    public int getEndKey(float frame) {
        for (int f = (int) (frame + 1); f <= getMaxFrameNumber(); f++) {
            if (containsKey(f)) {
                return f;
            }
        }
        return -1;
    }

    public void interpolateAndSet(float frame) {
        int beforeKey = getStartKey(frame);
        int afterKey = getEndKey(frame);

        if (beforeKey == -1 && afterKey == -1) {
            // nothing can be done.
            return;
        }
        if (beforeKey == -1) {
            Quaternion q = rotationFrames.get(afterKey);
            target.setLocalRotation(q);
        } else if (afterKey == -1) {
            Quaternion q = rotationFrames.get(beforeKey);
            target.setLocalRotation(q);
        } else {
            Quaternion start = rotationFrames.get(beforeKey);
            Quaternion end = rotationFrames.get(afterKey);
            Quaternion startCopy = new Quaternion(start);
            startCopy.slerp(end, (frame - beforeKey) / (afterKey - beforeKey));
            target.setLocalRotation(startCopy);
        }

    }

    public void addAllKeys(TimeLine tl) {
        // share animation keys
        rotationFrames.addAll(tl.rotationFrames);
    }

    public void removeRotation(int currentFrame) {
        if (currentFrame < rotationFrames.size()) {
            rotationFrames.set(currentFrame, null);
        }
    }

}
