package dae.prefabs.lights;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowRenderer;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.ConeShape;
import dae.prefabs.ui.events.ShadowEvent;

/**
 * This class allows the user to manipulate a spotlight in the scene.
 *
 * @author Koen Samyn
 */
public class SpotLightPrefab extends Prefab implements ShadowCastSupport {

    private SpotLight spotLight;
    private Material lightMaterial;
    private Material lightMaterialOuter;
    private Material lightGizmoMaterial;
    private Spatial lightGizmo;
    private Spatial innerLightCone;
    private Spatial outerLightCone;
    private float spotLightIntensity = 1.0f;
    private ColorRGBA spotLightColor = ColorRGBA.White;
    private boolean castShadow;
    /**
     * The shadow renderen
     */
    private SpotLightShadowRenderer shadowRenderer;
    /**
     * The assetmanager for this spotlight.
     */
    private AssetManager manager;

    public SpotLightPrefab() {
        spotLight = new SpotLight();

        LightControl lc = new LightControl(spotLight);
        addControl(lc);
    }

    public SpotLightPrefab(
            float spotinnerangle,
            float spotouterangle,
            float spotrange,
            ColorRGBA spotcolor,
            float spotlightintensity) {
        spotLight = new SpotLight();
        LightControl lc = new LightControl(spotLight);
        addControl(lc);

        spotLight.setSpotInnerAngle(spotinnerangle * FastMath.DEG_TO_RAD);
        spotLight.setSpotOuterAngle(spotouterangle * FastMath.DEG_TO_RAD);
        spotLight.setSpotRange(spotrange);
        spotLight.setColor(spotcolor);
        this.spotLightColor = spotcolor.clone();
        spotLightIntensity = spotlightintensity;
        setCategory("Light");
        setType("SpotLight");
        setLayerName("lights");


    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        setPivot(new Vector3f(0, -0.2f, 0));
        //spotLight.setPosition(this.getLocalTranslation());
        spotLight.setSpotRange(5.0f);
        Vector3f dir = this.getWorldRotation().mult(Vector3f.UNIT_Y).mult(-1.0f);
        spotLight.setDirection(dir);

        spotLight.setColor(spotLightColor.mult(spotLightIntensity));

        lightMaterial = manager.loadMaterial("/Materials/LightMaterial.j3m");
        lightMaterialOuter = manager.loadMaterial("/Materials/LightMaterialOuter.j3m");
        lightGizmoMaterial = manager.loadMaterial("/Materials/LightGizmoMaterial.j3m");

        this.manager = manager;
        adaptLightCones();
    }

    @Override
    public Node clone(boolean cloneMaterials) {
        return (Node) clone();
    }

    @Override
    public Spatial clone() {
        Prefab duplicate = duplicate(manager);

        return duplicate;
    }

    @Override
    public Prefab duplicate(AssetManager assetManager) {
        SpotLightPrefab sl = new SpotLightPrefab();

        sl.setLocalPrefabTranslation(this.getLocalPrefabTranslation());
        sl.setLocalPrefabRotation(this.getLocalPrefabRotation());
        sl.setLocalScale(this.getLocalScale());
        sl.initialize(assetManager, getObjectType(), null);
        sl.setName(this.getName());
        sl.setSpotLightIntensity(this.spotLightIntensity);
        sl.setSpotLightColor(this.spotLightColor.clone());
        sl.setSpotRange(this.spotLight.getSpotRange());
        sl.setSpotInnerAngle(this.spotLight.getSpotInnerAngle() * FastMath.RAD_TO_DEG);
        sl.setSpotOuterAngle(this.spotLight.getSpotOuterAngle() * FastMath.RAD_TO_DEG);
        return sl;
    }

    private Spatial createLightCone(Vector3f axis, float angle, float length, int lengthSegments, int radialSegments, Material lightMaterial, boolean generateTexCoords) {
        ConeShape result = new ConeShape(axis, angle, length, lengthSegments, radialSegments, generateTexCoords, Vector2f.UNIT_XY);
        Geometry innerCone = new Geometry("lightCone", result);
        innerCone.setMaterial(lightMaterial);
        return innerCone;
    }

    private void adaptLightCones() {
        if (innerLightCone != null) {
            innerLightCone.removeFromParent();
            outerLightCone.removeFromParent();
            lightGizmo.removeFromParent();
        }

        Vector3f spotLightDir = Vector3f.UNIT_Y.mult(-1.0f);
        lightGizmo = createLightCone(spotLightDir, spotLight.getSpotOuterAngle(), 0.5f, 3, 12, lightGizmoMaterial, true);

        innerLightCone = createLightCone(spotLightDir, spotLight.getSpotInnerAngle(), spotLight.getSpotRange(), 4, 12, lightMaterial, false);
        outerLightCone = createLightCone(spotLightDir, spotLight.getSpotOuterAngle(), spotLight.getSpotRange(), 4, 12, lightMaterialOuter, false);
        if (isSelected()) {
            attachChild(innerLightCone);
            attachChild(outerLightCone);
        }
        attachChild(lightGizmo);
        // light cones should not be pickable, as they interfere with normal picking.
        innerLightCone.setUserData("Pickable",Boolean.FALSE);
        outerLightCone.setUserData("Pickable",Boolean.FALSE);
    }

    public float getSpotInnerAngle() {
        return spotLight.getSpotInnerAngle() * FastMath.RAD_TO_DEG;
    }

    public void setSpotInnerAngle(float angle) {
        spotLight.setSpotInnerAngle(angle * FastMath.DEG_TO_RAD);
        adaptLightCones();
    }

    public float getSpotOuterAngle() {
        return spotLight.getSpotOuterAngle() * FastMath.RAD_TO_DEG;
    }

    public void setSpotOuterAngle(float angle) {
        spotLight.setSpotOuterAngle(angle * FastMath.DEG_TO_RAD);
        adaptLightCones();
    }

    public float getSpotRange() {
        return spotLight.getSpotRange();
    }

    public void setSpotRange(float range) {
        spotLight.setSpotRange(range);
        adaptLightCones();
    }

    @Override
    public void setParent(Node parent) {
        super.setParent(parent);
        if (parent != null) {
            parent.addLight(this.spotLight);
        }
    }

    @Override
    public boolean removeFromParent() {
        Node parentNode = this.getParent();
        if (parentNode != null) {
            parentNode.removeLight(this.spotLight);
        }
        return super.removeFromParent();
    }

    public void setSpotLightColor(ColorRGBA color) {
        this.spotLightColor = color;
        spotLight.setColor(color.mult(spotLightIntensity));
    }

    public ColorRGBA getSpotLightColor() {
        return spotLightColor;
    }

    public float getSpotLightIntensity() {
        return spotLightIntensity;
    }

    public void setSpotLightIntensity(float spotLightIntensity) {
        this.spotLightIntensity = spotLightIntensity;
        spotLight.setColor(spotLightColor.mult(spotLightIntensity));
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            if (this.innerLightCone != null) {
                attachChild(innerLightCone);
            }
            if (this.outerLightCone != null) {
                attachChild(outerLightCone);
            }
        } else {
            if (this.innerLightCone != null) {
                innerLightCone.removeFromParent();
            }
            if (this.outerLightCone != null) {
                outerLightCone.removeFromParent();
            }
        }
    }

    public boolean getCastShadow() {
        return castShadow;
    }

    public void setCastShadow(boolean castShadow) {
        boolean changed = this.castShadow != castShadow;
        this.castShadow = castShadow;
        if (castShadow && changed) {
            if (shadowRenderer == null) {
                shadowRenderer = new SpotLightShadowRenderer(manager, 512);
                shadowRenderer.setShadowZExtend(0.5f);
                shadowRenderer.setLight(spotLight);
                shadowRenderer.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
            }
        }

        if (changed) {
            ShadowEvent se = new ShadowEvent(this);
            GlobalObjects.getInstance().postEvent(se);
        }
    }

    public AbstractShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }
}
