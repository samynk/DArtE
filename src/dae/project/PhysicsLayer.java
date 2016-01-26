/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.project;

import com.jme3.bullet.BulletAppState;

/**
 * This is a special layer that is always locked and that enables
 * the user to show the physics that are currently used by the application.
 * @author Koen Samyn
 */
public class PhysicsLayer extends Layer{

    private BulletAppState bulletAppState;
   
    
    public PhysicsLayer(){
        super("physics");
        
    }
    
    public void setBulletAppState(BulletAppState appState){
        this.bulletAppState = appState;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (bulletAppState != null){
            bulletAppState.setDebugEnabled(visible);
        }    
    }
}
