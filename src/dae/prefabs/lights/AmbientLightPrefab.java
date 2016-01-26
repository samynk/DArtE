/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.lights;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class AmbientLightPrefab extends Prefab {

    private AmbientLight light;
    private float ambientLightIntensity = 1.0f;
    private ColorRGBA ambientLightColor;

    public AmbientLightPrefab() {
        light = new AmbientLight();
    }

    @Override
    public void create( AssetManager manager, String extraInfo) {
        ambientLightColor = light.getColor().clone();

        Sphere s = new Sphere(12, 12, 0.05f);
        Geometry g = new Geometry("pointlightgizmo", s);
        g.setMaterial(manager.loadMaterial("/Materials/LightGizmoMaterial.j3m"));
        attachChild(g);
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
        if (parentNode != null) {
            parentNode.removeLight(this.light);
        }
        return super.removeFromParent();
    }
    

    public void setAmbientLightColor(ColorRGBA color) {
        this.ambientLightColor = color.clone();
        light.setColor(color.mult(ambientLightIntensity));
    }

    public ColorRGBA getAmbientLightColor() {
        return ambientLightColor;
    }

    public float getAmbientLightIntensity() {
        return ambientLightIntensity;
    }

    public void setAmbientLightIntensity(float intensity) {
        this.ambientLightIntensity = intensity;
        light.setColor(ambientLightColor.mult(ambientLightIntensity));
    }
}
