package dae.prefabs.lights;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.ArrowShape;
import dae.prefabs.ui.events.ShadowEvent;

/**
 *
 * @author Koen Samyn
 */
public class DirectionalLightPrefab extends Prefab implements ShadowCastSupport {

    private DirectionalLight light;
    private Material lightGizmoMaterial;
    private Spatial lightGizmo;
    private Spatial lightDirection;
    private float directionalLightIntensity = 1.0f;
    private ColorRGBA directionalLightColor;
    /**
     * The asset manager
     */
    private AssetManager manager;
    /**
     * this light casts a shadow.
     */
    private boolean castShadow;
    /**
     * The BasicShadowRenderer for this light.
     */
    private DirectionalLightShadowRenderer dlsr;

    public DirectionalLightPrefab() {
        light = new DirectionalLight();
        setCategory("Light");
        setType("DirectionalLight");
        setLayerName("lights");
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        setName(name);


        Vector3f dir = this.getWorldRotation().mult(Vector3f.UNIT_X);
        light.setDirection(dir);
        directionalLightColor = light.getColor().clone();
        lightGizmoMaterial = manager.loadMaterial("/Materials/LightGizmoMaterial.j3m");

        ArrowShape lightGizmoShape = new ArrowShape(Vector3f.UNIT_X, 0.75f, 0.5f, 0.1f, 12, true);
        lightGizmo = new Geometry("directionalgizmo", lightGizmoShape);
        lightGizmo.setMaterial(lightGizmoMaterial);
        attachChild(lightGizmo);

        this.manager = manager;
    }

    @Override
    public void setParent(Node parent) {
        super.setParent(parent);
        if (parent != null) {
            parent.addLight(this.light);
        }
    }

    @Override
    public boolean removeFromParent() {
        Node parentNode = this.getParent();
        parentNode.removeLight(this.light);
        return super.removeFromParent();
    }

    
    @Override
    public void rotationChanged() {
        Vector3f dir = this.getWorldRotation().mult(Vector3f.UNIT_X);
        light.setDirection(dir);
    }
    
    /**
     * Utility function that sets the light as a direction.
     * @param direction 
     */
    public void setLightDirection(Vector3f direction) {
        Vector3f dir = direction.normalize();
        
        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.fromStartEndVectors(Vector3f.UNIT_X, dir);
        Quaternion q = new Quaternion();
        q.fromRotationMatrix(rotationMatrix);
        this.setLocalPrefabRotation(q);
        
        light.setDirection(dir);
    }

    public void setDirectionalLightColor(ColorRGBA color) {
        this.directionalLightColor = color;
        light.setColor(color.mult(directionalLightIntensity));
    }

    public ColorRGBA getDirectionalLightColor() {
        return directionalLightColor;
    }

    public float getDirectionalLightIntensity() {
        return directionalLightIntensity;
    }

    public void setDirectionalLightIntensity(float intensity) {
        this.directionalLightIntensity = intensity;
        light.setColor(directionalLightColor.mult(directionalLightIntensity));
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            if (this.lightDirection != null) {
                attachChild(lightDirection);
            }

        } else {
            if (this.lightDirection != null) {
                lightDirection.removeFromParent();
            }
        }
    }

    /**
     * Checks if this direction light casts a shadow or not.
     *
     * @return true if this light casts a shadow, false otherwise.
     */
    public boolean getCastShadow() {
        return castShadow;
    }

    /**
     * Sets the shadow casting property of this light.
     *
     * @param castShadow true if this light casts a shadow, false otherwise.
     */
    public void setCastShadow(boolean castShadow) {
        boolean changed = castShadow != this.castShadow;
        this.castShadow = castShadow;
        if (castShadow && changed) {
            // create shadow renderer
            if (dlsr == null) {
                dlsr = new DirectionalLightShadowRenderer(manager, 512, 1);
                dlsr.setLight(this.light);

            }
        }

        if (changed) {
            ShadowEvent se = new ShadowEvent(this);
            GlobalObjects.getInstance().postEvent(se);
        }
    }

    public DirectionalLightShadowRenderer getShadowRenderer() {
        return dlsr;
    }

    
}
