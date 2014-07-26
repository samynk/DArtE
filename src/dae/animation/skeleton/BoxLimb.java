/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import dae.io.XMLUtils;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public class BoxLimb extends Node implements BodyElement {
    private Material mat;
    private float length;
    private float width;
    private float height;
    
    public BoxLimb(Material mat, String name, float length, float width, float height) {
        super(name);
        /**
         * create a red box straight above the blue one at (1,3,1)
         */
        Box joint = new Box(new Vector3f(0, 0, height), length, width, height);
        Geometry jg = new Geometry(name + "_joint", joint);
        jg.setShadowMode(ShadowMode.Cast);
        jg.setMaterial(mat);
        jg.rotate(-(float) Math.PI / 2, 0, 0);
        this.attachChild(jg);
        
        this.mat = mat;
        this.length = length;
        this.width = width;
        this.height = height;
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
    private int xindex = -1;
    private int yindex = -1;
    private int zindex = -1;
    private int rxindex = -1;
    private int ryindex = -1;
    private int rzindex = -1;

    public void setXYZIndexInChannel(int xindex, int yindex, int zindex) {
        this.xindex = xindex;
        this.yindex = yindex;
        this.zindex = zindex;
    }

    public void setRXRYRZIndexInChannel(int rxindex, int ryindex, int rzindex) {
        this.rxindex = rxindex;
        this.ryindex = ryindex;
        this.rzindex = rzindex;
    }
    
    @Override
    public Spatial clone(){
        return new BoxLimb(mat,this.name,this.length,this.width,this.height);
    }
    
    @Override
    public void hideTargetObjects(){
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }
    
   
    public void showTargetObjects() {
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }

    public void write(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; ++i) {
            w.write('\t');
        }
        w.write("<limb ");
        XMLUtils.writeAttribute(w, "name", this.getName());
        XMLUtils.writeAttribute(w, "type", "BOX");
        XMLUtils.writeAttribute(w, "length", this.length);
        XMLUtils.writeAttribute(w, "width", this.width);
        XMLUtils.writeAttribute(w, "height", this.height);

        boolean hasBodyElements = false;
        for (Spatial child : this.getChildren()) {
            if (child instanceof BodyElement) {
                hasBodyElements = true;
                break;
            }
        }

        if (!hasBodyElements) {
            w.write("/>\n");
        } else {
            for ( Spatial child : this.getChildren()){
                if ( child instanceof BodyElement ){
                    ((BodyElement)child).write(w, depth+1);
                }
            }
            for (int i = 0; i < depth; ++i) {
                w.write('\t');
            }
            w.write("</limb>\n");
        }
    }
}
