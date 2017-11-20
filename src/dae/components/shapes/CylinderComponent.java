/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * Adds an existing shape to an object. This makes it easier to create prefabs
 * by assembling all required functionality instead of subclassing the prefab.
 *
 * @author Koen.Samyn
 */
public class CylinderComponent extends PrefabComponent {

    private int radialSegments;
    private int axialSegments;
    private float radius;
    private float height;
    private Quaternion rotation;

    private boolean visible;
    private ColorRGBA diffuseColor;

    private Geometry cylinderGeo;
    private Material cylinderMat;
    private Prefab parent;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        recreate();
    }

    private void createShape(Prefab parent) {
        Cylinder cyl = new Cylinder(axialSegments, radialSegments, radius, getHeight(), true);
        cylinderGeo = new Geometry("cylinder", cyl);
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        cylinderMat = am.loadMaterial("Materials/RigMaterial.j3m");
        cylinderGeo.setMaterial(cylinderMat);
        resetColor();
        cylinderGeo.setLocalRotation(getRotation());
        parent.attachChild(cylinderGeo);
    }

    private void resetColor() {
        MatParam color = cylinderMat.getParam("Color");
        if (color != null) {
            color.setValue(diffuseColor);
        }
    }

    public void recreate() {
        deinstall();
        createShape(parent);
    }

    @Override
    public void deinstall() {
        if (cylinderGeo != null) {
            cylinderGeo.removeFromParent();
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {

    }

    /**
     * @return the radialSegments
     */
    public int getRadialSegments() {
        return radialSegments;
    }

    /**
     * @param radialSegments the radialSegments to set
     */
    public void setRadialSegments(int radialSegments) {
        this.radialSegments = radialSegments;
        recreate();
    }

    /**
     * @return the axialSegments
     */
    public int getAxialSegments() {
        return axialSegments;
    }

    /**
     * @param axialSegments the axialSegments to set
     */
    public void setAxialSegments(int axialSegments) {
        this.axialSegments = axialSegments;
        recreate();
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
        recreate();
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
        recreate();
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
        recreate();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (cylinderGeo != null) {
            if (!visible) {
                cylinderGeo.removeFromParent();
            } else {
                if (cylinderGeo.getParent() == null) {
                    parent.attachChild(cylinderGeo);
                }
            }
        }
    }

    public boolean getVisible() {
        return visible;
    }

    public ColorRGBA getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(ColorRGBA diffuseColor) {
        this.diffuseColor = diffuseColor;
        resetColor();

    }
}
