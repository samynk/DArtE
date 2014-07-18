/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

/**
 *
 * @author Koen
 */
public class ActionParameter extends Parameter {

    private String methodName;

    public ActionParameter(String type, String id) {
        super(type, id);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
