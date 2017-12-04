package dae.animation.trajectory;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/** 
 * A curve target defines a target within a curve. A target can define
 * metadata to help with the generation of the final animation.
 * @author Koen Samyn
 */
public class CurveTarget {
    /**
     * The name of the target
     */
    private final String name;
    /**
     * The translation of the target.
     */
    private final Vector3f translation;
    /**
     * The rotation of the target.
     */
    private final Quaternion rotation;
    
    
    public CurveTarget(String name, Vector3f translation, Quaternion rotation)
    {
        this.translation = translation;
        this.rotation = rotation;
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    /**
     * Return the local translation of the target.
     * @return the local translation.
     */
    public Vector3f getLocalTranslation(){
        return translation;
    }
    
    /**
     * Return the local rotation of the target.
     * @return the local rotation.
     */
    public Quaternion getLocalRotation(){
        return rotation;
    }
}
