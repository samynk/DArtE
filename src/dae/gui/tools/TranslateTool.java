package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.gui.SandboxViewport;
import dae.math.RayIntersect;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.types.ObjectType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This tool performs all the actions that are necessary to deal with
 * translation.
 *
 * @author Koen Samyn
 */
public class TranslateTool extends ViewportTool {

    /**
     * The translation gizmo.
     */
    private Axis a;

    private enum TranslateState {

        TRANSLATEX, TRANSLATEY, TRANSLATEZ, TRANSLATETWOAXIS, IDLE
    };
    private TranslateState currentState = TranslateState.IDLE;
    private Vector3f pickAxis1 = new Vector3f();
    private Vector3f pickAxis2 = new Vector3f();
    private Vector3f pickOffset;

    /**
     * Creates a new translatetool.
     */
    public TranslateTool() {
        super("TranslateTool");
    }

    public void initialize(AssetManager manager, InputManager inputManager) {
        a = new Axis(manager, 3, 0.15f);
    }

    @Override
    public void activate(SandboxViewport viewport) {
        Node selected = viewport.getFirstSelectedNode();
        if (selected != null) {
            selected.attachChild(a);
        }
        setActive();
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        a.removeFromParent();
        setInactive();
    }

    /**
     * Called when the mouse moves
     *
     * @param evt the original mouse event.
     * @param cam the current camera.
     * @param currentSelection the current selection to translate.
     */
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
        if (currentState == TranslateState.IDLE) {
            return;
        }
        Vector2f screenCoords = new Vector2f(evt.getX(), evt.getY());
        Camera cam = viewport.getCamera();
        Vector3f p1 = cam.getWorldCoordinates(screenCoords, 0.01f);
        Vector3f p2 = cam.getWorldCoordinates(screenCoords, 0.95f);


        for (Node n : viewport.getSelection()) {
            Vector3f pa = new Vector3f();
            Vector3f pb = new Vector3f();
            Vector2f t = new Vector2f();

            Vector3f diff1 = Vector3f.ZERO;


            pb.set(0, 0, 0);
            if (currentState == TranslateState.TRANSLATETWOAXIS) {
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
            Vector3f translation = baseTranslation.add(diff1);

            if (n instanceof Prefab) {
                Prefab p = (Prefab) n;
                ObjectType oType = p.getObjectType();
                if (oType != null) {
                    Parameter ptrans = oType.findParameter("TransformComponent", "translation");
                    if (ptrans != null) {
                        ptrans.invokeSet(p, translation, true);
                    }
                } else {
                    Logger.getLogger("DArtE").log(Level.INFO, "Could not find object type for :" + p.getClass().getName());
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
        currentState = TranslateState.IDLE;
        for (Node n : viewport.getSelection()) {
            ((Prefab) n).enablePhysics();
        }
        viewport.enableFlyCam();
    }

    /**
     * Called when the mouse button is pressed.
     *
     * @param viewport the viewport where the mouse button was released.
     */
    @Override
    public void onMouseButtonPressed(SandboxViewport viewport) {
        if (currentState == TranslateState.IDLE) {
            Prefab p = viewport.pick();
            if ( p == null)
            {
                viewport.pickGizmo();
            }else{
                viewport.clearSelection();
                viewport.addToSelection(p);
            }
        }
    }

    /**
     * Called to update the scene. Useful for realtime tools.
     *
     * @param tpf the frame time in milliseconds.
     */
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        Camera cam = viewport.getCamera();
        float d = cam.getLocation().distance(a.getWorldTranslation());
        float rd = (d - cam.getFrustumNear()) / (cam.getFrustumFar() - cam.getFrustumNear());

        Vector3f ws = a.getParent() != null ? a.getParent().getWorldScale() : Vector3f.UNIT_XYZ;
        float factor = 1.5f * (0.1f + rd * 10);
        a.setLocalScale(factor / ws.x, factor / ws.y, factor / ws.z);
    }

    @Override
    public void cleanup() {
        a.removeFromParent();
    }

    @Override
    public void pickGizmo(Ray ray, CollisionResults results) {
        a.collideWith(ray, results);
    }

    /**
     * Set ups the gizmo element that was picked by the pickGizmo function.
     *
     * @param viewport the active viewport.
     * @param g the geometry that was picked.
     * @param contactPoint the point where the geometry was picked.
     *
     */
    @Override
    public void gizmoPicked(SandboxViewport viewport, Geometry g, Vector3f contactPoint) {
        String transform = g.getUserData("Transform");
        if ("translate_X".equals(transform)) {
            currentState = TranslateState.TRANSLATEX;
            a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
        } else if ("translate_Y".equals(transform)) {
            currentState = TranslateState.TRANSLATEY;
            a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis1);
        } else if ("translate_Z".equals(transform)) {
            currentState = TranslateState.TRANSLATEZ;
            a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis1);
        } else if ("translate_XY".equals(transform)) {
            a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
            a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis2);
            currentState = TranslateState.TRANSLATETWOAXIS;
        } else if ("translate_YZ".equals(transform)) {
            a.getWorldRotation().mult(new Vector3f(0, 1, 0), pickAxis1);
            a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis2);
            currentState = TranslateState.TRANSLATETWOAXIS;
        } else if ("translate_XZ".equals(transform)) {
            a.getWorldRotation().mult(new Vector3f(1, 0, 0), pickAxis1);
            a.getWorldRotation().mult(new Vector3f(0, 0, 1), pickAxis2);
            currentState = TranslateState.TRANSLATETWOAXIS;
        }

        if (currentState != TranslateState.IDLE) {
            pickOffset = contactPoint.clone();

            for (Node n : viewport.getSelection()) {
                n.setUserData("BaseTranslation", n.getLocalTranslation().clone());
            }
            viewport.disableFlyCam();
        }
    }

    @Override
    public void selectionChanged(SandboxViewport viewport, Node node) {
        System.out.println("selection change:" + node.getName());
        node.attachChild(a);
    }

    @Override
    public void removeGizmo() {
        a.removeFromParent();
    }
}