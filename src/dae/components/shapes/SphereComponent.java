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
public class SphereComponent extends PrefabComponent{
    private int radialSegments;
    private int axialSegments;
    private float radius;
    
    private Geometry sphereGeo;

    @Override
    public void install(Prefab parent) {
        Sphere sphere = new Sphere(axialSegments, radialSegments,radius);
        sphereGeo = new Geometry("sphere", sphere);
        
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        Material boneMat = am.loadMaterial("Materials/RigMaterial.j3m");

        sphereGeo.setMaterial(boneMat);
        
        parent.attachChild(sphereGeo);
    }

    @Override
    public void deinstall() {
        if ( sphereGeo != null ){
            sphereGeo.removeFromParent();
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {
        
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
    }
}
