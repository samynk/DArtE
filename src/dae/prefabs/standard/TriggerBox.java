package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import dae.prefabs.Prefab;

/**
 * Creates a physics trigger box.
 *
 * @author Koen Samyn
 */
public class TriggerBox extends Prefab {

    private Vector3f dimension;
    private String id;
    private PhysicsSpace physicsSpace;

    public TriggerBox() {
        dimension = new Vector3f(1, 1, 1);
    }

    /**
     * Returns the prefix for this trigger box.
     *
     * @return the prefix.
     */
    @Override
    public String getPrefix() {
        return "Trigger";
    }

    @Override
    public final void create(AssetManager manager, String extraInfo) {       

        Box b = new Box(dimension.mult(-0.5f), dimension.mult(0.5f)); // create cube shape at the origin
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape

        Material mat = manager.loadMaterial("Materials/TriggerBoxMaterial.j3m");
        setOriginalMaterial(mat);
        geom.setMaterial(mat);
        attachChild(geom);

        this.setQueueBucket(Bucket.Transparent);
    }

    @Override
    public void addPhysics(PhysicsSpace space) {
        this.physicsSpace = space;
        createControl();
    }

    /**
     * Creates the control on the basis of the current dimensions.
     */
    private void createControl() {
        GhostControl gc = this.getControl(GhostControl.class);
        if (gc == null) {
            gc = new GhostControl(new BoxCollisionShape(dimension.mult(0.5f)));
            addControl(gc);

        }
        if (physicsSpace != null && gc.getPhysicsSpace() == null) {
            physicsSpace.add(gc);
        }
    }

    /**
     * @return the dimension
     */
    public Vector3f getDimension() {
        return dimension;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(Vector3f dimension) {
        Box b = new Box(dimension.mult(-0.5f), dimension.mult(0.5f));
        Geometry geom = new Geometry("Box", b);
        geom.setMaterial(this.getOriginalMaterial());
        detachAllChildren();
        attachChild(geom);
        this.dimension = dimension;

        GhostControl gc = this.getControl(GhostControl.class);
        if (gc != null) {
            gc.setEnabled(false);
            removeControl(gc);
        }
        createControl();
    }

    public void setWidth(float width) {
        this.dimension.x = width;
        setDimension(dimension);
    }

    public float getWidth() {
        return dimension.x;
    }

    public void setHeight(float height) {
        this.dimension.y = height;
        setDimension(dimension);
    }

    public float getHeight() {
        return dimension.y;
    }

    public void setLength(float length) {
        this.dimension.z = length;
        setDimension(dimension);
    }

    public float getLength() {
        return dimension.z;
    }
}
