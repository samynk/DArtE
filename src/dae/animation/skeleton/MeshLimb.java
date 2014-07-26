/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.io.XMLUtils;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen Samyn
 */
public class MeshLimb extends Node implements BodyElement {
    private Spatial mesh;
    public MeshLimb(Spatial mesh) {
        this.attachChild(mesh);
        this.mesh = mesh;
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
    public void hideTargetObjects() {
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).hideTargetObjects();
            }
        }
    }

    public void showTargetObjects() {
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).showTargetObjects();
            }
        }
    }

    public void write(Writer w, int depth) throws IOException{
        for (int i = 0; i < depth; ++i) {
            w.write('\t');
        }
        w.write("<limb ");
        XMLUtils.writeAttribute(w, "name", this.getName());
        XMLUtils.writeAttribute(w, "type", "MESH");
        XMLUtils.writeAttribute(w, "mesh", mesh.getKey().getName());

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
            for (Spatial child : this.getChildren()) {
                if (child instanceof BodyElement) {
                    ((BodyElement) child).write(w, depth + 1);
                }
            }
            for (int i = 0; i < depth; ++i) {
                w.write('\t');
            }
            w.write("</limb>\n");
        }
    }
}
