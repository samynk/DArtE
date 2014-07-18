/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import dae.GlobalObjects;
import dae.prefabs.magnets.Magnet;

/**
 *
 * @author Koen
 */
public class MagnetObject extends Node {

    private Magnet magnet;

    public MagnetObject(Magnet m) {
        magnet = m;

        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        float r = m.getRadius();
        Sphere s = new Sphere(6, 6, r);
        Geometry geom = new Geometry("magnet", s);  // create cube geometry from the shape
        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        geom.setMaterial(mat);
        geom.setLocalRotation(m.getLocalFrame());
        geom.setLocalTranslation(m.getLocation());
        attachChild(geom);
    }

    public Magnet getMagnet() {
        return magnet;
    }
}
