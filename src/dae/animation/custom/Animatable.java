package dae.animation.custom;

import com.jme3.math.Vector3f;

/**
 *
 * @author Koen Samyn
 */
public interface Animatable {

    public void activateAnimation(String animation);

    public Vector3f getForwardDirection();

    public Vector3f getWorldTranslation();

    public void setLocation(Vector3f location);

    public void rotateY(float angle);

    public void setCharacterPath(CharacterPath path);

    public float getLocationOnCharacterPath();

    public boolean isOnPath();

    public void unlockMovement();

    public void lockMovement();
}
