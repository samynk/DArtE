/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.standard;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.rig.PrefabPlaceHolderCallback;
import dae.prefabs.Prefab;

/**
 * This prefab is a place holder that can be used when the name of a prefab
 * is known, but the prefab is not necessarily added to the scene.
 * 
 * This placeholder will hold the place of the prefab until the prefab
 * is added to the scene. When that happens a user defined action will
 * occur.
 * 
 * @author Koen Samyn
 */
public class PrefabPlaceHolder extends Prefab{
    /**
     * The task to execute when the actual prefab is found.
     */
    private PrefabPlaceHolderCallback onPrefabFound;
    /**
     * The name of the prefab that we search.
     */
    private String prefabName;
    /**
     * The top level node of the scene.
     */
    private Node topLevelNode;
    /**
     * The interval to use for checking (in seconds)
     */
    private float checkInterval = 0.5f;
    private float time = 0;
    
    /**
     * Creates a new prefab placeholder. The onPrefabFound runnable
     * will be executed when the actual prefab is found.
     * @param prefabName
     * @param onPrefabFound 
     * @param checkInterval the interval between checks (in milliseconds).
     */
    public PrefabPlaceHolder(String prefabName, PrefabPlaceHolderCallback onPrefabFound, float checkInterval)
    {
        this.prefabName = prefabName;
        this.onPrefabFound = onPrefabFound;
        
    }

    /**
     * Check the scene for the prefab, when found the Runnable object
     * will be activated.
     * @param tpf 
     */
    @Override
    public void updateLogicalState(float tpf) {
        time += tpf;
        if (time > checkInterval)
        {
            if (topLevelNode == null){
                topLevelNode = this;
                while( topLevelNode.getParent() !=  null){
                    topLevelNode = topLevelNode.getParent();
                }
            }
            Spatial child = topLevelNode.getChild(prefabName);
            if ( child != null && child instanceof Prefab)
            {
                onPrefabFound.prefabFound((Prefab)child, this);
            }
            time = 0;
        }
    }
    
    
    
    
    /**
     * Returns the name of the prefab we look for, for presentation
     * reasons.
     * @return the name of the prefab we search.
     */
    @Override
    public String toString(){
        return prefabName;
    }
}
