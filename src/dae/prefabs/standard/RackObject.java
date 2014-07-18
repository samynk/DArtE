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
public class RackObject extends Prefab {

    public RackObject() {
    }

    @Override
    public String getPrefix() {
        return "Rack";
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        setName(name);
    }
}
