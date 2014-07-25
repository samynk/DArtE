/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.prefab.undo;

import com.jme3.scene.Node;
import dae.GlobalObjects;
import dae.prefabs.ui.events.LevelEvent;
import dae.project.Level;
import dae.project.ProjectTreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author samyn_000
 */
public class DeletePrefabEdit extends AbstractUndoableEdit {
    private Level level;
    
    private Node deleted;
    private Node parent;

    public DeletePrefabEdit(Level level,Node deleted) {
        this.level = level;
        this.deleted = deleted;
        this.parent = deleted.getParent();
    }

    @Override
    public boolean canUndo() {
        return deleted != null && parent != null;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        parent.attachChild(deleted);
        
        LevelEvent le = new LevelEvent(level, LevelEvent.EventType.NODEADDED,deleted);
        GlobalObjects.getInstance().postEvent(le);

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();
        if ( deleted.getParent() != null && deleted instanceof ProjectTreeNode)
        {
            ProjectTreeNode node = (ProjectTreeNode)deleted;
            ProjectTreeNode parentNode = node.getProjectParent();
            LevelEvent le = new LevelEvent(level, LevelEvent.EventType.NODEREMOVED,deleted, parentNode, parentNode.getIndexOfChild(node));
            GlobalObjects.getInstance().postEvent(le);
        }
        deleted.removeFromParent();
    }
}
