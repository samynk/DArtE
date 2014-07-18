/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Defines a location within a limb that can be used for targeting or goal
 * related purposes.
 *
 * @author Koen
 */
public class AttachmentPoint extends Node implements BodyElement {
    private Vector3f axis1 = Vector3f.UNIT_X;
    private Vector3f axis2 = Vector3f.UNIT_Z;
    
    private Handle handle;
    
    public AttachmentPoint(String name, AssetManager manager, Vector3f location) {
        super(name);
        this.setLocalTranslation(location);
        handle = new Handle();
        handle.create(name+"_handle", manager, null);
        handle.setLocalScale(0.25f);
        this.attachChild(handle);

        
    }
    
    public AttachmentPoint(String name, AssetManager manager, Vector3f location, Vector3f axis1, Vector3f axis2) {
        super(name);
        this.setLocalTranslation(location);
        this.axis1 = axis1;
        this.axis2 = axis2;
        
        handle = new Handle(axis1, axis2);
        handle.create(name+"_handle", manager, null);
        handle.setLocalScale(0.25f);
        this.attachChild(handle);
    }
    
    public Vector3f getLocalAxis1(){
        return axis1;
    }
    
    public Vector3f getWorldAxis1(){
        return this.getWorldRotation().mult(axis1);
    }
    
    public Vector3f getLocalAxis2(){
        return axis2;
    }
    
    public Vector3f getWorldAxis2(){
        return this.getWorldRotation().mult(axis2);
    }

    public void attachBodyElement(BodyElement element) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reset() {
    }

    public void hideTargetObjects() {
        handle.removeFromParent();
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }

    public void showTargetObjects() {
        this.attachChild(handle);
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }
}
