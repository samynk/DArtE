/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public class BallJoint extends Node implements BodyElement {

    private float minPhi, maxPhi;
    private float minTheta, maxTheta;
    private float currentTheta, currentPhi;
    private Vector3f axis;
    private Vector3f location;
    private Vector3f rotation;
    private Vector3f jointPosition = new Vector3f();
    private float radius = 0.5f;
    private Geometry ballJointGeometry;
    private Geometry jointGeometry;
    /**
     * Contains the proper local transformation for child nodes.
     */
    private Node childTransformNode;

    public BallJoint(Material mat, Vector3f rotation, Vector3f location, Vector3f axis,
            float currentTheta, float currentPhi,
            float minPhi, float maxPhi,
            float minTheta, float maxTheta) {

        this.rotation = rotation.mult(FastMath.PI / 180.0f);

        rotate(this.rotation.y, this.rotation.x, this.rotation.z);
        setLocalTranslation(location);

        Dome d = new Dome(new Vector3f(0, 0, 0), 6, 12, radius, false);
        ballJointGeometry = new Geometry("jointGeometry", d);
        ballJointGeometry.setShadowMode(ShadowMode.Cast);
        ballJointGeometry.setMaterial(mat);
        this.attachChild(ballJointGeometry);


        Sphere jl = new Sphere(6, 6, radius / 2);
        jointGeometry = new Geometry("jointLocation", jl);
        jointGeometry.setShadowMode(ShadowMode.Off);
        jointGeometry.setMaterial(mat);
        this.attachChild(jointGeometry);


        this.location = location;
        this.axis = axis;

        this.currentTheta = currentTheta * FastMath.PI / 180.0f;
        this.currentPhi = currentPhi * FastMath.PI / 180.0f;
        this.minPhi = minPhi * FastMath.PI / 180.0f;
        this.maxPhi = maxPhi * FastMath.PI / 180.0f;
        this.minTheta = minTheta * FastMath.PI / 180.0f;
        this.maxTheta = maxTheta * FastMath.PI / 180.0f;


        childTransformNode = new Node(this.name + "_tchild");
        this.attachChild(childTransformNode);
        adjustPosition();
    }

    public void movePhiUp(float amount) {
        currentPhi += amount;
        adjustPosition();
    }

    public void movePhiDown(float amount) {
        currentPhi -= amount;
        adjustPosition();
    }

    public void moveThetaUp(float amount) {
        currentTheta += amount;
        adjustPosition();
    }

    public void moveThetaDown(float amount) {
        currentTheta -= amount;
        adjustPosition();
    }

    private void adjustPosition() {
        jointPosition.x = (float) (radius * Math.cos(currentPhi) * Math.sin(currentTheta));
        jointPosition.z = (float) (radius * Math.sin(currentPhi) * Math.sin(currentTheta));
        jointPosition.y = (float) (radius * Math.cos(currentTheta));

        jointGeometry.setLocalTranslation(jointPosition);

        Vector3f rotAxis = this.axis.cross(jointPosition);
        rotAxis = rotAxis.normalize();
        //System.out.println("rotAxis : " + rotAxis);
        float angle = (float) Math.acos(jointPosition.y / radius);
        if (Float.isNaN(angle) || Float.isInfinite(angle)) {
            System.out.println("oh oh");
        }
        Quaternion q = AxisAngleTransform.createAxisAngleTransform(rotAxis, angle);
        childTransformNode.setLocalRotation(q);
    }

    public void attachBodyElement(BodyElement node) {
        if (node instanceof Node) {
            childTransformNode.attachChild((Node) node);
        }
    }

    public void reset() {
    }
    
     public void hideTargetObjects() {
        
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }

    public void showTargetObjects() {
        
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }

    public void write(Writer w, int depth) throws IOException {
        throw new UnsupportedOperationException("Ball joint is no longer supported"); //To change body of generated methods, choose Tools | Templates.
    }
}
