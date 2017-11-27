/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import dae.GlobalObjects;
import dae.animation.skeleton.debug.BoneVisualization;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen.Samyn
 */
public class BoneComponent extends VisualComponent {
    private Vector3f axis;
    private float radius;
    private float height;
    private int sides;

    @Override
    protected void createShape(Prefab parent) {
        BoneVisualization bv = new BoneVisualization(getAxis(), getRadius(), getHeight(), getSides());
        Geometry visual = new Geometry("bone");
        visual.setMesh(bv);
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        Material mat  = am.loadMaterial("Materials/RigMaterial.j3m");
        visual.setMaterial(mat);
        setVisual(visual);
        setMaterial(mat);
    }
    
     /**
     * @return the axis
     */
    public Vector3f getAxis() {
        return axis;
    }

    /**
     * @param axis the axis to set
     */
    public void setAxis(Vector3f axis) {
        this.axis = axis;
        recreate();
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
     * @return the sides
     */
    public int getSides() {
        return sides;
    }

    /**
     * @param sides the sides to set
     */
    public void setSides(int sides) {
        this.sides = sides;
        recreate();
    }
}
