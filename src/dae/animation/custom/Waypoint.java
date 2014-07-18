/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import dae.prefabs.Prefab;

/**
 * A handle object can define a number of targets for an animation. The most
 * important property is the location property but axises that are targets for
 * alignment can also be defined.
 *
 * @author Koen Samyn
 */
public class Waypoint extends Prefab {
    public Waypoint() {
        setLayerName("waypoints");
    }
   
    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.attachChild(manager.loadModel("Skeleton/Helpers/waypoint.j3o"));
    }

   

    @Override
    public void setXTranslation(float x) {
        super.setXTranslation(x); //To change body of generated methods, choose Tools | Templates.
        if ( getParent() instanceof CharacterPath )
        {
            ((CharacterPath)getParent()).createPathMesh();
        }
    }

    @Override
    public void setYTranslation(float y) {
        super.setYTranslation(y); //To change body of generated methods, choose Tools | Templates.
        if ( getParent() instanceof CharacterPath )
        {
            ((CharacterPath)getParent()).createPathMesh();
        }
    }

    @Override
    public void setZTranslation(float z) {
        super.setZTranslation(z); //To change body of generated methods, choose Tools | Templates.
        if ( getParent() instanceof CharacterPath )
        {
            ((CharacterPath)getParent()).createPathMesh();
        }
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation); //To change body of generated methods, choose Tools | Templates.
        if ( getParent() instanceof CharacterPath )
        {
            ((CharacterPath)getParent()).createPathMesh();
        }
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z); //To change body of generated methods, choose Tools | Templates.
        if ( getParent() instanceof CharacterPath )
        {
            ((CharacterPath)getParent()).createPathMesh();
        }
    }
    
    
    
    
}