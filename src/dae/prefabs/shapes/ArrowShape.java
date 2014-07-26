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
 *
 * @author Koen Samyn
 */
public class ArrowShape extends Mesh {

    public ArrowShape(Vector3f axis, float coneRatio, float axisHeight, float axisRadius, int sides, boolean generateTexCoords) {
        if (generateTexCoords) {
            createTexturedArrow(axis, axisHeight, axisRadius, coneRatio, sides);
        } else {
            createUntexturedArrow(axis, axisHeight, axisRadius, coneRatio, sides);
        }
    }

    /**
     * Creates a geometry that shows the hinge.
     */
    private void createUntexturedArrow(Vector3f axis, float height, float radius, float coneRatio, int sides) {
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
        Vector3f to = z.mult(height * coneRatio);
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

        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }

    private void createTexturedArrow(Vector3f axis, float height, float radius, float coneRatio, int sides) {
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
        Vector3f to = z.mult(height * coneRatio);
        Vector3f[] vertices = new Vector3f[(sides + 1) * 3 + 1];
        Vector2f[] texCoords = new Vector2f[vertices.length];
        int[] indices = new int[(sides + 1) * 9];

        for (int i = 0; i <= sides; ++i) {
            float angle = 2 * i * (FastMath.PI / sides);
            Vector3f xo = x.mult(FastMath.cos(angle));
            Vector3f yo = y.mult(FastMath.sin(angle));
            Vector3f offset = xo.add(yo);
            vertices[i] = bo.add(offset);
            vertices[i + (sides + 1)] = to.add(offset);

            texCoords[i] = new Vector2f(0, (i * 1.0f) / sides);
            texCoords[i + sides] = new Vector2f(coneRatio, (i * 1.0f) / sides);

            // bottom vertices
            if (i != sides) {
                int i1 = i;
                int i2 = (i + 1);
                int i3 = i + (sides + 1);
                int i4 = i2 + (sides + 1);

                int ti = i * 6;
                indices[ti] = i3;
                indices[ti + 1] = i2;
                indices[ti + 2] = i1;

                indices[ti + 3] = i4;
                indices[ti + 4] = i2;
                indices[ti + 5] = i3;
            }
        }
        int vOffset = (sides + 1) * 2;
        int iOffset = (sides + 1) * 6;
        // cone part.
        x.multLocal(1.5f);
        y.multLocal(1.5f);

        for (int i = 0; i <= sides; ++i) {
            float angle = 2 * i * (FastMath.PI / sides);
            Vector3f xo = x.mult(FastMath.cos(angle));
            Vector3f yo = y.mult(FastMath.sin(angle));
            Vector3f offset = xo.add(yo);

            vertices[i + vOffset] = to.add(offset);
            texCoords[i + vOffset] = new Vector2f(coneRatio, (i * 1.0f) / sides);

            if (i != sides) {
                int i1 = vOffset + i;
                int i2 = vOffset + (i + 1);
                int i3 = vertices.length - 1; //top of cone is last index

                int ti = i * 3;
                indices[iOffset + ti] = i2;
                indices[iOffset + ti + 1] = i1;
                indices[iOffset + ti + 2] = i3;
            }
        }

        Vector3f toc = z.mult(height);
        vertices[vertices.length - 1] = toc;
        texCoords[vertices.length-1] = new Vector2f(1,1);

        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }
}
