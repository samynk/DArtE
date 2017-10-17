
package dae.animation.skeleton.constraints;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * Defines a sector constraint for the 
 * @author Koen.Samyn
 */
public class SectorConstraint extends PrefabComponent implements Constraint{
    private Vector3f baseAxis;
    private float angle;
    private float dotAngle;
    
    // helper quaternion
    private Quaternion helper = new Quaternion();
    
    public SectorConstraint(){
        this(new Vector3f(1,0,0), 30*FastMath.DEG_TO_RAD);
    }
    
    public SectorConstraint(Vector3f baseAxis, float angle){
        this.baseAxis = baseAxis.normalize();
        this.angle = angle;
        this.dotAngle = FastMath.cos(angle);
    }
    
    /**
     * Check if the constraint is valid.
     * @param knob the constrained vertex.
     * @return true if the knob is inside the constraint, false otherwise.
     */
    @Override
    public boolean checkConstraint(Vector3f knob){
        Vector3f nKnob = knob.normalize();
        float dot = nKnob.dot(getBaseAxis());
        return dot > dotAngle;
    }
    
    /**
     * Calculates a rotation that rotates the point back on the constraint
     * surface.
     * @param vector to point to rotate onto the constraint surface.
     * @return a correction rotation.
     */
    public Vector3f calculateCorrection(Vector3f vector) {
        float cosAngle = vector.dot(this.baseAxis);
        float eAngle = FastMath.acos(cosAngle);
        Vector3f rotAxis = baseAxis.cross(vector);
        helper.fromAngleAxis(-(eAngle-angle), rotAxis);
        helper.multLocal(vector);
        return vector;
    }

    /**
     * @return the baseAxis
     */
    public Vector3f getBaseAxis() {
        return baseAxis;
    }

    /**
     * @param baseAxis the baseAxis to set
     */
    public void setBaseAxis(Vector3f baseAxis) {
        this.baseAxis = baseAxis;
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
    }

    @Override
    public void install(Prefab parent) {
        
    }

    @Override
    public void deinstall() {
        
    }

    @Override
    public void installGameComponent(Spatial parent) {
        
    }

    
}
