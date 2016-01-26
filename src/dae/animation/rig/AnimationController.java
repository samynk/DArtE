/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig;

import mlproject.fuzzy.FuzzyRuleBlock;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;

/**
 * This class binds an input connector to an output connector. The optional
 * field controller defines which controller the use (for now there is only one
 * possible controller).
 *
 * @author Koen Samyn
 */
public class AnimationController {

    private String name;
    private InputConnector input;
    private OutputConnector output;
    private String controllerInputName;
    private String controllerOutputName;
    // TODO more general interface.
    private FuzzyVariable outputOfController;
    private FuzzyVariable inputToController;
    private FuzzySystem system;

    public AnimationController(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    /**
     * @return the input
     */
    public InputConnector getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(InputConnector input) {
        this.input = input;
    }

    /**
     * @return the output
     */
    public OutputConnector getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(OutputConnector output) {
        this.output = output;
    }

    public void initialize(Rig rig) {
        if (input != null) {
            input.initialize(rig);
        }
        if (output != null) {
            output.initialize(rig);
        }
        system = rig.getFuzzySystem();
        inputToController = system.getFuzzyInputVariable(controllerInputName);
        outputOfController = system.getFuzzyOutputVariable(controllerOutputName);

    }

    /**
     * @return the controllerInputName
     */
    public String getControllerInputName() {
        return controllerInputName;
    }

    /**
     * @param controllerInputName the controllerInputName to set
     */
    public void setControllerInputName(String controllerInputName) {
        this.controllerInputName = controllerInputName;
    }

    /**
     * @return the controllerOutputName
     */
    public String getControllerOutputName() {
        return controllerOutputName;
    }

    /**
     * @param controllerOutputName the controllerOutputName to set
     */
    public void setControllerOutputName(String controllerOutputName) {
        this.controllerOutputName = controllerOutputName;
    }

    @Override
    public AnimationController clone() {
        AnimationController clone = new AnimationController(name);
        InputConnector ic = getInput().cloneConnector();
        OutputConnector oc = getOutput().cloneConnector();
        clone.setInput(ic);
        clone.setOutput(oc);

        clone.setControllerInputName(controllerInputName);
        clone.setControllerOutputName(controllerOutputName);
        return clone;
    }

    public void update(float tpf) {
        if (    input != null && output != null &&
                input.isInitialized() && output.isInitialized() 
                && inputToController !=null && outputOfController != null) {
            float value = input.getValue();
            //System.out.print("Calculating " + this.name + " : " + value);
            inputToController.setInputValue(value);
            for(FuzzyRuleBlock block : system.getFuzzyRuleBlocks())
            {
                block.evaluate();
            }
            //system.evaluate();
            float result = outputOfController.getOutputValue() * tpf;
            //System.out.println("-> " + outputOfController.getOutputValue());
            output.setValue(result);
        }
    }
    
     /**
     * Returns the name of the controller system this AnimationController is bound with.
     * @return the system name.
     */
    public String getSystemName() {
        return "default";
    }

    @Override
    public String toString(){
        return name;
    }
}
