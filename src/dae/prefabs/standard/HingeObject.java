/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class HingeObject extends Prefab {

    private String id;
    private String hingeObject;
    private Spatial hingeObjectNode;
    private AssetManager assetManager;

    public HingeObject() {
    }

    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.assetManager = manager;

        attachChild(assetManager.loadModel("Entities/M_Joint.j3o"));

        this.setCategory("Standard");
        this.setType("Hinge");
    }

    @Override
    public String getPrefix() {
        return "Hinge";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setHingeObject(String object) {
        try {
            hingeObjectNode = assetManager.loadModel(object);
            this.attachChild(hingeObjectNode);
            hingeObject = object;
        } catch (Exception ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null,ex);
        }
    }

    public String getHingeObject() {
        return hingeObject;
    }

    public void setXOffset(float xoffset) {

        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            hingeObjectNode.setLocalTranslation(xoffset, l.y, l.z);
        }
    }

    public float getXOffset() {
        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            return l.x;
        } else {
            return 0.0f;
        }
    }

    public void setYOffset(float yoffset) {

        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            hingeObjectNode.setLocalTranslation(l.x, yoffset, l.z);
        }
    }

    public float getYOffset() {
        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            return l.y;
        } else {
            return 0.0f;
        }
    }

    public void setZOffset(float zoffset) {

        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            hingeObjectNode.setLocalTranslation(l.x, l.y, zoffset);
        }
    }

    public float getZOffset() {
        if (hingeObjectNode != null) {
            Vector3f l = hingeObjectNode.getLocalTranslation();
            return l.z;
        } else {
            return 0.0f;
        }
    }
    private float[] offsetAngles = new float[3];

    public void setXOffsetRotation(float xRot) {
        this.getLocalRotation().toAngles(offsetAngles);
        Quaternion q = new Quaternion();
        q.fromAngles(FastMath.DEG_TO_RAD * xRot, offsetAngles[1], offsetAngles[2]);
        this.setLocalRotation(q);
    }

    public float getXOffsetRotation() {
        this.getLocalRotation().toAngles(offsetAngles);
        float angle = offsetAngles[0];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }

    public void setYOffsetRotation(float yRot) {
        this.getLocalRotation().toAngles(offsetAngles);
        Quaternion q = new Quaternion();
        q.fromAngles(offsetAngles[0], FastMath.DEG_TO_RAD * yRot, offsetAngles[2]);
        this.setLocalRotation(q);
    }

    public float getYOffsetRotation() {
        this.getLocalRotation().toAngles(offsetAngles);
        float angle = offsetAngles[1];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }

    public void setZOffsetRotation(float zRot) {
        this.getLocalRotation().toAngles(offsetAngles);
        Quaternion q = new Quaternion();
        q.fromAngles(offsetAngles[0], offsetAngles[1], FastMath.DEG_TO_RAD * zRot);
        this.setLocalRotation(q);
    }

    public float getZOffsetRotation() {
        this.getLocalRotation().toAngles(offsetAngles);
        float angle = offsetAngles[2];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }
}
