/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.project;

import com.google.common.io.Files;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.rig.io.RigWriter;
import dae.io.SceneLoader;
import dae.io.SceneSaver;
import dae.prefabs.Prefab;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.ui.events.ErrorMessage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This is a subclass of the Level class that deals with the editing of one
 * particular asset. The asset level will be visible in the project panel and
 * also allows the user to add other assets to it ( making assemblies a
 * possibility.
 *
 * @author Koen Samyn
 */
public class AssetLevel extends Level {
    
    private Path asset;
    private boolean levelShown = false;
    
    private Node savableObject;

    /**
     * Creates a new AssetLevel.
     *
     * @param asset the asset to edit.
     */
    public AssetLevel(Path asset) {
        super(asset.toString(), true);
        this.asset = asset;
        // don't save the default lights.
        for (Prefab p : getDefaultLights()) {
            p.setUserData("Save", Boolean.FALSE);
        }
    }

    /**
     * Returns the location in the
     */
    @Override
    public File getRelativeLocation() {
        return asset.toFile();
    }

    /**
     * This method is called when this asset level is shown in the viewport
     * manager.
     *
     * @param manager the assetmanager that is used to load the asset.
     * @param state the bulle app state that can be used to load the physics
     * properties.
     */
    @Override
    public void levelShown(AssetManager manager, BulletAppState state) {
        ArrayList<Spatial> toRemove = new ArrayList<Spatial>();
        for(Spatial s : this.getChildren())
        {
            if ( !defaultLights.contains(s)){
                toRemove.add(s);
            }
        }
        for ( Spatial s : toRemove){
            s.removeFromParent();
        }
        
        String assetLocation = asset.toString();
        if (Files.getFileExtension(assetLocation).equalsIgnoreCase("j3o")) {
            File file = new File(project.getRelativeKlatchDirectory(), assetLocation + ".klatch");
            super.setLocation(file);
            super.levelShown(manager, state);
            if (!levelShown) {
                MeshObject mo = new MeshObject();
                mo.setType("Mesh");
                mo.setCategory("Standard");
                mo.create(asset.getFileName().toString(), manager, assetLocation);
                this.attachChild(mo);
                levelShown = true;
            }
            this.asset = Paths.get(assetLocation + ".klatch");
            
        } else if (Files.getFileExtension(assetLocation).equalsIgnoreCase("klatch")) {
            File file = new File(project.getRelativeKlatchDirectory(), assetLocation);
            super.setLocation(file);
            super.levelShown(manager, state);
            // must be loaded as separate 
            URL locationOnDisk = project.getResource(assetLocation);
            try {
                SceneLoader.loadScene(locationOnDisk.openStream(), manager, this, GlobalObjects.getInstance().getObjectsTypeCategory(), null);
                savableObject = this;
            } catch (IOException ex) {
                Logger.getLogger(AssetLevel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            levelShown = true;
        } else if (Files.getFileExtension(assetLocation).equalsIgnoreCase("rig")) {
            File file = new File(project.getRelativeKlatchDirectory(), assetLocation);
            super.setLocation(file);
            super.levelShown(manager, state);
            // must be loaded as separate 
            try {
                Rig rig = (Rig)manager.loadModel(assetLocation);
                attachChild(rig);
                savableObject = rig;
            } catch (AssetNotFoundException ex) {
                GlobalObjects.getInstance().postEvent(new ErrorMessage("Could not load " + assetLocation));
            }
            levelShown = true;
        }
        this.attachChild(super.ground);
        for (Prefab p : getDefaultLights()) {
            p.setUserData("Save", Boolean.FALSE);
        }
        for (Spatial s : this.children) {
            if (s instanceof Prefab) {
                ((Prefab) s).setChanged(false, true);
            }
        }
    }
    
    @Override
    public void levelHidden() {
        if (isChanged()) {
            this.save();
        }
    }

    /**
     * Checks if this AssetLevel object has changed.
     *
     * @return true if the level has changed, false otherwise.
     */
    public boolean isChanged() {
        boolean changed = false;
        for (Spatial s : this.children) {
            if (s instanceof Prefab) {
                changed |= ((Prefab) s).isChanged(true);
            }
        }
        return changed;
    }
    
    public void save() {
        String assetLocation = asset.toString();
        File file = new File(project.getKlatchDirectory(), assetLocation);
        if (assetLocation.endsWith("klatch")) {
            SceneSaver.writeScene(file, savableObject);
        } else  {
            RigWriter.writeRig(file, (Rig)savableObject);
        }
    }
    
    public Path getAsset() {
        return this.asset;
    }
    
    @Override
    public int attachChild(Spatial node) {
        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.setChanged(true, false);
        }
        return super.attachChild(node); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected int attachChildDirectly(Node node) {
        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.setChanged(true, false);
        }
        return super.attachChildDirectly(node); //To change body of generated methods, choose Tools | Templates.
    }
}
