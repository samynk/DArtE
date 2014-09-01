/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Describes a Hinge shape. The shape shows the direction of positive angle
 * change and the limits of the joint.
 *
 * @author Koen Samyn
 */
public class HingeShape extends Node {

    private float lowerLimit;
    private float upperLimit;
    private Vector3f axis;
    private float height = 0.3f;
    private float radius = 0.025f;
    private Geometry hingeGeometry;
    private Geometry limitGeometry;
    private AssetManager manager;
    private Material limitMaterial;

    public HingeShape(Vector3f axis, float lowerLimit, float upperLimit) {
        this.axis = axis;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        setName("hinge");
    }

    /**
     * Creates the mesh that represents the hinge.
     *
     * @param manager the AssetManager for the creation of materials.
     */
    public void create(AssetManager manager) {
        ColorRGBA limitColor = new ColorRGBA(128 / 255f, 128 / 255f, 192 / 255f, 1f);
        limitMaterial = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        limitMaterial.setColor("Color", limitColor);
        // hinge geometyr
        createHingeGeometry(manager, 12, new ColorRGBA(153 / 255f, 217 / 255f, 234 / 255f, 1f));
        createLimits( 12, 12, height, radius);
    }

    /**
     * Updates the limits of the HingeShape
     * @param lowerLimit the lower limit (in degrees)
     * @param upperLimit the upper limit (in degrees)
     */
    public void updateLimits(float lowerLimit, float upperLimit) {
        this.lowerLimit = lowerLimit * FastMath.DEG_TO_RAD;
        this.upperLimit = upperLimit * FastMath.DEG_TO_RAD;
        if (this.limitGeometry != null) {
            limitGeometry.removeFromParent();
        }
        createLimits(12, 12, height, radius);
    }

    /**
     * Creates a geometry that shows the hinge.
     */
    private void createHingeGeometry(AssetManager manager, int sides, ColorRGBA color) {
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


        hingeGeometry = new Geometry("Axis", axisMesh);
        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        hingeGeometry.setMaterial(mat);
        attachChild(hingeGeometry);
    }

    private void createLimits(int outerSegments, int innerSegments, float axisRadius, float gizmoRadius) {
        Vector3f z = axis.normalize();
        Vector3f x, y;
        if (z.x > z.y && z.x > z.z) {
            // x component is largest so cross with unit.y
            x = z.cross(Vector3f.UNIT_Y);
            y = x.cross(z);
        } else if ( z.y > z.x && z.y > z.z ){
            // y component is largest so cross with unit.z
            x = z.cross(Vector3f.UNIT_Z);
            y = x.cross(z);
        }else{
            x = z.cross(Vector3f.UNIT_X);
            y = x.cross(z);
        }
        x.normalizeLocal();
        y.normalizeLocal();


        Vector3f donuty = axis.clone();

        x.multLocal(axisRadius);
        y.multLocal(axisRadius);

        donuty.multLocal(gizmoRadius);

        Vector3f[] vertices = new Vector3f[outerSegments * innerSegments+2];
        int[] indices = new int[(outerSegments +1 )* innerSegments * 6 ];

        int index = 0;
        float angleDiff = upperLimit - lowerLimit;
        
        for (int outer = 0; outer < outerSegments; ++outer) {
            float angle = this.lowerLimit + (angleDiff * outer) / (outerSegments);
            Vector3f center = x.mult(FastMath.cos(angle)).add(y.mult(FastMath.sin(angle)));
            
            for (int inner = 0; inner < innerSegments; ++inner) {
                Vector3f donutx = center.normalize().multLocal(gizmoRadius);
                float angle2 = (FastMath.TWO_PI * inner) / (innerSegments);
                Vector3f offset = donutx.mult(FastMath.cos(angle2)).add(donuty.mult(FastMath.sin(angle2)));
                vertices[index++] = center.add(offset);
            }
        }
        
        vertices[index++] = x.mult(FastMath.cos(lowerLimit)).add(y.mult(FastMath.sin(lowerLimit)));
        vertices[index++] = x.mult(FastMath.cos(upperLimit)).add(y.mult(FastMath.sin(upperLimit)));

        // create the indices
        index = 0;
        for (int outer = 0; outer < (outerSegments-1); ++outer) {
            for (int inner = 0; inner < innerSegments; ++inner) {
                int inner1 = (inner + 1) % innerSegments;
                int outer1 = (outer + 1);
                int i1 = (outer * innerSegments) + inner;
                int i2 = (outer * innerSegments) + inner1;
                int i3 = outer1 * innerSegments + inner1;
                int i4 = outer1 * innerSegments + inner;

                indices[index] = i1;
                indices[index + 1] = i2;
                indices[index + 2] = i3;
                indices[index + 3] = i1;
                indices[index + 4] = i3;
                indices[index + 5] = i4;
                index += 6;
            }
        }
        int startVertex = vertices.length-2;
        
        for ( int inner = 0; inner < innerSegments;++inner)
        {
            indices[index] = inner;
            indices[index + 2] = (inner+1)%innerSegments;
            indices[index+ 1] = startVertex;
            index += 3;
        }
        
        int endVertex = vertices.length-1;
        int startIndex = (outerSegments-1)*innerSegments;
        for ( int inner = 0; inner < innerSegments;++inner)
        {
            indices[index] = startIndex+inner;
            indices[index + 1] = startIndex + (inner+1)%innerSegments;
            indices[index+ 2] = endVertex;
            index += 3;
        }
        
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        result.updateBound();

        limitGeometry = new Geometry(name, result);
        limitGeometry.setMaterial(limitMaterial);

        this.attachChild(limitGeometry);
    }
}
