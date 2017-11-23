/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen.Samyn
 */
public class BoxComponent extends PrefabComponent {

    private float length;
    private float width;
    private float height;

    private Vector3f center;

    private boolean visible;
    private ColorRGBA diffuseColor;
    private Material boxMat;

    private Geometry boxGeo;
    private Prefab parent;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        recreate();
    }

    private void createShape(Prefab parent) {
        Vector3f min = center.subtract(length / 2, width / 2, height / 2);
        Vector3f max = center.add(length / 2, width / 2, height / 2);
        Box box = new Box(min, max);
        boxGeo = new Geometry("box", box);

        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        boxMat = am.loadMaterial("Materials/RigMaterial.j3m");
        boxGeo.setMaterial(boxMat);
        resetColor();

        parent.attachChild(boxGeo);
    }

    private void resetColor() {
        MatParam color = boxMat.getParam("Color");
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
        if (boxGeo != null) {
            boxGeo.removeFromParent();
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {

    }

    /**
     * @return the lenght
     */
    public float getLength() {
        return length;
    }

    /**
     * @param lenght the lenght to set
     */
    public void setLength(float lenght) {
        this.length = lenght;
        recreate();
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
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
     * @return the center
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Vector3f center) {
        this.center = center;
        recreate();

    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (boxGeo != null) {
            if (!visible) {
                boxGeo.removeFromParent();
            } else {
                if (boxGeo.getParent() == null) {
                    parent.attachChild(boxGeo);
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
