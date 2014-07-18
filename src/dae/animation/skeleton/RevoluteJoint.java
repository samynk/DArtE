/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.animation.Bone;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.debug.BoneVisualization;
import dae.animation.skeleton.debug.JointVisualization;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class RevoluteJoint extends Prefab implements BodyElement {

    private float minAngle, maxAngle;
    private float currentAngle;
    private float startAngle;
    private Vector3f axis;
    private Vector3f worldAxis;
    private Vector3f location;
    private boolean counterRotating = false;
    private Geometry jg;
    private String group;
    private boolean parallelTargetType = false;
    private Vector3f targetAxis;
    private boolean targetAxisSet = false;
    private boolean logRotations = false;
    private boolean logTranslations = false;
    private float offset = 0.0f;
    private float postScale = 1.0f;
    private RJLogSymbol logSymbol = RJLogSymbol.NONE;
    private float radius;
    private float height;
    private boolean centered;
    private String logName = "error";
    private Material mat;
    // needed for relative transformations
    private Matrix3f initialFrame = Matrix3f.IDENTITY;
    private Matrix3f rotMatrix = new Matrix3f();
    private Quaternion relativeBoneRotation = new Quaternion();
    private Vector3f xAxis = Vector3f.UNIT_X;
    private Vector3f yAxis = Vector3f.UNIT_Y;
    private Vector3f zAxis = Vector3f.UNIT_Z;
    private Vector3f xAxisBackup = Vector3f.UNIT_X;
    private Vector3f yAxisBackup = Vector3f.UNIT_Y;
    private Vector3f zAxisBackup = Vector3f.UNIT_Z;
    // chaining transformation
    private boolean chainWithChild = false;
    private boolean chainWithParent = false;
    private String chainChildName;
    // the bone that is attached to this RevoluteJoint
    private Bone bone;
    // visualization of the joint
    private AssetManager manager;
    private ColorRGBA jointColor = ColorRGBA.Blue;
    
    // maximum allowed changed for the angle
    private float maxAngleChange;

    public RevoluteJoint(Material mat, String name, String group, String targetType, Vector3f location, Vector3f axis, float currentAngle, float minAngle, float maxAngle,
            float radius, float height, boolean centered) {
        setName(name);
        this.group = group;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;

        parallelTargetType = "parallel".equalsIgnoreCase(targetType);

        this.axis = axis;
        this.location = location.clone();

        setCurrentAngle(currentAngle);
        this.setLocalTranslation(location);

        this.setCategory("Animation");
        this.setType("RevoluteJoint");

        this.radius = radius;
        this.height = height;
        this.centered = centered;
        this.mat = mat;
    }

    /**
     * Sets the current angle for the revolute joint.
     *
     * @param angle the current angle.
     */
    final public void setCurrentAngle(float angle) {
        float angleBackup = currentAngle;
        if (angle < minAngle) {
            currentAngle = minAngle;
        } else if (angle > maxAngle) {
            currentAngle = maxAngle;
        } else {
            this.currentAngle = angle;
        }
        updateTransform(this.axis, this.currentAngle - angleBackup);
    }

    /**
     * Gets the current angle.
     *
     * @return the current angle for the rotation.
     */
    final public float getCurrentAngle() {
        return currentAngle;
    }

    private void updateTransform(Vector3f axis, float dAngle) {
        // step 0 : return if difference is too smal.
//        if (FastMath.abs(dangle) < FastMath.ZERO_TOLERANCE) {
//            return;
//        }
        // step 1 : express the axis2 in the current axis system.
        Vector3f localAxis = this.getLocalRotation().mult(axis);
        // step 2 : create a quaternion with this local axis and angle
        Quaternion q = AxisAngleTransform.createAxisAngleTransform(localAxis, dAngle * FastMath.DEG_TO_RAD);
        // step 3 : rotate the current axis system with this quaternion.
        this.xAxis = q.mult(xAxis);
        this.yAxis = q.mult(yAxis);
        this.zAxis = q.mult(zAxis);

        // step 4: create the rotation matrix from the axis system.
        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        rotMatrix.normalizeLocal();
        this.setLocalRotation(rotMatrix);
        // step 4: update the bone, relative to the initial matrix

        updateBoneTransform();

    }

    public void updateBoneTransform() {
        if (chainWithParent) {
            if (parent instanceof RevoluteJointTwoAxis) {
                RevoluteJointTwoAxis rj2 = (RevoluteJointTwoAxis) parent;
                rj2.updateBoneTransform();
            } else if (parent instanceof RevoluteJoint) {
                RevoluteJoint rj = (RevoluteJoint) parent;
                rj.updateBoneTransform();
            }
        }
        if (bone != null) {
            Matrix3f result = initialFrame.mult(rotMatrix);
            if (chainWithChild) {
                Spatial child = this.getChild(chainChildName);
                if (child != null) {
                    Quaternion childRotation = child.getLocalRotation();
                    Matrix3f matrix = childRotation.toRotationMatrix();
                    result.multLocal(matrix);
                }
            }
            relativeBoneRotation.fromRotationMatrix(result);
            bone.setUserTransforms(Vector3f.ZERO, relativeBoneRotation, Vector3f.UNIT_XYZ);
        }
    }

    public boolean isParallelTargetType() {
        return this.parallelTargetType;
    }

    public float moveAngleUp(float amount) {
        float angleBackup = currentAngle;
        setCurrentAngle(currentAngle + amount);
        //updateRotation();
        return currentAngle - angleBackup;
    }

    public float moveAngleDown(float amount) {
        float angleBackup = currentAngle;
        setCurrentAngle(currentAngle - amount);
        //updateRotation();
        return currentAngle - angleBackup;
    }

    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            Node n = (Node)element;
            this.attachChild((Node) element);
            if (manager != null) {
                // create a visualization for the bone.
                Vector3f localTranslation = n.getLocalTranslation();
                if (localTranslation.length() > FastMath.ZERO_TOLERANCE) {
                    // create cylinder from this translation to the origin.
                    BoneVisualization bv = new BoneVisualization(localTranslation.normalize(), 0.005f, localTranslation.length(), 12);
                    Geometry boneGeo = new Geometry("bone", bv); // using our custom mesh object
                    Material boneMat = new Material(manager,
                            "Common/MatDefs/Misc/Unshaded.j3md");
                    boneMat.setColor("Color", new ColorRGBA(32/255.0f,222/255.0f,61/255.0f,1.0f));
                    boneGeo.setMaterial(boneMat);
                    attachChild(boneGeo);
                }
            }
        }
    }

    public boolean isCounterRotating() {
        return counterRotating;
    }

    public void setCounterRotation(boolean counterRotating) {
        this.counterRotating = counterRotating;
    }
    private Vector3f tempOrigin = new Vector3f();
    private Vector3f worldTempOrigin = new Vector3f();
    private Vector3f worldTempAxis = new Vector3f();

    public Vector3f getWorldRotationAxis() {
        this.localToWorld(tempOrigin, worldTempOrigin);
        this.localToWorld(axis, worldTempAxis);
        return worldTempAxis.subtract(worldTempOrigin);
    }

    public void getWorldAxis(Vector3f axisOrigin, Vector3f worldAxis) {
        this.localToWorld(tempOrigin, axisOrigin);
        //jg.localToWorld(tempOrigin, axisOrigin);
        //tempLocalAxis.set( location.x + axis.x,location.y+axis.y,location.z+axis.z);
        this.localToWorld(axis, worldAxis);
        worldAxis.setX(worldAxis.x - axisOrigin.x);
        worldAxis.setY(worldAxis.y - axisOrigin.y);
        worldAxis.setZ(worldAxis.z - axisOrigin.z);

        //worldAxis.set(tempWorldAxis.x-axisOrigin.x,tempWorldAxis.y - axisOrigin.y, tempWorldAxis.z- axisOrigin.z);
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the targetAxis
     */
    public Vector3f getTargetAxis() {
        return targetAxis;
    }

    /**
     * @param targetAxis the targetAxis to set
     */
    public void setTargetAxis(Vector3f targetAxis) {
        this.targetAxis = targetAxis;
        this.targetAxisSet = (targetAxis != null);
    }

    public boolean isTargetAxisSet() {
        return targetAxisSet;
    }

    public void reset() {
        setCurrentAngle(this.startAngle);
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {

                ((BodyElement) s).reset();
            }
        }
    }

    public float getCurrentRotation() {
        return this.currentAngle;
    }

    public void setLogRotations(boolean blog) {
        this.logRotations = blog;
    }

    public boolean logsRotations() {
        return this.logRotations;
    }

    /**
     * @return the offset
     */
    public float getLogOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setLogOffset(float offset) {
        this.offset = offset;
    }

    /**
     * @return the postOffsetScale
     */
    public float getLogPostScale() {
        return postScale;
    }

    public void setLogPostScale(float scale) {
        this.postScale = scale;
    }

    public void setLogSymbol(String symbol) {
        try {
            this.logSymbol = RJLogSymbol.valueOf(symbol);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public RJLogSymbol getLogSymbol() {
        return this.logSymbol;
    }

    void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogTranslation(boolean blogTrans) {
        this.logTranslations = blogTrans;
    }

    public boolean logsTranslations() {
        return logTranslations;
    }

    public float getMinAngle() {
        return minAngle;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    @Override
    public Spatial clone() {
        String targetType = this.parallelTargetType ? "parallel" : "default";
        RevoluteJoint copy = new RevoluteJoint(this.getOriginalMaterial(), this.name, this.group,
                targetType, this.location.clone(), this.axis.clone(), this.currentAngle, this.minAngle, this.maxAngle, this.radius, this.height, this.centered);
        copy.setChaining(chainWithChild, chainWithParent);
        copy.setChainChildName(this.chainChildName);
        copy.setInitialLocalFrame(xAxisBackup, yAxisBackup, zAxisBackup);
        copy.setJointColor(this.jointColor);
        copy.createVisualization(manager);

        for (Spatial child : this.children) {
            if (child instanceof BodyElement) {
                copy.attachBodyElement((BodyElement) child.clone());
            }
        }
        return copy;
    }

    public void createVisualization(AssetManager manager) {
        this.manager = manager;
        // create a visualization for the first rotation axis.
        // attach the axises as children
        JointVisualization jv = new JointVisualization(this.axis, this.radius, this.height, 12);
        Geometry geo1 = new Geometry("axis1", jv); // using our custom mesh object
        Material mat = new Material(manager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", jointColor);
        geo1.setMaterial(mat);

        this.attachChild(geo1);


    }

    public void setAttachedBone(Bone b) {
        this.bone = b;
        b.setUserControl(true);
    }

    public void setChaining(boolean chainwithchild, boolean chainwithparent) {
        this.chainWithChild = chainwithchild;
        this.chainWithParent = chainwithparent;
    }

    public void setChainChildName(String childName) {
        this.chainChildName = childName;
    }

    public void setInitialLocalFrame(Vector3f xa, Vector3f ya, Vector3f za) {
        xAxis = xa.clone();
        yAxis = ya.clone();
        zAxis = za.clone();

        xAxisBackup = xa.clone();
        yAxisBackup = ya.clone();
        zAxisBackup = za.clone();

        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        this.setLocalRotation(rotMatrix);
        this.initialFrame = rotMatrix.clone();
        initialFrame.invertLocal();
    }

    public void setJointColor(ColorRGBA jointColor) {
        this.jointColor = jointColor;
    }

    public  void setCurrentMaxAngleChange(float angle) {
       this.maxAngleChange = angle;
    }
    
    public float getCurrentMaxAngleChange(){
        return maxAngleChange;
    }
    
     @Override
    public void hideTargetObjects(){
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
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
}