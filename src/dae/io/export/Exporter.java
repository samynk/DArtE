/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io.export;

import com.jme3.asset.AssetManager;
import dae.project.Level;
import java.io.File;

/**
 *
 * @author Koen Samyn
 */
public interface Exporter {
    /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param node the root node of the scene.
     */
    public void writeScene(File location, AssetManager manager, Level level);
}
