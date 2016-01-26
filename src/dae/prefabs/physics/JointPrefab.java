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
    public void create(AssetManager manager, String extraInfo) {
        // two example objects are temporarily added to this node.
        // once the hinge joint is added to the scene the children will be detached
        // and added to main scene.
       
//        ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Crate");
//        CrateObject co1 = (CrateObject)type.create(manager, "Object A");
//        this.setObjectA(co1);
//        setObjectAPivot(new Vector3f(1.5f, 0, 0));
//        co1.setLocalTranslation(getObjectAPivot().negate());
//
//        attachChild(co1);
//
//        CrateObject co2 = (CrateObject)type.create(manager, "Object B");
//        this.setObjectB(co2);
//        setObjectBPivot(new Vector3f(-0.5f, 0, 0));
//        co2.setLocalTranslation(getObjectBPivot().negate());
//        attachChild(co2);

        hingeShape = new HingeShape(this.axis, this.lowerLimit, this.upperLimit);
        hingeShape.create(manager);
        attachChild(hingeShape);
    }

    @Override
    public void addPhysics(PhysicsSpace space) {
        this.space = space;
        Node scene = this.getParent();
        Transform aTransform = getObjectA().getWorldTransform();
        scene.attachChild(getObjectA());
        getObjectA().setLocalTransform(aTransform);

        Transform bTransform = getObjectB().getWorldTransform();
        scene.attachChild(getObjectB());
        getObjectB().setLocalTransform(bTransform);

        getObjectA().addPhysics(space, 0);
        getObjectB().addPhysics(space);
        RigidBodyControl rbcB = getObjectB().getControl(RigidBodyControl.class);
        rbcB.setGravity(Vector3f.ZERO);



        joint = new HingeJoint(getObjectA().getControl(RigidBodyControl.class), getObjectB().getControl(RigidBodyControl.class), getObjectAPivot(), getObjectBPivot(), axis, axis);
        joint.setAngularOnly(false);
        updateJoint();
        space.add(joint);

        addedToScene = true;
    }

    public void turnCW() {
//        PhysicsControl pc = objectB.getControl(PhysicsControl.class);
//        pc.setEnabled(true);
        getObjectB().getControl(RigidBodyControl.class).activate();
        joint.enableMotor(true, velocity, getMaxMotorImpulse());
    }

    public void turnCCW() {
//        PhysicsControl pc = objectB.getControl(PhysicsControl.class);
//        pc.setEnabled(true);
        RigidBodyControl rbcB = getObjectB().getControl(RigidBodyControl.class);
        if ( rbcB != null)
        {
            rbcB.activate();
            joint.enableMotor(true, -velocity, getMaxMotorImpulse());
        }
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
        Vector3f aPivotWorld = getObjectA().localToWorld(aPivot, null);
        this.setLocalTranslation(aPivotWorld);
        getObjectB().getControl(RigidBodyControl.class).activate();
        //}
        
        this.setLocalRotation(getObjectA().getWorldRotation());
    }

    @Override
    public void translationChanged() {
        if (addedToScene) {
            Vector3f world = this.getWorldTranslation();
            setObjectAPivot(getObjectA().worldToLocal(world, null));
            recreateJoint();
        }
    }

    private void recreateJoint() {
        if ( space == null || objectA == null || objectB == null )
            return;
        if (joint != null) {
            space.remove(joint);
            joint.destroy();
        }
        RigidBodyControl rbcA = getObjectA().getControl(RigidBodyControl.class);
        RigidBodyControl rbcB = getObjectB().getControl(RigidBodyControl.class);
        if ( rbcA == null || rbcB == null)
            return;
        joint = new HingeJoint(
                getObjectA().getControl(RigidBodyControl.class),
                getObjectB().getControl(RigidBodyControl.class),
                new Vector3f(getObjectAPivot()),
                new Vector3f(getObjectBPivot()),
                axis, axis);
        updateJoint();
        this.space.add(joint);
    }

    /**
     * @return the objectA
     */
    public Prefab getObjectA() {
        return objectA;
    }

    /**
     * @param objectA the objectA to set
     */
    public void setObjectA(Prefab objectA) {
        this.objectA = objectA;
        if (this.objectA != null){
            recreateJoint();
        }
    }

    /**
     * @return the objectB
     */
    public Prefab getObjectB() {
        return objectB;
    }

    /**
     * @param objectB the objectB to set
     */
    public void setObjectB(Prefab objectB) {
        this.objectB = objectB;
        if (this.objectB != null){
            recreateJoint();
        }
    }

    /**
     * @return the objectAPivot
     */
    public Vector3f getObjectAPivot() {
        return objectAPivot;
    }

    /**
     * @param objectAPivot the objectAPivot to set
     */
    public void setObjectAPivot(Vector3f objectAPivot) {
        this.objectAPivot = objectAPivot;
    }

    /**
     * @return the objectBPivot
     */
    public Vector3f getObjectBPivot() {
        return objectBPivot;
    }

    /**
     * @param objectBPivot the objectBPivot to set
     */
    public void setObjectBPivot(Vector3f objectBPivot) {
        this.objectBPivot = objectBPivot;
    }
}
