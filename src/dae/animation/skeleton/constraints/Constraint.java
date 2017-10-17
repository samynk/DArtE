
package dae.animation.skeleton.constraints;

import com.jme3.math.Vector3f;

/**
 *
 * @author Koen.Samyn
 */
public interface Constraint {
    /**
     * Checks if the given point is still within the constraint.
     * within the constraint.
     * @param point the point to check.
     * @return true if the point is within the constraint, false otherwise.
     */
    public boolean checkConstraint(Vector3f point);
}
