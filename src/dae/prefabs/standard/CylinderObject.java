package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class CylinderObject extends Prefab {

    private Geometry cylinder;
    private float radius = 0.5f;
    private float height = 1.0f;
    private int axialSegments = 6;
    private int radialSegments = 12;
    private AssetManager manager;
    private PhysicsSpace physicsSpace;

    public CylinderObject() {
        setPivot(new Vector3f(0, -radius, 0));
    }

    public CylinderObject(float radius, float height, int axialSegments, int radialSegments) {
        this.radius = radius;
        this.height = height;
        this.axialSegments = axialSegments;
        this.radialSegments = radialSegments;
        setPivot(new Vector3f(0, -height/2, 0));
    }

    @Override
    public final void create(AssetManager manager, String extraInfo) {
        this.manager = manager;
        recreateCylinder();
    }

    @Override
    public String getPrefix() {
        return "Cylinder";
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * The new radius for the cylinder.
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
        recreateCylinder();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        recreateCylinder();
        setPivot(new Vector3f(0, -height/2, 0));

    }

    public int getAxialSegments() {
        return axialSegments;
    }

    public void setAxialSegments(int axialSegments) {
        this.axialSegments = axialSegments;
    }

    public int getRadialSegments() {
        return radialSegments;
    }

    public void setRadialSegments(int radialSegments) {
        this.radialSegments = radialSegments;
    }

    public void drop() {
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.activate();
        }
    }

    @Override
    public void addPhysics(PhysicsSpace space) {
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        this.physicsSpace = space;
        if (rbc == null) {
            rbc = new RigidBodyControl(new CylinderCollisionShape(new Vector3f(radius, height / 2, radius), 1), 1.0f);
            addControl(rbc);
            space.add(rbc);
        }
    }

    private void recreateCylinder() {
        if (cylinder != null) {
            cylinder.removeFromParent();
        }

        Cylinder s = new Cylinder(axialSegments, radialSegments, radius, height, true); // create cube shape at the origin
        cylinder = new Geometry("Sphere", s);  // create cube geometry from the shape

//        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Orange);

        Material cube1Mat = new Material(manager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = manager.loadTexture(
                "Textures/cylinderPattern.png");
        cube1Mat.setTexture("ColorMap", cube1Tex);
        cylinder.setMaterial(cube1Mat);
        Quaternion q = new Quaternion();
        q.fromAngles(-FastMath.PI / 2, 0, 0);
        cylinder.setLocalRotation(q);
        setOriginalMaterial(cube1Mat);
        attachChild(cylinder);



        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.setEnabled(false);
            removeControl(rbc);
        }
        if (physicsSpace != null) {
            rbc = new RigidBodyControl(new CylinderCollisionShape(new Vector3f(radius, height / 2, radius), 1), 1.0f);
            addControl(rbc);
            physicsSpace.add(rbc);
        }
    }
}
