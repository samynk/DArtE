/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.math.Vector3f;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public interface BodyElement {
    /**
     * Attaches a body element to the body.
     * @param element 
     */
    public void attachBodyElement(BodyElement element);

    /**
     * Resets the transformation of the body element.
     */
    public void reset();

    /**
     * Sets the local translation of the body element.
     * @param translation the new local translation.
     */
    public void setLocalTranslation(Vector3f translation);
    
    /**
     * Hides the target objects.
     */
    public void hideTargetObjects();
    
    /**
     * Shows the target objects.
     */
    public void showTargetObjects();
}
