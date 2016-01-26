package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import dae.GlobalObjects;
import dae.gui.SandboxViewport;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.LineShape;
import dae.prefabs.shapes.QuadShape;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.project.Layer;
import dae.project.Level;
import dae.project.ProjectTreeNode;

/**
 *
 * @author Koen Samyn
 */
public class LinkTool extends ViewportTool {

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
     * Internal link tool state.
     */
    private enum LinkState {

        LINK, LINKPARENT
    };
    private LinkState linkState = LinkState.LINK;
    /**
     * The current child element for linking.
     */
    private Prefab currentChildElement;

    /**
     * Creates a new link tool.
     */
    public LinkTool() {
        super("LinkTool");
    }

    /**
     * Initializes the link tool.
     *
     * @param assetManager the asset manager to load objects from.
     * @param inputManager the input manager object.
     */
    @Override
    public void initialize(AssetManager assetManager, InputManager inputManager) {
        // helper objects for linking
        linkShape = new LineShape(Vector3f.ZERO, Vector3f.ZERO);
        linkShape.setLineWidth(5.0f);

        linkGeometry = new Geometry("link", linkShape);

        linkGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkMaterial.j3m"));
        wireBoxGeometryLinkParent = new Geometry("linked parent", wireBoxLinkParent);
        wireBoxGeometryLinkParent.setMaterial(assetManager.loadMaterial("Materials/LinkParentMaterial.j3m"));

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        linkText = new BitmapText(guiFont, false);
        linkText.setSize(guiFont.getCharSet().getRenderedSize());
        linkText.setText("");

        this.textBackground = new QuadShape(1, 1);
        this.textBackgroundGeometry = new Geometry("linktext", textBackground);
        textBackgroundGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkTextBackgroundMaterial.j3m"));
    }

    /**
     * Activates the tool in the viewport.
     *
     * @param viewport the viewport to activate the tool in.
     */
    @Override
    public void activate(SandboxViewport viewport) {
        viewport.clearSelection();
        linkState = LinkState.LINK;

        viewport.attachGuiElement(linkText);
        viewport.attachGuiElement(textBackgroundGeometry);

        setActive();
        viewport.disableFlyCam();
    }

    /**
     * Deactivate the tool in the viewport.
     *
     * @param viewport the viewport to deactivate the tool in.
     */
    @Override
    public void deactivate(SandboxViewport viewport) {
        linkText.removeFromParent();
        textBackgroundGeometry.removeFromParent();

        setInactive();
        viewport.enableFlyCam();
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
        if (linkState == LinkState.LINKPARENT) {
            doLink(viewport);
        }
    }

    /**
     * Called when the mouse button is pressed.
     *
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonPressed(SandboxViewport viewport) {
        if (linkState == LinkState.LINK) {
            this.currentChildElement = viewport.pick();
            viewport.addToSelection(currentChildElement);
            viewport.getRootNode().attachChild(linkGeometry);
            linkState = LinkState.LINKPARENT;
        } else if (linkState == LinkState.LINKPARENT) {
            Prefab p = viewport.pick();
            if (p == currentChildElement || p == null) {
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

            Level level = viewport.getLevel();
            LevelEvent le = new LevelEvent(level, EventType.NODEMOVED, currentChildElement, previousParent, previousIndex, pn);
            GlobalObjects.getInstance().postEvent(le);

            viewport.activateIdleState();
            linkText.setText("");
            textBackground.setDimension(0, 0);
            textBackgroundGeometry.updateModelBound();
            linkGeometry.removeFromParent();
            wireBoxGeometryLinkParent.removeFromParent();
            currentChildElement = null;

            linkText.removeFromParent();
            textBackgroundGeometry.removeFromParent();

        }
    }

    /**
     * Called when the mouse button is released.
     *
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonReleased(SandboxViewport viewport) {
    }

    private Vector3f doLink(SandboxViewport viewport) {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        InputManager inputManager = viewport.getInputManager();
        Vector2f click2d = inputManager.getCursorPosition();

        Camera cam = viewport.getCamera();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.

        Node sceneElements = viewport.getSceneElements();
        sceneElements.collideWith(ray, results);

        CollisionResult result = results.getClosestCollision();
        if (result == null) {
            return cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.5f).clone();
        }

        Geometry g = result.getGeometry();
        Prefab prefab = viewport.findPrefabParent(g);

        if (prefab != null) {
            prefab.updateModelBound();
            BoundingVolume bv = prefab.getWorldBound();
            if (bv.getType() == Type.AABB) {
                BoundingBox bb = (BoundingBox) bv;
                wireBoxLinkParent.fromBoundingBox(bb);
                wireBoxGeometryLinkParent.setLocalTranslation(bb.getCenter().clone());
                if (wireBoxGeometryLinkParent.getParent() == null) {

                    viewport.getRootNode().attachChild(wireBoxGeometryLinkParent);
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

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        if (linkState == LinkState.LINKPARENT) {
            Vector3f from = this.currentChildElement.getWorldTranslation();
            Vector3f to = this.doLink(viewport);
            linkShape.setLine(from, to);
        }
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
}
