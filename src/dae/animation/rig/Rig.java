/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.animation.skeleton.BodyElement;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.DiamondShape;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import mlproject.fuzzy.FuzzySystem;

/**
 * A rig provides support for procedural animation for mechanical models.
 * A rig typically is not animated with skinning but has distinct meshes
 * that move indepently without deformations.
 * @author Koen Samyn
 */
public class Rig extends Prefab implements BodyElement{
    private HashMap<String,FuzzySystem> controllerMap = new HashMap<String,FuzzySystem>();
    
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
}