package dae.components.physics;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Gizmo;

/**
 *
 * @author Koen Samyn
 */
public class PhysicsBoxComponent extends PrefabComponent {

    private Prefab parentComponent;
    private float mass = 10.0f;
    private float restitution = 0.0f;
    private float friction = 0.0f;
    private float linearDamping = 0.0f;
    private Vector3f dimension;
    private int collisionGroup;
    
    private RigidBodyControl rigidBodyControl;

    /**
     * @return the mass
     */
    public float getMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    /**
     * @return the dimension
     */
    public Vector3f getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(Vector3f dimension) {
        this.dimension = dimension;
    }

    /**
     * Activates a sleeping physics object.
     */
    public void drop() {
        if (parentComponent != null) {
            RigidBodyControl rbc = parentComponent.getControl(RigidBodyControl.class);
            if (rbc != null) {
                rbc.activate();
            }
        }
    }

    @Override
    public void install(Prefab parent) {
        parentComponent = parent;
        Vector3f backupTrans = parent.getLocalTranslation().clone();
        Quaternion backupRotation = parent.getLocalRotation().clone();
        parent.setLocalTranslation(Vector3f.ZERO);
        parent.setLocalRotation(Quaternion.IDENTITY);
        parent.updateModelBound();
        CompoundCollisionShape finalShape = new CompoundCollisionShape();
        mass = 0;
        for (Spatial s : parent.getChildren()) {
            if (!(s instanceof Gizmo)) {
                BoundingBox bb = (BoundingBox) s.getWorldBound();
                Vector3f center = new Vector3f();
                bb.getCenter(center);
                Vector3f exts = new Vector3f();
                bb.getExtent(exts);

                BoxCollisionShape shape = new BoxCollisionShape(exts);
                finalShape.addChildShape(shape, Vector3f.ZERO);
                mass += bb.getVolume();
            }
        }
        rigidBodyControl = new RigidBodyControl(finalShape, mass);
        rigidBodyControl.setRestitution(restitution);
        rigidBodyControl.setFriction(getFriction());
        parent.addControl(rigidBodyControl);
        if ( PhysicsSpace.getPhysicsSpace() != null)
        {
            PhysicsSpace.getPhysicsSpace().add(rigidBodyControl);
        }
        rigidBodyControl.setEnabled(true);
        rigidBodyControl.setPhysicsLocation(backupTrans);
        rigidBodyControl.setPhysicsRotation(backupRotation);

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

    /**
     * @return the restitution
     */
    public float getRestitution() {
        return restitution;
    }

    /**
     * @param restitution the restitution to set
     */
    public void setRestitution(float restitution) {
        this.restitution = restitution;
        if ( rigidBodyControl != null){
            rigidBodyControl.setRestitution(restitution);
            System.out.println("Setting restitution");
        }
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
         if ( rigidBodyControl != null){
            rigidBodyControl.setFriction(friction);
            System.out.println("Setting restitution");
        }
    }

    /**
     * @return the linearDamping
     */
    public float getLinearDamping() {
        return linearDamping;
    }

    /**
     * @param linearDamping the linearDamping to set
     */
    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }
}
