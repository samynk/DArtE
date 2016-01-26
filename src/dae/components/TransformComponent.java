package dae.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
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
            System.out.println("TransformComponent setTranslation on " +parent.getName() + " translation");
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
            System.out.println("TransformComponent setRotation on " +parent.getName() + " translation");
            parent.setLocalPrefabRotation(rotation);
        }
    }

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        parent.setLocalPrefabRotation(rotation);
        parent.setLocalPrefabTranslation(translation);
        parent.setLocalScale(scale);
        //parent.updateGeometricState();
    }
    
    @Override
    public void deinstall(){
        // unnecessary to change the prefab parent.
    }
    
    /**
     * Sets the properties of this component on a game object.
     * @param parent the parent to install the component in.
     */
    @Override
    public void installGameComponent(Spatial parent){
        parent.setLocalTranslation(this.translation);
        parent.setLocalRotation(this.rotation);
        Vector3f localScale = parent.getLocalScale();
        parent.setLocalScale(this.scale.mult(localScale));
    }
}
