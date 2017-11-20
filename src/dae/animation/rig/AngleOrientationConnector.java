/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dae.animation.skeleton.RevoluteJoint;
import dae.io.XMLUtils;
import dae.util.MathUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This connector calculates the angle difference between two vectors, given an
 * axis of rotation.
 *
 * @author Koen.Samyn
 */
public class AngleOrientationConnector extends InputConnector {

    private Vector3f jointOrientationAxis = Vector3f.UNIT_Y.clone();
    private Vector3f targetOrientationAxis = Vector3f.UNIT_Y.clone();

    @Override
    public float getValue() {
        Vector3f axis = getJoint().getWorldRotationAxis();
        axis.normalizeLocal();

        Vector3f fromVector = getAttachment().getWorldRotation().mult(jointOrientationAxis);
        Vector3f toVector = getTarget().getWorldRotation().mult(targetOrientationAxis);

        MathUtil.project(fromVector, axis);
        MathUtil.project(toVector, axis);
        fromVector.normalizeLocal();
        toVector.normalizeLocal();
        float angle = fromVector.angleBetween(toVector) * FastMath.RAD_TO_DEG;
        if (fromVector.cross(toVector).dot(axis) < 0) {
            angle = -angle;
        }
        return angle;
    }

    @Override
    public InputConnector cloneConnector() {
        AngleOrientationConnector copy = new AngleOrientationConnector();
        copy.jointOrientationAxis = jointOrientationAxis.clone();
        copy.targetOrientationAxis = targetOrientationAxis.clone();

        copy.setJointName(getJointName());
        copy.setTargetName(getTargetName());
        copy.setAttachmentName(getAttachmentName());
        copy.setInitialized(false);
        return copy;
    }

    @Override
    public String toXML() {
        try {
            StringWriter sw = new StringWriter();
            sw.write("<input class='");
            sw.write(this.getClass().getCanonicalName());
            sw.write("' ");
            XMLUtils.writeAttribute(sw, "jointName", getJointName());
            XMLUtils.writeAttribute(sw, "attachmentName", getAttachmentName());
            XMLUtils.writeAttribute(sw, "targetName", getTargetName());
            XMLUtils.writeAttribute(sw, "jointOrientationAxis", this.jointOrientationAxis);
            XMLUtils.writeAttribute(sw, "targetOrientationAxis", this.targetOrientationAxis);
            sw.write("/>\n");
            return sw.toString();
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void fromXML(Node inputNode) {
        NamedNodeMap map = inputNode.getAttributes();
        setJointName(XMLUtils.getAttribute("jointName", map));
        setAttachmentName(XMLUtils.getAttribute("attachmentName", map));
        setTargetName(XMLUtils.getAttribute("targetName", map));
        
        String sjoAxis = XMLUtils.getAttribute("jointOrientationAxis", map);
        this.jointOrientationAxis = XMLUtils.parseFloat3(sjoAxis);

        String stoAxis = XMLUtils.getAttribute("targetOrientationAxis", map);
        this.targetOrientationAxis = XMLUtils.parseFloat3(stoAxis);
    }

    /**
     * @return the targetOrientationAxis
     */
    public Vector3f getTargetOrientationAxis() {
        return targetOrientationAxis;
    }

    /**
     * @param targetOrientationAxis the targetOrientationAxis to set
     */
    public void setTargetOrientationAxis(Vector3f targetOrientationAxis) {
        this.targetOrientationAxis = targetOrientationAxis;
    }

    @Override
    public ConnectorType getConnectorType() {
        return RevoluteJoint.ANGLE_ORIENTATION_TYPE;
    }

}
