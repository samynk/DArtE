/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.prefab.undo;

import com.jme3.scene.Node;
import dae.prefabs.Prefab;
import dae.prefabs.standard.UpdateObject;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author samyn_000
 */
public class UndoPrefabPropertyEdit extends AbstractUndoableEdit {

    private Node node;
    private String property;
    private Object oldValue;
    private Object newValue;
    private boolean significant = true;

    public UndoPrefabPropertyEdit(Node node, String property, Object oldValue, Object newValue) {
        this.node = node;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public boolean canUndo() {
        return node instanceof Prefab;
    }

    @Override
    public boolean isSignificant() {
        return significant;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.addUpdateObject(new UpdateObject(property,oldValue,false));
            //GlobalObjects.getInstance().postEvent(new PrefabChangedEvent(p));
        }
    }

    @Override
    public void redo() throws CannotUndoException {
        super.redo();
        if (node instanceof Prefab) {
            Prefab p = (Prefab) node;
            p.addUpdateObject(new UpdateObject(property,newValue,false));
            //GlobalObjects.getInstance().postEvent(new PrefabChangedEvent(p));
        }
    }

    public boolean compareEdit(UndoPrefabPropertyEdit toCompare) {
        return node == toCompare.node && property.equals(toCompare.property);
    }

    public void setSignificant(boolean significant) {
        this.significant = significant;
    }
    
    @Override
    public String toString(){
        return this.property + ":" + oldValue + " changed to " + newValue;
    }
}
