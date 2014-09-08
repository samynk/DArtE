/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.shapes;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * An implementation of a quad that can be resized.
 *
 * @author Koen Samyn
 */
public class QuadShape extends Mesh {

    private float width, length;

    public QuadShape(int width, int length) {
        this.width = width;
        this.length = length;
        setDynamic();
        create();
    }

    public void setDimension(float width, float length) {
        this.width = width;
        this.length = length;
        create();
    }

    private void create() {
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(width, 0, 0);
        vertices[2] = new Vector3f(0, length, 0);
        vertices[3] = new Vector3f(width, length, 0);

        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);

        int[] indexes = {2, 0, 1, 1, 3, 2};

        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        updateBound();
        
    }
}
