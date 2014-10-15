/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.font.BitmapText;
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
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import com.jme3.shadow.AbstractShadowRenderer;
import dae.DAECamAppState;
import dae.GlobalObjects;
import dae.animation.rig.io.RigLoader;
import dae.animation.skeleton.BodyLoader;
import dae.controller.ControllerLoader;

import dae.io.ObjectTypeReader;
import dae.io.SceneLoader;
import dae.math.RayIntersect;
import dae.prefabs.AxisEnum;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.gizmos.RotateGizmo;
import dae.prefabs.gizmos.events.AutoGridEvent;
import dae.prefabs.magnets.FillerParameter;
import dae.prefabs.magnets.Magnet;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.prefab.undo.AddPrefabEdit;
import dae.prefabs.prefab.undo.DeletePrefabEdit;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
import dae.prefabs.shapes.LineShape;
import dae.prefabs.shapes.QuadShape;
import dae.prefabs.standard.CameraFrame;
import dae.prefabs.standard.MagnetObject;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.ui.classpath.FileNode;
import dae.prefabs.ui.events.AssetEvent;
import dae.prefabs.ui.events.AssetEventType;
import dae.prefabs.ui.events.CreateObjectEvent;
import dae.prefabs.ui.events.GizmoEvent;
import dae.prefabs.ui.events.GizmoType;
import static dae.prefabs.ui.events.GizmoType.ROTATE;
import static dae.prefabs.ui.events.GizmoType.TRANSLATE;
import dae.prefabs.ui.events.InsertObjectEvent;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.prefabs.ui.events.PickEvent;
import dae.prefabs.ui.events.ProjectEvent;
import dae.prefabs.ui.events.SelectionEvent;
import dae.prefabs.ui.events.ShadowEvent;
import dae.prefabs.ui.events.ViewportReshapeEvent;
import dae.prefabs.ui.events.ZoomEvent;
import dae.project.Grid;
import dae.project.Layer;
import dae.project.Project;
import dae.project.ProjectTreeNode;
import java.io.File;
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
     * The selection material for the sandbox
     */
    private Material selectionMaterial = new Material();
    /**
     * The translation axis.
     */
    private Axis a;
    /**
     * The rotate gizmo
     */
    private RotateGizmo r;
    /**
     * The elements to insert.
     */
    private Node insertionElements = new Node("insertion");
    /**
     * The current gizmo type
     */
    private GizmoType gizmoType = GizmoType.TRANSLATE;
    private GizmoType newGizmoType = GizmoType.TRANSLATE;
    /**
     * The currently picked element.
     */
    private Prefab currentPickElement;
    /**
     * The current pick property.
     */
    private String pickProperty;
    /**
     * The current child element for linking.
     */
    private Prefab currentChildElement;

    /**
     * The editor state.
     */
    private enum EditorState {

        IDLE, INSERTIONEVENT, INSERTION, ADDTOSCENE, LEVELCHANGED, SELECTION, TRANSLATEX, TRANSLATEY, TRANSLATEZ, ROTATEX, ROTATEY, ROTATEZ, MOUSEMOVE, LINK, LINKPARENT, PICK, TRANSLATETWOAXIS
    };
    /**
     * The current editor state.
     */
    private EditorState editorState = EditorState.IDLE;
    /**
     * Shift key is used for copying of items
     */
    private boolean shiftIsDown = false;
    /**
     * helper objects for translation
     */
    private Vector3f pickOffset = new Vector3f();
    private Vector3f pickAxis1 = new Vector3f();
    private Vector3f pickAxis2 = new Vector3f();
    /**
     * helper objects for rotation
     */
    private Vector3f rotationDir = new Vector3f();
    private ArrayList<Node> currentSelection = new ArrayList<Node>();
    private Node currentInsertion;
    private ObjectType objectTypeToCreate;
    private int objectId = 0;
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
     * Selection from other thread (such as user interface.
     */
    private ArrayList<Node> selectionFromOutside = new ArrayList<Node>();
    /**
     * shows the selected object.
     */
    private WireBox wireBox = new WireBox();
    private Geometry wireBoxGeometry;
    private Material wireBoxMaterial;
    /**
     * Used for linking objects.
     */
    private Geometry linkGeometry;
    private LineShape linkShape;
    private BitmapText linkText;
    private QuadShape textBackground;
    private Geometry textBackgroundGeometry;
    /**
     * Used to highlight the parent
     */
    private WireBox wireBoxLinkParent = new WireBox();
    private Geometry wireBoxGeometryLinkParent;
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


        objectsToCreate = (ObjectTypeCategory) assetManager.loadAsset("Objects/ObjectTypes.types");
        selectionMaterial = assetManager.loadMaterial("Materials/SelectionMaterial.j3m");
        setPauseOnLostFocus(false);

        Material gridMaterial = assetManager.loadMaterial("Materials/GridMaterial.j3m");
        Grid grid = new Grid(10, 10, gridMaterial);
        //grid.setUpAxis(AxisEnum.Y);
        a = new Axis(assetManager, 3, 0.15f);
        r = new RotateGizmo(assetManager, 3, 0.15f);

        sceneElements.attachChild(grid);
        rootNode.attachChild(sceneElements);
        rootNode.attachChild(insertionElements);

        rootNode.attachChild(a);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.25f));
        rootNode.addLight(al);

        AmbientLight insertionLight = new AmbientLight();
        insertionLight.setColor(ColorRGBA.White.mult(5.0f));
        insertionElements.addLight(insertionLight);



        GlobalObjects.getInstance().setObjectTypeCategory(objectsToCreate);
        GlobalObjects.getInstance().registerListener(this);

        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.setLocation(new Vector3f(5, 2, 0));
        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

        initKeys();

//        this.helperSphere1 = createDebugSphere(this.assetManager, 0.02f, ColorRGBA.White);
//        this.helperSphere2 = createDebugSphere(this.assetManager, 0.02f, ColorRGBA.Red);
//        this.helperSphere3 = createDebugSphere(this.assetManager, 0.02f, ColorRGBA.Green);

//        cursorConnect1 = (JmeCursor) assetManager.loadAsset("Interface/connect1.ico");
//        cursorConnect2 = (JmeCursor) assetManager.loadAsset("Interface/connect2.ico");
//        cursorMove = (JmeCursor) assetManager.loadAsset("Interface/move.ico");
//        cursorSelect = (JmeCursor) assetManager.loadAsset("Interface/select.ico");
//        cursorRotate = (JmeCursor)assetManager.loadAsset("Interface/rotate.ico");
//        cursorScale = (JmeCursor)assetManager.loadAsset("Interface/scale.ico");

        BulletAppState bulletAppState = new BulletAppState();

        stateManager.attach(bulletAppState);





        GlobalObjects.getInstance().setAssetManager(assetManager);
        GlobalObjects.getInstance().setInputManager(this.inputManager);


        wireBoxMaterial = assetManager.loadMaterial("Materials/SelectionBoxMaterial.j3m");
        wireBoxGeometry = new Geometry("wireframe cube", wireBox);
        wireBoxGeometry.setMaterial(wireBoxMaterial);

        rootNode.attachChild(wireBoxGeometry);

        // helper objects for linking
        linkShape = new LineShape(Vector3f.ZERO, Vector3f.ZERO);
        linkShape.setLineWidth(5.0f);

        linkGeometry = new Geometry("link", linkShape);

        linkGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkMaterial.j3m"));
        wireBoxGeometryLinkParent = new Geometry("linked parent", wireBoxLinkParent);
        wireBoxGeometryLinkParent.setMaterial(assetManager.loadMaterial("Materials/LinkParentMaterial.j3m"));

        //guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        linkText = new BitmapText(guiFont, false);
        linkText.setSize(guiFont.getCharSet().getRenderedSize());
        linkText.setText("");

        this.textBackground = new QuadShape(1, 1);
        this.textBackgroundGeometry = new Geometry("linktext", textBackground);
        textBackgroundGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkTextBackgroundMaterial.j3m"));
    }

    public void adaptSelectionBox() {
        Node parent = null;
        switch (this.gizmoType) {
            case TRANSLATE:
                parent = a.getParent();
                a.removeFromParent();
                break;
            case ROTATE:
                parent = r.getParent();
                r.removeFromParent();
        }
        BoundingVolume bv = null;
        for (Node n : this.currentSelection) {
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
    }

    private Geometry createDebugSphere(AssetManager assetManager, float size, ColorRGBA color) {
        Geometry g = new Geometry("wireframe sphere", new WireSphere(size));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        //g.setLocalTranslation(pos);
        rootNode.attachChild(g);
        return g;
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
                if (objectTypeToCreate != null) {
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
            if (editorState == EditorState.INSERTION) {
                if ("ACCEPT_INSERTION".equals(name)) {
                    clearSelection();
                    addToScene();
                } else if ("REJECT_INSERTION".equals(name)) {
                    clearSelection();
                    linkGeometry.removeFromParent();
                    wireBoxGeometryLinkParent.removeFromParent();
                    insertionElements.detachAllChildren();
                    editorState = EditorState.IDLE;
                } else if ("ROTATION_UP".equals(name)) {
                    if (currentPickElement != null) {
                        currentPickElement.nextRotationValue();
                    }
                } else if ("ROTATION_DOWN".equals(name)) {
                    if (currentPickElement != null) {
                        currentPickElement.previousRotationValue();
                    }
                }
            } else if (editorState == EditorState.IDLE) {
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

    private Material createAlphaMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.setTransparent(true);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        return mat;
    }

    private Material createColorMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
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
        if (editorState == EditorState.TRANSLATEX
                || editorState == EditorState.TRANSLATEY
                || editorState == EditorState.TRANSLATEZ
                || editorState == EditorState.TRANSLATETWOAXIS) {
            Vector2f screenCoords = new Vector2f(evt.getX(), evt.getY());
            Vector3f p1 = cam.getWorldCoordinates(screenCoords, 0.01f);
            Vector3f p2 = cam.getWorldCoordinates(screenCoords, 0.95f);


            for (Node n : currentSelection) {
                Vector3f oldValue = n.getLocalTranslation().clone();

                Vector3f pa = new Vector3f();
                Vector3f pb = new Vector3f();
                Vector2f t = new Vector2f();

                Vector3f diff1 = Vector3f.ZERO;


                pb.set(0, 0, 0);
                if (editorState == EditorState.TRANSLATETWOAXIS) {
                    Vector3f normal = pickAxis1.cross(pickAxis2);
                    float u = normal.dot(pickOffset.subtract(p1)) / normal.dot(p2.subtract(p1));

                    Vector3f loc = p1.add(p2.subtract(p1).mult(u));
                    Vector3f b1 = n.getParent().worldToLocal(pickOffset, null);
                    Vector3f b2 = n.getParent().worldToLocal(loc, null);
                    diff1 = b2.subtract(b1);
                } else {
                    if (RayIntersect.rayIntersect(p1, p2, pickOffset, pickOffset.add(pickAxis1), pa, pb, t)) {
                        Vector3f b1 = n.getParent().worldToLocal(pickOffset, null);
                        Vector3f b2 = n.getParent().worldToLocal(pb, null);
                        diff1 = b2.subtract(b1);
                        //helperSphere2.setLocalTranslation(pb);
                    }
                }

                Vector3f baseTranslation = n.getUserData("BaseTranslation");
                //n.getLocalScale().mult(diff,diff);
                if (n instanceof Prefab) {
                    ((Prefab) n).setLocalPrefabTranslation(baseTranslation.add(diff1));
                } else {
                    n.setLocalTranslation(baseTranslation.add(diff1));
                }
                Vector3f newValue = n.getLocalTranslation().clone();
                UndoPrefabPropertyEdit edit = new UndoPrefabPropertyEdit(n, "localPrefabTranslation", oldValue, newValue);
                GlobalObjects.getInstance().addEdit(edit);
            }
        } else if (editorState == EditorState.ROTATEX || editorState == EditorState.ROTATEY || editorState == EditorState.ROTATEZ) {
            Vector2f screenCoords = new Vector2f(evt.getX(), evt.getY());
            Vector3f p1 = cam.getWorldCoordinates(screenCoords, 0.01f);
            Vector3f p2 = cam.getWorldCoordinates(screenCoords, 0.95f);
            Vector3f pa = new Vector3f();
            Vector3f pb = new Vector3f();
            Vector2f t = new Vector2f();
            if (RayIntersect.rayIntersect(p1, p2, pickOffset, pickOffset.add(rotationDir), pa, pb, t)) {

                float projectedLength = pb.distance(pickOffset) * 100.0f;
                if (pb.subtract(pickOffset).dot(rotationDir) > 0) {
                    projectedLength = -projectedLength;
                }

                Vector3f axis = null;
                switch (editorState) {
                    case ROTATEX:
                        axis = Vector3f.UNIT_X;
                        break;
                    case ROTATEY:
                        axis = Vector3f.UNIT_Y;
                        break;
                    case ROTATEZ:
                        axis = Vector3f.UNIT_Z;
                        break;
                }
                Quaternion q = new Quaternion();
                q.fromAngleAxis(projectedLength * FastMath.DEG_TO_RAD, axis);

                for (Node n : currentSelection) {
                    Quaternion base = n.getUserData("BaseRotation");
                    ((Prefab) n).setLocalPrefabRotation(base.mult(q));
                }
            }
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        if (evt.isReleased()) {
            handleMouseReleased();
        } else if (evt.isPressed()) {
            if (editorState == EditorState.IDLE) {
                //System.out.println("picking");
                pick();
                pickGizmo();
            } else if (editorState == EditorState.LINK || editorState == EditorState.LINKPARENT) {
                pick();
            } else if (editorState == EditorState.PICK) {
                pick();
            }
        }
    }

    private void handleMouseReleased() {
        if (editorState == EditorState.TRANSLATEX
                || editorState == EditorState.TRANSLATEY
                || editorState == EditorState.TRANSLATEZ
                || editorState == EditorState.TRANSLATETWOAXIS
                || editorState == EditorState.ROTATEX || editorState == EditorState.ROTATEY || editorState == EditorState.ROTATEZ) {

            // re-enable physics
            for (Node n : this.currentSelection) {
                ((Prefab) n).enablePhysics();
            }
            editorState = EditorState.IDLE;
            flyCam.setEnabled(true);
        }
    }

    private void addToScene() {
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
        }

        if (editorState == EditorState.INSERTIONEVENT) {
            // wait until the mouse is back in a normal state.
            try {
                Prefab p;
                if (objectTypeToCreate.usesDefaultLoader()) {
                    ModelKey mk = new ModelKey(objectTypeToCreate.getExtraInfo());
                    p = (Prefab) assetManager.loadAsset(mk);
                    p.setName(createName(this.sceneElements, objectTypeToCreate.getLabel()));
                } else {
                    p = (Prefab) Class.forName(objectTypeToCreate.getObjectToCreate()).newInstance();
                    p.create(createName(this.sceneElements, objectTypeToCreate.getLabel()), assetManager, objectTypeToCreate.getExtraInfo());
                }

                p.setType(objectTypeToCreate.getLabel());
                p.setCategory(objectTypeToCreate.getCategory());
                p.notifyLoaded();
                insertionElements.attachChild((Node) p);
                currentInsertion = (Node) p;

                MagnetParameter mp = (MagnetParameter) objectTypeToCreate.findParameter("magnets");
                p.setMagnets(mp);

                FillerParameter fp = (FillerParameter) objectTypeToCreate.findParameter("filler");
                p.setFillers(fp);

                BulletAppState state = stateManager.getState(BulletAppState.class);
                state.getPhysicsSpace().addAll(p);
                this.addToSelection(p);

                editorState = EditorState.INSERTION;
            } catch (IllegalAccessException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
        if (editorState == EditorState.INSERTION) {
            //ystem.out.println("Doing insertion");
            doInsertion();
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

                bas.getPhysicsSpace().addAll(level);

                // for nodes that need the physics space to create physics controls.


                CameraFrame cf = level.getLastCamera();
                if (cf != null) {
                    this.cam.setLocation(cf.getTranslation().clone());
                    this.cam.setRotation(cf.getRotation().clone());
                    this.cam.setProjectionMatrix(cf.getProjectionMatrix().clone());
                }
            }
        }

        if (editorState == EditorState.LINKPARENT) {
            Vector3f from = this.currentChildElement.getWorldTranslation();
            Vector3f to = this.doLink();
            linkShape.setLine(from, to);
        }

        // Gizmo part
        if (newGizmoType != gizmoType) {
            switchGizmo();
        }

        // Adjust gizmo scale
        float d = cam.getLocation().distance(a.getWorldTranslation());
        float rd = (d - cam.getFrustumNear()) / (cam.getFrustumFar() - cam.getFrustumNear());
        Vector3f ws = a.getParent() != null ? a.getParent().getWorldScale() : Vector3f.UNIT_XYZ;
        switch (gizmoType) {
            case TRANSLATE:
                float factor = 1 * (0.1f + rd * 10);
                a.setLocalScale(factor / ws.x, factor / ws.y, factor / ws.z);
                break;
            case ROTATE:
                float factorr = 1 * (0.1f + rd * 10);
                r.setLocalScale(factorr / ws.x, factorr / ws.y, factorr / ws.z);
                break;
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
    private Triangle contactTriangle = new Triangle();

    private void doInsertion() {
        // Reset results list.
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.

        sceneElements.collideWith(ray, results);

        CollisionResult result = results.getClosestCollision();
        if (result == null) {
            return;
        }

        Geometry g = result.getGeometry();
        Prefab prefab = findPrefabParent(g);
        this.currentPickElement = prefab;

        Vector3f point = result.getContactPoint().clone();
        result.getTriangle(contactTriangle);

        Quaternion rotation = Quaternion.IDENTITY;
        if (autoGridEnabled) {
            rotation = this.createRotationFromNormal(result.getContactNormal(), this.autoGridAxis);
        }

        if (g.getParent() instanceof MagnetObject) {
            MagnetObject mo = (MagnetObject) g.getParent();
            Magnet m = mo.getMagnet();
            Vector3f magnetLoc = m.getLocation();
            // there should be a prefab parent.
            Vector3f magnetLocWorld = new Vector3f();
            if (prefab != null) {
                mo.localToWorld(magnetLoc, magnetLocWorld);
                Quaternion moLocalRot = g.getWorldRotation();

                currentInsertion.setLocalRotation(moLocalRot);
                currentInsertion.setLocalTranslation(magnetLocWorld);
            }
        } else if (prefab == null) {
            if (currentInsertion instanceof Prefab) {
                ((Prefab) currentInsertion).setLocalPrefabTranslation(point);
                ((Prefab) currentInsertion).setLocalPrefabRotation(rotation);
            } else {
                currentInsertion.setLocalTranslation(point);
                currentInsertion.setLocalRotation(rotation);
            }

        } else {
            if (prefab.hasMagnets()) {
                float distance = Float.MAX_VALUE;
                Magnet closestMagnet = null;
                Vector3f worldLoc = new Vector3f();
                for (Magnet m : prefab.getMagnets()) {
                    if (m.hasMagnetArea()) {
                        Vector3f local = new Vector3f();
                        prefab.worldToLocal(point, local);
                        local.addLocal(prefab.getOffset());
                        //System.out.println("Local point : " + local);
                        if (m.isInside(local)) {
                            closestMagnet = m;
                            break;
                        }
                    } else {
                        float magnetDistance = m.calcDistance(point, prefab);
                        if (magnetDistance < distance) {
                            closestMagnet = m;
                            distance = magnetDistance;
                        }
                    }
                }
                if (closestMagnet != null) {
                    if (closestMagnet.hasSelectPivot()) {
                        if (currentInsertion instanceof Prefab) {
                            Prefab p = (Prefab) currentInsertion;
                            p.selectPivot(closestMagnet.getSelectPivot());
                        }

                    }
                    prefab.setAttachMagnet(closestMagnet);

                    Matrix3f rot = closestMagnet.getLocalFrame();
                    Quaternion world = prefab.getWorldRotation();
                    Quaternion local = new Quaternion();
                    local.fromRotationMatrix(rot);
                    Quaternion insertRotation = local.mult(world);

                    Vector3f magnetLoc = closestMagnet.getLocation();
                    Vector3f offsetLoc = magnetLoc.subtract(prefab.getOffset());
                    prefab.localToWorld(offsetLoc, worldLoc);
                    if (currentInsertion instanceof Prefab) {

                        Prefab p = (Prefab) currentInsertion;
                        p.setLocalPrefabTranslation(point.subtract(p.getPivot()));
                        if (closestMagnet.hasLocalFrame()) {
                            p.setLocalPrefabRotation(insertRotation);
                        } else {
                            p.setLocalPrefabRotation(rotation);
                        }

                    } else {
                        currentInsertion.setLocalTranslation(point);
                        if (closestMagnet.hasLocalFrame()) {
                            currentInsertion.setLocalRotation(insertRotation);
                        } else {
                            currentInsertion.setLocalRotation(rotation);
                        }
                    }
                }

            } else {
                if (currentInsertion instanceof Prefab) {
                    Prefab p = (Prefab) currentInsertion;
                    p.setLocalPrefabTranslation(point.subtract(p.getPivot()));
                    p.setLocalPrefabRotation(rotation);
                } else {
                    currentInsertion.setLocalTranslation(point);
                    currentInsertion.setLocalRotation(rotation);
                }

            }

        }
    }

    private Quaternion createRotationFromNormal(Vector3f normal, AxisEnum mainAxis) {
        Vector3f x, y;
        Quaternion result = new Quaternion();
        if (normal.x > normal.y && normal.x > normal.z) {
            x = normal.cross(Vector3f.UNIT_Y);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();

        } else if (normal.y > normal.x && normal.y > normal.z) {
            x = normal.cross(Vector3f.UNIT_Z);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();
        } else {
            x = normal.cross(Vector3f.UNIT_X);
            x.normalizeLocal();
            y = normal.cross(x);
            y.normalizeLocal();
        }

        switch (mainAxis) {
            case X:
                result.fromAxes(normal, x, y);
                break;
            case Y:
                result.fromAxes(y, normal, x);
                break;
            case Z:
                result.fromAxes(x, y, normal);
                break;
        }
        return result;
    }

    private Vector3f doLink() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.

        sceneElements.collideWith(ray, results);

        CollisionResult result = results.getClosestCollision();
        if (result == null) {
            return cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.5f).clone();
        }

        Geometry g = result.getGeometry();
        Prefab prefab = findPrefabParent(g);

        if (prefab != null) {
            prefab.updateModelBound();
            BoundingVolume bv = prefab.getWorldBound();
            if (bv.getType() == Type.AABB) {
                BoundingBox bb = (BoundingBox) bv;
                wireBoxLinkParent.fromBoundingBox(bb);
                wireBoxGeometryLinkParent.setLocalTranslation(bb.getCenter().clone());
                if (wireBoxGeometryLinkParent.getParent() == null) {
                    rootNode.attachChild(wireBoxGeometryLinkParent);
                }
            }
            linkText.setText(currentChildElement.getName() + "->" + prefab.getName());
            linkText.setLocalTranslation(click2d.x, click2d.y, 0.01f);
            textBackground.setDimension(linkText.getLineWidth() + 2, linkText.getLineHeight() + 4);
            textBackgroundGeometry.updateModelBound();
            textBackgroundGeometry.setLocalTranslation(click2d.x - 1, click2d.y - linkText.getLineHeight() - 2, 0f);
            return result.getContactPoint();
        } else {
            wireBoxGeometryLinkParent.removeFromParent();
            return result.getContactPoint();
        }
    }

    private Prefab findPrefabParent(Geometry g) {
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

    private void pick() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        sceneElements.collideWith(ray, results);

        if ( results.size() == 0){
            return;
        }
        
        // check if the axis is in there.
        for (Iterator<CollisionResult> it = results.iterator(); it.hasNext();) {
            CollisionResult cr = it.next();
            Geometry g = cr.getGeometry();
            if (hasGizmoParent(g)) {
                return;
            }
        }

        if (editorState == EditorState.IDLE || editorState == EditorState.LINK || editorState == EditorState.LINKPARENT || editorState == EditorState.PICK) {
            int index = 0;
            for (; index < results.size(); ++index) {
                Geometry g = results.getCollision(index).getGeometry();
                Boolean pickable = (Boolean) g.getUserData("Pickable");
                if (pickable == null || pickable == true) {
                    break;
                }
            }
            if (index == results.size()) {
                return;
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
                return;
            }

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
                    // express the world transformation of the child as a local transformation in the parent space.
                    Matrix4f parentMatrix = new Matrix4f();
                    parentMatrix.setTranslation(p.getWorldTranslation());
                    parentMatrix.setRotationQuaternion(p.getWorldRotation());
                    parentMatrix.setScale(p.getWorldScale());
                    parentMatrix.invertLocal();

                    Matrix4f childMatrix = new Matrix4f();
                    childMatrix.setTranslation(currentChildElement.getWorldTranslation());
                    childMatrix.setRotationQuaternion(currentChildElement.getWorldRotation());
                    childMatrix.setScale(currentChildElement.getWorldScale());

                    Matrix4f local = parentMatrix.mult(childMatrix);

                    currentChildElement.setLocalTranslation(local.toTranslationVector());
                    currentChildElement.setLocalRotation(local.toRotationQuat());
                    currentChildElement.setLocalScale(local.toScaleVector());

                    // child element is moved from one place to another.
                    ProjectTreeNode previousParent = currentChildElement.getProjectParent();
                    int previousIndex = previousParent.getIndexOfChild(currentChildElement);
                    p.attachChild(currentChildElement);
                    // remove child from its layer
                    if (previousParent instanceof Layer) {
                        Layer l = (Layer) previousParent;
                        l.removeNode(currentChildElement);
                    }

                    LevelEvent le = new LevelEvent(level, EventType.NODEMOVED, currentChildElement, previousParent, previousIndex, p);
                    GlobalObjects.getInstance().postEvent(le);

                    editorState = EditorState.IDLE;
                    newGizmoType = GizmoType.TRANSLATE;
                    linkGeometry.removeFromParent();
                    wireBoxGeometryLinkParent.removeFromParent();

                    guiNode.detachChild(linkText);
                    guiNode.detachChild(textBackgroundGeometry);
                }
            }
        }
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
        currentSelection.add(node);

        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.setSelected(true);

            GlobalObjects.getInstance().postEvent(new SelectionEvent((Prefab) node, this));
        }
        if (gizmoType == GizmoType.TRANSLATE) {
            node.attachChild(a);
        } else if (gizmoType == GizmoType.ROTATE) {
            node.attachChild(r);
        }

        adaptSelectionBox();
    }

    public void clearSelection() {
        for (Node n : currentSelection) {
            ((Prefab) n).setSelected(false);
        }
        currentSelection.clear();
        a.removeFromParent();
        r.removeFromParent();

        adaptSelectionBox();
    }

    private void pickGizmo() {
        if (editorState == EditorState.IDLE) {
            CollisionResults results = new CollisionResults();
            // Convert screen click to 3d position
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.5f).subtractLocal(click3d);
            // Aim the ray from the clicked spot forwards.
            Ray ray = new Ray(click3d, dir);
            // Collect intersections between ray and all nodes in results list.
            switch (gizmoType) {
                case TRANSLATE:
                    this.a.collideWith(ray, results);
                    break;
                case ROTATE:
                    r.collideWith(ray, results);
                    break;
            }


            if (results.size() > 1) {
                Geometry g = results.getCollision(0).getGeometry();
                Vector3f contactPoint = results.getClosestCollision().getContactPoint();

                if (g.getUserData("Transform") != null) {
                    setupTransform(g, contactPoint);
                }
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

        for (Node n : currentSelection) {
            n.setUserData("BaseTranslation", n.getLocalTranslation().clone());
        }

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
    }

    /**
     * This function is called when a new object is crated.
     *
     * @param coe
     */
    @Subscribe
    public void onObjectCreation(CreateObjectEvent coe) {

        synchronized (this) {

            insertionElements.detachAllChildren();
            editorState = EditorState.INSERTIONEVENT;
            this.objectTypeToCreate = coe.getObjectType();

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
                        if (mo.getMeshFile().equals(fullName)) {
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
                this.pickProperty = ge.getPickProperty();
            }
        }
    }

    private void switchGizmo() {

        gizmoType = newGizmoType;
        Node n = null;
        if (currentSelection.size() > 0) {
            a.removeFromParent();
            r.removeFromParent();
            n = this.currentSelection.get(0);
        }
        switch (gizmoType) {
            case TRANSLATE:
//                    inputManager.setMouseCursor(cursorMove);
                editorState = EditorState.IDLE;
                if (n != null) {
                    n.attachChild(a);
                }
                break;
            case ROTATE:
//                    inputManager.setMouseCursor(cursorRotate);
                editorState = EditorState.IDLE;
                if (n != null) {
                    n.attachChild(r);
                }
                break;
            case LINK:
                clearSelection();
//                    inputManager.setMouseCursor(cursorConnect1);
                editorState = EditorState.LINK;
                guiNode.attachChild(linkText);
                guiNode.attachChild(textBackgroundGeometry);
                break;
            case PICK:
//                    inputManager.setMouseCursor(cursorSelect);
                editorState = EditorState.PICK;
                break;
        }

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
}
