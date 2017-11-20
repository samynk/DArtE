package dae.prefabs.shapes;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Creates a texture diamond shape, centered on the origin.
 * @author Koen Samyn
 */
public class DiamondShape extends Mesh {
    float halfHeight;
    float halfWidth;
    float halfLength;
    
    boolean generateTexCoords;
    
    /**
     * Creates a new diamond shape
     * @param halfSize half the size of the diamond shape.
     * @param generateTexCoords true, if texcoords are necessary, false otherwise.
     */
    public DiamondShape(float halfSize, boolean generateTexCoords){
        this.halfWidth = halfSize;
        this.halfHeight = halfSize;
        this.halfLength = halfSize;
        this.generateTexCoords = generateTexCoords;
        if ( generateTexCoords ){
            generateDiamondShapeWithTexcoords();
        }else{
            generateDiamondShape();
        }
    }
    
    /**
     * Creates a new diamond shape
     * @param halfWidth half the width of the diamond.
     * @param halfLength half the length of the diamond.
     * @param halfHeight half the height of the diamond.
     * @param generateTexCoords true, if texcoords are necessary, false otherwise.
     */
    public DiamondShape(float halfWidth, float halfLength, float halfHeight, boolean generateTexCoords){
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.halfLength = halfLength;
        this.generateTexCoords = generateTexCoords;
        if ( generateTexCoords ){
            generateDiamondShapeWithTexcoords();
        }else{
            generateDiamondShape();
        }
    }

    /**
     * Generates a diamond shape with texture coordinates.
     */
    private void generateDiamondShape() {
        Vector3f[] cs = new Vector3f[6];
        cs[0] = new Vector3f(0,halfHeight,0);
        cs[1] = new Vector3f(halfWidth,0, halfLength);
        cs[2] = new Vector3f(-halfWidth,0, halfLength);
        cs[3] = new Vector3f(-halfWidth,0, -halfLength);
        cs[4] = new Vector3f(halfWidth,0, -halfLength);
        cs[5] = new Vector3f(0,-halfHeight,0);
        
        int[] indices = {0,2,1,0,3,2,0,4,3,0,1,4,5,1,2,5,2,3,5,3,4,5,4,1};
        
        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(cs));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }

    /**
     * Generates a diamond shape without texture coordinates.
     */
    private void generateDiamondShapeWithTexcoords() {
        Vector3f[] cs = new Vector3f[6];
        cs[0] = new Vector3f(0,halfHeight,0);
        cs[1] = new Vector3f(halfWidth,0, halfLength);
        cs[2] = new Vector3f(-halfWidth,0, halfLength);
        cs[3] = new Vector3f(-halfWidth,0, -halfLength);
        cs[4] = new Vector3f(halfWidth,0, -halfLength);
        cs[5] = new Vector3f(0,-halfHeight,0);
        
        int[] indices = {0,2,1,0,3,2,0,4,3,0,1,4,5,1,2,5,2,3,5,3,4,5,4,1};
        Vector2f[] tcs = new Vector2f[6];
        tcs[0] = new Vector2f(0.5f,0.5f);
        tcs[1] = new Vector2f(0,0);
        tcs[2] = new Vector2f(1,0);
        tcs[3] = new Vector2f(1,1);
        tcs[4] = new Vector2f(0,1);
        tcs[5] = new Vector2f(0.5f,0.5f);
        
        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(cs));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(tcs));
        this.updateBound();
    }
    
    
}
