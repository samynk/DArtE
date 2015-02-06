package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class PhysicsConcaveComponent extends PrefabComponent {

    private Prefab parentComponent;
    private RigidBodyControl rigidBodyControl;
    private float friction;
    private int collisionGroup;

    @Override
    public void install(Prefab parent) {
        parentComponent = parent;
        Vector3f backupTrans = parent.getLocalTranslation().clone();
        Quaternion backupRotation = parent.getLocalRotation().clone();
        parent.setLocalTranslation(Vector3f.ZERO);
        parent.setLocalRotation(Quaternion.IDENTITY);
        parent.updateModelBound();

        CollisionShape cs;
        CompoundCollisionShape ccs = new CompoundCollisionShape();
        Spatial physicsChild = parentComponent.getChild("physics");
        if (physicsChild != null) {
            
            cs = CollisionShapeFactory.createMeshShape(physicsChild);
        } else {
            cs = CollisionShapeFactory.createMeshShape(parentComponent);
        }


        rigidBodyControl = new RigidBodyControl(cs, 0);
        rigidBodyControl.setFriction(getFriction());
        parent.addControl(rigidBodyControl);
        if (PhysicsSpace.getPhysicsSpace() != null) {
            PhysicsSpace.getPhysicsSpace().add(rigidBodyControl);
        }
        rigidBodyControl.setEnabled(true);
        rigidBodyControl.setPhysicsLocation(backupTrans);
        rigidBodyControl.setPhysicsRotation(backupRotation);
    }

    /**
     * @return the friction
     */
    public float getFriction() {
        return friction;
    }

    /**
     * @param friction the friction to set
     */
    public void setFriction(float friction) {
        this.friction = friction;
        if (rigidBodyControl != null) {
            rigidBodyControl.setFriction(friction);
        }
    }

    /**
     * @return the collisionGroup
     */
    public int getCollisionGroup() {
        return collisionGroup;
    }

    /**
     * @param collisionGroup the collisionGroup to set
     */
    public void setCollisionGroup(int collisionGroup) {
        this.collisionGroup = collisionGroup;
    }
}
