/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

import dae.prefabs.ui.classpath.FileNode;

/**
 *
 * @author Koen Samyn
 */
public class AssetEvent {
    private AssetEventType eventType;
    private FileNode node;
    
    /**
     * Creates a new AssetEvent.
     * @param type the type of asset event.
     * @param node the FileNode that holds the location of the asset.
     */
    public AssetEvent(AssetEventType type, FileNode node){
        this.eventType = type;
        this.node = node;
    }
    
    /**
     * Returns the type of event.
     * @return either AssetEventType.EDIT, AssetEventType.REMOVE or AssetEventType.RENAME
     */
    public AssetEventType getAssetEventType(){
        return eventType;
    }
    
    public FileNode getFileNode(){
        return node;
    }
}
