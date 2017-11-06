package dae.animation.rig;

import com.jme3.math.Vector3f;
import java.util.List;

/**
 * This interface defines the basic capabilities of a joint.
 * This is necessary to support the input and output framework.
 * @author Koen.Samyn
 */
public interface Joint {
    /**
     * Returns the name of the joint.
     * @return the name of the joint.
     */
    public String getName();
    
     /**
     * Return the supported input connector types of this joint.
     *
     * @return the supported input connector types.
     */
    public List<ConnectorType> getInputConnectorTypes();

    /**
     * Return the supported output connector types of this joint.
     *
     * @return the supported input connector types.
     */
    public List<ConnectorType> getOutputConnectorTypes();
    
    /**
     * Returns the world translation of the joint.
     * @return the world translation of the joint.
     */
    public Vector3f getWorldTranslation();

    /**
     * Returns the current world rotation axis of the joint.
     * @return the world rotation axis of the joint.
     */
    public Vector3f getWorldRotationAxis();
    
    /**
     * Sets the world rotation axis. 
     * @param axis the axis in world coordinates.
     */
    public void setWorldRotationAxis(Vector3f axis);

    /**
     * Rotates the joint from its current position over the specified angle.
     * @param angle the angle of rotation.
     */
    public void rotate(float angle);

    
}
