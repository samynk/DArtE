/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.physics;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dae.animation.event.TransformListener;
import dae.animation.event.TransformType;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.HingeShape;
import dae.prefabs.standard.CrateObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Koen Samyn
 */
public class JointPrefab extends Prefab implements PropertyChangeListener, TransformListener {

    private Prefab objectA;
    private Vector3f objectAPivot;
    private Prefab objectB;
    private Vector3f objectBPivot;
    private boolean addedToScene = false;
    private HingeJoint joint;
    // joint parameters
    private Vector3f axis = Vector3f.UNIT_Y;
    private float lowerLimit = 0;
    private float upperLimit = FastMath.PI / 2;
    private float velocity = 3;
    private float maxMotorImpulse = 10;
    private float biasFactor = 0.3f;
    private float limitSoftness = 0.9f;
    private float relaxationFactor = 1.0f;
    private PhysicsSpace space;
    // debug visulization.
    private HingeShape hingeShape;

    public JointPrefab() {
        this.setPivot(new Vector3f(0, -1, 0));
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);


        // two example objects are temporarily added to this node.
        // once the hinge joint is added to the scene the children will be detached
        // and added to main scene.
        CrateObject co1 = new CrateObject(new Vector3f(.1f, .1f, .1f));
        co1.create("Object A", manager, null);
        co1.setType("Crate");
        co1.setCategory("Standard");
        this.objectA = co1;
        objectAPivot = new Vector3f(0.5f, 0, 0);
        co1.setLocalTranslation(objectAPivot.negate());

        co1.addTransformListener(this);

        attachChild(co1);

        CrateObject co2 = new CrateObject(new Vector3f(.6f, 0.9f, .05f));
        co2.create("Object B", manager, null);
        co2.setType("Crate");
        co2.setCategory("Standard");
        this.objectB = co2;
        objectBPivot = new Vector3f(-0.3f, 0, 0);
        co2.setLocalTranslation(objectBPivot.negate());
        attachChild(co2);


        objectB.addPropertyListener("dimension", this);

        hingeShape = new HingeShape(this.axis, this.lowerLimit, this.upperLimit);
        hingeShape.create(manager);
        attachChild(hingeShape);
    }

    @Override
    public void addPhysics(PhysicsSpace space) {
        this.space = space;
        Node scene = this.getParent();
        Transform aTransform = objectA.getWorldTransform();
        scene.attachChild(objectA);
        objectA.setLocalTransform(aTransform);

        Transform bTransform = objectB.getWorldTransform();
        scene.attachChild(objectB);
        objectB.setLocalTransform(bTransform);

        objectA.addPhysics(space, 0);
        objectB.addPhysics(space);
        RigidBodyControl rbcB = objectB.getControl(RigidBodyControl.class);
        rbcB.setGravity(Vector3f.ZERO);



        joint = new HingeJoint(objectA.getControl(RigidBodyControl.class), objectB.getControl(RigidBodyControl.class), objectAPivot, objectBPivot, axis, axis);
        joint.setAngularOnly(false);
        updateJoint();
        space.add(joint);

        addedToScene = true;
    }

    public void turnCW() {
//        PhysicsControl pc = objectB.getControl(PhysicsControl.class);
//        pc.setEnabled(true);
        objectB.getControl(RigidBodyControl.class).activate();
        joint.enableMotor(true, velocity, getMaxMotorImpulse());
    }

    public void turnCCW() {
//        PhysicsControl pc = objectB.getControl(PhysicsControl.class);
//        pc.setEnabled(true);
        objectB.getControl(RigidBodyControl.class).activate();
        joint.enableMotor(true, -velocity, getMaxMotorImpulse());
    }

    public void stop() {
//        PhysicsControl pc = objectB.getControl(PhysicsControl.class);
//        pc.setEnabled(false);
        joint.enableMotor(false, 0, 0);
    }

    private void updateJoint() {
        if (joint != null) {
            joint.setLimit(this.lowerLimit, this.upperLimit, this.limitSoftness, this.biasFactor, this.relaxationFactor);
        }

    }

    /**
     * @return the upperLimit
     */
    public float getUpperLimit() {
        return upperLimit * FastMath.RAD_TO_DEG;
    }

    /**
     * @param upperLimit the upperLimit to set
     */
    public void setUpperLimit(float upperLimit) {
        this.upperLimit = upperLimit * FastMath.DEG_TO_RAD;
        hingeShape.updateLimits(this.lowerLimit, this.upperLimit);
        updateJoint();
    }

    /**
     * @return the lowerLimit
     */
    public float getLowerLimit() {
        return lowerLimit * FastMath.RAD_TO_DEG;
    }

    /**
     * @param lowerLimit the lowerLimit to set
     */
    public void setLowerLimit(float lowerLimit) {
        this.lowerLimit = lowerLimit * FastMath.DEG_TO_RAD;
        hingeShape.updateLimits(this.lowerLimit, this.upperLimit);
        updateJoint();
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the biasFactor
     */
    public float getBiasFactor() {
        return biasFactor;
    }

    /**
     * @param biasFactor the biasFactor to set
     */
    public void setBiasFactor(float biasFactor) {
        this.biasFactor = biasFactor;
        updateJoint();
    }

    /**
     * @return the limitSoftness
     */
    public float getLimitSoftness() {
        return limitSoftness;
    }

    /**
     * @param limitSoftness the limitSoftness to set
     */
    public void setLimitSoftness(float limitSoftness) {
        this.limitSoftness = limitSoftness;
        updateJoint();
    }

    /**
     * @return the relaxationFactor
     */
    public float getRelaxationFactor() {
        return relaxationFactor;
    }

    /**
     * @param relaxationFactor the relaxationFactor to set
     */
    public void setRelaxationFactor(float relaxationFactor) {
        this.relaxationFactor = relaxationFactor;
        updateJoint();
    }

    /**
     * @return the maxMotorImpulse
     */
    public float getMaxMotorImpulse() {
        return maxMotorImpulse;
    }

    /**
     * @param maxMotorImpulse the maxMotorImpulse to set
     */
    public void setMaxMotorImpulse(float maxMotorImpulse) {
        this.maxMotorImpulse = maxMotorImpulse;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("dimension")) {
            recreateJoint();
        }
    }

    public void transformChanged(Prefab source, TransformType type) {
        //if (type == TransformType.TRANSLATION || type == Transform) {
        // update the location of this node.
        Vector3f aPivot = joint.getPivotA();
        Vector3f aPivotWorld = objectA.localToWorld(aPivot, null);
        this.setLocalTranslation(aPivotWorld);
        objectB.getControl(RigidBodyControl.class).activate();
        //}
        
        this.setLocalRotation(objectA.getWorldRotation());
    }

    @Override
    public void translationChanged() {
        if (addedToScene) {
            Vector3f world = this.getWorldTranslation();
            objectAPivot = objectA.worldToLocal(world, null);
            recreateJoint();
        }
    }

    private void recreateJoint() {
        if (joint != null) {
            space.remove(joint);
            joint.destroy();
        }
        joint = new HingeJoint(
                objectA.getControl(RigidBodyControl.class),
                objectB.getControl(RigidBodyControl.class),
                new Vector3f(objectAPivot),
                new Vector3f(objectBPivot),
                axis, axis);
        updateJoint();
        this.space.add(joint);
    }
}
