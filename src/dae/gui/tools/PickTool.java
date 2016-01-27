package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
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
import dae.prefabs.shapes.QuadShape;
import dae.prefabs.ui.events.PickEvent;

/**
 *
 * @author Koen Samyn
 */
public class PickTool extends ViewportTool {

    private BitmapText linkText;
    private QuadShape textBackground;
    private Geometry textBackgroundGeometry;
    private WireBox wireBoxLinkParent = new WireBox();
    private Geometry wireBoxGeometryLinkParent;
    /**
     * The currently picked element.
     */
    private String pickProperty;

    public void setPickProperty(String pickProperty) {
        this.pickProperty = pickProperty;
    }

    private enum PickState {

        PICK, IDLE
    };
    private SandboxViewport viewport;

    /**
     * Creates a new pick tool
     */
    public PickTool() {
        super("Pick");
    }

    @Override
    public void initialize(AssetManager assetManager, InputManager inputManager) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        linkText = new BitmapText(guiFont, false);
        linkText.setSize(guiFont.getCharSet().getRenderedSize());
        linkText.setText("");

        wireBoxGeometryLinkParent = new Geometry("linked parent", wireBoxLinkParent);
        wireBoxGeometryLinkParent.setMaterial(assetManager.loadMaterial("Materials/LinkParentMaterial.j3m"));

        this.textBackground = new QuadShape(1, 1);
        this.textBackgroundGeometry = new Geometry("linktext", textBackground);
        textBackgroundGeometry.setMaterial(assetManager.loadMaterial("Materials/LinkTextBackgroundMaterial.j3m"));
    }

    @Override
    public void activate(SandboxViewport viewport) {
        this.viewport = viewport;
        viewport.attachGuiElement(textBackgroundGeometry);
        viewport.attachGuiElement(linkText);
        setActive();
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        textBackgroundGeometry.removeFromParent();
        wireBoxGeometryLinkParent.removeFromParent();
        linkText.removeFromParent();
        setInactive();
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
        Prefab picked = viewport.pick();
        if (picked != null) {
            System.out.println("Object picked : " + ((Prefab)picked).getName());
            System.out.println("Pickproperty : " + pickProperty);
            PickEvent pe = new PickEvent((Prefab) picked, this, pickProperty);
            GlobalObjects.getInstance().postEvent(pe);
            viewport.clearSelection();

            linkText.setText("");
            textBackground.setDimension(0, 0);
            textBackgroundGeometry.updateModelBound();

            wireBoxGeometryLinkParent.removeFromParent();
            linkText.removeFromParent();
            textBackgroundGeometry.removeFromParent();
            viewport.activateIdleState(this);
        }
    }

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        doPickText();
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

    private void doPickText() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position

        Vector2f click2d = viewport.getInputManager().getCursorPosition();
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
            linkText.setText("");
            textBackground.setDimension(0, 0);
            textBackgroundGeometry.updateModelBound();
            return;
        }

        Geometry g = result.getGeometry();
        Prefab prefab = viewport.findPrefabParent(g);

        if (prefab != null) {
            prefab.updateModelBound();
            BoundingVolume bv = prefab.getWorldBound();
            if (bv.getType() == BoundingVolume.Type.AABB) {
                BoundingBox bb = (BoundingBox) bv;
                wireBoxLinkParent.fromBoundingBox(bb);
                wireBoxGeometryLinkParent.setLocalTranslation(bb.getCenter().clone());
                if (wireBoxGeometryLinkParent.getParent() == null) {
                    viewport.getRootNode().attachChild(wireBoxGeometryLinkParent);
                }
            }
            linkText.setText(prefab.getName());
            linkText.setLocalTranslation(click2d.x, click2d.y, 0.01f);
            textBackground.setDimension(linkText.getLineWidth() + 2, linkText.getLineHeight() + 4);
            textBackgroundGeometry.updateModelBound();
            textBackgroundGeometry.setLocalTranslation(click2d.x - 1, click2d.y - linkText.getLineHeight() - 2, 0f);

        } else {
            linkText.setText("");
            textBackground.setDimension(0, 0);
            textBackgroundGeometry.updateModelBound();
            wireBoxGeometryLinkParent.removeFromParent();
        }
    }
}
