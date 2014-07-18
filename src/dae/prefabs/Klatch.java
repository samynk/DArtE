/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * This is a node that represents a collection of other objects. When an object
 * inside the klatch is selected, the entire klatch will be selected.
 *
 * @author Koen Samyn
 */
public class Klatch extends Prefab {

    private String klatchFile;

    public Klatch() {
        setCategory("Standard");
        setType("Klatch");
    }

    public Klatch(String klatchFile) {
        this.klatchFile = klatchFile;
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.name = name;
        this.klatchFile = extraInfo;
    }
    
    public void setExtraInfo(String klatchFile){
        this.klatchFile = klatchFile;
    }

    @Override
    public Node clone(boolean cloneMaterials){
        return (Node)clone();
    }
    
    @Override
    public Spatial clone() {
        Klatch k = new Klatch(this.klatchFile);
        k.setType(this.getType());
        k.setCategory(this.getCategory());

        for (Spatial s : this.children) {
            Spatial clone = s.clone(true);
            Object klatchpart = s.getUserData("klatchpart");
            if ( klatchpart!=null){
                clone.setUserData("klatchpart", klatchpart);
            }
            k.attachChild(clone);
        }
        return k;
    }

    @Override
    public Prefab duplicate(AssetManager assetManager) {
        Prefab duplicate = (Prefab)clone();
        duplicate.setLocalPrefabTranslation(this.getWorldTranslation());
        return (Prefab)clone();
    }
    
    
    

    /**
     * @return the klatchFile
     */
    public String getKlatchFile() {
        return klatchFile;
    }

    /**
     * @param klatchFile the klatchFile to set
     */
    public void setKlatchFile(String klatchFile) {
        this.klatchFile = klatchFile;
    }

    @Override
    public String getPrefix() {
        return "assembly";
    }
    
    
}