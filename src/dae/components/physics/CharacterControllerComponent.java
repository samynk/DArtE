package dae.components.physics;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class CharacterControllerComponent extends PrefabComponent {

    private BetterCharacterControl characterControl;
    private float radius = 0.2f;
    private float height = 1.0f;
    private float mass = 10.0f;
    private int collisionGroup = 1;
    private Prefab parent;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        characterControl = new BetterCharacterControl(radius, height, mass);
        
        parent.addControl(characterControl);
        if (PhysicsSpace.getPhysicsSpace() != null) {
            PhysicsSpace.getPhysicsSpace().add(characterControl);
        }else{
            characterControl.setEnabled(false);
        }
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
        recreate();
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
        recreate();
    }

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
        recreate();
    }

    private void recreate() {
        if (parent != null) {
            if (characterControl != null) {
                parent.removeControl(characterControl);
                if (PhysicsSpace.getPhysicsSpace() != null) {
                    PhysicsSpace.getPhysicsSpace().remove(characterControl);
                }
            }
            characterControl = new BetterCharacterControl(radius, height, mass);
            parent.addControl(characterControl);
            if (PhysicsSpace.getPhysicsSpace() != null) {
                PhysicsSpace.getPhysicsSpace().add(characterControl);
            }
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
     * Warps the character controller to a specific location.
     *
     * @param location the location to warp to.
     */
    public void warp(Vector3f location) {
        if (characterControl != null) {
            characterControl.warp(location);
        }
    }

    public void setViewDirection(Vector3f view) {
        if (characterControl != null) {
            characterControl.setViewDirection(view);
        }
    }

    public void setWalkDirection(Vector3f walk) {
        if (characterControl != null) {
            characterControl.setWalkDirection(walk);
        }
    }
}
