/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.model;

import dae.prefabs.ui.classpath.FileNode;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Represents all the assets that are in the
 *
 * @author Koen Samyn
 */
public class AssetTreeModel implements TreeModel {

    private FileNode root;
    private ArrayList<TreeModelListener> treeModelListeners =
            new ArrayList<TreeModelListener>();

    public AssetTreeModel(FileNode node) {
        this.root = node;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        FileNode fnParent = (FileNode) parent;
        return fnParent.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        FileNode fnParent = (FileNode) parent;
        return fnParent.getChildSize();
    }

    public boolean isLeaf(Object node) {
        FileNode fnNode = (FileNode) node;
        return fnNode.isFile();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getIndexOfChild(Object parent, Object child) {
        FileNode fnParent = (FileNode) parent;
        FileNode fnChild = (FileNode) child;
        return fnParent.getIndexOf(fnChild);
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    public void fireStructureChanged(final TreeModelEvent tme) {
        if (SwingUtilities.isEventDispatchThread()) {
            for (TreeModelListener listener : treeModelListeners) {
                listener.treeStructureChanged(tme);
            }
        } else {
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    for (TreeModelListener listener : treeModelListeners) {
                        listener.treeStructureChanged(tme);
                    }
                }
            });
        }
       
    }

    public void fireNodeAdded(final TreeModelEvent tme) {
        if (SwingUtilities.isEventDispatchThread()) {
            for (TreeModelListener listener : treeModelListeners) {
                listener.treeNodesInserted(tme);
            }
        } else {
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    for (TreeModelListener listener : treeModelListeners) {
                        listener.treeNodesInserted(tme);
                    }
                }
            });
        }
    }

    public void fireNodeRemoved(final TreeModelEvent tme) {
        if (SwingUtilities.isEventDispatchThread()) {
            for (TreeModelListener listener : treeModelListeners) {
                listener.treeNodesRemoved(tme);
            }
        } else {
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    for (TreeModelListener listener : treeModelListeners) {
                        listener.treeNodesRemoved(tme);
                    }
                }
            });
        }
    }
}
