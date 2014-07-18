/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

import com.jme3.math.Transform;
import com.jme3.scene.Node;
import dae.project.Level;

/**
 * Creates a new insert object event.
 * Objects that are created in this manner are directly inserted into the scene.
 * This main purpose of this klatch is to enable the replacement of a tree of meshes
 * with one klatch file.
 * @author Koen Samyn
 */
public class InsertObjectEvent {
    public Level level;
    public String path;
    private String name;
    private Node parent;
    private Transform transform;
    
    /**
     * Creates a new InsertObjectEvent.
     * @param level what is the level for the insert object event.
     * @param path the path to load.
     * @param name the name of the object.
     * @param parent the parent of the object.
     * @param transform the transform to set.
     */
    public InsertObjectEvent(Level level, String path, String name, Node parent, Transform transform){
        this.level = level;
        this.path = path;
        this.name = name;
        this.parent = parent;
        this.transform = transform;
    }
    
    /**
     * Returns the level for the insertion event.
     * @return the current level.
     */
    public Level getLevel(){
        return level;
    }
    
    /**
     * Return the path to the object to load via the asset manager.
     * @return the path to load.
     */
    public String getPath(){
        return path;
    }
    
    /**
     * Returns the new name of the object.
     * @return the name of the object.
     */
    public String getName(){
        return name;
    }
    
    /**
     * Returns the parent to attach the created item to.
     * @return the parent node.
     */
    public Node getParent(){
        return parent;
    }
    
    /**
     * Returns the transform to set on the new object.
     * @return the transform to use.
     */
    public Transform getTransform(){
        return transform;
    }
}
