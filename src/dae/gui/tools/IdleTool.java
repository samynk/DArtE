package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.gui.SandboxViewport;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class IdleTool extends ViewportTool {

    public IdleTool(){
        super("IdleTool");
    }
    
    @Override
    public void initialize(AssetManager manager, InputManager inputManager) {
        
    }

    @Override
    public void activate(SandboxViewport viewport) {
        setActive();
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
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
     * @param viewport the viewport where the mouse button was released.
     */
    public void onMouseButtonPressed(SandboxViewport viewport){
        Prefab picked = viewport.pick();
        viewport.clearSelection();
        viewport.addToSelection(picked);
    }

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        
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
