package dae.animation.skeleton;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import dae.io.SceneSaver;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.ArrowShape;
import java.io.IOException;
import java.io.Writer;

/**
 * Defines a location within a limb that can be used for targeting or goal
 * related purposes.
 *
 * @author Koen
 */
public class AttachmentPoint extends Prefab implements BodyElement {

    private Vector3f axis1 = Vector3f.UNIT_X;
    private Vector3f axis2 = Vector3f.UNIT_Z;
    
    private Geometry gAxis1;
    private Geometry gAxis2;

    /**
     * Empty constructor.
     */
    public AttachmentPoint(){
        
    }
  
    
    public AttachmentPoint(String name, AssetManager manager, Vector3f location) {
        setName(name);
        this.setLocalTranslation(location);
        createArrows(manager);
    }

    public AttachmentPoint(String name, AssetManager manager, Vector3f location, Vector3f axis1, Vector3f axis2) {
        setName(name);
        this.setLocalTranslation(location);
        this.axis1 = axis1;
        this.axis2 = axis2;
        createArrows(manager);
        
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        createArrows(manager);
    }
    
    private void createArrows(AssetManager manager){
        Material mat1 = manager.loadMaterial("Materials/XPivotMaterial.j3m");
        ArrowShape as1 = new ArrowShape(axis1,0.75f,0.30f, 0.025f,12,true);
        gAxis1 = new Geometry("axis1",as1);
        gAxis1.setMaterial(mat1);
        attachChild(gAxis1);
        
        Material mat2 = manager.loadMaterial("Materials/ZPivotMaterial.j3m");
        ArrowShape as2 = new ArrowShape(axis2,0.75f,0.30f, 0.025f,12,true);
        gAxis2 = new Geometry("axis2",as2);
        gAxis2.setMaterial(mat2);
        attachChild(gAxis2);
    }
    
    

    public Vector3f getLocalAxis1() {
        return axis1;
    }

    public Vector3f getWorldAxis1() {
        return this.getWorldRotation().mult(axis1);
    }

    public Vector3f getLocalAxis2() {
        return axis2;
    }

    public Vector3f getWorldAxis2() {
        return this.getWorldRotation().mult(axis2);
    }

    public void attachBodyElement(BodyElement element) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reset() {
    }

    @Override
    public void hideTargetObjects() {
        gAxis1.removeFromParent();
        gAxis2.removeFromParent();
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).hideTargetObjects();
            }
        }
    }

    @Override
    public void showTargetObjects() {
        attachChild(gAxis1);
        attachChild(gAxis2);
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).showTargetObjects();
            }
        }
    }
}
