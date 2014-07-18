/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;

/**
 * This class describes a camera. The main property of a camera is its id.
 *
 * @author Koen
 */
public class CameraEntity extends Prefab {

    private String cameraId;
    private boolean startCam;

    public CameraEntity() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        Spatial node = manager.loadModel("Entities/Entity_Camera.j3o");
        this.attachChild(node);
        this.setCategory("Standard");
        this.setType("Camera");
    }

    public void setCameraId(String eventid) {
        this.cameraId = eventid;
    }

    public String getCameraId() {
        return cameraId;
    }

    public boolean getStartCam() {
        return startCam;
    }

    public void setStartCam(boolean startCam) {
        this.startCam = startCam;
    }
}
