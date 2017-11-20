package dae.project;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import dae.DAEFlyByCamera;
import dae.GlobalObjects;
import dae.io.SceneSaver;
import dae.prefabs.AxisEnum;
import dae.prefabs.Prefab;
import dae.prefabs.lights.DirectionalLightPrefab;
import dae.prefabs.standard.CameraFrame;
import dae.prefabs.types.ObjectType;
import dae.prefabs.ui.events.LayerEvent;
import dae.prefabs.ui.events.LayerEvent.LayerEventType;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.prefabs.ui.events.ShadowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author Koen Samyn
 */
public class Level extends Node implements ProjectTreeNode {

    private File location;
    private AxisEnum upAxis = AxisEnum.Z;
    protected Grid ground;
    private HashMap<String, Layer> layerMap = new HashMap<String, Layer>();
    private ArrayList<Layer> layers = new ArrayList<Layer>();
    private PhysicsLayer physicsLayer;
    protected Project project;
    private boolean relativeLocation = true;
    private boolean createDefaultLights = true;
    protected ArrayList<Prefab> defaultLights = new ArrayList<Prefab>();
    private HashMap<String, File> exportLocations = new HashMap<String, File>();
    private boolean exportOnSave = true;
    private CameraFrame lastCamera;
    /**
     * Render settings
     */
    private String skyBoxTexture = "";
    /**
     * The assetmanager to use.
     */
    private AssetManager manager;

    /**
     * Creates a new level.
     *
     * @param name the name of the level.
     */
    public Level(String name, boolean createDefaultLights) {
        super(name);
        addLayer("default");
        addLayer("waypoints");
        addLayer("lights");
        physicsLayer = new PhysicsLayer();
        addLayer(physicsLayer);
        this.createDefaultLights = createDefaultLights;
    }

    /**
     * Create some default lighting.
     */
    protected void createLights(AssetManager manager) {
        ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Light", "DirectionalLight");

        DirectionalLightPrefab light1 = (DirectionalLightPrefab) type.create(manager, "Light1");
        light1.setDirectionalLightColor(new ColorRGBA(0.97f, 0.91f, 0.69f, 1.0f).mult(0.75f));
        light1.setLocalTranslation(new Vector3f(2, 2, 2));
        light1.setLightDirection(new Vector3f(FastMath.cos(FastMath.PI * 2 / 3), -.6f, FastMath.sin(FastMath.PI * 2 / 3)));
        attachChild(light1);

        DirectionalLightPrefab light2 = (DirectionalLightPrefab) type.create(manager, "Light2");
        light2.setDirectionalLightColor(new ColorRGBA(0.97f, 0.91f, 0.69f, 1.0f).mult(0.75f));
        light2.setLocalTranslation(new Vector3f(-2, 2, 2));
        light2.setLightDirection(new Vector3f(FastMath.cos(FastMath.PI * 4 / 3), -.6f, FastMath.sin(FastMath.PI * 4 / 3)).normalizeLocal());
        attachChild(light2);

        DirectionalLightPrefab backlight = (DirectionalLightPrefab) type.create(manager, "Backlight");
        backlight.setDirectionalLightColor(new ColorRGBA(0.65f, 0.65f, 0.78f, 1.0f).mult(0.75f));
        backlight.setLocalTranslation(new Vector3f(2, 2, -2));
        backlight.setLightDirection(new Vector3f(1.0f, -.6f, 0.0f).normalizeLocal());
        attachChild(backlight);

        defaultLights.add(light1);
        defaultLights.add(light2);
        defaultLights.add(backlight);

        if (ground == null) {
            ObjectType gridtype = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Ground");
            ground = (Grid) gridtype.create(manager, name);
            this.attachChild(ground);
            LevelEvent le = new LevelEvent(this, EventType.NODEADDED, ground);
            GlobalObjects.getInstance().postEvent(le);
        }
    }

    protected Iterable<Prefab> getDefaultLights() {
        return defaultLights;
    }

    /**
     * Adds a new layer with the given name
     *
     * @param layer
     */
    public final Layer addLayer(String layer) {
        Layer l = new Layer(layer);
        layerMap.put(layer, l);
        layers.add(l);
        l.setParentLevel(this);
        return l;
    }

    /**
     * Adds a new layer.
     *
     * @param layer the layer to add.
     */
    public final void addLayer(Layer layer) {
        layerMap.put(layer.getName(), layer);
        layers.add(layer);
        layer.setParentLevel(this);
    }

    /**
     * Checks if this level has a layer with the given name.
     *
     * @return true if the layer exists, false otherwise.
     */
    public boolean hasLayer(String layerName) {
        return this.layerMap.containsKey(layerName);
    }

    /**
     * Returns the layer with the specified name.
     *
     * @param layerName the name of the layer.
     * @return the layer, if the layer does not exist, a layer with default
     * properties will be created.
     */
    public Layer getLayer(String layerName) {
        Layer l = layerMap.get(layerName);
        if (l == null) {
            l = addLayer(layerName);
        }
        return l;
    }

    /**
     * @return the location
     */
    public File getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(File location) {
        this.location = location;
    }

    /**
     * Returns the location in the
     */
    public File getRelativeLocation() {
        return location;
    }

    /**
     * Checks if this level has a relative location within the project. For
     * normal levels, this value is set to true.
     *
     * @return true if the level has a relative location , false otherwise.
     */
    public boolean hasRelativeLocation() {
        return relativeLocation;
    }

    /**
     * Set to true if the location of the level is relative to the project
     * folder. This makes it easier to move projects.
     *
     * @param relativeLocation
     */
    public void setRelativeLocation(boolean relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    /**
     * Checks if this level is already saved at a certain location.
     *
     * @return true if the level has a location, false otherwise.
     */
    public boolean hasLocation() {
        return location != null;
    }

    /**
     * Returns the absolute location of the level file.
     *
     * @return
     */
    public File getAbsoluteLocation() {
        if (hasRelativeLocation()) {
            File projectLocation = project.getProjectLocation().getParentFile();
            return new File(projectLocation, this.getRelativeLocation().getPath());
        } else {
            return this.getLocation();
        }
    }

    /**
     * Gets the up axis for this level, default is Z Axis.
     *
     * @return the upAxis
     */
    public AxisEnum getUpAxis() {
        return upAxis;
    }

    /**
     * Sets the up axis for this level, can be X, Y or Z.
     *
     * @param upAxis the upAxis to set
     */
    public void setUpAxis(AxisEnum upAxis) {
        this.upAxis = upAxis;
    }

    /**
     * Notification that the level has been added to the viewport.
     *
     * @param manager the manager to use for loading .
     */
    public void levelShown(AssetManager manager, BulletAppState state) {
        this.manager = manager;

        if (this.createDefaultLights) {
            this.createLights(manager);
            createDefaultLights = false;
        }

        physicsLayer.setBulletAppState(state);

        // find lights that have shadow enabled and activate
        // them.
        List<DirectionalLightPrefab> dirLights = this.descendantMatches(DirectionalLightPrefab.class);
        for (DirectionalLightPrefab dl : dirLights) {
            if (dl.getCastShadow()) {
                ShadowEvent se = new ShadowEvent(dl);
                GlobalObjects.getInstance().postEvent(se);
            }
        }
    }

    /**
     * Checks if this Level object has changed.
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

    /**
     * Sets the level to the changed status.
     */
    public void setChanged() {
        for (Spatial s : this.children) {
            if (s instanceof Prefab) {
                ((Prefab) s).setChanged(true, false);
            }
        }
    }

    /**
     * Returns the last know camera frame.
     *
     * @return the last camera frame that was used for this level.
     */
    public CameraFrame getLastCamera() {
        return lastCamera;
    }

    public AssetManager getAssetManager() {
        return manager;
    }

    /**
     * Called when the level is hidden.
     */
    public void levelHidden() {
        GlobalObjects go = GlobalObjects.getInstance();
        DAEFlyByCamera cam = go.getCamera();
        if (lastCamera == null) {
            this.lastCamera = new CameraFrame(cam);
        } else {
            lastCamera.copy(cam);
        }
    }

    /**
     * Returns the name of this level.
     *
     * @return the name of the level.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Attaches a child to this node, and adds it to the correct layer.
     */
    @Override
    public int attachChild(Spatial node) {
        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.setChanged(true, false);
            String layer = p.getLayerName();

            if (!(this.getChildIndex(node) > -1)) {
                Layer currentLayer;
                if (!this.hasLayer(layer)) {
                    currentLayer = this.addLayer(layer);
                    LayerEvent le = new LayerEvent(currentLayer, LayerEventType.CREATED);
                    GlobalObjects.getInstance().postEvent(le);
                } else {
                    currentLayer = getLayer(layer);
                }
                currentLayer.addNode(p);
                return super.attachChild(node);
            }
        }
        return -1;
    }

    protected int attachChildDirectly(Node node) {
        return super.attachChild(node);
    }

    private int getLayerCount() {
        int layerCount = 0;
        for (Layer l : layers) {
            if (l.isRootLayer()) {
                ++layerCount;
            }
        }
        return layerCount;
    }

    /**
     * Returns the root layer at the specified position.
     *
     * @param index the index of the layer.
     * @return the found Layer object, or null if none was found.
     */
    public Layer getRootLayer(int index) {
        int layerCount = 0;
        for (Layer l : layers) {
            if (l.isRootLayer()) {
                if (layerCount == index) {
                    return l;
                }
                ++layerCount;
            }
        }
        return null;
    }

    /**
     * Counts the number of children.
     *
     * @return the number of children.
     */
    public int getChildCount() {
        // count the number of root layers.
        return getLayerCount();
    }

    /**
     * Returns the child at the specified position.
     *
     * @param index the index of the child object.
     * @return depending on the index, a root layer can be returned, or a node
     * object.
     */
    public Object getLevelChild(int index) {
        return getRootLayer(index);
    }

    /**
     * Returns the number of child layers of this layer.
     *
     * @param layer the child layer.
     * @return the number of child layers of this layer.
     */
    public int getNumberOfChildLayers(Layer layer) {
        int numLayers = 0;
        for (Layer l : layers) {
            if (l.isChildOf(layer)) {
                numLayers++;
            }
        }
        return numLayers;
    }

    /**
     * Returns the child layer at the specific position.
     *
     * @param layer the parent layer.
     * @param index the index of the child layer.
     * @return the child layer object, or null if none is found.
     */
    public Layer getChildLayerAt(Layer layer, int index) {
        int numLayers = 0;
        for (Layer l : layers) {
            if (l.isChildOf(layer)) {
                if (index == numLayers) {
                    return l;
                }
                numLayers++;
            }
        }
        return null;
    }
    StringBuilder helper = new StringBuilder();

    /**
     * Returns the Layer object.
     *
     * @param layerName the name of the layer.
     * @param i the index of the parent of the layer. 0 is the root, 1 the layer
     * under the root ....
     * @return the Layer object.
     */
    public Layer getParentLayer(String layerName, int layerIndex) {
        helper.delete(0, helper.length());
        String[] components = layerName.split("\\.");

        for (int i = 0; i <= layerIndex && i < components.length; ++i) {
            helper.append(components[i]);
            helper.append('.');
        }

        helper.deleteCharAt(helper.length() - 1);
        String parentLayerName = helper.toString();
        return this.getLayer(parentLayerName);
    }

    /**
     * Returns the direct parent of the layer.
     *
     * @param layerName the name of the layer
     * @return the parent layer of the layer name, or null if no such parent
     * layer exists.
     */
    public Layer getParentLayer(String layerName) {
        int lastDotIndex = layerName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String parentlayer = layerName.substring(0, lastDotIndex);
            return this.getLayer(parentlayer);
        } else {
            return null;
        }
    }
    /**
     * Threading related/
     */
    ConcurrentLinkedDeque<Runnable> todoQueue = new ConcurrentLinkedDeque<Runnable>();

    /**
     * Posts a new task to the executor.
     *
     * @param executor
     */
    public void postTask(Runnable executor) {
        todoQueue.add(executor);
    }

    @Override
    public void updateLogicalState(float tpf) {

        Runnable r = todoQueue.poll();
        while (r != null) {
            r.run();
            r = todoQueue.poll();
        }
        super.updateLogicalState(tpf);
    }
    private boolean showTargetObjects = true;

    public void showTargetObjects(boolean show) {
        if (show != showTargetObjects) {
            if (show) {
                postTask(new Runnable() {
                    public void run() {
                        showTargetObjects();
                    }
                });
            } else {
                postTask(new Runnable() {
                    public void run() {
                        hideTargetObjects();
                    }
                });
            }
            showTargetObjects = show;
        }
    }

    public boolean getShowsTargetObjects() {
        return showTargetObjects;
    }

    /**
     * Makes all the internal handle objects invisible.
     */
    private void hideTargetObjects() {
        for (Spatial s : this.getChildren()) {
            if (s instanceof Prefab) {
                Prefab p = (Prefab) s;
                p.hideTargetObjects();
            }
        }
    }

    /**
     * Makes all the internal handle objects invisible.
     */
    private void showTargetObjects() {
        for (Spatial s : this.getChildren()) {
            if (s instanceof Prefab) {
                Prefab p = (Prefab) s;
                p.showTargetObjects();
            }
        }
    }

    /**
     * Sets the parent project of this level.
     *
     * @param project the parent project of the level.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    // project tree node implementation
    @Override
    public boolean hasChildren() {
        return layers.size() > 0;
    }

    @Override
    public int getIndexOfChild(ProjectTreeNode object) {
        return layers.indexOf(object);
    }

    public boolean isLeaf() {
        return layers.size() > 0;
    }

    public ProjectTreeNode getProjectChild(int index) {
        return layers.get(index);
    }

    public ProjectTreeNode getProjectParent() {
        return this.project;
    }

    public void setExportLocation(String key, File selected) {
        exportLocations.put(key, selected);
    }

    public File getExportLocation(String key) {
        return exportLocations.get(key);
    }

    public boolean isExportOnSave() {
        return exportOnSave;
    }

    public void setExportOnSave(boolean value) {
        this.exportOnSave = value;
    }

    public boolean hasExportKeys() {
        return !exportLocations.isEmpty();
    }

    public Iterable<String> getExportKeys() {
        return exportLocations.keySet();
    }

    public void save(File location) {
        SceneSaver.writeScene(location, this);
    }

    /**
     * Returns the skybox texture.
     *
     * @return the path to the skybox texture.
     */
    public String getSkyBoxTexture() {
        return skyBoxTexture;
    }

    /**
     * Sets the skybox texture.
     *
     * @param texture the texture to set.
     */
    public void setSkyBoxTexture(String texture) {
        if (!texture.equals(this.skyBoxTexture)) {
            System.out.println("Setting sky box texture.");
            this.skyBoxTexture = texture;
            postTask(new Runnable() {
                public void run() {
                    getParent().attachChild(SkyFactory.createSky(
                            manager, skyBoxTexture, false));
                }
            });
        }
    }
}
