/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import java.io.IOException;
import java.io.Writer;

/**
 * A handle object can define a number of targets for an animation. The most
 * important property is the location property but axises that are targets for
 * alignment can also be defined.
 *
 * @author Koen Samyn
 */
public class Handle extends Prefab implements BodyElement {

    private Vector3f axis1 = Vector3f.UNIT_X;
    private Vector3f axis2 = Vector3f.UNIT_Z;
    private ColorRGBA color1 = new ColorRGBA(232 / 255.0f, 36 / 255.0f, 13 / 255.0f, 1.0f);
    private ColorRGBA color2 = new ColorRGBA(194 / 255.0f, 206 / 255.0f, 49 / 255.0f, 1.0f);
    private AssetManager assetManager;
    // backup of the initial translation and rotation for cloning.
    private Vector3f translation;
    private Vector3f rotation;
    private Geometry axis1Geometry;
    private Geometry axis2Geometry;

    public Handle() {
        setLayerName("animation");
    }

    public Handle(Vector3f axis1, Vector3f axis2) {
        this.axis1 = axis1.clone();
        this.axis2 = axis2.clone();
        setLayerName("animation");
    }

    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.assetManager = manager;
        axis1Geometry = createAxis(manager, axis1, 0.15f, 0.01f, 12, color1);
        attachChild(axis1Geometry);
        axis2Geometry = createAxis(manager, axis2, 0.15f, 0.01f, 12, color2);
        attachChild(axis2Geometry);

    }

    public void setTransformation(Vector3f translation, Vector3f rotation) {
        this.translation = translation.clone();
        this.rotation = rotation.clone();
        this.setLocalTranslation(translation);
        Quaternion q = new Quaternion();
        Vector3f rot = rotation.mult(FastMath.DEG_TO_RAD);
        q.fromAngles(rot.x, rot.y, rot.z);
        this.setLocalRotation(q);
    }

    @Override
    public Spatial clone() {
        Handle h = new Handle(axis1, axis2);
        h.create(this.name, assetManager, getObjectType(), null);
        h.setTransformation(translation, rotation);
        return h;
    }

    /**
     * Create an axis that points in the correct direction.
     *
     * @param axis
     * @param height
     * @param radius
     * @return
     */
    private Geometry createAxis(AssetManager manager, Vector3f axis, float height, float radius, int sides, ColorRGBA color) {
        Mesh axisMesh = new Mesh();
        Vector3f z = axis.normalize();
        Vector3f x, y;
        if (z.x > z.y && z.x > z.z) {
            // x component is largest so cross with unit.y
            x = z.cross(Vector3f.UNIT_Y);
            y = x.cross(z);
        } else {
            // y or z component is largest so cross with unit.x
            x = z.cross(Vector3f.UNIT_X);
            y = x.cross(z);
        }
        x.normalizeLocal();
        y.normalizeLocal();
        x.multLocal(radius);
        y.multLocal(radius);

        // bottom vertex 
        Vector3f bo = Vector3f.ZERO;
        Vector3f to = z.mult(height * .75f);
        Vector3f[] vertices = new Vector3f[sides * 3 + 1];
        int[] indices = new int[sides * 9];

        for (int i = 0; i < sides; ++i) {
            float angle = 2 * i * (FastMath.PI / sides);
            Vector3f xo = x.mult(FastMath.cos(angle));
            Vector3f yo = y.mult(FastMath.sin(angle));
            Vector3f offset = xo.add(yo);
            vertices[i] = bo.add(offset);
            vertices[i + sides] = to.add(offset);

            // bottom vertices
            int i1 = i;
            int i2 = (i + 1) % sides;
            int i3 = i + sides;
            int i4 = i2 + sides;

            int ti = i * 6;
            indices[ti] = i3;
            indices[ti + 1] = i2;
            indices[ti + 2] = i1;

            indices[ti + 3] = i4;
            indices[ti + 4] = i2;
            indices[ti + 5] = i3;
        }
        int vOffset = sides * 2;
        int iOffset = sides * 6;
        // cone part.
        x.multLocal(1.5f);
        y.multLocal(1.5f);

        for (int i = 0; i < sides; ++i) {
            float angle = 2 * i * (FastMath.PI / sides);
            Vector3f xo = x.mult(FastMath.cos(angle));
            Vector3f yo = y.mult(FastMath.sin(angle));
            Vector3f offset = xo.add(yo);

            vertices[i + vOffset] = to.add(offset);

            int i1 = vOffset + i;
            int i2 = vOffset + ((i + 1) % sides);
            int i3 = vertices.length - 1; //top of cone is last index

            int ti = i * 3;
            indices[iOffset + ti] = i2;
            indices[iOffset + ti + 1] = i1;
            indices[iOffset + ti + 2] = i3;
        }

        Vector3f toc = z.mult(height);
        vertices[vertices.length - 1] = toc;

        axisMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        axisMesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        axisMesh.updateBound();


        Geometry geom = new Geometry("Axis", axisMesh);
        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        return geom;
    }

    @Override
    public String getPrefix() {
        return "Crate";
    }

    /**
     * @return the axis1
     */
    public Vector3f getAxis1() {
        return axis1;
    }

    /**
     * @param axis1 the axis1 to set
     */
    public void setAxis1(Vector3f axis1) {
        this.axis1 = axis1;
    }

    /**
     * @return the axis2
     */
    public Vector3f getAxis2() {
        return axis2;
    }

    /**
     * @param axis2 the axis2 to set
     */
    public void setAxis2(Vector3f axis2) {
        this.axis2 = axis2;
    }

    /**
     * Not supported for Handle objects.
     */
    public void attachBodyElement(BodyElement element) {
    }

    /**
     * Changes the transformation to the initial transformation of the Handle
     * object.
     */
    public void reset() {
    }

    @Override
    public void hideTargetObjects() {
        axis1Geometry.removeFromParent();
        axis2Geometry.removeFromParent();
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).hideTargetObjects();
            }
        }
    }

    @Override
    public void showTargetObjects() {
        attachChild(axis1Geometry);
        attachChild(axis2Geometry);
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).showTargetObjects();
            }
        }
    }
    
    public void write(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; ++i) {
            w.write('\t');
        }
        w.write("<target ");
        XMLUtils.writeAttribute(w, "name", this.getName());
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
            w.write("</target>\n");
        }
    }
}
