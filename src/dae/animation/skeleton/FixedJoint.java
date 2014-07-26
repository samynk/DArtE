/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.io.XMLUtils;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public class FixedJoint extends Node implements BodyElement, ChannelSupport {

    private Material mat;
    private Vector3f location = new Vector3f();
    private Vector3f rotation = new Vector3f();

    public FixedJoint(Material mat, String name) {
        super(name);
        this.mat = mat;

    }

    public FixedJoint(Material mat, String name, Vector3f location, Vector3f rotation) {
        super(name);
        this.mat = mat;
        this.location = location.clone();
        this.rotation = rotation.clone();
        this.setLocalTranslation(location);
        Quaternion q = new Quaternion();
        Vector3f rot = rotation.mult(FastMath.DEG_TO_RAD);
        q.fromAngles(rot.x, rot.y, rot.z);
        this.setLocalRotation(q);
    }

    public void attachBodyElement(BodyElement node) {
        if (node instanceof Node) {
            this.attachChild((Node) node);
        }
    }

    @Override
    public Spatial clone() {
        FixedJoint copy = new FixedJoint(mat, getName(), location, rotation);
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                Spatial clone = s.clone();
                copy.attachBodyElement((BodyElement) clone);
            } else {
                copy.attachChild(s.clone());
            }
        }
        return copy;
    }

    public void reset() {
        //this.setLocalTranslation(this.startLocation);
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

    public void setXIndexInChannel(int xindex) {
        this.xindex = xindex;
    }

    public void setYIndexInChannel(int yindex) {
        this.yindex = yindex;
    }

    public void setZIndexInChannel(int zindex) {
        this.zindex = zindex;
    }

    public void setRXIndexInChannel(int rxindex) {
        this.rxindex = rxindex;
    }

    public void setRYIndexInChannel(int ryindex) {
        this.ryindex = ryindex;
    }

    public void setRZIndexInChannel(int rzindex) {
        this.rzindex = rzindex;
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
        w.write("<joint ");
        XMLUtils.writeAttribute(w, "name", this.getName());
        XMLUtils.writeAttribute(w, "type" , "FIXED");
        XMLUtils.writeAttribute(w, "location", this.getLocalTranslation());
        Quaternion localRotation = this.getLocalRotation();
        Vector3f rotation = new Vector3f();
        float[] angles = new float[3];
        localRotation.toAngles(angles);
        rotation.set(angles[0],angles[1],angles[2]);
        rotation.multLocal(FastMath.RAD_TO_DEG);
        XMLUtils.writeAttribute(w, "rotation", rotation);

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
            w.write("</joint>\n");
        }
    }
}
