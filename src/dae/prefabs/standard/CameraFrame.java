package dae.prefabs.standard;

import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.DAEFlyByCamera;

/**
 * A data class that contains the relevant information to position a camera in
 * the level.
 *
 * @author Koen Samyn
 */
public class CameraFrame {

    private Vector3f translation;
    private Quaternion rotation;
    private Matrix4f projectionMatrix;

    /**
     * Copies the data from the DAEFlyByCamera object.
     *
     * @param cam the camera to copy.
     */
    public CameraFrame(DAEFlyByCamera cam) {
        copy(cam);
    }

    /**
     * Makes a copy of the view and projection matrix of the camera.
     *
     * @param cam the camera to copy.
     */
    public void copy(DAEFlyByCamera cam) {
        translation = cam.getTranslation();
        rotation = cam.getRotation();
        projectionMatrix = cam.getProjectionMatrix();
    }

    /**
     * @return the translation of the camera.
     */
    public Vector3f getTranslation() {
        return translation;
    }
    
    /**
     * @return the rotation of the camera.
     */
    public Quaternion getRotation(){
        return rotation;
    }

    /**
     *
     * @return the projection matrix of this camera frame.
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
