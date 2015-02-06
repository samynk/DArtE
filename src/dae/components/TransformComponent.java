package dae.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.prefabs.Prefab;

/**
 * A simple component that allows the user to set the 
 * transform for a prefab. 
 * @author Koen Samyn
 */
public class TransformComponent extends PrefabComponent{
    private Vector3f translation = Vector3f.ZERO;
    private Vector3f scale = Vector3f.UNIT_XYZ;
    private Quaternion rotation = Quaternion.IDENTITY;
    
    private Prefab parent;

    /**
     * @return the translation
     */
    public Vector3f getTranslation() {
        return translation;
    }

    /**
     * @param translation the translation to set
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
        if ( parent != null ){
            parent.setLocalPrefabTranslation(translation);
        }
    }

    /**
     * @return the scale
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
        if ( parent != null ){
            parent.setLocalScale(scale);
        }
    }

    /**
     * @return the rotation
     */
    public Quaternion getRotation() {
        return rotation;
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
        if ( parent != null ){
            parent.setLocalPrefabRotation(rotation);
        }
    }

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        parent.setLocalPrefabRotation(rotation);
        parent.setLocalPrefabTranslation(translation);
        parent.setLocalScale(scale);
        parent.updateGeometricState();
    }
    
}
