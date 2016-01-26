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
import java.util.List;

/**
 * Describes a tool that can be used in the viewport. The tool can have an
 * internal state, and offers support for initialization and cleanup. The input
 * events will be transferred to the current tool in the sandbox viewport.
 *
 * @author Koen Samyn
 */
public abstract class ViewportTool {

    private String toolName;
    /**
     * Indicates if this viewport tool is active or not.
     */
    private boolean active;

    /**
     * Creates a new viewport tool with the given name
     */
    public ViewportTool(String name) {
        this.toolName = name;
    }
    
    /**
     * Sets the state of this tool to active.
     */
    public void setActive(){
        active = true;
    }
  
    /**
     * Sets the state of this tool to inactive.
     */
    public void setInactive(){
        active = false;
    }
    
    /**
     * Returns the active state of this tool.
     * @return true if the tool is active , false otherwise.
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Initializes the tool. The tool can load helper objects (gizmos) and add
     * listeners for semantic input events.
     *
     * @param manager the AssetManager for this application.
     * @param inputManager the InputManager for the application.
     */
    public abstract void initialize(AssetManager manager, InputManager inputManager);

    /**
     * Returns the current tool.
     *
     * @return the current tool.
     */
    public String getToolName() {
        return toolName;
    }
    
    /**
     * Activates this tool.
     * @param viewport 
     */
    public abstract void activate(SandboxViewport viewport);
    
    /**
     * Deactivates this tool.
     * @param viewport 
     */
    public abstract void deactivate(SandboxViewport viewport);
    

    /**
     * Called when the mouse moves.
     *
     * @param evt the mouse motion event.
     */
    public abstract void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport);
    /**
     * Called when the mouse button is released.
     * @param viewport the viewport where the mouse button was released.
     */
    public abstract void onMouseButtonReleased(SandboxViewport viewport);
     /**
     * Called when the mouse button is pressed.
     * @param viewport the viewport where the mouse button was released.
     */
    public abstract void onMouseButtonPressed(SandboxViewport viewport);

    /**
     * Called to update the scene. Useful for realtime tools.
     *
     * @param tpf the frame time in milliseconds.
     * @param viewport the viewport wheret the tool is activated.
     */
    public abstract void simpleUpdate(float tpf, SandboxViewport viewport);

    /**
     * Reset all the temporary variables.
     */
    public abstract void cleanup();

    /**
     * Can be used if a gizmo has pickable elements, for example the different
     * axises of the translate tool.
     *
     * @param ray the ray to test the gizmo against.
     * @param results the CollisionResults object that contains the results of
     * the ray picking.
     */
    public abstract void pickGizmo(Ray ray, CollisionResults results);
    
    /**
     * Set ups the gizmo element that was picked by the pickGizmo function.
     * 
     * @param g the geometry that was picked.
     * @param contactPoint the point where the geometry was picked.
     * 
     */
    public abstract void gizmoPicked(SandboxViewport viewport, Geometry g, Vector3f contactPoint);
        
    /**
     * Called when the selection changes.
     * @param node the node that was added to the selection.
     */
    public abstract void selectionChanged(SandboxViewport viewport, Node node);
    
    /**
     * Called when the gizmo should no longer be visible.
     */
    public abstract void removeGizmo();
}
