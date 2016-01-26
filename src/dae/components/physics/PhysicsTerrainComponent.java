package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.prefabs.standard.Terrain;

/**
 *
 * @author Koen Samyn
 */
public class PhysicsTerrainComponent extends PrefabComponent {
    private Spatial parent;
    private RigidBodyControl rbc;
    private float friction;
    private int collisionGroup = 1;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        installGameComponent(parent);
        if (PhysicsSpace.getPhysicsSpace() != null) {
            PhysicsSpace.getPhysicsSpace().add(parent);
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {
        if (parent instanceof Terrain || parent instanceof TerrainQuad) {
            Vector3f backupTrans = parent.getLocalTranslation().clone();
            Quaternion backupRotation = parent.getLocalRotation().clone();
            parent.setLocalTranslation(Vector3f.ZERO);
            parent.setLocalRotation(Quaternion.IDENTITY);

            CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(parent);
            rbc = new RigidBodyControl(terrainShape, 0);
            rbc.setFriction(friction);
            rbc.setCollisionGroup(collisionGroup);
            parent.addControl(rbc);
            rbc.setPhysicsLocation(backupTrans);
            rbc.setPhysicsRotation(backupRotation);
            rbc.setEnabled(true);
        }
    }
    
    @Override
     public void deinstall(){
        if ( parent != null){
            parent.removeControl(rbc);
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
