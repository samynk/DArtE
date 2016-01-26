package dae.gui.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.gui.SandboxViewport;

/**
 *
 * @author Koen Samyn
 */
public class ScaleTool extends ViewportTool{
    
    public ScaleTool(){
        super("ScaleTool");
    }

    @Override
    public void initialize(AssetManager manager, InputManager inputManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void activate(SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deactivate(SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt, SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMouseButtonReleased(SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMouseButtonPressed(SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void simpleUpdate(float tpf, SandboxViewport viewport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pickGizmo(Ray ray, CollisionResults results) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gizmoPicked(SandboxViewport viewport, Geometry g, Vector3f contactPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectionChanged(SandboxViewport viewport, Node node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeGizmo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
