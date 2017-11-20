package dae.animation.skeleton.debug;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Visualize a sphere sector with a given angle around a given main axis.
 *
 * @author Koen.Samyn
 */
public class SectorVisualization extends Mesh {

    private Vector3f mainAxis;
    private float angle;
    private float radius;

    private int radialSamples = 16;

    public SectorVisualization(Vector3f mainAxis, float angle, float radius) {
        this.mainAxis = mainAxis;
        this.angle = angle;
        this.radius = radius;

        updateGeometry();
    }

    public final void updateGeometry() {
        Vector3f[] vertices = new Vector3f[radialSamples + 1];
        Vector2f[] tc = new Vector2f[radialSamples+1];
        int[] indices = new int[radialSamples * 3];

        vertices[radialSamples] = new Vector3f(0, 0, 0);
        tc[radialSamples] = new Vector2f(.5f,.5f);
        

        float absx = FastMath.abs(mainAxis.x);
        float absy = FastMath.abs(mainAxis.y);
        float absz = FastMath.abs(mainAxis.z);
        
        Vector3f helpVector = Vector3f.UNIT_X;
        if ( absx > absy && absx > absz){
            helpVector = Vector3f.UNIT_Y;
        }else if ( absy > absz ){
            helpVector = Vector3f.UNIT_Z;
        }
        Vector3f axis1 = helpVector.cross(mainAxis);
        axis1.normalizeLocal();
        
        Vector3f axis2 = axis1.cross(mainAxis);
        axis2.normalizeLocal();
        
        
        float z = radius * FastMath.cos(angle); // z value
        for (int i = 0; i < radialSamples; ++i) {
            float x = FastMath.cos(2 * i * FastMath.PI / radialSamples);
            float y = FastMath.sin(2 * i * FastMath.PI / radialSamples);
            
            tc[i] = new Vector2f(.5f + x /2f, .5f + y/2f);
            x *= radius * FastMath.sin(angle);
            y *= radius * FastMath.sin(angle);
            
            
            float rx = x * axis1.x + y * axis2.x + z *  mainAxis.x;
            float ry = x * axis1.y + y * axis2.y + z *  mainAxis.y;
            float rz = x * axis1.z + y * axis2.z + z *  mainAxis.z;
            
            vertices[i] = new Vector3f(rx, ry, rz);
        }
        
        for ( int ti = 0 ; ti < radialSamples;ti++)
        {
            indices[ti*3] = radialSamples;
            indices[ti*3+1] = (ti+1)%radialSamples;
            indices[ti*3+2] = (ti+2)%radialSamples;
        }

        // rotate to main axis frame.
        this.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        this.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(tc));
        this.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        this.updateBound();
    }

    /**
     * @return the angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(float angle) {
        this.angle = angle;
        updateGeometry();
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
        updateGeometry();
    }

    /**
     * @return the mainAxis
     */
    public Vector3f getMainAxis() {
        return mainAxis;
    }

    /**
     * @param mainAxis the mainAxis to set
     */
    public void setMainAxis(Vector3f mainAxis) {
        this.mainAxis = mainAxis;
        updateGeometry();
    }
}
