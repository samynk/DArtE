/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.io;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import dae.io.game.GameSceneLoader;
import dae.project.Level;
import java.io.File;

/**
 * This class exports a given scene to a J3O file. All the specific
 * sandbox constructs ( waypoints, cameras, ... ) are exported as dummies.
 * @author Koen Samyn
 */
public class SceneToJ3OExporter {

    /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param node the root node of the scene.
     */
    public static void writeScene(String location, AssetManager manager, Level level) {
        File levelLocation = level.getLocation();
        Node result = new Node(level.getName());
        GameSceneLoader.loadScene(levelLocation, manager, result);
        
        
    }
}
