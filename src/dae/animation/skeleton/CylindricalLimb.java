package dae.animation.skeleton;

import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.debug.RotatableCylinder;
import dae.io.XMLUtils;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public class CylindricalLimb extends Node implements BodyElement {
    private Material mat;
    private float radius;
    private float height;
    public CylindricalLimb(Material mat, String name, float radius, float height) {
        super(name);
        /**
         * create a red box straight above the blue one at (1,3,1)
         */
        RotatableCylinder joint = new RotatableCylinder(12, 12, radius, height, true);
        Geometry jg = new Geometry(name + "_joint", joint);
        jg.setShadowMode(ShadowMode.Cast);
        jg.setMaterial(mat);
        jg.rotate(-(float) Math.PI / 2, 0, 0);
        this.attachChild(jg);
        this.mat = mat;
    }

    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    public void reset() {
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).reset();
            }
        }
    }
    
    @Override
    public Spatial clone(){
        return new CylindricalLimb(mat,this.name,this.radius,this.height);
    }
    
     @Override
    public void hideTargetObjects(){
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }
    
   
    @Override
    public void showTargetObjects() {
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }
}
