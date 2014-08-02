/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.BodyElement;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.DiamondShape;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import mlproject.fuzzy.FuzzySystem;

/**
 * A rig provides support for procedural animation for mechanical models. A rig
 * typically is not animated with skinning but has distinct meshes that move
 * indepently without deformations.
 *
 * @author Koen Samyn
 */
public class Rig extends Prefab implements BodyElement {

    private FuzzySystem fuzzySystem = new FuzzySystem("default");
    private ArrayList<String> targetKeys = new ArrayList<String>();
    private HashMap<String, Prefab> targets = new HashMap<String, Prefab>();

    /**
     * Create a new Rig.
     */
    public Rig() {
        super.setName("rig");
        setLayerName("default");
        setCategory("Animation");
        setType("Rig");
    }
    
    /**
     * Creates a clone of this rig object.
     * @return a clone of this rig object.
     */
    @Override
    public Spatial clone() {
        Rig rig = new Rig();
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
        for ( String targetKey : this.targetKeys)
        {
            rig.addTargetKey(targetKey);
        }
        AnimationListControl listControl = new AnimationListControl();
        listControl.cloneForSpatial(rig);
        return rig;
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
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

    public void write(Writer w, int depth) throws IOException {
    }

    /**
     * Returns the fuzzy system that is used by this Rig
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
    
    public void addTargetKey( String key){
        targetKeys.add(key);
    }
    
    public void removeTargetKey(String key){
        targetKeys.remove(key);
        targets.remove(key);
    }
    
    public int getNrOfTargetKeys(){
        return targetKeys.size();
    }
    
    public int getIndexOfTargetKey(String key){
        return targetKeys.indexOf(key);
    }
    
    public String getTargetKeyAt(int index) {
        return targetKeys.get(index);
    }

    public void setTarget(String key, Prefab value){
        targets.put(key,value);
        if ( !targetKeys.contains(key)){
            targetKeys.add(key);
        }
    }
    
    public Prefab getTarget(String key){
        return targets.get(key);
    }
}