/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.watchservice;

import java.nio.file.Path;

/**
 *
 * @author samyn_000
 */
public interface WatchServiceListener {
    /**
     * Indicates that a path was modified.
     * @param path 
     */
    public void pathModified(Path path);
    /**
     * Indicates that an entry was modified
     */
    public void assetModified(Path path);

    public void pathCreated(Path subDir);

    public void assetCreated(Path subDir);

    public void pathDeleted(Path subDir);

    public void assetDeleted(Path subDir);
}
