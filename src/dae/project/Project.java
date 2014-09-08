/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.project;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class describes a Sandbox project, with all the available relevant meta
 * data.
 *
 * @author samyn_000
 */
public class Project implements ProjectTreeNode{

    private String projectName = "empty";
    private File projectLocation;
    /**
     * The location for clatch folders, this can be relative or absolute,
     * default is the clatch subfolder in the project folder.
     */
    private String clatchDirectory = "klatches";
    private ArrayList<File> assetFolders = new ArrayList<File>();
    private ArrayList<Level> levels = new ArrayList<Level>();
    private boolean saved = false;
    // the classpath loader for this project.
    private URLClassLoader projectAssetsLoader;

    public Project() {
        this.projectName = "project1";
        addLevel("default");
    }

    /**
     * Return the folder for the clatches. The clatch directory is a subfolder
     * of the project directory which is named "clatches" by default.
     *
     * @return
     */
    public File getKlatchDirectory() {
        File parentFile = getProjectLocation().getParentFile().getAbsoluteFile();
        return new File(parentFile, this.clatchDirectory);
    }
    
    /**
     * Returns the relative klatch directory.
     * @return the relative klatch directory.
     */
    public String getRelativeKlatchDirectory(){
        return this.clatchDirectory;
    }

    /**
     * Creates an empty project.
     *
     * @param empty if true , no level will be added to the project.
     */
    public Project(boolean empty) {
        this.projectName = "project1";
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the projectLocation
     */
    public File getProjectLocation() {
        return projectLocation;
    }

    /**
     * @param projectLocation the projectLocation to set
     */
    public void setProjectLocation(File projectLocation) {
        this.projectLocation = projectLocation;
    }

    /**
     * Checks if the project has a file location.
     *
     * @return true if the project has a file location,false otherwise.
     */
    public boolean hasFileLocation() {
        return this.projectLocation != null;
    }

    /**
     * Adds an asset folder to this project.
     *
     * @param folder the folder to add.
     */
    public void addAssetFolder(File folder) {

        if (!assetFolders.contains(folder)) {
            this.assetFolders.add(folder);
        }
        addAssetFoldersToClassPath();


    }

    /**
     * Removes an asset folder from this project.
     *
     * @param folder the folder to remove.
     */
    public void removeAssetFolder(File folder) {
        this.assetFolders.remove(folder);
        addAssetFoldersToClassPath();
    }

    /**
     * Returns the list of asset folders.
     *
     * @return the list of asset folders.
     */
    public Iterable<File> getAssetFolders() {
        return assetFolders;
    }

    /**
     * Returns the index of an asset folder.
     *
     * @param dir the asset folder to get the index of.
     * @return the index of the asset folder.
     */
    public int getAssetFolderIndex(File assetFolder) {
        return assetFolders.indexOf(assetFolder);
    }

    /**
     * Creates a new level and adds it to the list of levels.
     *
     * @param name the name of the level to add.
     * @return the index where the level was added.
     */
    public int addLevel(String name) {
        if (!hasLevel(name)) {
            Level level = new Level(name,true);
            this.levels.add(level);
            level.setProject(this);
            return levels.indexOf(level);
        } else {
            return -1;
        }
    }

    /**
     * Checks if a level with this name allready exists.
     *
     * @param name the name of the level.
     * @return true if the level exists, false other wise.
     */
    public boolean hasLevel(String name) {
        for (Level l : this.levels) {
            if (l.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Checks if there is an asset level present for the given assetLocation.
     * @param assetLocation the assetlocation to check.
     * @return true if an assetlevel is present, false otherwise.
     */
    public boolean hasAssetLevelForFile(String assetLocation) {
        Path p = Paths.get(assetLocation);
        for(Level l: this.levels)
        {
            if ( l instanceof AssetLevel){
                AssetLevel al = (AssetLevel)l;
                String path = al.getAsset().toString();
                System.out.println("Comparing " + path + " to " + p);
                if ( al.getAsset().equals(p)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds an existing level to this project.
     *
     * @param l the level to add.
     * @return the index where the level was added.
     */
    public int addLevel(Level l) {
        this.levels.add(l);
        l.setProject(this);
        return levels.indexOf(l);
    }

    /**
     * Creates a new name for a level.
     *
     * @return a new default name.
     */
    public String createLevelName() {
        String levelName;
        int i = 1;
        do {
            levelName = "level" + (++i);
        } while (this.hasLevelWithName(levelName));
        return levelName;
    }

    /**
     * Checks if a level exists with the given name.
     *
     * @param levelName the name of the level.
     * @return true if the level exists , false otherwise.
     */
    private boolean hasLevelWithName(String levelName) {
        for (Level l : this.levels) {
            if (l.getName().equals(levelName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the levels that are present in this game.
     *
     * @return the list of levels.
     */
    public Iterable<Level> getLevels() {
        return levels;
    }

    /**
     * Checks if the project already has levels or not.
     *
     * @return true if the projects has levels, false otherwise.
     */
    public boolean hasLevels() {
        return levels.size() > 0;
    }

    /**
     * Get the saved status of this project.
     *
     * @return the saved status of this project.
     */
    public boolean getSaved() {
        return saved;
    }

    /**
     * Sets the saved status of this project.
     *
     * @parameter saved true if the project is saved, false otherwise.
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    /**
     *
     */
    public void addAssetFoldersToClassPath() {
        if (projectAssetsLoader != null) {
            try {
                projectAssetsLoader.close();
            } catch (IOException ex) {
                Logger.getLogger(Project.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        projectAssetsLoader = new URLClassLoader(getClassPathUrls(), this.getClass().getClassLoader());
    }

    public URL[] getClassPathUrls() {
        URL[] urls = new URL[assetFolders.size()];
        for (int i = 0; i < assetFolders.size(); ++i) {
            try {
                urls[i] = assetFolders.get(i).toURI().toURL();
            } catch (MalformedURLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return urls;
    }

    public ClassLoader getAssetLoader() {
        return projectAssetsLoader;
    }

    public File getProjectFile() {
        return new File(getProjectLocation(), getProjectName() + ".zbk");
    }

    public Object getAssetFoldersAt(int index) {
        return assetFolders.get(index);
    }

    public Iterable<File> getAssetLocations() {
        return assetFolders;
    }

    public int getNrOfAssetFolders() {
        return this.assetFolders.size();
    }

    public Level getLevel(int index) {
        return levels.get(index);
    }

    public int getNrOfLevels() {
        return levels.size();
    }

    public int getIndexOfLevel(Level level) {
        return levels.indexOf(level);
    }

    @Override
    public String toString() {
        return projectName;
    }

    public URL getResource(String fullName) {
        return this.projectAssetsLoader.findResource(fullName);
    }

    public void removeLevel(Level level) {
        this.levels.remove(level);
    }

    public boolean hasChildren() {
        return levels.size() > 0;
    }

    public ProjectTreeNode getProjectChild(int index) {
        return levels.get(index);
    }

    public int getIndexOfChild(ProjectTreeNode object) {
        return levels.indexOf(object);
    }

    public ProjectTreeNode getProjectParent() {
        return null;
    }

    public boolean isLeaf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}