package dae.gui.tools;

import com.google.common.eventbus.Subscribe;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import dae.GlobalObjects;
import dae.gui.SandboxViewport;
import dae.prefabs.Prefab;
import dae.prefabs.brush.Brush;
import dae.prefabs.brush.TerrainBrush;
import dae.prefabs.types.ObjectType;
import dae.prefabs.ui.events.BrushEvent;
import dae.prefabs.ui.events.BrushEventType;
import dae.prefabs.ui.events.LevelEvent;
import dae.project.Layer;
import dae.project.Level;

/**
 *
 * @author Koen Samyn
 */
public class BrushTool extends ViewportTool {

    /**
     * helper objects for the brush.
     */
    private TerrainBrush defaultBrush;
    private Geometry brushGizmo;
    private Cylinder brushShape;
    private Brush selectedBrush;

    private enum BrushState {

        DOBRUSH, IDLE
    };
    private BrushState brushState = BrushState.IDLE;

    public BrushTool() {
        super("BrushTool");
    }

    /**
     * Initializes the tool.
     *
     * @param manager the AssetManager to load objects and materials from.
     * @param inputManager can be used to register keypresses that can be used
     * with this tool.
     */
    public void initialize(AssetManager manager, InputManager inputManager) {
        ObjectType objectType = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Terrain", "Brush");
        defaultBrush = (TerrainBrush)objectType.create(manager, "defaultbrush");
        defaultBrush.setLayerName("terrain");
        
        brushShape = new Cylinder(2, 12, defaultBrush.getRadius(), 1.0f);
        brushGizmo = new Geometry("terrainBrush", brushShape);
        Quaternion q = new Quaternion();
        q.fromAngles(FastMath.HALF_PI, 0, 0);
        brushGizmo.setLocalRotation(q);
        brushGizmo.setMaterial(manager.loadMaterial("Materials/BrushGizmoMaterial.j3m"));

        inputManager.addListener(shiftListener, new String[]{"DOBRUSH"});
        
        GlobalObjects.getInstance().registerListener(this);
        
    }

    @Override
    public void activate(SandboxViewport viewport) {
        setActive();
        viewport.getRootNode().attachChild(brushGizmo);
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        brushGizmo.removeFromParent();
        setInactive();
    }
    
    
    
    private ActionListener shiftListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("DOBRUSH")) {
                if (keyPressed) {
                    System.out.println("setting state to dobrush");
                    brushState = BrushState.DOBRUSH;
                } else {
                    System.out.println("setting state to idle brush");
                    brushState = BrushState.IDLE;
                }
            }
        }
    };

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position

        InputManager inputManager = viewport.getInputManager();
        Vector2f click2d = inputManager.getCursorPosition();

        Camera cam = viewport.getCamera();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        dir.normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        Node sceneElements = viewport.getSceneElements();
        sceneElements.collideWith(ray, results);

        if (results.size() > 0) {
            Vector3f location = results.getClosestCollision().getContactPoint();

            if (selectedBrush != null) {
                brushShape.updateGeometry(2, 12, selectedBrush.getRadius(), selectedBrush.getRadius(), 2.0f, true, false);
                brushShape.updateBound();
                brushGizmo.setLocalTranslation(location);
                Vector3f normal = results.getClosestCollision().getContactNormal();
                System.out.println("Brush state : " + brushState);
                String layer = selectedBrush.getBrushLayer();
                // if the layer is null, use the layer of the brush itself.
                if (layer == null || layer.length() == 0) {
                    layer = selectedBrush.getLayerName();
                }
                String nodeName = selectedBrush.getBrushNode();

                Level level = viewport.getLevel();
                if (nodeName == null || nodeName.length() == 0) {
                    nodeName = viewport.createName(level, selectedBrush.getName());
                    selectedBrush.setBrushNode(nodeName);
                }
                Layer brushLayer = level.getLayer(layer);
                if (brushLayer == null) {
                    brushLayer = level.addLayer(layer);
                }
                Node node = brushLayer.getNodeChild(nodeName);
                if (node == null) {
                    ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Terrain", "BrushBatch");
                    if (ot != null) {
                        AssetManager assetManager = viewport.getAssetManager();
                        Prefab p = ot.create(assetManager, nodeName);
                        p.setLayerName(layer);
                        node = p;
                        //brushLayer.addNode(node);
                        level.attachChild(node);
                        LevelEvent levelEvent = new LevelEvent(level, LevelEvent.EventType.NODEADDED, node);
                        GlobalObjects.getInstance().postEvent(levelEvent);
                    }

                }

                if (brushState == BrushState.DOBRUSH) {
                    //System.out.println("doing brush at : " + location);
                    selectedBrush.doBrush(level, node, location, normal);
                }
            }
        }
    }
    
    /**
     * Called when the mouse button is released.
     *
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonReleased(SandboxViewport viewport) {
        
    }
    
     /**
     * Called when the mouse button is pressed.
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonPressed(SandboxViewport viewport){
        
    }

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
    }

    /**
     * Cleans up the gizmo.
     */
    public void cleanup() {
        brushGizmo.removeFromParent();
    }

    /**
     * Brush gizmo has no pickable elements.
     *
     * @param ray the ray to pick with.
     * @param results the results of the collision.
     */
    public void pickGizmo(Ray ray, CollisionResults results) {
    }

    @Override
    public void gizmoPicked(SandboxViewport viewport, Geometry g, Vector3f contactPoint) {
    }

    @Override
    public void selectionChanged(SandboxViewport viewport, Node node) {
        
    }

    @Override
    public void removeGizmo() {
       brushGizmo.removeFromParent();
    }
    
    @Subscribe
    public void brushSelected(BrushEvent be) {
        if (be.getType() == BrushEventType.SELECTED) {
            this.selectedBrush = be.getBrush();
        }
    }
}
