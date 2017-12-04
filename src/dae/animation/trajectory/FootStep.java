package dae.animation.trajectory;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Koen
 */
public class FootStep extends Node {

    private final Vector3f location;
    private Vector3f direction;
    private static Vector3f normalDirAxis = new Vector3f(1, 0, 0);
    private String type;

    public FootStep(Material material, Vector3f location) {
        Box step = new Box(0.5f, 0.01f, 0.5f);
        Geometry jg = new Geometry(name + "_joint", step);
        jg.setShadowMode(ShadowMode.Off);
        jg.setMaterial(material);
        this.location = location;
        this.location.y += 0.1f;
        setLocalTranslation(location);
        this.attachChild(jg);
    }

    public FootStep(Material material, String type, Vector3f location, Vector3f direction) {
        this.type = type;
        this.location = location;
        this.direction = direction;

        Vector3f nDirection = direction.normalize();

        Vector3f rotAxis = direction.cross(normalDirAxis);
        float angle = FastMath.acos(rotAxis.dot(nDirection));

        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, rotAxis);

        this.setLocalRotation(q);
        this.setLocalTranslation(location);

        Box step = new Box(0.5f, 0.01f, 0.5f);
        Geometry jg = new Geometry(name + "_joint", step);
        jg.setShadowMode(ShadowMode.Off);
        jg.setMaterial(material);

        this.attachChild(jg);
    }

    public String getType() {
        return type;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
