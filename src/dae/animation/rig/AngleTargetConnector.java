/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.AttachmentPoint;
import dae.animation.skeleton.RevoluteJoint;

/**
 *
 * @author Koen Samyn
 */
public class AngleTargetConnector implements InputConnector {

    private String jointName;
    private String targetName;
    private String attachmentName;
    private RevoluteJoint joint;
    private AttachmentPoint attachment;
    private Spatial target;
    private boolean initialized = false;

    public AngleTargetConnector() {
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

    public void initialize(Rig rig) {
        Spatial sjoint = rig.getChild(jointName);
        if (sjoint instanceof RevoluteJoint) {
            this.joint = (RevoluteJoint) sjoint;
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
        Node top = rig;
        while (top.getParent() != null) {
            top = top.getParent();
        }
        target = top.getChild(targetName);
        if (target != null) {
            initialized = true;
        }

    }

    public float getValue() {
        Vector3f axis = joint.getWorldRotationAxis();
        axis.normalizeLocal();
        Vector3f origin = joint.getWorldTranslation();

        Vector3f apLoc = attachment.getWorldTranslation();
        Vector3f targetLoc = target.getWorldTranslation();

        Vector3f vector1 = apLoc.subtract(origin);
        Vector3f vector2 = targetLoc.subtract(origin);
        
        this.project(vector1, axis);
        this.project(vector2, axis);
        vector1.normalizeLocal();
        vector2.normalizeLocal();
        float angle = vector1.angleBetween(vector2) * FastMath.RAD_TO_DEG;
        if (vector1.cross(vector2).dot(axis) > 0) {
            angle = -angle;
        }
        return angle;
    }
    
    private void project(Vector3f toProject, Vector3f axis) {
        float dot = axis.dot(toProject);
        toProject.x = toProject.x - dot * axis.x;
        toProject.y = toProject.y - dot * axis.y;
        toProject.z = toProject.z - dot * axis.z;
    }
}
