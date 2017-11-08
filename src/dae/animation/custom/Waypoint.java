package dae.animation.custom;

import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;

/**
 * A handle object can define a number of targets for an animation. The most
 * important property is the location property but axises that are targets for
 * alignment can also be defined.
 *
 * @author Koen Samyn
 */
public class Waypoint extends Prefab {

    public Waypoint() {
    }

    @Override
    public final void create(AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Skeleton/Helpers/waypoint.j3o"));
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation); //To change body of generated methods, choose Tools | Templates.
        if (getParent() instanceof CharacterPath) {
            ((CharacterPath) getParent()).createPathMesh();
        }
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z); //To change body of generated methods, choose Tools | Templates.
        if (getParent() instanceof CharacterPath) {
            ((CharacterPath) getParent()).createPathMesh();
        }
    }

    @Override
    public int attachChild(Spatial child) {
        if (getParent() instanceof CharacterPath && child instanceof Waypoint) {
            Waypoint w = (Waypoint) child;
            // local transform is relative to this transform.
            // needs to be relative to parent.
            Matrix4f parentMatrix = new Matrix4f();
            parentMatrix.setTranslation(getParent().getWorldTranslation());
            parentMatrix.setRotationQuaternion(getParent().getWorldRotation());
            parentMatrix.setScale(getParent().getWorldScale());
            parentMatrix.invertLocal();

            Matrix4f childMatrix = new Matrix4f();
            childMatrix.setTranslation(child.getWorldTranslation());
            childMatrix.setRotationQuaternion(child.getWorldRotation());
            childMatrix.setScale(child.getWorldScale());

            Matrix4f local = parentMatrix.mult(childMatrix);

            child.setLocalTranslation(local.toTranslationVector());
            child.setLocalRotation(local.toRotationQuat());
            child.setLocalScale(local.toScaleVector());

            ((CharacterPath) getParent()).insertWaypointAfter(this, w);

            // child is not attached to this node.
            return -1;
        } else {
            return super.attachChild(child);
        }
    }
}