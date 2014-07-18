/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.prefab.undo;

import com.jme3.scene.Node;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author samyn_000
 */
public class DeletePrefabEdit extends AbstractUndoableEdit {

    private Node deleted;
    private Node parent;

    public DeletePrefabEdit(Node deleted) {
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

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();
        deleted.removeFromParent();
    }
}
