/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig;

import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.RevoluteJoint;
import dae.io.XMLUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Koen Samyn
 */
public class RevoluteJointOutputConnector implements OutputConnector {

    private String jointName;
    private RevoluteJoint joint;
    private boolean initialized;
    /**
     * The factor acts as a multiplier for the output of the controller.
     * This is a quick way to change the controller if the controller is
     * too fast or too slow.
     */
    private float factor;

    public RevoluteJointOutputConnector() {
    }

    public void initialize(Rig rig) {
        initialized = false;
        Spatial s = rig.getChild(jointName);
        if (s != null && s instanceof RevoluteJoint) {
            joint = (RevoluteJoint) s;
            initialized = true;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setValue(float value) {
        if ( joint != null)
        {
            joint.setCurrentAngle(joint.getCurrentAngle() + value);
        }
    }

    /**
     * @return the jointName
     */
    public String getJointName() {
        return jointName;
    }

    /**
     * @param jointName the jointName to set
     */
    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    public OutputConnector cloneConnector() {
        RevoluteJointOutputConnector rjoc = new RevoluteJointOutputConnector();
        rjoc.setJointName(jointName);
        rjoc.initialized = false;
        return rjoc;
    }

    /**
     * @return the factor
     */
    public float getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(float factor) {
        this.factor = factor;
    }
    
    /**
     * Creates an xml representation of this input connector.
     * @return this object as an xml string.
     */
    public String toXML() {
        try {
            StringWriter sw = new StringWriter();
            sw.write("<output class='dae.animation.rig.RevoluteJointOutputConnector' " );
            XMLUtils.writeAttribute( sw, "jointName", jointName);
            XMLUtils.writeAttribute( sw, "factor", factor);
            sw.write("/>\n");
            return sw.toString();
        } catch (IOException ex) {
            Logger.getLogger(AngleTargetConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    /**
     * Fills the properties of this inputconnector from the properties in the xml file.
     * @param outputNode the xml node with the definition of this output connector.
     */
    public void fromXML( Node outputNode){
        NamedNodeMap map = outputNode.getAttributes();
        this.jointName = XMLUtils.getAttribute("jointName", map);
        this.factor = XMLUtils.parseFloat("factor", map);
    }
}
