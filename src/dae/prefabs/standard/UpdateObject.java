package dae.prefabs.standard;

import dae.prefabs.Prefab;
import dae.prefabs.parameters.Parameter;

/**
 * An object that encapsulates the change of a property to an object.
 *
 * @author Koen
 */
public class UpdateObject {

    /**
     * The value to set.
     */
    private Object value;
    /**
     * The parameter to set.
     */
    private Parameter parameter;
    /**
     * Is this edit undoable ?
     */
    private boolean undoableEdit;

    /**
     * Creates a new update object.
     *
     * @param value the value for the object.
     * @param p
     * @param undoableEdit
     */
    public UpdateObject(Parameter p, Object value, boolean undoableEdit) {
        this.value = value;
        this.parameter = p;
        this.undoableEdit = undoableEdit;
    }

    /**
     * Returns the value to set.
     *
     * @return the value to set.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns true if the update of the object can be undone.
     *
     * @return true if the update of the object can be undone, false otherwise.
     */
    public boolean isUndoableEdit() {
        return undoableEdit;
    }

    /**
     * Executes the update object.
     * @param prefab the prefab to execute the update on.
     * @param undoable is this an undoable edit or not.
     */
    public void execute(Prefab prefab, boolean undoable) {
        parameter.invokeSet(prefab, value, undoable);
    }
}
