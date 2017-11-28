/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 * Animates an object The keys for the animation are
 * specified as frame numbers.
 * The rotation keys are always defined in local space.
 * @author Koen.Samyn
 */
public class TimeLine {
    private final Spatial target;
    private final ArrayList<Quaternion> rotationFrames=
            new ArrayList<>();
    
    /**
     * Creates a new TimeLine object.
     * @param target the target for the time line object.
     */
    public TimeLine(Spatial target){
        this.target = target;
    }
    
    /**
     * Returns the target of this time line.
     * @return the target object.
     */
    public Spatial getTarget(){
        return target;
    }
    
    /**
     * Adds a rotation the list of key frames.
     * @param frame the frame to add the rotation to.
     * @param rotation the rotation key.
     */
    public void addRotation(int frame, Quaternion rotation)
    {
        // copy the quaternion, avoid sharing quaternion rotations.
        Quaternion copy = new Quaternion(rotation);
        if ( frame +1 > rotationFrames.size()){
            rotationFrames.ensureCapacity(frame+1);
        }
        rotationFrames.add(frame,copy);
    }
    
    /**
     * Returns the maximum frame number in this timeline.
     * @return the maximum frame number.
     */
    public int getMaxFrameNumber(){
        return rotationFrames.size()-1;
    }

    /**
     * Checks if an animation key is set in this time line.
     * @param i the animation key.
     * @return true if the animation key is set, false otherwise.
     */
    public boolean containsKey(int i) {
        if ( i < rotationFrames.size()){
            return rotationFrames.get(i) != null;
        }
        return false;
    }
}
