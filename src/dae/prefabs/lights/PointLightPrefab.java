package dae.prefabs.lights;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Sphere;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class PointLightPrefab extends Prefab {

    private PointLight pointLight;
    private Spatial pointLightSphere;
    private Material lightMaterial;
    private ColorRGBA pointLightColor;
    private float pointLightIntensity;

    public PointLightPrefab() {
        pointLight = new PointLight();

        LightControl lightControl = new LightControl(pointLight);
        this.addControl(lightControl);
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        pointLight.setPosition(this.getLocalTranslation());
        pointLightColor = pointLight.getColor().clone();
        lightMaterial = manager.loadMaterial("/Materials/LightMaterial.j3m");

        Sphere s = new Sphere(12, 12, 0.15f);
        Geometry g = new Geometry("pointlightgizmo", s);
        g.setMaterial(manager.loadMaterial("/Materials/LightGizmoMaterial.j3m"));
        attachChild(g);
        adaptLightSphere();
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return pointLight.getRadius();
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        pointLight.setRadius(radius);
        adaptLightSphere();
    }

    /**
     * @return the pointLightColor
     */
    public ColorRGBA getPointLightColor() {
        return pointLightColor;
    }

    /**
     * @param pointLightColor the pointLightColor to set
     */
    public void setPointLightColor(ColorRGBA pointLightColor) {
        this.pointLightColor = pointLightColor;
        this.pointLight.setColor(pointLightColor.mult(pointLightIntensity));
    }

    private void adaptLightSphere() {
        Sphere sphere = new Sphere(12, 12, pointLight.getRadius());
        Geometry innerCone = new Geometry("lightSphere", sphere);
        innerCone.setMaterial(lightMaterial);

        if (this.pointLightSphere != null) {
            pointLightSphere.removeFromParent();
        }

        pointLightSphere = innerCone;
        if (isSelected()) {
            attachChild(pointLightSphere);
        }
    }

    
    @Override
    public void setParent(Node parent) {
        super.setParent(parent);
        if (parent != null) {
            parent.addLight(this.pointLight);
        }
    }

    @Override
    public boolean removeFromParent() {
        Node parentNode = this.getParent();
        parentNode.removeLight(this.pointLight);
        return super.removeFromParent();
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (pointLightSphere == null) {
            return;
        }
        if (selected) {
            attachChild(pointLightSphere);
        } else {
            pointLightSphere.removeFromParent();

        }
    }

    /**
     * @return the pointLightIntensity
     */
    public float getPointLightIntensity() {
        return pointLightIntensity;
    }

    /**
     * @param pointLightIntensity the pointLightIntensity to set
     */
    public void setPointLightIntensity(float pointLightIntensity) {
        this.pointLightIntensity = pointLightIntensity;
        this.pointLight.setColor(pointLightColor.mult(pointLightIntensity));
    }
}
