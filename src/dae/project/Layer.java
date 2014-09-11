/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.project;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 * The Layer object groups objects of the world in layers.
 *
 * @author Koen Samyn
 */
public class Layer implements ProjectTreeNode{

    /**
     * The Level object that is the parent of this layer.
     */
    private Level parent;
    /**
     * The name of the layer.
     */
    private String name;
    /**
     * The nodes that are present in this layer.
     */
    private ArrayList<Node> nodes = new ArrayList<Node>();
    /**
     * Is the layer visible?
     */
    private boolean visible = true;
    /**
     * Is the layer locked?
     */
    private boolean locked = false;

    /**
     * Creates a new layer object.
     *
     * @param name
     */
    public Layer(String name) {
        this.name = name;
    }

    /**
     * Sets the parent level of this layer.
     *
     * @param level the parent level of this layer.
     */
    void setParentLevel(Level level) {
        this.parent = level;
    }

    /**
     * Returns the parent level of this layer.
     *
     * @return the parent level of this layer.
     */
    public Level getParentLevel() {
        return parent;
    }

    /**
     * Checks if this is a root layer.
     *
     * @return true if this is a root layer, false otherwise.
     */
    public boolean isRootLayer() {
        return name.indexOf('.') == -1;
    }

    /**
     * Returns true if this layer is a child of the parent layer.
     *
     * @param parent the name of the parent.
     * @return true if this layer is a child of the parent Layer object.
     */
    public boolean isChildOf(Layer parent) {
        return parent != this && name.startsWith(parent.getName());
    }

    /**
     * Returns the number of child layers.
     *
     * @return the number of child layers.
     */
    public int getNrOfLayers() {
        return this.parent.getNumberOfChildLayers(this);
    }

    /**
     * Returns the name of this layer.
     *
     * @return the name of the layer.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the layer.
     *
     * @param name the name of the layer.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a node to this layer.
     *
     * @param node the node to add.
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Removes a node from this layer.
     *
     * @param node the node to remove.
     */
    public void removeNode(Node node) {
        nodes.remove(node);
    }

    /**
     * Returns the number of nodes.
     *
     * @return the number of nodes.
     */
    public int getNrOfNodes() {
        return nodes.size();
    }

    /**
     * Gets the node at the specified index.
     *
     * @return the node.
     */
    public Node getNodeAt(int index) {
        return nodes.get(index);
    }

    /**
     * Returns the index of the specified node.
     *
     * @param node the node to get the index of.
     * @return the index of the node, or -1 if the node is not found.
     */
    public int getIndexOfNode(Node node) {
        return parent.getNumberOfChildLayers(this) + nodes.indexOf(node);
    }

    /**
     * Gets the parent layer of this layer.
     *
     * @return the parent layer of this layer, or null if the layer has no
     * parent layer.
     */
    public Layer getParentLayer() {
        String layerName = this.getName();
        int lastDotIndex = layerName.indexOf('.');
        if ( lastDotIndex > 0 ){
            String parentLayerName = layerName.substring(0,lastDotIndex);
            return parent.getLayer(parentLayerName);
        }else{
            return null;
        }
    }

    /**
     * Returns the number of parent layers.
     *
     */
    public int getNumParentLayers() {
        int startIndex = 0;
        int count =0;
        while( (startIndex = name.indexOf('.',startIndex)) > 0){
            ++startIndex;
            ++count;
        }
        return count;
    }

    /**
     * Returns the index of a layer that is a child of this layer.
     *
     * @param layer the layer to calculate the index of.
     * @return the index of the child layer or -1 if the child layer is not a
     * child of this layer.
     */
    public int getIndexOfLayer(Layer layer) {
        if (layer.isChildOf(this)) {
            for (int i = 0; i < parent.getNumberOfChildLayers(this); ++i) {
                Layer child = parent.getChildLayerAt(this, i);
                if (layer == child) {
                    return i;
                }
            }
        }
        return -1;

    }

    /**
     * Return the number of child nodes of this layer. (this can be a mix of
     * Layer objects and Prefab objects.
     *
     * @return the number of child nodes.
     */
    public int getChildCount() {
        int numLayers = parent.getNumberOfChildLayers(this);
        return numLayers + this.getNrOfNodes();
    }

    /**
     * Returns the object at the specified position.
     *
     * @param index the index.
     * @return the Object at that position.
     */
    public Object getChild(int index) {
        int numLayers = parent.getNumberOfChildLayers(this);
        if (index < numLayers) {
            return parent.getChildLayerAt(this, index);
        } else {
            return this.getNodeAt(index - numLayers);
        }
    }

    /**
     * Checks if this layer is locked or not.
     *
     * @return true if the layer is locked, false otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the locked status of this layer.
     *
     * @param locked true if the objects in this layer should be locked, false
     * otherwise.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Checks the visibility of this layer.
     *
     * @return true if the layer is visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility of this layer.
     *
     * @param visible true if the layer is visible, false otherwise.
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            // visibility change needs to be posted to the 3d thread.
            this.parent.postTask(new Runnable() {
                public void run() {
                    if (!Layer.this.visible) {
                        for (Node n : nodes) {
                            n.removeFromParent();
                        }
                    } else {
                        for (Node n : nodes) {
                            parent.attachChildDirectly(n);
                        }
                    }
                }
            });

        }
    }

    /**
     * Return the name of the layer.
     *
     * @return the name of the layer.
     */
    @Override
    public String toString() {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    // implementation for tree model.
    
    public boolean hasChildren() {
        return (this.getNrOfLayers()+ this.getNrOfNodes()) > 0;
    }

    public ProjectTreeNode getProjectChild(int index) {
        return (ProjectTreeNode)this.getChild(index);
    }

    public int getIndexOfChild(ProjectTreeNode object) {
        if ( object instanceof Layer){
            return getIndexOfLayer((Layer)object);
        }else{
            return getIndexOfNode((Node)object);
        }
    }

    public ProjectTreeNode getProjectParent() {
        Layer layerParent = getParentLayer();
        if ( layerParent == null){
            return getParentLevel();
        }else{
            return layerParent;
        }
    }

    public boolean isLeaf() {
        return false;
    }

    public boolean hasNode(Node node) {
       return this.nodes.contains(node);
    }
}
