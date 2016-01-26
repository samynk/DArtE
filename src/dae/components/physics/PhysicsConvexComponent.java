package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.components.PrefabComponent;
import dae.components.TransformComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class PhysicsConvexComponent extends PrefabComponent {

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
        installGameComponent(parent);
        if (PhysicsSpace.getPhysicsSpace() != null) {
            PhysicsSpace.getPhysicsSpace().add(rigidBodyControl);
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {
        CollisionShape cs;
        if (parent instanceof Node) {
            Node p = (Node) parent;
            Spatial physics = p.getChild("physics");
            if (physics != null) {
                Vector3f scaleToApply = parent.getWorldScale().clone();
                physics.setLocalScale(scaleToApply);
                physics.updateGeometricState();
                CompoundCollisionShape ccs = new CompoundCollisionShape();

                ccs.addChildShape(CollisionShapeFactory.createDynamicMeshShape(physics), Vector3f.ZERO);
                cs = ccs;
                /*parent.setLocalScale(Vector3f.UNIT_XYZ);
                for (Spatial s : p.getChildren()) {
                    if (physics != s) {
                        s.setLocalScale(scaleToApply.x, scaleToApply.y, scaleToApply.z);
                    }
                }*/
            } else {
                cs = CollisionShapeFactory.createDynamicMeshShape(parent);
            }
        } else {
            cs = CollisionShapeFactory.createDynamicMeshShape(parent);
        }
        parent.updateGeometricState();
        rigidBodyControl = new RigidBodyControl(cs, mass);
        rigidBodyControl.setRestitution(restitution);
        rigidBodyControl.setFriction(getFriction());
        rigidBodyControl.setDamping(0.5f, 0.25f);
        parent.addControl(rigidBodyControl);
    }

    @Override
    public void deinstall() {
        if (parentComponent != null) {
            PhysicsSpace space = rigidBodyControl.getPhysicsSpace();
            Vector3f loc = rigidBodyControl.getPhysicsLocation();
            Quaternion rot = rigidBodyControl.getPhysicsRotation();

            parentComponent.removeControl(rigidBodyControl);

            if (space != null) {
                space.remove(rigidBodyControl);
            }
            
            TransformComponent tc = (TransformComponent) parentComponent.getComponent("TransformComponent");
            tc.setTranslation(loc);
            tc.setRotation(rot);
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
        if (rigidBodyControl != null) {
            rigidBodyControl.setRestitution(restitution);
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
        if (rigidBodyControl != null) {
            rigidBodyControl.setFriction(friction);
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
