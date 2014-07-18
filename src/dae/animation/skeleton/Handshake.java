/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.skeleton;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Sphere;
import dae.prefabs.Prefab;

/**
 * This is a utility class that defines a hand shake. A handshake
 * has two configurable handle objects that allow the user to customize 
 * the behaviour.
 * @author Koen Samyn
 */
public class Handshake extends Prefab{
    private AssetManager assetManager;
    // backup of the initial translation and rotation for cloning.
    private Vector3f translation;
    private Vector3f rotation;
    
    private Handle handle1;
    private Handle handle2;

    public Handshake() {
        setLayerName("animation");
        setCategory("Animation");
        setType("Handshake");
    }

    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.assetManager = manager;
        
        handle1 = new Handle();
        handle1.create(name+".handle1", manager, extraInfo);
        handle1.setLocalTranslation(0,0,0.1f);
        Quaternion q1 = new Quaternion();
        q1.fromAngles(-90*FastMath.DEG_TO_RAD,0,0);
        handle1.setLocalRotation(q1);
        attachChild(handle1);
        
        handle2 = new Handle();
        handle2.create(name+".handle2",manager, extraInfo);
        handle2.setLocalTranslation(0,0,-0.1f);
        Quaternion q2= new Quaternion();
        q2.fromAngles(-90*FastMath.DEG_TO_RAD,180*FastMath.DEG_TO_RAD,0);
        handle2.setLocalRotation(q2);
        attachChild(handle2);
        
        Geometry g = new Geometry("wireframe sphere", new Sphere(12,12,0.03f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(false);
        mat.setColor("Color", ColorRGBA.Gray);
        g.setMaterial(mat);
        //g.setLocalTranslation(pos);
        attachChild(g); 
    }
}
