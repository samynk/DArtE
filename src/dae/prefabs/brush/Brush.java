package dae.prefabs.brush;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dae.prefabs.Prefab;
import dae.project.Level;
/**
 * Defines a brush that can be used to shape terrain or add foliage etc to the
 * terrain.
 * @author Koen Samyn
 */
public abstract class Brush extends Prefab{
    
    private String brushLayer="";
    private String brushNode="";
    
    private float radius = 1.0f;
    
    public Brush(){
    }
    
    /**
     * performs a brush stroke.
     * @param scene the scene that will be used for raycasting.
     * @param attach the node to attach the brush strokes to.
     * @param location the location where the brush stroke should be performed.
     * @param normal the normal of the brush stroke.
     */
    public abstract void doBrush(Level scene, Node attach, Vector3f location, Vector3f normal);

    /**
     * @return the brushLayer
     */
    public String getBrushLayer() {
        return brushLayer;
    }

    /**
     * @param brushLayer the brushLayer to set
     */
    public void setBrushLayer(String brushLayer) {
        this.brushLayer = brushLayer;
    }

    /**
     * @return the brushNode
     */
    public String getBrushNode() {
        return brushNode;
    }

    /**
     * @param brushNode the brushNode to set
     */
    public void setBrushNode(String brushNode) {
        this.brushNode = brushNode;
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
