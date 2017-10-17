package dae.animation.rig;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.skeleton.BodyElement;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.DiamondShape;
import dae.prefabs.types.ObjectType;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import mlproject.fuzzy.FuzzySystem;

/**
 * A rig provides support for procedural animation for mechanical models. A rig
 * typically is not animated with skinning but has distinct meshes that move
 * independently without deformations.
 *
 * @author Koen Samyn
 */
public class Rig extends Prefab implements BodyElement {

    private FuzzySystem fuzzySystem = new FuzzySystem("default");
    private final ArrayList<String> targetKeys = new ArrayList<>();
    private final HashMap<String, Prefab> targets = new HashMap<>();

    /**
     * Create a new Rig.
     */
    public Rig() {
//        setLayerName("default");
        setCategory("Animation");
        setType("Rig");
    }

    /**
     * Creates a clone of this rig object.
     *
     * @return a clone of this rig object.
     */
    @Override
    public Spatial clone() {
        Rig rig = new Rig();
        rig.objectType = objectType;

        if (objectType != null) {
            this.duplicateComponents(rig, GlobalObjects.getInstance().getObjectsTypeCategory());
        }

        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                Spatial clone = s.clone();
                rig.attachBodyElement((BodyElement) clone);
            } else {
                Spatial modelClone = s.clone();
                rig.attachChild(modelClone);
            }
        }
        rig.fuzzySystem = (FuzzySystem) fuzzySystem.clone();
        for (String targetKey : this.targetKeys) {
            rig.addTargetKey(targetKey);
        }
        AnimationListControl listControl = this.getControl(AnimationListControl.class);
        if (listControl != null) {
            listControl.cloneForSpatial(rig);
        }
        return rig;
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        Material rigMaterial = manager.loadMaterial("Materials/RigMaterial.j3m");
        DiamondShape ds = new DiamondShape(0.1f, true);
        Geometry g = new Geometry("rigshape", ds);
        g.setMaterial(rigMaterial);
        attachChild(g);
    }

    /**
     * Attaches a body element to this rig.
     *
     * @param element the element to attach.
     */
    @Override
    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    /**
     * Resets the rig to its original state.
     */
    public void reset() {
    }

    @Override
    public void write(Writer w, int depth) throws IOException {
    }

    /**
     * Returns the fuzzy system that is used by this Rig
     *
     * @return the fuzzy controller that is used by this rig.
     */
    public FuzzySystem getFuzzySystem() {
        return fuzzySystem;
    }

    public void setFuzzySystem(FuzzySystem system) {
        this.fuzzySystem = system;
    }

    // implementation of dictionary prototype.
    // void addPropertyKey( String key ); 
    //  void removePropertyKey( String key );
    // 
    // int getNrOfPropertyKeys();
    // int getIndexOfPropertyKey(String key);
    // String getPropertyKeyAt(int index);
    // 
    // void setProperty( String key, T value);
    // T getProperty( String key );
    /**
     * Get the indexed target.
     */
    public void addTargetKey(String key) {
        targetKeys.add(key);
    }

    public void removeTargetKey(String key) {
        targetKeys.remove(key);
        targets.remove(key);
    }

    public int getNrOfTargetKeys() {
        return targetKeys.size();
    }

    public int getIndexOfTargetKey(String key) {
        return targetKeys.indexOf(key);
    }

    public String getTargetKeyAt(int index) {
        return targetKeys.get(index);
    }

    public void setTarget(String key, Prefab value) {
        targets.put(key, value);
        if (!targetKeys.contains(key)) {
            targetKeys.add(key);
        }

        if (value != null) {
            AnimationListControl alc = getControl(AnimationListControl.class);
            if (alc != null) {
                alc.initialize();
            }
        }
    }

    public Prefab getTarget(String key) {
        return targets.get(key);
    }
}
