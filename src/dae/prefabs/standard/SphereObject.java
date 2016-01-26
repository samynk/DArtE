/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class SphereObject extends Prefab {
    
    private Geometry sphere;
    private float radius = 0.5f;
    private AssetManager manager;
    private PhysicsSpace space;
    
    public SphereObject() {
        setPivot(new Vector3f(0, -0.5f, 0));
    }
    
    public SphereObject(float radius) {
        this.radius = radius;
        setPivot(new Vector3f(0, -radius, 0));
    }
    
    @Override
    public Prefab duplicate(AssetManager manager) {
        SphereObject so = new SphereObject(this.radius);
        so.space = this.space;
        so.create( manager, null);
        so.setName(name);
        so.setType(this.getType());
        so.setCategory(this.getCategory());
        return so;
    }
    
    @Override
    public final void create(AssetManager manager, String extraInfo) {
        this.manager = manager;
        recreate();
    }
    
    private void recreate() {
        if (sphere != null) {
            sphere.removeFromParent();
        }
        
        Sphere s = new Sphere(24, 24, radius); // create cube shape at the origin
        sphere = new Geometry("Sphere", s);  // create cube geometry from the shape

//        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Orange);

        Material cube1Mat = new Material(manager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = manager.loadTexture(
                "Textures/spherePattern.png");
        cube1Mat.setTexture("ColorMap", cube1Tex);
        sphere.setMaterial(cube1Mat);
        
        setOriginalMaterial(cube1Mat);
        //box.setMaterial(mat);
        attachChild(sphere);
        
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.setEnabled(false);
            removeControl(rbc);
        }
        if (space != null) {
            rbc = new RigidBodyControl(new SphereCollisionShape(radius), 1.0f);
            addControl(rbc);
            space.add(rbc);
            rbc.setEnabled(false);
        }
    }
    
    @Override
    public String getPrefix() {
        return "Sphere";
    }

    /**
     * @return the dimension
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
        setPivot(new Vector3f(0, -radius, 0));
        recreate();
    }
    
    public void drop() {
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            if (!rbc.isEnabled()) {
                rbc.setEnabled(true);
            }
            rbc.activate();
        }
    }
    
    @Override
    public void addPhysics(PhysicsSpace space) {
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        this.space = space;
        if (rbc == null) {
            rbc = new RigidBodyControl(new SphereCollisionShape(radius), 1.0f);
            addControl(rbc);
            space.add(rbc);
        }
    }
}
