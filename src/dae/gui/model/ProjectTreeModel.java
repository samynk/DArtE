/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.model;

import com.jme3.scene.Node;
import dae.prefabs.Prefab;
import dae.prefabs.ui.events.LayerEvent;
import dae.prefabs.ui.events.LevelEvent;
import dae.project.Layer;
import dae.project.Level;
import dae.project.Project;
import java.util.ArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Koen Samyn
 */
public class ProjectTreeModel implements TreeModel {

    private ArrayList<TreeModelListener> listeners =
            new ArrayList<TreeModelListener>();
    private Project project;

    public ProjectTreeModel(Project project) {
        this.project = project;
    }

    public Object getRoot() {
        return project;
    }

    public Object getChild(Object parent, int index) {
        if (parent == project) {
            return project.getLevel(index);
        } else if (parent instanceof Level) {
            Level l = (Level) parent;
            return l.getLevelChild(index);
        } else if (parent instanceof Layer) {
            Layer layer = (Layer) parent;
            return layer.getChild(index);
        } else if (parent instanceof Prefab) {
            Prefab p = (Prefab) parent;
            return p.getPrefabChildAt(index);
        } else {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        if (parent == project) {
            return project.getNrOfLevels();
        } else if (parent instanceof Level) {
            Level l = (Level) parent;
            return l.getChildCount();
        } else if (parent instanceof Layer) {
            Layer layer = (Layer) parent;
            return layer.getChildCount();
        } else if (parent instanceof Prefab) {
            Prefab p = (Prefab) parent;
            return p.getPrefabChildChildCount();
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {

        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            return !p.hasSavableChildren();
        } else {
            return false;
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Object lastComponent = path.getLastPathComponent();
        if (lastComponent instanceof Project) {
            Project p = (Project) lastComponent;
            p.setProjectName(newValue.toString());
        } else if (lastComponent instanceof Level) {
            Level l = (Level) lastComponent;
            l.setName(newValue.toString());
        } else if (lastComponent instanceof Layer) {
            Layer layer = (Layer) lastComponent;
            layer.setName(newValue.toString());
        } else if (lastComponent instanceof Prefab) {
            Prefab p = (Prefab) lastComponent;
            p.setName(newValue.toString());
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == project) {
            return project.getIndexOfLevel((Level) child);
        } else {
            return -1;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    /**
     * Called when the contents of the JTree has changed.
     *
     * @param le the LevelEvent object
     */
    public void levelChanged(LevelEvent le) {
        if (le.getEventType() == LevelEvent.EventType.NODEADDED) {
            for (Node n : le.getNodes()) {

                TreeModelEvent event = createInsertTreeModelEvent(n);
                for (TreeModelListener tml : listeners) {
                    tml.treeNodesInserted(event);
                }
            }
        } else if (le.getEventType() == LevelEvent.EventType.NODEREMOVED) {
            for (Node n : le.getNodes()) {

                TreeModelEvent event = createDeleteTreeModelEvent(le.getLevel(), n);
                if (n instanceof Prefab) {
                    Prefab p = (Prefab) n;
                    Layer l = le.getLevel().getLayer(p.getLayerName());
                    l.removeNode(p);
                }
                for (TreeModelListener tml : listeners) {
                    tml.treeNodesRemoved(event);
                }
                
            }
        } else if (le.getEventType() == LevelEvent.EventType.NODEMOVED) {
            // first remove the node from the previous parent.
            for (Node n : le.getNodes()) {
                TreeModelEvent event = createDeleteTreeModelEvent(le.getLevel(), le.getPreviousParent(), le.getPreviousIndex(), n);
                for (TreeModelListener tml : listeners) {
                    tml.treeNodesRemoved(event);
                }
            }
            // now add it to the correct node.
            for (Node n : le.getNodes()) {
                TreeModelEvent event = createInsertTreeModelEvent(n);
                for (TreeModelListener tml : listeners) {
                    tml.treeNodesInserted(event);
                }
            }
        }
    }

    public void fireInsertLevelEvent(Level l, int index) {
        Object[] path = new Object[1];
        path[0] = this.project;
        int[] indices = {index};
        TreeModelEvent event = new TreeModelEvent(this, path, indices, new Object[]{l});

        for (TreeModelListener tml : listeners) {
            tml.treeNodesInserted(event);
        }
    }

    public TreeModelEvent createDeleteTreeModelEvent(Level level, Node n) {
        int pathCount = 2; // project and node itself

        Prefab p = (Prefab) n;
        int parentLayers = p.getNumParentLayers();
        Object[] path = new Object[pathCount + parentLayers];
        path[0] = this.project;
        path[1] = level;

        for (int i = 0; i < parentLayers; ++i) {
            path[2 + i] = level.getParentLayer(p.getLayerName(), i);
        }
        Layer l = level.getLayer(p.getLayerName());
        int index = l.getIndexOfNode(p);

        int[] indices = {index};
        Object[] objects = {p};
        System.out.println("Removing " + p.getName() + ", at index " + index);
        return new TreeModelEvent(this, path, indices, objects);

    }

    public TreeModelEvent createDeleteTreeModelEvent(Level level, Node previousParent, int previousIndex, Node n) {
        // check the parents of previousParent
        Node parent = previousParent;
        int numParents = 0;
        while (parent != level) {
            parent = parent.getParent();
            ++numParents;
        }

        // project and level itself
        int pathCount = 2;

        Prefab p = (Prefab) n;
        int parentLayers = p.getNumParentLayers();
        Object[] path = new Object[pathCount + numParents + parentLayers];
        path[0] = this.project;
        path[1] = level;



        for (int i = 0; i < parentLayers; ++i) {
            path[2 + i] = level.getParentLayer(p.getLayerName(), i);
        }

        parent = previousParent;
        for (int i = numParents; i > 0; --i) {
            path[2 + parentLayers + numParents] = parent;
            parent = parent.getParent();
        }

        int index;
        if (numParents == 0) {
            Layer l = level.getLayer(p.getLayerName());
            index = l.getIndexOfNode(p);
            l.removeNode(p);
        } else {
            index = previousIndex;
        }

        int[] indices = {index};
        Object[] objects = {p};
        System.out.println("Removing " + p.getName() + "  at index " + index);
        return new TreeModelEvent(this, path, indices, objects);

    }

    public TreeModelEvent createInsertTreeModelEvent(Node n) {
        int pathCount = 2; // project and node itself
        Node original = n;
        Node layerChild = n;

        while (n.getParent() != null && !(n instanceof Level)) {
            ++pathCount;
            layerChild = n;
            n = n.getParent();
        }

        if (n instanceof Level && layerChild instanceof Prefab) {
            Level level = (Level) n;
            Prefab p = (Prefab) layerChild;

            int parentLayers = p.getNumParentLayers();
            Object[] path = new Object[pathCount + p.getNumParentLayers() - 1];
            path[0] = this.project;
            path[1] = n;

            for (int i = 0; i < parentLayers; ++i) {
                path[2 + i] = level.getParentLayer(p.getLayerName(), i);
            }
            
            // add the parent(s) of the node
            int startIndex = path.length-1;
            n = original;
            while( n != layerChild){
                path[startIndex] = n.getParent();
                n = n.getParent();
                startIndex--;
            }

            int index = -1;
            if ( original == layerChild)
            {
                Layer l = level.getLayer(p.getLayerName());
                index = l.getIndexOfNode(p);
            }else{
                Prefab parent = (Prefab)path[path.length-1];
                index = parent.indexOfPrefab((Prefab)original);
            }
            
            int[] indices = {index};
            Object[] objects = {original};
            
            System.out.println("Inserting " + original.getName() + " at index " + index);
            return new TreeModelEvent(this, path, indices, objects);
        }



        return null;
    }

    public void layerAdded(LayerEvent le) {
        Layer added = le.getLayer();
        Object path[] = new Object[added.getNumParentLayers() + 2]; //  project and the level.
        path[0] = this.project;
        path[1] = added.getParentLevel();

        Layer start = added.getParentLayer();
        for (int index = path.length - 1; index > 1; --index) {
            path[index] = start;
            start = start.getParentLayer();
        }

        int[] indices = {added.getParentLayer().getIndexOfLayer(added)};
        Object[] objects = {added};

        TreeModelEvent event = new TreeModelEvent(this, path, indices, objects);

        for (TreeModelListener tml : listeners) {
            tml.treeNodesInserted(event);
        }
    }

    public void fireDeleteLevelEvent(Level l, int index) {
        Object[] path = new Object[1];
        path[0] = this.project;
        int[] indices = {index};
        TreeModelEvent event = new TreeModelEvent(this, path, indices, new Object[]{l});

        for (TreeModelListener tml : listeners) {
            tml.treeNodesRemoved(event);
        }
    }
}
