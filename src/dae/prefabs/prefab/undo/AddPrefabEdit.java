/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package motum.objects.prefab.undo;

import com.jme3.scene.Node;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author samyn_000
 */
public class AddPrefabEdit extends AbstractUndoableEdit {

    private Node added;
    private Node parent;

    public AddPrefabEdit(Node added) {
        this.added = added;
        this.parent = added.getParent();
    }

    @Override
    public boolean canUndo() {
        return added != null && parent != null;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        added.removeFromParent();

    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();
        parent.attachChild(added);
    }
}
