/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.skeleton.debug;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Creates a visualization for a joint.
 * @author Koen Samyn
 */
public class JointVisualization extends Mesh{
    private Vector3f axis;
    private float radius;
    private float height;
    private int sides;
    
    public JointVisualization(Vector3f axis, float radius, float height, int sides){
        this.axis = axis;
        this.radius = radius;
        this.height = height;
        this.sides = sides;
        createJoint();
    }
    
    private void createJoint() {
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
        Vector3f bo = z.mult(-height / 2);
        Vector3f to = z.mult(height / 2);
        Vector3f[] vertices = new Vector3f[sides * 2];
        int[] indices = new int[sides * 6];

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

        
        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }
}
