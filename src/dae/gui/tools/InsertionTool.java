package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.gui.SandboxViewport;
import dae.prefabs.Prefab;
import dae.prefabs.magnets.Magnet;
import dae.prefabs.shapes.QuadShape;
import dae.prefabs.standard.MagnetObject;
import dae.util.MathUtil;

/**
 * This tool inserts an object into the scene.
 *
 * @author Koen Samyn
 */
public class InsertionTool extends ViewportTool {

    private SandboxViewport viewport;
    private BitmapText linkText;
    private QuadShape textBackground;
    private Geometry textBackgroundGeometry;
    private Prefab currentPickElement;
    private Node currentInsertion;
    private Triangle contactTriangle = new Triangle();

    public InsertionTool() {
        super("InsertionTool");
    }

    @Override
    public void initialize(AssetManager assetManager, InputManager inputManager) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        linkText = new BitmapText(guiFont, false);
        linkText.setSize(guiFont.getCharSet().getRenderedSize());
        linkText.setText("");

        textBackground = new QuadShape(1, 1);
        textBackgroundGeometry = new Geometry("linktext", textBackground);
        textBackgroundGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkTextBackgroundMaterial.j3m"));

        inputManager.addListener(analogListener, new String[]{"ACCEPT_INSERTION", "REJECT_INSERTION"});
    }
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (isActive()) {
                if ("ACCEPT_INSERTION".equals(name)) {
                    viewport.clearSelection();
                    viewport.addToScene();
                    viewport.activateIdleState(this);
                } else if ("REJECT_INSERTION".equals(name)) {
                    viewport.clearSelection();
                    viewport.activateIdleState(this);
                    viewport.clearInsertionElements();
                } else if ("ROTATION_UP".equals(name)) {
                    if (currentPickElement != null) {
                        currentPickElement.nextRotationValue();
                    }
                } else if ("ROTATION_DOWN".equals(name)) {
                    if (currentPickElement != null) {
                        currentPickElement.previousRotationValue();
                    }
                }
            }
        }
    };

    @Override
    public void activate(SandboxViewport viewport) {
        setActive();
        this.viewport = viewport;
        viewport.attachGuiElement(linkText);
        viewport.attachGuiElement(textBackgroundGeometry);
        

    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        setInactive();
        viewport.clearInsertionElements();
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
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
     *
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonPressed(SandboxViewport viewport) {
    }

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        doInsertion();
    }

    @Override
    public void cleanup() {
    }

    @Override
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
    }

    private void doInsertion() {
        currentInsertion = viewport.getInsertionElement();
        if ( currentInsertion == null)
            return;
        // Reset results list.
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        InputManager inputManager = viewport.getInputManager();
        Vector2f click2d = inputManager.getCursorPosition();
        Camera cam = viewport.getCamera();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        dir.normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        Node sceneElements = viewport.getSceneElements();
        sceneElements.collideWith(ray, results);

        int index = 0;
        for (; index < results.size(); ++index) {
            Geometry g = results.getCollision(index).getGeometry();
            Boolean pickable = g.getUserData("Pickable");
            if (pickable == null || pickable == true) {
                break;
            }
        }
        if (index == results.size()) {
            return;
        }

        CollisionResult result = results.getCollision(index);
        Geometry g = result.getGeometry();
        Prefab prefab = viewport.findPrefabParent(g);
        this.currentPickElement = prefab;

        Vector3f point = result.getContactPoint().clone();
        result.getTriangle(contactTriangle);

        Quaternion rotation = Quaternion.IDENTITY;
        if (viewport.isAutoGridEnabled()) {
            rotation = MathUtil.createRotationFromNormal(result.getContactNormal(), viewport.getAutoGridAxis());
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
                viewport.setLocationOnPrefab((Prefab) currentInsertion, magnetLoc, false);
            }
        } else if (prefab == null) {
            if (currentInsertion instanceof Prefab) {
                viewport.setLocationOnPrefab((Prefab) currentInsertion, point, false);
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
                        viewport.setLocationOnPrefab((Prefab) currentInsertion, point.subtract(p.getPivot()), false);
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
                    viewport.setLocationOnPrefab(p, point.subtract(p.getPivot()), false);
                    p.setLocalPrefabRotation(rotation);
                } else {
                    currentInsertion.setLocalTranslation(point);
                    currentInsertion.setLocalRotation(rotation);
                }
            }
        }
    }
}
