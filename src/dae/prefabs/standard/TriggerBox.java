/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class TriggerBox extends Prefab {

    private Vector3f dimension;
    private String id;

    public TriggerBox() {
        dimension = new Vector3f(1, 1, 1);
    }

    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);

        Box b = new Box(Vector3f.ZERO, dimension); // create cube shape at the origin
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape

        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);

        setOriginalMaterial(mat);
        geom.setMaterial(mat);
        attachChild(geom);
    }

    @Override
    public String getPrefix() {
        return "Trigger";
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
        Box b = new Box(Vector3f.ZERO, dimension);
        Geometry geom = new Geometry("Box", b);
        geom.setMaterial(this.getOriginalMaterial());
        detachAllChildren();
        attachChild(geom);
        this.dimension = dimension;
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
