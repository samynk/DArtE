package dae.animation.rig;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.skeleton.BodyElement;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.DiamondShape;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private void createTestRig(AssetManager manager) {
        ObjectTypeCategory otc = GlobalObjects.getInstance().getObjectsTypeCategory();
        ObjectType ot = otc.getObjectType("Animation", "RevoluteJointTwoAxis");
        Prefab joint = ot.create(manager, "joint");
        this.attachChild(joint);

        ObjectType ot2 = otc.getObjectType("Animation", "AttachmentPoint");
        Prefab ap = ot2.create(manager, "knob");
        joint.attachChild(ap);
        ap.setLocalPrefabTranslation(new Vector3f(1, 0, 0));

        Prefab ap2 = ot2.create(manager, "target");
        attachChild(ap2);
        ap.setLocalPrefabTranslation(new Vector3f(3, 4, 0));

        this.setTarget("victim", ap2);
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

        
        for (int i = 0 ; i < this.getPrefabChildCount(); ++i) {
            Prefab toClone = (Prefab)this.getPrefabChildAt(i);
            Spatial modelClone = toClone.clone();
            rig.attachChild(modelClone);
        }
        try {
            rig.fuzzySystem = (FuzzySystem) fuzzySystem.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Rig.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        // createTestRig(manager);
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
