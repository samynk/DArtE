package dae.animation.rig;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dae.animation.skeleton.RevoluteJointTwoAxis;

/**
 * This input connector calculates the angle between the attachment point
 * and the target with two degrees of freedom.
 * @author Koen Samyn
 */
public class AngleTargetDof2Connector extends AngleTargetConnector {

   

    public AngleTargetDof2Connector() {
    }

    @Override
    public float getValue() {
        
        Vector3f origin = getJoint().getWorldTranslation();

        Vector3f apLoc = attachment.getWorldTranslation();
        Vector3f targetLoc = target.getWorldTranslation();

        Vector3f vector1 = apLoc.subtract(origin);
        Vector3f vector2 = targetLoc.subtract(origin);
        
        Vector3f axis = vector1.cross(vector2);
        vector1.normalizeLocal();
        vector2.normalizeLocal();
        
        axis.normalizeLocal();
        getJoint().setWorldRotationAxis(axis);
        
        float angle = vector1.angleBetween(vector2) * FastMath.RAD_TO_DEG;

        return angle;
    }

    @Override
    public InputConnector cloneConnector() {
        AngleTargetDof2Connector ac = new AngleTargetDof2Connector();
        ac.setAttachmentName(getAttachmentName());
        ac.setJointName(getJointName());
        ac.setTargetName(getTargetName());
        return ac;
    }

    @Override
    public ConnectorType getConnectorType() {
        return RevoluteJointTwoAxis.ANGLE_TARGET_TYPE;
    }
    
    
}
