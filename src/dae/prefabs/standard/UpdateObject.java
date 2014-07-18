/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

/**
 *
 * @author Koen
 */
public class UpdateObject {

    private String property;
    private Object value;
    private boolean undoableEdit;
    private boolean hasParameter;

    public UpdateObject(String property, Object value, boolean undoableEdit) {
        this.property = property;
        this.value = value;
        this.undoableEdit = undoableEdit;
        this.hasParameter = true;
    }

    public UpdateObject(String methodName, boolean b) {
        this.property = methodName;
        this.undoableEdit = b;
        this.hasParameter = false;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }
    
    public boolean isUndoableEdit(){
        return undoableEdit;
    }
    
    public boolean hasParameter(){
        return hasParameter;
    }
}
