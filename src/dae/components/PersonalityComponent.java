/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.components;

import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;
import org.dae.game.controls.PersonalityControl;

/**
 * Creates a new Personality component.
 * @author Koen Samyn
 */
public class PersonalityComponent extends PrefabComponent{
    private float bully;
    private float friendly;
    
    /**
     * Creates a new PersonalityComponent.
     */
    public PersonalityComponent(){
        
    }

    /**
     * @return the bully
     */
    public float getBully() {
        return bully;
    }

    /**
     * @param bully the bully to set
     */
    public void setBully(float bully) {
        this.bully = bully;
    }

    /**
     * @return the friendly
     */
    public float getFriendly() {
        return friendly;
    }

    /**
     * @param friendly the friendly to set
     */
    public void setFriendly(float friendly) {
        this.friendly = friendly;
    }

    @Override
    public void install(Prefab parent) {
        
    }
    
    @Override
    public void deinstall(){
        
    }

    @Override
    public void installGameComponent(Spatial parent) {
        parent.addControl(new PersonalityControl(bully,friendly));
    }
    
    
}
