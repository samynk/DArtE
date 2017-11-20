package dae.animation.rig;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.AttachmentPoint;
import dae.animation.skeleton.RevoluteJoint;
import dae.io.XMLUtils;
import dae.prefabs.standard.PrefabPlaceHolder;
import dae.util.MathUtil;
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
public class AngleTargetConnector extends InputConnector {

    private String targetName;
    private String attachmentName;

    

    public AngleTargetConnector() {
    }

    

    @Override
    public void initialize(Rig rig) {
        
    }

    @Override
    public float getValue() {
        Vector3f axis = getJoint().getWorldRotationAxis();
        axis.normalizeLocal();
        Vector3f origin = getJoint().getWorldTranslation();

        Vector3f apLoc = attachment.getWorldTranslation();
        Vector3f targetLoc = target.getWorldTranslation();

        Vector3f vector1 = apLoc.subtract(origin);
        Vector3f vector2 = targetLoc.subtract(origin);

        MathUtil.project(vector1, axis);
        MathUtil.project(vector2, axis);
        vector1.normalizeLocal();
        vector2.normalizeLocal();
        float angle = vector1.angleBetween(vector2) * FastMath.RAD_TO_DEG;
        if (vector1.cross(vector2).dot(axis) > 0) {
            angle = -angle;
        }
        return angle;
    }

    @Override
    public InputConnector cloneConnector() {
        AngleTargetConnector ac = new AngleTargetConnector();
        ac.setAttachmentName(attachmentName);
        ac.setJointName(getJointName());
        ac.setTargetName(targetName);
        ac.setInitialized(false);
        return ac;
    }

    /**
     * Creates an xml representation of this input connector.
     *
     * @return this object as an xml string.
     */
    @Override
    public String toXML() {
        try {
            StringWriter sw = new StringWriter();
            sw.write("<input class='");
            sw.write(this.getClass().getCanonicalName());
            sw.write("' ");
            XMLUtils.writeAttribute(sw, "jointName", getJointName());
            XMLUtils.writeAttribute(sw, "attachmentName", attachmentName);
            XMLUtils.writeAttribute(sw, "targetName", targetName);
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
        this.attachmentName = XMLUtils.getAttribute("attachmentName", map);
        this.targetName = XMLUtils.getAttribute("targetName", map);
    }

    @Override
    public ConnectorType getConnectorType() {
        return RevoluteJoint.ANGLE_TARGET_TYPE;
    }
}
