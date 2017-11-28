/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * Adds an existing shape to an object. This makes it easier to create prefabs
 * by assembling all required functionality instead of subclassing the prefab.
 * @author Koen.Samyn
 */
public class SphereComponent extends VisualComponent{
    private int radialSegments;
    private int axialSegments;
    private float radius;
    
    @Override
    protected void createShape(Prefab parent) {
        Sphere sphere = new Sphere(axialSegments, radialSegments,radius);
        Geometry visual = new Geometry("sphere", sphere);
        
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        Material mat = am.loadMaterial("Materials/RigMaterial.j3m");

        visual.setMaterial(mat);
        
        setVisual(visual);
        setMaterial(mat);
    }
    
    /**
     * @return the radialSegments
     */
    public int getRadialSegments() {
        return radialSegments;
    }

    /**
     * @param radialSegments the radialSegments to set
     */
    public void setRadialSegments(int radialSegments) {
        this.radialSegments = radialSegments;
        recreate();
    }

    /**
     * @return the axialSegments
     */
    public int getAxialSegments() {
        return axialSegments;
    }

    /**
     * @param axialSegments the axialSegments to set
     */
    public void setAxialSegments(int axialSegments) {
        this.axialSegments = axialSegments;
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
}
