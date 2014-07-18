/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class PlayerStartEntity extends Prefab {

    public PlayerStartEntity() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Entities/M_Playerstart.j3o"));
        this.setCategory("Standard");
        this.setType("PlayerStart");
    }
}
