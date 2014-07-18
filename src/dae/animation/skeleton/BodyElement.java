/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.math.Vector3f;

/**
 *
 * @author Koen
 */
public interface BodyElement {

    public void attachBodyElement(BodyElement element);

    public void reset();

    public void setLocalTranslation(Vector3f translation);
    
    public void hideTargetObjects();
    
    public void showTargetObjects();
}
