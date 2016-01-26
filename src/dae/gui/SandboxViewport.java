package dae.gui;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.shadow.AbstractShadowRenderer;
import dae.DAECamAppState;
import dae.GlobalObjects;
import dae.animation.rig.io.RigLoader;
import dae.animation.skeleton.BodyLoader;
import dae.components.ComponentType;
import dae.components.MeshComponent;
import dae.components.PrefabComponent;
import dae.controller.ControllerLoader;
import dae.gui.tools.BrushTool;
import dae.gui.tools.IdleTool;
import dae.gui.tools.InsertionTool;
import dae.gui.tools.LinkTool;
import dae.gui.tools.PickTool;
import dae.gui.tools.RotateTool;
import dae.gui.tools.TranslateTool;
import dae.gui.tools.ViewportTool;
import dae.io.AnimationReader;
import dae.io.ComponentReader;

import dae.io.ObjectTypeReader;
import dae.io.SceneLoader;
import dae.io.game.GameBuilder;
import dae.io.game.GameWriter;
import dae.io.readers.OVMReader;
import dae.prefabs.AxisEnum;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.gizmos.RotateGizmo;
import dae.prefabs.gizmos.events.AutoGridEvent;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.prefab.undo.AddPrefabEdit;
import dae.prefabs.prefab.undo.DeletePrefabEdit;
import dae.prefabs.standard.CameraFrame;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.ui.classpath.FileNode;
import dae.prefabs.ui.events.AssetEvent;
import dae.prefabs.ui.events.AssetEventType;
import dae.prefabs.ui.events.ComponentEvent;
import dae.prefabs.ui.events.CreateObjectEvent;
import dae.prefabs.ui.events.GizmoEvent;
import dae.prefabs.ui.events.GizmoType;
import static dae.prefabs.ui.events.GizmoType.ROTATE;
import static dae.prefabs.ui.events.GizmoType.TRANSLATE;
import dae.prefabs.ui.events.InsertObjectEvent;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.prefabs.ui.events.PlayEvent;
import dae.prefabs.ui.events.ProjectEvent;
import dae.prefabs.ui.events.SelectionEvent;
import dae.prefabs.ui.events.ShadowEvent;
import dae.prefabs.ui.events.ViewportReshapeEvent;
import dae.prefabs.ui.events.ZoomEvent;
import dae.project.Grid;
import dae.project.Project;
import dae.project.ProjectTreeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Koen Samyn
 */
public class SandboxViewport extends SimpleApplication implements RawInputListener {

    /**
     * The available object types.
     */
    private ObjectTypeCategory objectsToCreate;
    /**
     * The default layer for all elements that are added to the scene.
     */
    private Node sceneElements = new Node("scene");
    /**
     * The elements to insert.
     */
    private Node insertionElements = new Node("insertion");
    private Prefab insertionElement;
    /**
     * The current gizmo type
     */
    private GizmoType gizmoType = GizmoType.NONE;
    private GizmoType newGizmoType = GizmoType.NONE;
    /**
     * The current pick property.
     */
    private String pickProperty;

    /**
     * The editor state.
     */
    private enum EditorState {

        IDLE, INSERTIONEVENT, ADDTOSCENE, LEVELCHANGED, SELECTION
    };
    /**
     * The current editor state.
     */
    private EditorState editorState = EditorState.IDLE;
    /**
     * Shift key is used for copying of items
     */
    private boolean shiftIsDown = false;
    private ArrayList<Node> currentSelection = new ArrayList<Node>();
    private CreateObjectEvent objectCreationEvent;
    /**
     * The current level.
     */
    private dae.project.Level level;
    /**
     * The current project.
     */
    private Project project;
    /**
     * Code that has to be executed on the JMonkey thread.
     */
    private final ArrayList<Runnable> viewportTasks = new ArrayList<Runnable>();
    /**
     * Selection from other thread (such as user interfac)e.
     */
    private ArrayList<Node> selectionFromOutside = new ArrayList<Node>();
    /**
     * shows the selected object.
     */
    private WireBox wireBox = new WireBox();
    private Geometry wireBoxGeometry;
    private Material wireBoxMaterial;
    /**
     * Cursor
     */
    private JmeCursor cursorConnect1;
    private JmeCursor cursorConnect2;
    private JmeCursor cursorSelect;
    private JmeCursor cursorMove;
    private JmeCursor cursorRotate;
    private JmeCursor cursorScale;
    /**
     * Tool support
     */
    private IdleTool idleTool;
    private ViewportTool currentTool;
    private TranslateTool translateTool;
    private RotateTool rotateTool;
    private BrushTool brushTool;
    private PickTool pickTool;
    private LinkTool linkTool;
    private InsertionTool insertionTool;
    /**
     * Autogrid functionality
     */
    private boolean autoGridEnabled = false;
    private AxisEnum autoGridAxis = AxisEnum.Y;

    public SandboxViewport() {
        super(new DAECamAppState());
    }

    @Override
    public void simpleInitApp() {


        assetManager.registerLoader(ObjectTypeReader.class, "types");
        assetManager.registerLoader(BodyLoader.class, "skel");
        assetManager.registerLoader(ControllerLoader.class, "fcl");
        assetManager.registerLoader(SceneLoader.class, "klatch");
        assetManager.registerLoader(RigLoader.class, "rig");
        assetManager.registerLoader(AnimationReader.class, "animset");
        assetManager.registerLoader(ComponentReader.class, "components");
        assetManager.registerLoader(OVMReader.class, "ovm");


        objectsToCreate = (ObjectTypeCategory) assetManager.loadAsset("Objects/ObjectTypes.types");
        GlobalObjects.getInstance().setObjectTypeCategory(objectsToCreate);
        GlobalObjects.getInstance().registerListener(this);
        setPauseOnLostFocus(false);

        Material gridMaterial = assetManager.loadMaterial("Materials/GridMaterial.j3m");

        translateTool = new TranslateTool();
        translateTool.initialize(assetManager, inputManager);
        rotateTool = new RotateTool();
        rotateTool.initialize(assetManager, inputManager);
        brushTool = new BrushTool();
        brushTool.initialize(assetManager, inputManager);
        pickTool = new PickTool();
        pickTool.initialize(assetManager, inputManager);
        linkTool = new LinkTool();
        linkTool.initialize(assetManager, inputManager);
        idleTool = new IdleTool();
        idleTool.initialize(assetManager, inputManager);
        insertionTool = new InsertionTool();
        insertionTool.initialize(assetManager, inputManager);

        currentTool = idleTool;

        rootNode.attachChild(sceneElements);
        rootNode.attachChild(insertionElements);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.25f));
        rootNode.addLight(al);

        AmbientLight insertionLight = new AmbientLight();
        insertionLight.setColor(ColorRGBA.White.mult(5.0f));
        insertionElements.addLight(insertionLight);

        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.setLocation(new Vector3f(5, 2, 0));
        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

        initKeys();

//        cursorConnect1 = (JmeCursor) assetManager.loadAsset("Interface/connect1.ico");
//        cursorConnect2 = (JmeCursor) assetManager.loadAsset("Interface/connect2.ico");
//        cursorMove = (JmeCursor) assetManager.loadAsset("Interface/move.ico");
//        cursorSelect = (JmeCursor) assetManager.loadAsset("Interface/select.ico");
//        cursorRotate = (JmeCursor)assetManager.loadAsset("Interface/rotate.ico");
//        cursorScale = (JmeCursor)assetManager.loadAsset("Interface/scale.ico");

        BulletAppState bulletAppState = new BulletAppState();

        stateManager.attach(bulletAppState);
        bulletAppState.setBroadphaseType(PhysicsSpace.BroadphaseType.AXIS_SWEEP_3_32);
        PhysicsSpace.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));

        GlobalObjects.getInstance().setAssetManager(assetManager);
        GlobalObjects.getInstance().setInputManager(this.inputManager);


        wireBoxMaterial = assetManager.loadMaterial("Materials/SelectionBoxMaterial.j3m");
        wireBoxGeometry = new Geometry("wireframe cube", wireBox);
        wireBoxGeometry.setMaterial(wireBoxMaterial);

        rootNode.attachChild(wireBoxGeometry);
    }

    /**
     * Attaches a guid element to the viewport.
     *
     * @param s the spatial to attach to the viewport.
     */
    public void attachGuiElement(Spatial s) {
        guiNode.attachChild(s);
    }

    public Node getSceneElements() {
        return this.sceneElements;
    }

    public dae.project.Level getLevel() {
        return this.level;
    }

    public Iterable<Node> getSelection() {
        return this.currentSelection;
    }

    public boolean isAutoGridEnabled() {
        return autoGridEnabled;
    }

    public AxisEnum getAutoGridAxis() {
        return this.autoGridAxis;
    }

    public void disableFlyCam() {
        flyCam.setEnabled(false);
    }

    public void enableFlyCam() {
        flyCam.setEnabled(true);
    }

    public void adaptSelectionBox() {
        Node parent = null;
        // TODO : remove commented code when everything works again.
        // gizmo should be removed when tool is inactive
        // should not be needed anymore.
        /*
         switch (this.gizmoType) {
         case TRANSLATE:
         parent = a.getParent();
         a.removeFromParent();
         break;
         case ROTATE:
         parent = r.getParent();
         r.removeFromParent();
         }
         */
        BoundingVolume bv = null;
        for (Node n : this.currentSelection) {
            if ( n == null)
                continue;
            if (bv == null) {
                bv = n.getWorldBound();
            } else {
                bv.merge(n.getWorldBound());
            }
        }
        if (bv != null && bv.getType() == Type.AABB) {
            BoundingBox bb = (BoundingBox) bv;

            wireBox.fromBoundingBox(bb);
            wireBoxGeometry.setLocalTranslation(bb.getCenter());
            if (wireBoxGeometry.getParent() == null) {
                this.rootNode.attachChild(wireBoxGeometry);
            }
        } else {
            wireBoxGeometry.removeFromParent();
        }
        /*
         if (parent != null) {
         switch (this.gizmoType) {
         case TRANSLATE:
         parent.attachChild(a);
         break;
         case ROTATE:
         parent.attachChild(r);
         break;
         }
         }
         */
    }

    @Override
    public void reshape(int w, int h) {
        super.reshape(w, h);
        GlobalObjects.getInstance().postEvent(new ViewportReshapeEvent(w, h));
    }

    /**
     * Initialize the inputs.
     */
    private void initKeys() {
        inputManager.setCursorVisible(true);
        // You can map one or several inputs to one named action
        inputManager.addMapping("ACCEPT_INSERTION", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("ACCEPT_INSERTION", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("REJECT_INSERTION", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("DELETE_SELECTION", new KeyTrigger(KeyInput.KEY_DELETE));
        inputManager.addMapping("CHANGE_PIVOT", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("COPY_LASTOBJECT", new KeyTrigger(KeyInput.KEY_F1));

        inputManager.addMapping("DOBRUSH", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));

        inputManager.addMapping("UNDO", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("REDO", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addMapping("ROTATION_UP", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("ROTATION_DOWN", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addListener(analogListener, new String[]{"ACCEPT_INSERTION", "SELECT_OBJECT", "REJECT_INSERTION", "DELETE_SELECTION", "ROTATION_UP", "ROTATION_DOWN"});
        inputManager.addListener(actionListener, new String[]{"CHANGE_PIVOT", "COPY_LASTOBJECT"});
        inputManager.addListener(undoRedoListener, new String[]{"UNDO", "REDO"});

        inputManager.addRawInputListener(this);
    }
    private ActionListener undoRedoListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("UNDO") && !keyPressed) {

                try {
                    GlobalObjects.getInstance().undo();
                } catch (CannotUndoException ex) {
                    Logger.getLogger("DArtE").log(Level.WARNING, "Cannot undo.", ex);
                }
            } else if (name.equals("REDO") && !keyPressed) {
                try {
                    GlobalObjects.getInstance().redo();
                } catch (CannotRedoException ex) {
                    Logger.getLogger("DArtE").log(Level.WARNING, "Cannot redo.", ex);
                }
            }
        }
    };
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("CHANGE_PIVOT") && !keyPressed) {

                for (Spatial s : insertionElements.getChildren()) {
                    if (s instanceof Prefab) {
                        ((Prefab) s).cyclePivot();
                    }
                }

            } else if (name.equals("COPY_LASTOBJECT") && !keyPressed) {
                if (objectCreationEvent != null) {
                    insertionElements.detachAllChildren();
                    editorState = EditorState.INSERTIONEVENT;

                }
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            //System.out.println("EditorState is : " + editorState);
            //System.out.println("onAnalog : " + name);
            if (editorState == EditorState.IDLE) {
                if ("DELETE_SELECTION".equals(name)) {
                    for (Node n : currentSelection) {
                        removeNode(level, n);
                    }
                    clearSelection();
                }
            }
        }
    };

    /**
     * Return the objects to create.
     *
     * @return the objects to create.
     */
    public ObjectTypeCategory getObjectsToCreate() {
        return this.objectsToCreate;
    }

    /**
     *
     */
    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        currentTool.onMouseMotionEvent(evt, this);
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        if (evt.isReleased()) {
            currentTool.onMouseButtonReleased(this);
        } else if (evt.isPressed()) {
            currentTool.onMouseButtonPressed(this);
            /*
             if (editorState == EditorState.IDLE) {
             //System.out.println("picking");
             pick();
             pickGizmo();
             } else if (editorState == EditorState.LINK || editorState == EditorState.LINKPARENT) {
             pick();
             } else if (editorState == EditorState.PICK) {
             pick();
             }
             */
        }
    }

    public void addToScene() {
        clearSelection();

        for (Spatial p : insertionElements.getChildren()) {
            addToSelection((Node) p);
            sceneElements.attachChild(p);
            if (p instanceof Prefab) {
                Prefab prefab = (Prefab) p;
                //prefab.connect(sceneElements);
                BulletAppState bas = this.stateManager.getState(BulletAppState.class);
                prefab.addPhysics(bas.getPhysicsSpace());
            }

            AddPrefabEdit edit = new AddPrefabEdit((Node) p);
            GlobalObjects.getInstance().addEdit(edit);

            LevelEvent le = new LevelEvent(this.level, LevelEvent.EventType.NODEADDED, (Node) p);
            GlobalObjects.getInstance().postEvent(le);

        }
        insertionElements.detachAllChildren();
        editorState = EditorState.IDLE;
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        int kc = evt.getKeyCode();
        if (evt.isPressed()) {
            //controlIsDown = (kc == KeyInput.KEY_LCONTROL || kc == KeyInput.KEY_RCONTROL);
            shiftIsDown = (kc == KeyInput.KEY_LSHIFT || kc == KeyInput.KEY_RSHIFT);
        } else {
            switch (kc) {
                case KeyInput.KEY_LSHIFT:
                case KeyInput.KEY_RSHIFT:
                    shiftIsDown = false;
                    break;
//                case KeyInput.KEY_LCONTROL:
//                case KeyInput.KEY_RCONTROL:
//                    controlIsDown = false;
//                    break;
            }
        }
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

    /**
     * Clears everything from the scene.
     */
    public void clearScene() {
        this.insertionElements.detachAllChildren();
        this.sceneElements.detachAllChildren();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (this.selectionFromOutside.size() > 0) {
            clearSelection();
            for (Node n : selectionFromOutside) {
                this.addToSelection(n);
            }
            selectionFromOutside.clear();
            editorState = EditorState.IDLE;
            activateIdleState();
        }

        if (editorState == EditorState.INSERTIONEVENT) {
            ObjectType objectTypeToCreate = objectCreationEvent.getObjectType();
            String name = createName(this.sceneElements, objectTypeToCreate.getLabel());

            Prefab p = objectTypeToCreate.create(assetManager, name);
            if (objectCreationEvent.hasComponents()) {
                for (PrefabComponent pc : objectCreationEvent.getComponents()) {
                    p.removeComponents(pc.getId());
                    p.addPrefabComponent(pc);
                }
            }
            if (p != null) {
                BulletAppState state = stateManager.getState(BulletAppState.class);
                state.getPhysicsSpace().addAll(p);
                insertionElement = p;
                this.insertionElements.attachChild(insertionElement);
                this.clearSelection();
                this.addToSelection(p);
                activateInsertionTool();
            }

            editorState = EditorState.IDLE;
        }


        if (editorState == EditorState.ADDTOSCENE) {
            addToScene();
        }

        if (editorState == EditorState.LEVELCHANGED) {
            if (this.level != null) {
                viewPort.clearProcessors();
                BulletAppState bas = this.getStateManager().getState(BulletAppState.class);
                if (this.sceneElements != null) {
                    sceneElements.removeFromParent();
                    bas.getPhysicsSpace().removeAll(sceneElements);
                }
                clearSelection();
                rootNode.attachChild(level);
                this.sceneElements = level;

                this.level.levelShown(this.assetManager, bas);
                editorState = EditorState.IDLE;
                level.updateGeometricState();
                bas.getPhysicsSpace().addAll(level);

                CameraFrame cf = level.getLastCamera();
                if (cf != null) {
                    this.cam.setLocation(cf.getTranslation().clone());
                    this.cam.setRotation(cf.getRotation().clone());
                    this.cam.setProjectionMatrix(cf.getProjectionMatrix().clone());
                }
            }
        }


        currentTool.simpleUpdate(tpf, this);


        // Gizmo part
        if (newGizmoType != gizmoType) {
            switchGizmo();
        }


        synchronized (viewportTasks) {
            for (Runnable tasks : viewportTasks) {
                tasks.run();
            }
            viewportTasks.clear();
        }

        adaptSelectionBox();
    }

    public String createName(Node base, String prefix) {
        int i = 0;
        Spatial spatial = null;
        do {
            ++i;
            spatial = base.getChild(prefix + i);

        } while (spatial != null);
        return prefix + i;
    }

    public Prefab findPrefabParent(Geometry g) {
        Node parent = g.getParent();
        while (!(parent instanceof Prefab) && parent != this.sceneElements && parent != null) {
            parent = parent.getParent();
        }
        if (parent != null && parent instanceof Prefab) {
            return (Prefab) parent;
        } else {
            return null;
        }
    }

    public Prefab pick() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        sceneElements.collideWith(ray, results);

        if (results.size() == 0) {
            return null;
        }

        // check if the axis is in there.
        for (Iterator<CollisionResult> it = results.iterator(); it.hasNext();) {
            CollisionResult cr = it.next();
            Geometry g = cr.getGeometry();
            if (hasGizmoParent(g)) {
                return null;
            }
        }

        // if (editorState == EditorState.IDLE || editorState == EditorState.LINK || editorState == EditorState.LINKPARENT || editorState == EditorState.PICK) {
        int index = 0;
        for (; index < results.size(); ++index) {
            Geometry g = results.getCollision(index).getGeometry();
            Boolean pickable = (Boolean) g.getUserData("Pickable");
            if (pickable == null || pickable == true) {
                break;
            }
        }
        if (index == results.size()) {
            return null;
        }

        Geometry g = results.getCollision(index).getGeometry();
        Node parent = g.getParent();
        boolean hasKlatchParent = false;
        boolean hasPrefabParent = false;

        Node check = parent;
        while (check != this.sceneElements) {
            if (check instanceof Prefab) {
                hasPrefabParent = true;
            }
            if (check instanceof Klatch) {
                hasKlatchParent = true;
            }
            check = check.getParent();
        }

        if (hasKlatchParent) {
            while (!(parent instanceof Klatch) && parent != this.sceneElements) {
                parent = parent.getParent();
            }
        } else if (hasPrefabParent) {
            while (!(parent instanceof Prefab) && parent != this.sceneElements) {
                parent = parent.getParent();
            }
        } else {
            return null;
        }

        return (Prefab) parent;
        /*
         if (parent != this.sceneElements) {
         if (editorState == EditorState.IDLE) {
         clearSelection();
         this.addToSelection(parent);
         } else if (editorState == EditorState.PICK) {
         PickEvent pe = new PickEvent((Prefab) parent, this, pickProperty);
         GlobalObjects.getInstance().postEvent(pe);
         clearSelection();
         this.newGizmoType = GizmoType.TRANSLATE;
         editorState = EditorState.IDLE;
         linkText.setText("");
         textBackground.setDimension(0, 0);
         textBackgroundGeometry.updateModelBound();
         linkGeometry.removeFromParent();
         wireBoxGeometryLinkParent.removeFromParent();
         currentChildElement = null;
         guiNode.detachChild(linkText);
         guiNode.detachChild(textBackgroundGeometry);
         } else if (editorState == EditorState.LINK) {
         this.currentChildElement = (Prefab) parent;
         addToSelection(currentChildElement);
         rootNode.attachChild(linkGeometry);
         editorState = EditorState.LINKPARENT;
         } else if (editorState == EditorState.LINKPARENT) {
         Prefab p = (Prefab) parent;
         if (p == currentChildElement) {
         return;
         }

         Vector3f wtrans = currentChildElement.getWorldTranslation().clone();
         Quaternion wrot = currentChildElement.getWorldRotation().clone();
         Vector3f wscale = currentChildElement.getWorldScale().clone();

         // child element is moved from one place to another.
         ProjectTreeNode previousParent = currentChildElement.getProjectParent();
         int previousIndex = previousParent.getIndexOfChild(currentChildElement);
         p.attachChild(currentChildElement);
         // it is possible that the attachment process attaches the child somewhere else.
         // express the world transformation of the child as a local transformation in the parent space.
         Node pn = currentChildElement.getParent();
         if (pn != null) {
         Matrix4f parentMatrix = new Matrix4f();
         parentMatrix.setTranslation(pn.getWorldTranslation());
         parentMatrix.setRotationQuaternion(pn.getWorldRotation());
         parentMatrix.setScale(pn.getWorldScale());
         parentMatrix.invertLocal();

         Matrix4f childMatrix = new Matrix4f();
         childMatrix.setTranslation(wtrans);
         childMatrix.setRotationQuaternion(wrot);
         childMatrix.setScale(wscale);

         Matrix4f local = parentMatrix.mult(childMatrix);

         currentChildElement.setLocalTranslation(local.toTranslationVector());
         currentChildElement.setLocalRotation(local.toRotationQuat());
         currentChildElement.setLocalScale(local.toScaleVector());
         }

         // remove child from its layer
         if (previousParent instanceof Layer) {
         Layer l = (Layer) previousParent;
         l.removeNode(currentChildElement);
         }

         LevelEvent le = new LevelEvent(level, EventType.NODEMOVED, currentChildElement, previousParent, previousIndex, pn);
         GlobalObjects.getInstance().postEvent(le);

         editorState = EditorState.IDLE;
         newGizmoType = GizmoType.TRANSLATE;
         linkText.setText("");
         textBackground.setDimension(0, 0);
         textBackgroundGeometry.updateModelBound();
         linkGeometry.removeFromParent();
         wireBoxGeometryLinkParent.removeFromParent();
         currentChildElement = null;
         guiNode.detachChild(linkText);
         guiNode.detachChild(textBackgroundGeometry);
         }
         }
         }
         */
    }

    private boolean hasGizmoParent(Geometry g) {
        Spatial parent = g;
        do {
            parent = parent.getParent();
            if (parent instanceof Axis || parent instanceof RotateGizmo) {
                return true;
            }
        } while (!(parent instanceof Prefab) && parent != this.sceneElements);
        return false;
    }

    public void addToSelection(Node node) {
        if ( node == null)
            return;
        currentSelection.add(node);

        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.setSelected(true);

            GlobalObjects.getInstance().postEvent(new SelectionEvent((Prefab) node, this));
        }
        currentTool.selectionChanged(this, node);
        adaptSelectionBox();
    }

    public void clearSelection() {
        for (Node n : currentSelection) {
            ((Prefab) n).setSelected(false);
        }
        currentSelection.clear();
        currentTool.removeGizmo();

        adaptSelectionBox();
    }

    public void pickGizmo() {

        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.5f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        currentTool.pickGizmo(ray, results);

        if (results.size() > 0) {
            Geometry g = results.getCollision(0).getGeometry();
            Vector3f contactPoint = results.getClosestCollision().getContactPoint();

            if (g.getUserData("Transform") != null) {
                currentTool.gizmoPicked(this, g, contactPoint);
            }
        }
    }

    private void setupTransform(Geometry g, Vector3f contactPoint) {
        flyCam.setEnabled(false);
        switch (gizmoType) {
            case TRANSLATE:
                setupTranslateTransform(g, contactPoint);
                break;
            case ROTATE:
                setupRotateTransform(g, contactPoint);
                break;
        }
    }

    private void setupTranslateTransform(Geometry g, Vector3f contactPoint) {
        /*
         String transform = g.getUserData("Transform");
         if ("translate_X".equals(transform)) {
         editorState = EditorState.TRANSLATEX;
         a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
         } else if ("translate_Y".equals(transform)) {
         editorState = EditorState.TRANSLATEY;
         a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis1);
         } else if ("translate_Z".equals(transform)) {
         editorState = EditorState.TRANSLATEZ;
         a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis1);
         } else if ("translate_XY".equals(transform)) {
         a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
         a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis2);
         editorState = EditorState.TRANSLATETWOAXIS;
         } else if ("translate_YZ".equals(transform)) {
         a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis1);
         a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis2);
         editorState = EditorState.TRANSLATETWOAXIS;
         } else if ("translate_XZ".equals(transform)) {
         a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
         a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis2);
         editorState = EditorState.TRANSLATETWOAXIS;
         }
         pickOffset = contactPoint.clone();
         */

        for (Node n : currentSelection) {
            n.setUserData("BaseTranslation", n.getLocalTranslation().clone());
        }

        // TODO : copy to TranslateTool class.
        if (shiftIsDown) {
            // create a copy
            ArrayList<Prefab> duplicates = new ArrayList<Prefab>();
            for (Node n : this.currentSelection) {
                if (n instanceof Prefab) {
                    Prefab copy = ((Prefab) n).duplicate(assetManager);
                    // copy will be added to sceneElements.
                    Quaternion q = n.getLocalRotation();
                    copy.setLocalPrefabRotation(q);
                    copy.setLocalPrefabTranslation(n.getLocalTranslation().clone());
                    //System.out.println("Copy created :" + copy);
                    duplicates.add(copy);
                    copy.setUserData("BaseTranslation", n.getLocalTranslation().clone());
                    copy.disablePhysics();
                    copy.setUserData("DuplicateParent", n.getParent());
                    copy.setName(this.createName(this.level, copy.getPrefix()));

                    BulletAppState bas = this.getStateManager().getState(BulletAppState.class);
                    copy.addPhysics(bas.getPhysicsSpace());
                }
            }
            clearSelection();
            for (Prefab p : duplicates) {
                Node parent = (Node) p.getUserData("DuplicateParent");
                if (parent != null) {
                    parent.attachChild(p);
                } else {
                    sceneElements.attachChild(p);
                }
                AddPrefabEdit edit = new AddPrefabEdit(p);
                GlobalObjects.getInstance().addEdit(edit);

                LevelEvent le = new LevelEvent(this.level, LevelEvent.EventType.NODEADDED, (Node) p);
                GlobalObjects.getInstance().postEvent(le);

                addToSelection(p);
            }
        } else {
            for (Node n : currentSelection) {
                ((Prefab) n).disablePhysics();
            }
        }
    }

    private void setupRotateTransform(Geometry g, Vector3f contactPoint) {
        /*
         String transform = g.getName();
         Vector3f cp = g.worldToLocal(contactPoint, null);
         if ("X".equals(transform)) {
         editorState = EditorState.ROTATEX;
         Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_X);
         rotationDir = cp.cross(axis);
         } else if ("Y".equals(transform)) {
         editorState = EditorState.ROTATEY;
         Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_Y);
         rotationDir = cp.cross(axis);
         } else if ("Z".equals(transform)) {
         editorState = EditorState.ROTATEZ;
         Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_Z);
         rotationDir = cp.cross(axis);
         }
         g.getWorldRotation().mult(rotationDir, rotationDir);
         rotationDir.normalizeLocal();

         pickOffset = contactPoint.clone();

         for (Node n : currentSelection) {
         n.setUserData("BaseRotation", n.getLocalRotation().clone());
         }
         */
    }

    /**
     * This function is called when a new object is crated.
     *
     * @param coe
     */
    @Subscribe
    public void onObjectCreation(CreateObjectEvent coe) {

        synchronized (this) {
            editorState = EditorState.INSERTIONEVENT;
            this.objectCreationEvent = coe;
            //creationTime = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onObjectInsertion(final InsertObjectEvent ioe) {
        submitViewportTask(new Runnable() {
            public void run() {
                Spatial s = assetManager.loadModel(ioe.getPath());
                s.setName(ioe.getName());
                s.setLocalTransform(ioe.getTransform());
                ioe.getParent().attachChild(s);
                LevelEvent le = new LevelEvent(ioe.getLevel(), EventType.NODEADDED, (Node) s);
                GlobalObjects.getInstance().postEvent(le);
                GlobalObjects.getInstance().addEdit(new AddPrefabEdit((Node) s));
            }
        });
    }

    @Subscribe()
    public void prefabSelected(SelectionEvent se) {
        if (se.getSource() != this) {
            this.selectionFromOutside.clear();
            if (!currentSelection.contains(se.getSelectedNode())) {
                selectionFromOutside.add(se.getSelectedNode());
            }

        }
    }

    @Subscribe
    public void setCurrentProject(ProjectEvent pe) {
        project = pe.getProject();
        for (File file : project.getAssetLocations()) {
            assetManager.registerLocator(file.getPath(), FileLocator.class);
        }
    }

    @Subscribe
    public void setCurrentLevel(final LevelEvent le) {
        if (le.getEventType() == EventType.LEVELSELECTED) {

            if (le.getLevel() != this.level) {
                if (this.level != null) {
                    this.level.levelHidden();
                }
                this.level = le.getLevel();
                editorState = EditorState.LEVELCHANGED;
            }
        } else if (le.getEventType() == EventType.NODEREMOVEREQUEST) {
            submitViewportTask(new Runnable() {
                public void run() {
                    for (Node n : le.getNodes()) {
                        removeNode(le.getLevel(), n);
                    }
                    clearSelection();
                }
            });
        }
    }

    private void removeNode(dae.project.Level level, Node n) {
        if (n instanceof Prefab) {
            Prefab p = (Prefab) n;
            // remove the physics
            BulletAppState bas = this.stateManager.getState(BulletAppState.class);
            bas.getPhysicsSpace().removeAll(p);
            ProjectTreeNode parent = p.getProjectParent();
            if (parent != null) {
                int childIndex = parent.getIndexOfChild(p);
                GlobalObjects.getInstance().addEdit(new DeletePrefabEdit(this.level, n));
                n.removeFromParent();
                LevelEvent le = LevelEvent.createNodeRemovedEvent(level, n, parent, childIndex);
                GlobalObjects.getInstance().postEvent(le);

            }

        }
    }

    @Subscribe
    public void editAsset(final AssetEvent ae) {
        if (ae.getAssetEventType() == AssetEventType.MODIFIED) {
            submitViewportTask(new Runnable() {
                public void run() {
                    reloadAsset(ae.getFileNode());
                }
            });
        }
    }

    private void reloadAsset(FileNode asset) {
        if (asset == null) {
            return;
        }
        if (assetManager instanceof DesktopAssetManager) {
            ModelKey mk = new ModelKey(asset.getFullName());
            DesktopAssetManager dt = (DesktopAssetManager) assetManager;
            dt.deleteFromCache(mk);
        }
        if (project != null) {
            String fullName = asset.getFullName();
            for (dae.project.Level l : project.getLevels()) {
                if (asset.getExtension().equals("j3o")) {
                    List<MeshObject> meshesToChange = l.descendantMatches(MeshObject.class);
                    for (MeshObject mo : meshesToChange) {
                        MeshComponent mc = (MeshComponent) mo.getComponent("MeshComponent");
                        if (mc.getMeshFile().equals(fullName)) {
                            mo.reloadMesh();
                        }
                    }
                } else if (asset.getExtension().equals("klatch")) {
                    List<Klatch> klatchesToChange = l.descendantMatches(Klatch.class);
                    for (Klatch k : klatchesToChange) {
                        if (k.getKlatchFile().equals(fullName)) {
                            Node parent = k.getParent();
                            Transform backup = k.getLocalTransform();
                            k.removeFromParent();

                            Klatch newversion = (Klatch) this.assetManager.loadModel(k.getKlatchFile());
                            newversion.setLocalTransform(backup);
                            parent.attachChild(newversion);
                            for (Spatial s : k.getChildren()) {
                                Object klatchpart = s.getUserData("klatchpart");
                                if (klatchpart != Boolean.TRUE) {
                                    newversion.attachChild(s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void setCurrentGizmo(GizmoEvent ge) {
        if (ge.getSource() != this) {
            this.newGizmoType = ge.getType();
            if (ge.getType() == GizmoType.PICK) {
                this.pickTool.setPickProperty(ge.getPickProperty());
            }
        }
    }

    public Node getFirstSelectedNode() {
        return currentSelection.size() > 0 ? currentSelection.get(0) : null;
    }

    private void switchGizmo() {
        gizmoType = newGizmoType;
        Node n = null;

        if (currentTool != null) {
            currentTool.deactivate(this);
        }

        if (currentSelection.size() > 0) {
            n = this.currentSelection.get(0);
        }
        switch (gizmoType) {
            case TRANSLATE:
                currentTool = translateTool;
                break;
            case ROTATE:
                currentTool = rotateTool;
                break;
            case LINK:
                currentTool = linkTool;
                break;
            case PICK:
                currentTool = pickTool;
                break;
            case BRUSH:
                currentTool = brushTool;
                break;
        }
        currentTool.activate(this);

        GizmoEvent gce = new GizmoEvent(this, gizmoType);
        GlobalObjects.getInstance().postEvent(gce);
    }

    @Subscribe
    public void doZoomEvent(ZoomEvent ze) {
        switch (ze.getEventType()) {
            case EXTENTS_SELECTED:
                break;
            case LEVEL:
                break;
        }
    }

    @Subscribe
    public void shadowChanged(final ShadowEvent se) {
        synchronized (viewportTasks) {
            viewportTasks.add(new Runnable() {
                public void run() {
                    Prefab p = (Prefab) se.getSource();
                    if (p.hasAncestor(sceneElements)) {
                        enableShadowRenderer(se.getSource().getShadowRenderer(), se.getSource().getCastShadow());
                    }
                }
            });
        }
    }

    @Subscribe
    public void addComponent(final ComponentEvent event) {
        synchronized (viewportTasks) {
            viewportTasks.add(new Runnable() {
                public void run() {
                    for (Node n : currentSelection) {
                        if (n instanceof Prefab) {
                            Prefab prefab = (Prefab) n;
                            ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(event.getComponentId());
                            if (ct != null) {
                                PrefabComponent pc = ct.create();
                                prefab.addPrefabComponent(pc);
                            }
                        }
                    }
                }
            });
        }
    }

    private void enableShadowRenderer(AbstractShadowRenderer renderer, boolean enable) {
        if (enable) {
            this.viewPort.addProcessor(renderer);
        } else {
            this.viewPort.removeProcessor(renderer);
        }
    }

    private void zoomExtentsSelected() {
    }

    public void submitViewportTask(Runnable r) {
        synchronized (viewportTasks) {
            viewportTasks.add(r);
        }
    }

    @Subscribe
    public void autoGridStateChanged(final AutoGridEvent age) {
        submitViewportTask(new Runnable() {
            public void run() {
                autoGridEnabled = age.isAutoGridEnabled();
                autoGridAxis = age.getMainAxis();
            }
        });
    }

    public void setFlyByCamera(FlyByCamera flyCam) {
        this.flyCam = flyCam;
    }

    public void setLocationOnPrefab(Prefab prefab, Vector3f translation, boolean undoable) {
        ObjectType oType = prefab.getObjectType();
        if (oType != null) {
            Parameter ptrans = oType.findParameter("TransformComponent", "translation");
            if (ptrans != null) {
                ptrans.invokeSet(prefab, translation, undoable);
            }
        }
    }

    @Subscribe
    public void playOrPauseLevel(PlayEvent event) {
        switch (event.getType()) {
            case PLAY:
                try {
                    // get the asset directories, export the current level to one 
                    // of the asset directories and start the application.
                    this.submitViewportTask(new Runnable() {
                        public void run() {
                            GameBuilder gb = new GameBuilder();
                            gb.startGame(project, level);
                        }
                    });
                    GameWriter.writeGame(project, level);


                } catch (IOException ex) {
                    Logger.getLogger(SandboxViewport.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }

    public void activateIdleState() {
        if (currentTool != null) {
            currentTool.deactivate(this);
        }
        currentTool = idleTool;
        idleTool.activate(this);
        GizmoEvent ge = new GizmoEvent(this,GizmoType.NONE);
        GlobalObjects.getInstance().postEvent(ge);
    }

    private void activateInsertionTool() {
        if (currentTool != null) {
            currentTool.deactivate(this);
        }
        currentTool = insertionTool;
        insertionTool.activate(this);
    }

    public void clearInsertionElements() {
        insertionElements.detachAllChildren();
    }

    public Prefab getInsertionElement() {
        return insertionElement;
    }
}
