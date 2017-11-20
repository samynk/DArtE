/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen.Samyn
 */
public class BoxComponent extends PrefabComponent {

   
    private float length;
    private float width;
    private float height;
    
    private Vector3f center;
    
    private Geometry boxGeo;
    private Prefab parent;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        createShape(parent);
        
    }

    private void createShape(Prefab parent) {
        Vector3f min = center.subtract(length/2, width/2, height/2);
        Vector3f max = center.add(length/2, width/2, height/2);
        Box box = new Box(min,max);
        boxGeo = new Geometry("box", box);
        
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        Material boneMat = am.loadMaterial("Materials/RigMaterial.j3m");

        boxGeo.setMaterial(boneMat);
        
        parent.attachChild(boxGeo);
    }
    
    public void recreate(){
       deinstall();
       createShape(parent);
    }

    @Override
    public void deinstall() {
        if ( boxGeo != null){
            boxGeo.removeFromParent();
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {
        
    }

    /**
     * @return the lenght
     */
    public float getLength() {
        return length;
    }

    /**
     * @param lenght the lenght to set
     */
    public void setLength(float lenght) {
        this.length = lenght;
        recreate();
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
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
     * @return the center
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Vector3f center) {
        this.center = center;
        recreate();
        
    }
    
}
