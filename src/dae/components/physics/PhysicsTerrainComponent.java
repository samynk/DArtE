package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.prefabs.standard.Terrain;

/**
 *
 * @author Koen Samyn
 */
public class PhysicsTerrainComponent extends PrefabComponent {

    private RigidBodyControl rbc;
    private float friction;
    private int collisionGroup = 1;

    @Override
    public void install(Prefab parent) {
        if (parent instanceof Terrain) {
            Terrain t = (Terrain) parent;


            CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(t);
            rbc = new RigidBodyControl(terrainShape,  0);
            rbc.setFriction(friction);
            rbc.setCollisionGroup(collisionGroup);
            t.addControl(rbc);
            if (PhysicsSpace.getPhysicsSpace() != null) {
                PhysicsSpace.getPhysicsSpace().add(t);
            }
            rbc.setEnabled(true);
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
        if (rbc != null) {
            rbc.setFriction(friction);
        }
    }

    public int getCollisionGroup() {
        return collisionGroup;
    }

    public void setCollisionGroup(int cg) {
        this.collisionGroup = cg;
        if (rbc != null) {
            rbc.setCollisionGroup(collisionGroup);
        }
    }
}
