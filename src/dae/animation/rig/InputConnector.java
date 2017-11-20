package dae.animation.rig;

import com.jme3.scene.Spatial;
import dae.animation.skeleton.AttachmentPoint;
import dae.prefabs.standard.PrefabPlaceHolder;
import org.w3c.dom.Node;

/**
 * The input connector interface connects a calculated value (for example an
 * angle) with the input of a controller.
 *
 * @author Koen Samyn
 */
public abstract class InputConnector {

    private boolean initialized = false;
    // joint
    private String jointName;
    private Joint joint;
    // targets
    // joint target
    private String attachmentName;
    protected AttachmentPoint attachment;
    // destination target
    private String targetName;
    protected Spatial target;
    
    /**
     * This method is called to allow the InputConnector to determine then
     * necessary inputs.
     *
     * @param rig the rig with the necessary inputs.
     */
    public void initialize(Rig rig) {
        setInitialized(false);
        Spatial sjoint = rig.getChild(getJointName());
        if (sjoint instanceof Joint) {
            setJoint((Joint) sjoint);
        } else {
            return;
        }

        Spatial sattachment = rig.getChild(attachmentName);
        if (sattachment instanceof AttachmentPoint) {
            this.attachment = (AttachmentPoint) sattachment;
        } else {
            return;
        }

        // search for the target from the top level.
        target = rig.getTarget(targetName);

        if (target != null && !(target instanceof PrefabPlaceHolder)) {
            setInitialized(true);
        }
    }

    /**
     * Checks if the output is properly initialized.
     *
     * @return true if the connector is properly initialized, false otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialized property of this connector.
     *
     * @param initialized true if the connector is set to initialized, false
     * otherwise.
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Returns the calculated value.
     *
     * @return the calculated value.
     */
    public abstract float getValue();

    /**
     * Returs a copy (non initialized) of this input connector.
     */
    public abstract InputConnector cloneConnector();

    /**
     * Sets the joint name of this input connector.
     *
     * @param jointName the joint name.
     */
    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    /**
     * Returns the joint name of this input connector.
     *
     * @return the joint name.
     */
    public String getJointName() {
        return jointName;
    }

    /**
     * Returns the joint that is starting point of the calculations.
     *
     * @return the joint
     */
    public Joint getJoint() {
        return joint;
    }

    /**
     * Set the joint that is the starting point of the calculations.
     *
     * @param joint the joint to set
     */
    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName the targetName to set
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    
    /**
     * Returns the target of this calculation.
     * @return the target of the calculation.
     */
    public Spatial getTarget(){
        return target;
    }

    /**
     * @return the attachmentName
     */
    public String getAttachmentName() {
        return attachmentName;
    }

    /**
     * @param attachmentName the attachmentName to set
     */
    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }
    
    public AttachmentPoint getAttachment(){
        return attachment;
    }

    /**
     * Creates an xml representation of this input connector.
     *
     * @return this object as an xml string.
     */
    public abstract String toXML();

    /**
     * Parses the inputnode object and adjust the properties of this
     * inputconnector object.
     *
     * @param inputNode the xml element with the information for this
     * inputconnector object.
     */
    public abstract void fromXML(Node inputNode);

    /**
     * Returns the type of the connector.
     * @return the connector type.
     */
    public abstract ConnectorType getConnectorType();

}
