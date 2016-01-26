package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
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
        installGameComponent(parent);
        
        if (PhysicsSpace.getPhysicsSpace() != null) {
            PhysicsSpace.getPhysicsSpace().add(rigidBodyControl);
        }
    }
    
    @Override
    public void installGameComponent(Spatial parent)
    {
        CollisionShape cs;
        Spatial physicsChild = null;
        if ( parent instanceof Node)
        {
            Node n = (Node)parent;
            physicsChild = n.getChild("physics");
        }
        if (physicsChild != null) {
            cs = CollisionShapeFactory.createMeshShape(physicsChild);
        } else {
            cs = CollisionShapeFactory.createMeshShape(parentComponent);
        }


        rigidBodyControl = new RigidBodyControl(cs, 0);
        rigidBodyControl.setFriction(getFriction());
        parent.addControl(rigidBodyControl);
        
        rigidBodyControl.setEnabled(true);
    }
    
    @Override
     public void deinstall(){
        if ( parentComponent != null){
            parentComponent.removeControl(rigidBodyControl);
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
