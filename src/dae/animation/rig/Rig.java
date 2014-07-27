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
 * A rig provides support for procedural animation for mechanical models.
 * A rig typically is not animated with skinning but has distinct meshes
 * that move indepently without deformations.
 * @author Koen Samyn
 */
public class Rig extends Prefab implements BodyElement{
    private FuzzySystem fuzzySystem = new FuzzySystem("default");
    private HashMap<String,Spatial> targetMap = new HashMap<String,Spatial>();
    
    private ArrayList<String> targetKeys = new ArrayList<String>();
    
    /**
     * Create a new Rig.
     */
    public Rig(){
        super.setName("rig");
        setLayerName("default");
        setCategory("Animation");
        setType("Rig");
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        Material rigMaterial = manager.loadMaterial("Materials/RigMaterial.j3m");
        DiamondShape ds = new DiamondShape(0.1f,true);
        Geometry g = new Geometry("rigshape",ds);
        g.setMaterial(rigMaterial);
        attachChild(g);
    }
    
    
    
    /**
     * Attaches a body element to this rig.
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
     * Get the indexed target.
     */
    public String getTargetKey(int index){
        return targetKeys.get(index);
    }
    
    /**
     * Sets a a target key to a specific value.
     * @param index the index of the element.
     * @param key the key to put into the element.
     */
    public void setTargetKey(int index, String key){
        this.targetKeys.set(index,key);
    }
    
    /**
     * Returns the number of target keys.
     * @return the number of target keys.
     */
    public int getNrOfTargetKeys(){
        return targetKeys.size();
    }
    
    /**
     * Add a target key to the list.
     * @param key the key to add.
     */
    public void addTargetKey(String key){
        targetKeys.add(key);
    }
    
    /**
     * Returns the fuzzy system that is used by this Rig
     */
    public FuzzySystem getFuzzySystem(){
        return fuzzySystem;
    }
}