/*
 * Digital Arts and Entertainment 
 */
package dae.components.shapes;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;

/**
 * The visual component class servers as a base class for visual components that
 * can be added as debug shapes to prefabs. For example, bone visualizations,
 * cylinders, boxes, spheres, ...
 *
 * @author Koen.Samyn
 */
public abstract class VisualComponent extends PrefabComponent {

    // to set the diffuse color
    private Material material;
    private ColorRGBA diffuseColor;
    // to control the visibility
    private Geometry visual;
    private boolean visible;
    // the parent prefab
    private Prefab parent;

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
        createShape(parent);
    }

    @Override
    public void deinstall() {
        visual.removeFromParent();
    }

    @Override
    public void installGameComponent(Spatial parent) {

    }
    
    public void recreate() {
        deinstall();
        createShape(parent);
    }

    /**
     * Resets the color to the given diffuse color
     *
     * @param diffuseColor the diffuse color to use.
     */
    private void resetColor() {
        if (material != null) {
            MatParam color = material.getParam("Color");
            if (color != null) {
                color.setValue(diffuseColor);
            }
        }
    }

    /**
     * Sets the material to use for this visual component.
     *
     * @param material the material
     */
    public void setMaterial(Material material) {
        this.material = material;
        resetColor();
    }

    /**
     *
     * @param visual the geometry for this component.
     */
    public void setVisual(Geometry visual) {
        if ( this.visual != null){
            this.visual.removeFromParent();
        }
        this.visual = visual;
        this.parent.attachChild(visual);
        
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visual != null) {
            visual.setCullHint(visible ? CullHint.Inherit : CullHint.Always);
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
    
    protected abstract void createShape(Prefab parent);

}
