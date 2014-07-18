/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.prefabs.parameters.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This object defines defaults for the object. An example for a default is the
 * z offset of the
 *
 * @author Koen
 */
public class DefaultSection {

    private ArrayList<Parameter> parameters =
            new ArrayList<Parameter>();
    private HashMap<String, Parameter> parameterMap =
            new HashMap<String, Parameter>();

    public DefaultSection() {
    }

    public void addParameter(Parameter p) {
        this.parameters.add(p);
        parameterMap.put(p.getId(), p);
    }

    public Parameter getParameter(String property) {
        return parameterMap.get(property);
    }
}
