/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.prefabs.ui.BooleanParameterUI;
import dae.prefabs.ui.ChoiceParameterUI;
import dae.prefabs.ui.ColorParameterUI;
import dae.prefabs.ui.EnumListParameterUI;
import dae.prefabs.ui.Float3ParameterUI;
import dae.prefabs.ui.FloatParameterUI;
import dae.prefabs.ui.IntParameterUI;
import dae.prefabs.ui.MethodParameterUI;
import dae.prefabs.ui.ObjectParameterUI;
import dae.prefabs.ui.ParameterPanel;
import dae.prefabs.ui.RangeParameterUI;
import dae.prefabs.ui.TextParameterUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class ParameterSection {

    private String name;
    // lazy creation
    private ParameterPanel parameterPanel = null;
    private ArrayList<Parameter> parameters =
            new ArrayList<Parameter>();
    private HashMap<String, Parameter> parameterMap =
            new HashMap<String, Parameter>();
    
    

    public ParameterSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParameter(Parameter p) {
        this.parameters.add(p);
        parameterMap.put(p.getId(), p);
    }

    public Iterable<Parameter> getParameters() {
        return parameters;
    }

    public ParameterPanel createParameterPanel() {
        if (parameterPanel == null) {
            parameterPanel = new ParameterPanel();
            parameterPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0f;
            gbc.weighty = 0.0f;
            gbc.fill = GridBagConstraints.BOTH;
            for (Parameter p : this.parameters) {
                String type = p.getType();
                if ("float3".equals(type)) {
                    Float3ParameterUI float3ui = new Float3ParameterUI();
                    float3ui.setLabel(p.getLabel());
                    float3ui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), float3ui, gbc);
                } else if ("string".equals(type)) {
                    TextParameterUI textui = new TextParameterUI();
                    textui.setLabel(p.getLabel());
                    textui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), textui, gbc);
                } else if ("choice".equals(type)) {
                    ChoiceParameterUI cui = new ChoiceParameterUI();
                    cui.setLabel(p.getLabel());
                    cui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), cui, gbc);
                } else if ("color".equals(type)) {
                    ColorParameterUI cpui = new ColorParameterUI();
                    cpui.setLabel(p.getLabel());
                    cpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), cpui, gbc);
                } else if ("float".equals(type)) {
                    FloatParameterUI fpui = new FloatParameterUI();
                    fpui.setLabel(p.getLabel());
                    fpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), fpui, gbc);
                } else if ("integer".equals(type)) {
                    IntParameterUI ipui = new IntParameterUI();
                    ipui.setLabel(p.getLabel());
                    ipui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), ipui, gbc);
                    break;
                } else if ("range".equals(type)) {
                    RangeParameterUI rpui = new RangeParameterUI();
                    rpui.setLabel(p.getLabel());
                    rpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), rpui, gbc);
                } else if ("boolean".equals(type)) {
                    BooleanParameterUI bpui = new BooleanParameterUI();
                    bpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), bpui, gbc);
                } else if ("object".equals(type)) {
                    ObjectParameterUI opui = new ObjectParameterUI();
                    opui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), opui, gbc);
                } else if ("action".equals(type)) {
                    MethodParameterUI mpui = new MethodParameterUI();
                    mpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), mpui, gbc);
                } else if ("enumlist".equals(type)) {
                    EnumListParameterUI elpui = new EnumListParameterUI();
                    elpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), elpui, gbc);
                }

                gbc.gridy++;
            }
        }
        return parameterPanel;
    }

    public Parameter getParameter(String property) {
        return parameterMap.get(property);
    }
}
