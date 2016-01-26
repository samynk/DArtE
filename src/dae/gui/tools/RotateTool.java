package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.gui.SandboxViewport;
import dae.math.RayIntersect;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.RotateGizmo;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.types.ObjectType;
import static dae.prefabs.ui.events.GizmoType.ROTATE;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class RotateTool extends ViewportTool {

    private enum RotateState {

        ROTATEX, ROTATEY, ROTATEZ, IDLE
    };
    private RotateState currentState = RotateState.IDLE;
    private Vector3f pickOffset;
    private Vector3f rotationDir;
    /**
     * The rotate gizmo
     */
    private RotateGizmo r;

    /**
     * Creates a new Rotation tool.
     *
     * @param manager
     */
    public RotateTool() {
        super("RotateTool");

    }

    public void initialize(AssetManager manager, InputManager inputManager) {
        r = new RotateGizmo(manager, 3, 0.15f);
    }

    @Override
    public void activate(SandboxViewport viewport) {
        Node selected = viewport.getFirstSelectedNode();
        if (selected != null) {
            selected.attachChild(r);
        }
        setActive();
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        r.removeFromParent();
        setInactive();
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
        if (currentState == RotateState.IDLE) {
            return;
        }
        Vector2f screenCoords = new Vector2f(evt.getX(), evt.getY());
        Camera cam = viewport.getCamera();
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
            switch (currentState) {
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

            for (Node n : viewport.getSelection()) {
                Quaternion base = n.getUserData("BaseRotation");
                Quaternion newValue = base.mult(q);

                if (n instanceof Prefab) {
                    Prefab p = (Prefab) n;
                    ObjectType oType = p.getObjectType();
                    if (oType != null) {
                        Parameter rotation = oType.findParameter("TransformComponent", "rotation");
                        if (rotation != null) {
                            rotation.invokeSet(p, newValue, true);
                        }
                    } else {
                        Logger.getLogger("DArtE").log(Level.INFO, "Could not find object type for :{0}", p.getClass().getName());
                    }
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
        currentState = RotateState.IDLE;
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
    public void onMouseButtonPressed(SandboxViewport viewport) {
        if (currentState == RotateState.IDLE) {
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

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        Camera cam = viewport.getCamera();
        float d = cam.getLocation().distance(r.getWorldTranslation());
        float rd = (d - cam.getFrustumNear()) / (cam.getFrustumFar() - cam.getFrustumNear());

        Vector3f rws = r.getParent() != null ? r.getParent().getWorldScale() : Vector3f.UNIT_XYZ;
        float factorr = 0.9f * (0.1f + rd * 10);
        r.setLocalScale(factorr / rws.x, factorr / rws.y, factorr / rws.z);
    }

    /**
     * Removes the gizmo from the scene.
     */
    public void cleanup() {
        r.removeFromParent();
    }

    public void pickGizmo(Ray ray, CollisionResults results) {
        r.collideWith(ray, results);
    }

    @Override
    public void gizmoPicked(SandboxViewport viewport, Geometry g, Vector3f contactPoint) {
        String transform = g.getName();
        Vector3f cp = g.worldToLocal(contactPoint, null);
        if ("X".equals(transform)) {
            currentState = RotateState.ROTATEX;
            Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_X);
            rotationDir = cp.cross(axis);
        } else if ("Y".equals(transform)) {
            currentState = RotateState.ROTATEY;
            Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_Y);
            rotationDir = cp.cross(axis);
        } else if ("Z".equals(transform)) {
            currentState = RotateState.ROTATEZ;
            Vector3f axis = g.getLocalRotation().mult(Vector3f.UNIT_Z);
            rotationDir = cp.cross(axis);
        }

        if (currentState != RotateState.IDLE) {
            g.getWorldRotation().mult(rotationDir, rotationDir);
            rotationDir.normalizeLocal();

            pickOffset = contactPoint.clone();

            for (Node n : viewport.getSelection()) {
                n.setUserData("BaseRotation", n.getLocalRotation().clone());
            }
            viewport.disableFlyCam();
        }
    }

    @Override
    public void selectionChanged(SandboxViewport viewport, Node node) {
        node.attachChild(r);
    }

    @Override
    public void removeGizmo() {
        r.removeFromParent();
    }
}
