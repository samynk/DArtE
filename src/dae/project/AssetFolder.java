/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.project;

import java.io.File;

/**
 * This class stores the location of an asset folder.
 *
 * @author Koen Samyn
 */
public class AssetFolder {

    private File location;

    /**
     * Creates a new AssetFolder.
     *
     * @param location the file location for the asset folder.
     */
    public AssetFolder(File location) {
        this.location = location;
    }

    public File getLocation() {
        return location;
    }
}
