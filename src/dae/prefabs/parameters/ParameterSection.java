/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.parameters;

import dae.prefabs.ui.BooleanParameterUI;
import dae.prefabs.ui.ChoiceParameterUI;
import dae.prefabs.ui.ColorParameterUI;
import dae.prefabs.ui.ConnectorParameterUI;
import dae.prefabs.ui.EnumListParameterUI;
import dae.prefabs.ui.FileParameterUI;
import dae.prefabs.ui.Float2ParameterUI;
import dae.prefabs.ui.Float3ParameterUI;
import dae.prefabs.ui.FloatParameterUI;
import dae.prefabs.ui.FuzzyParameterUI;
import dae.prefabs.ui.IntParameterUI;
import dae.prefabs.ui.MethodParameterUI;
import dae.prefabs.ui.ObjectParameterUI;
import dae.prefabs.ui.ParameterPanel;
import dae.prefabs.ui.PrefabComponentHeader;
import dae.prefabs.ui.RangeParameterUI;
import dae.prefabs.ui.SoundParameterUI;
import dae.prefabs.ui.TextParameterUI;
import dae.prefabs.ui.collection.DictionaryParameterUI;
import dae.prefabs.ui.collection.ListParameterUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.SwingUtilities;

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
    
    public int getNrOfParameters(){
        return parameters.size();
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
            gbc.gridwidth = 2;
            gbc.gridy = 0;
            gbc.weightx = 1.0f;
            gbc.weighty = 0.0f;
            gbc.fill = GridBagConstraints.BOTH;
            
            PrefabComponentHeader pch = new PrefabComponentHeader();
            pch.setTitle(this.name);
            parameterPanel.add(pch,gbc);
            gbc.gridy++;
            gbc.gridx = 1;
            gbc.gridwidth = 1;
            
            
            for (Parameter p : this.parameters) {
                String type = p.getType();
                if ("float3".equals(type)) {
                    Float3ParameterUI float3ui = new Float3ParameterUI();
                    float3ui.setParameter(p);
                    System.out.println("minimum size float3 : " + float3ui.getMinimumSize());
                    parameterPanel.addParameterUI(p.getId(), float3ui, gbc);
                }else if ("float2".equals(type)){
                    Float2ParameterUI float2ui = new Float2ParameterUI();
                    float2ui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), float2ui, gbc);
                } else if ("string".equals(type)) {
                    TextParameterUI textui = new TextParameterUI();
                    textui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), textui, gbc);
                } else if ("choice".equals(type)) {
                    ChoiceParameterUI cui = new ChoiceParameterUI();
                    cui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), cui, gbc);
                } else if ("color".equals(type)) {
                    ColorParameterUI cpui = new ColorParameterUI();
                    cpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), cpui, gbc);
                } else if ("float".equals(type)) {
                    FloatParameterUI fpui = new FloatParameterUI();
                    fpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), fpui, gbc);
                } else if ("integer".equals(type)) {
                    IntParameterUI ipui = new IntParameterUI();
                    ipui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), ipui, gbc);
                } else if ("range".equals(type)) {
                    RangeParameterUI rpui = new RangeParameterUI();
                    System.out.println("minimum size range : " + rpui.getMinimumSize());
                    rpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), rpui, gbc);
                } else if ("boolean".equals(type)) {
                    BooleanParameterUI bpui = new BooleanParameterUI();
                    bpui.setParameter(p);
                    System.out.println("minimum size boolean : " + bpui.getMinimumSize());
                    parameterPanel.addParameterUI(p.getId(), bpui, gbc);
                } else if ("object".equals(type)) {
                    ObjectParameterUI opui = new ObjectParameterUI();
                    opui.setParameter(p);
                    System.out.println("minimum size object : " + opui.getMinimumSize());
                    parameterPanel.addParameterUI(p.getId(), opui, gbc);
                } else if ("action".equals(type)) {
                    MethodParameterUI mpui = new MethodParameterUI();
                    mpui.setParameter(p);
                    System.out.println("minimum size action : " + mpui.getMinimumSize());
                    parameterPanel.addParameterUI(p.getId(), mpui, gbc);
                } else if ("enumlist".equals(type)) {
                    EnumListParameterUI elpui = new EnumListParameterUI();
                    elpui.setParameter(p);
                    System.out.println("minimum size enum : " + elpui.getMinimumSize());
                    parameterPanel.addParameterUI(p.getId(), elpui, gbc);
                } else if ("fuzzy".equals(type)) {
                    FuzzyParameterUI fpui = new FuzzyParameterUI();
                    fpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), fpui, gbc);
                } else if ("dictionary".equals(type)) {
                    DictionaryParameterUI dpui = new DictionaryParameterUI();
                    dpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), dpui, gbc);
                } else if ("connector".equals(type)) {
                    ConnectorParameterUI cpui = new ConnectorParameterUI();
                    cpui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), cpui, gbc);
                } else if ("sound".equals(type)) {
                    SoundParameterUI spui = new SoundParameterUI();
                    spui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), spui, gbc);
                } else if ("file".equals(type)) {
                    FileParameterUI fui = new FileParameterUI();
                    fui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(), fui, gbc);
                }else if ("list".equals(type)){
                    ListParameterUI lui = new ListParameterUI();
                    lui.setParameter(p);
                    parameterPanel.addParameterUI(p.getId(),lui,gbc);
                }

            }
        }
        SwingUtilities.updateComponentTreeUI(parameterPanel);
        return parameterPanel;
    }

    

    public Parameter getParameter(String property) {
        return parameterMap.get(property);
    }

    /**
     * Returns all the parameters in this parameter section.
     *
     * @return all the parameters.
     */
    public Collection<? extends Parameter> getAllParameters() {
        return this.parameters;
    }
}
