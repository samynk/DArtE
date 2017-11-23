/*
 * Digital Arts and Entertainment 
 */
package dae.animation.skeleton;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.rig.ConnectorType;
import dae.animation.rig.Joint;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.gizmos.RotateGizmo;
import dae.project.ProjectTreeNode;
import java.util.List;

/**
 * This class provides extra base functionality for joint subclasses.
 * 
 * The most important facility is the transformNode facility which enables the
 * joint subclass to keep the joint prefab itself as a reference transform. This 
 * in turn makes it easier to visualize the constraints of the joint in this 
 * parent space.
 * 
 * 
 * @author Koen.Samyn
 */
public abstract class JointPrefab extends Prefab implements Joint{
    private Vector3f axis = new Vector3f(1,0,0);
    private final Node transformNode;

    @Override
    public abstract List<ConnectorType> getInputConnectorTypes();

    @Override
    public abstract List<ConnectorType> getOutputConnectorTypes();

    /**
     * Creates a new joint prefab. The joint prefab creates a transform
     * node that helps to separate the initial transform in the prefab itself
     * from the animated transforms.
     */
    public JointPrefab(){
        transformNode = new Node();
        super.attachChild(transformNode);
    }
    
    /**
     * Returns the transform node of this joint prefab.
     * @return the transform node.
     */
    public Node getTransformNode(){
        return transformNode;
    }
    
    /**
     * Returns the local axis of transformation.
     * @return the local axis.
     */
    public Vector3f getLocalAxis(){
       return axis; 
    }
    
    /**
     * Set the axis of rotation.
     *
     * @param axis the axis of rotation.
     */
    public void setLocalAxis(Vector3f axis) {
        this.axis = axis;
    }
    
    
    /**
     * Gets the axis of rotation in world space.
     * @return the axis of rotation in world space.
     */
    @Override
    public Vector3f getWorldRotationAxis() {
        return getWorldRotation().mult(axis);
    }

    /**
     * Sets the axis of rotation in world space.
     * @param axis the axis as defined in world space coordinates.
     */
    @Override
    public void setWorldRotationAxis(Vector3f axis) {
        this.worldToLocal(axis, this.axis);
    }

    @Override
    public void rotate(float angle) {
        
    }  
    
    @Override
    public int attachChild(Spatial child) {
        if (child instanceof Axis || child instanceof RotateGizmo) {
            child.setLocalTranslation(this.getPivot());
            return super.attachChild(child);
        } else {
            return transformNode.attachChild(child);
        }
    }

    /**
     * Checks if a prefab has children that need to be saved when the prefab is
     * written to file. First the state of the canHaveChildren is checked, next
     * a check will be performed to see if the object has savable children.
     *
     * @return true if this prefab has savable children, false otherwise.
     */
    @Override
    public boolean hasSavableChildren() {
        if (!canHaveChildren) {
            return false;
        } else {
            for (Spatial s : transformNode.getChildren()) {
                if (s instanceof Prefab) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean canHaveChildren() {
        return canHaveChildren;
    }

    @Override
    public Object getPrefabChildAt(int index) {
        int pindex = 0;
        for (Spatial s : transformNode.getChildren()) {
            if (s instanceof Prefab) {
                if (pindex == index) {
                    return s;
                }
                ++pindex;
            }
        }
        return null;
    }

    @Override
    public int getPrefabChildCount() {
        int pindex = 0;
        if (transformNode != null) {
            for (Spatial s : transformNode.getChildren()) {
                if (s instanceof Prefab) {
                    ++pindex;
                }
            }
            return pindex;
        }
        return 0;
    }

    @Override
    public int indexOfPrefab(Prefab prefab) {
        int pindex = 0;
        for (Spatial s : transformNode.getChildren()) {
            if (s == prefab) {
                return pindex;

            }
            ++pindex;
        }
        return -1;
    }

    @Override
    public int getIndexOfChild(ProjectTreeNode object) {
        int pindex = 0;
        for (Spatial s : transformNode.getChildren()) {
            if (s == object) {
                return pindex;
            } else if (s instanceof ProjectTreeNode) {
                ++pindex;
            }
        }
        return -1;
    }
    
    
    
    public void setTargetsVisible(boolean targets){
        
    }
}
