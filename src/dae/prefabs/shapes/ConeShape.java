/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.shapes;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Creates a cone shape mesh.
 *
 * @author Koen Samyn
 */
public class ConeShape extends Mesh {

    private int lengthSegments;
    private int radialSegments;

    /**
     * Creates a new cone shape with the specified axis, and cone angle.
     *
     * @param axis the axis of the cone.
     * @param coneAngle the angle of the cone.
     * @param length the length of the cone.
     * @param lengthSegments the number of length segments.
     * @param radialSegment the number of radial segments.
     * @param generateTexCoords generate texture coordinates or not.
     * @param texScale the texture scaling for the mesh.
     */
    public ConeShape(Vector3f axis, float coneAngle, float length, int lengthSegments, int radialSegments, boolean generateTexCoords, Vector2f texScale) {
        if (generateTexCoords) {
            generateTexturedCone(axis, coneAngle, length, lengthSegments, radialSegments);
        } else {
            generateNonTexturedCone(axis, coneAngle, length, lengthSegments, radialSegments);
        }
    }

    private void generateTexturedCone(Vector3f axis, float angle, float length, int lengthSegments, int radialSegments) {
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

        // 

        Vector3f[] vertices = new Vector3f[lengthSegments  * (radialSegments + 1)];
        Vector2f[] texCoords = new Vector2f[vertices.length];
        
        int vindex = 0;
        for (int li = 0; li < lengthSegments; ++li) {
            float l = (length * li) / (lengthSegments - 1);
            float radius = l * FastMath.tan(angle);
            Vector3f lx = x.mult(radius);
            Vector3f ly = y.mult(radius);
            Vector3f center = axis.mult(l);
            for (int ri = 0; ri <= radialSegments; ++ri) {
                float angle2 = (ri * FastMath.TWO_PI) / radialSegments;
                vertices[vindex] = center.add(lx.mult(FastMath.cos(angle2)).add(ly.mult(FastMath.sin(angle2))));
                texCoords[vindex] = new Vector2f((li * 1.0f) / (lengthSegments - 1), (ri * 1.0f) / (radialSegments));
                ++vindex;
            }
        }


        int[] indices = new int[lengthSegments * radialSegments * 6];
        int tIndex = 0;
        for (int li = 1; li < (lengthSegments - 1); ++li) {
            for (int ri = 0; ri < radialSegments; ++ri) {
                int ri1 = (ri + 1) ;
                int i1 = (li - 1) * (radialSegments+1) + ri;
                int i2 = (li - 1) * (radialSegments+1) + ri1;
                int i3 = (li * (radialSegments+1)) + ri1;
                int i4 = (li * (radialSegments+1)) + ri;

                indices[tIndex] = i1;
                indices[tIndex + 1] = i3;
                indices[tIndex + 2] = i2;
                indices[tIndex + 3] = i1;
                indices[tIndex + 4] = i4;
                indices[tIndex + 5] = i3;
                tIndex += 6;
            }
        }

        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();

    }

    private void generateNonTexturedCone(Vector3f axis, float angle, float length, int lengthSegments, int radialSegments) {
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

        // 

        Vector3f[] vertices = new Vector3f[(lengthSegments - 1) * radialSegments + 1];

        vertices[vertices.length - 1] = new Vector3f(0, 0, 0);
        int vindex = 0;
        for (int li = 1; li < lengthSegments; ++li) {
            float l = (length * li) / (lengthSegments - 1);
            float radius = l * FastMath.tan(angle);
            Vector3f lx = x.mult(radius);
            Vector3f ly = y.mult(radius);
            Vector3f center = axis.mult(l);
            for (int ri = 0; ri < radialSegments; ++ri) {
                float angle2 = (ri * FastMath.TWO_PI) / radialSegments;
                vertices[vindex] = center.add(lx.mult(FastMath.cos(angle2)).add(ly.mult(FastMath.sin(angle2))));
                ++vindex;
            }
        }


        int[] indices = new int[(lengthSegments - 1) * radialSegments * 6 + radialSegments * 3];

        int tIndex = 0;
        for (int i = 0; i < radialSegments; ++i) {
            // first triangle all connect to first vertex
            indices[tIndex++] = i;
            indices[tIndex++] = (i + 1) % radialSegments;
            // top of the cone.
            indices[tIndex++] = vertices.length - 1;
        }

        for (int li = 1; li < (lengthSegments - 1); ++li) {
            for (int ri = 0; ri < radialSegments; ++ri) {
                int ri1 = (ri + 1) % radialSegments;
                int i1 = (li - 1) * radialSegments + ri;
                int i2 = (li - 1) * radialSegments + ri1;
                int i3 = (li * radialSegments) + ri1;
                int i4 = (li * radialSegments) + ri;

                indices[tIndex] = i1;
                indices[tIndex + 1] = i3;
                indices[tIndex + 2] = i2;
                indices[tIndex + 3] = i1;
                indices[tIndex + 4] = i4;
                indices[tIndex + 5] = i3;
                tIndex += 6;
            }
        }
        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }
}
