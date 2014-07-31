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
import dae.animation.rig.ConnectorType;
import dae.animation.skeleton.debug.BoneVisualization;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import dae.prefabs.shapes.HingeShape;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Koen
 */
public class RevoluteJoint extends Prefab implements BodyElement {

    private float minAngle, maxAngle;
    private float currentAngle;
    private float startAngle;
    private Vector3f axis;
    private Vector3f location;
    private String group;
    private boolean logRotations = false;
    private boolean logTranslations = false;
    private float offset = 0.0f;
    private float postScale = 1.0f;
    private RJLogSymbol logSymbol = RJLogSymbol.NONE;
    private float radius;
    private float height;
    private boolean centered;
    private String logName = "error";
    // needed for relative transformations
    private Matrix3f initialFrame = Matrix3f.IDENTITY;
    private Quaternion initialFrameQuat = new Quaternion();
    private Matrix3f initialFrameInverse = Matrix3f.IDENTITY;
    private Matrix3f rotMatrix = new Matrix3f();
    private Quaternion relativeBoneRotation = new Quaternion();
    private Quaternion inverseRotation = new Quaternion();
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
    private Spatial visual;
    // maximum allowed changed for the angle
    private float maxAngleChange;
    
    // the possible connectors for this revolute joint
    private final static ArrayList<ConnectorType> supportedInputConnectorTypes =
            new ArrayList<ConnectorType>();
    
     // the possible connectors for this revolute joint
    private final static ArrayList<ConnectorType> supportedOutputConnectorTypes =
            new ArrayList<ConnectorType>();
    
    static{
       ConnectorType ct = new ConnectorType("angletargetrevjoint" ,"Angle target", 
               "This target calculates the angle between two vectors. "
               + "The first vector has the joint location as its origin and the selected attachment point as endpoint."
               + "The second vector has the joint location as its origin and the target as endpoint.", 
               "dae.animation.rig.gui.AngleTargetConnectorPanel");
       supportedInputConnectorTypes.add(ct);
       
       ConnectorType oct = new ConnectorType("anglerevjoint", "Angle",
               "This connector increments the current angle of the joint with the output of teh controller",
               "dae.animation.rig.gui.RevoluteJointOutputConnectorPanel");
       supportedOutputConnectorTypes.add(oct);
    }

    public RevoluteJoint() {
        axis = Vector3f.UNIT_Y.clone();
        location = Vector3f.ZERO;
        currentAngle = 0;
        minAngle = -45;
        maxAngle = 45;

        radius = 0.1f;
        height = 0.4f;
        centered = false;
    }

    public RevoluteJoint(Material mat, String name, String group, Vector3f location, Vector3f axis, float minAngle, float maxAngle,
            float radius, float height, boolean centered) {
        setName(name);
        this.group = group;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;

        this.axis = axis;
        this.location = location.clone();

        this.setLocalTranslation(location);

        this.setCategory("Animation");
        this.setType("RevoluteJoint");

        this.radius = radius;
        this.height = height;
        this.centered = centered;
    }
    
    /**
     * Return the supported input connector types of this joint.
     * @return the supported input connector types.
     */
    public List<ConnectorType> getInputConnectorTypes(){
        return supportedInputConnectorTypes;
    }
    
    /**
     * Return the supported output connector types of this joint.
     * @return the supported input connector types.
     */
    public List<ConnectorType> getOutputConnectorTypes(){
        return supportedOutputConnectorTypes;
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
        // step 5: update the bone, relative to the initial matrix
        updateBoneTransform();
        // step 6 : if a visualization is present, counter rotate the visualization.
        
        if (visual != null) {
            inverseRotation.fromAngleAxis(-dAngle * FastMath.DEG_TO_RAD, axis);
            visual.rotate(inverseRotation);
        }

    }

    /**
     * Because the initial frame is important, recalculate.
     *
     * @param q
     */
    @Override
    public void setLocalPrefabRotation(Quaternion q) {
        if (visual != null) {
            visual.setLocalRotation(Matrix3f.IDENTITY);
        }
        initialFrame = q.toRotationMatrix();
        xAxis = initialFrame.getRow(0);
        yAxis = initialFrame.getRow(1);
        zAxis = initialFrame.getRow(2);

        xAxisBackup = xAxis.clone();
        yAxisBackup = yAxis.clone();
        zAxisBackup = zAxis.clone();

        setLocalRotation(initialFrame);
        initialFrameInverse = initialFrame.invert();

        updateTransform(axis, currentAngle);
    }

    /**
     * Returns the initial local frame rotation.
     *
     * @return
     */
    @Override
    public Quaternion getLocalPrefabRotation() {
        initialFrameQuat.fromRotationMatrix(initialFrame);
        return initialFrameQuat;
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
            Matrix3f result = initialFrameInverse.mult(rotMatrix);
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
            Node n = (Node) element;
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
                    boneMat.setColor("Color", new ColorRGBA(32 / 255.0f, 222 / 255.0f, 61 / 255.0f, 1.0f));
                    boneGeo.setMaterial(boneMat);
                    attachChild(boneGeo);
                }
            }
        }
    }
    private Vector3f tempOrigin = new Vector3f();
    private Vector3f worldTempOrigin = new Vector3f();
    private Vector3f worldTempAxis = new Vector3f();

    public Vector3f getWorldRotationAxis() {
        //this.localToWorld(tempOrigin, worldTempOrigin);
        //this.localToWorld(axis, worldTempAxis);
        //return worldTempAxis.subtract(worldTempOrigin);
        return getWorldRotation().mult(axis);
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

    /**
     * Sets the minimum angle of this joint.
     *
     * @param minAngle the new minimum angle.
     */
    public void setMinAngle(float minAngle) {
        float angleBackup = currentAngle;
        this.minAngle = minAngle;
        if (currentAngle < minAngle) {
            currentAngle = minAngle;
            this.updateTransform(axis, currentAngle - angleBackup);
        }
    }

    /**
     * Returns the minimum angle for this joint.
     *
     * @return the minimum angle.
     */
    public float getMinAngle() {
        return minAngle;
    }

    /**
     * Sets the maximum angle of this joint.
     *
     * @param maxAngle the new maximum angle.
     */
    public void setMaxAngle(float maxAngle) {
        float angleBackup = currentAngle;
        this.maxAngle = maxAngle;
        if (currentAngle > maxAngle) {
            currentAngle = maxAngle;
            this.updateTransform(axis, currentAngle - angleBackup);
        }

    }

    /**
     * Returns the maximum angle for this joint.
     *
     * @return the maximum angle.
     */
    public float getMaxAngle() {
        return maxAngle;
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        createVisualization(manager);
    }

    @Override
    public Spatial clone() {
        RevoluteJoint copy = new RevoluteJoint(this.getOriginalMaterial(), this.name, this.group,
                this.location.clone(), this.axis.clone(), this.minAngle, this.maxAngle, this.radius, this.height, this.centered);
        copy.setChaining(chainWithChild, chainWithParent);
        copy.setChainChildName(this.chainChildName);
        copy.setInitialLocalFrame(xAxisBackup, yAxisBackup, zAxisBackup);
        copy.setJointColor(this.jointColor);
        copy.createVisualization(manager);
        copy.setCurrentAngle(this.currentAngle);

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
        HingeShape hs = new HingeShape(this.axis, minAngle * FastMath.DEG_TO_RAD, maxAngle * FastMath.DEG_TO_RAD);
        hs.create(manager);
        attachChild(hs);
        visual = hs;
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
        initialFrameInverse = initialFrame.invertLocal();
    }

    public void setJointColor(ColorRGBA jointColor) {
        this.jointColor = jointColor;
    }

    public void setCurrentMaxAngleChange(float angle) {
        this.maxAngleChange = angle;
    }

    public float getCurrentMaxAngleChange() {
        return maxAngleChange;
    }

    @Override
    public void hideTargetObjects() {
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).hideTargetObjects();
            }
        }
    }

    @Override
    public void showTargetObjects() {
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).showTargetObjects();
            }
        }
    }

    public void write(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; ++i) {
            w.write('\t');
        }
        w.write("<joint ");
        XMLUtils.writeAttribute(w, "name", this.getName());
        XMLUtils.writeAttribute(w, "type", "revolute");

        // name="Bip01$Head"  type="Revolute" axis="[1,0,0]" group="head" radius="0.01" height="0.1" 
        // rotation="[3.8787262E-9,0.077585526,8.486834E-8]"
        // location="[0.0998286,-8.62694E-9,0.0]"
        // minAngle="-80" maxAngle="80" angle="0"
        // chainwithchild="true" chainchildname="headJointY">
        XMLUtils.writeAttribute(w, "axis", this.axis);
        XMLUtils.writeAttribute(w, "group", this.group);
        XMLUtils.writeAttribute(w, "radius", this.radius);
        XMLUtils.writeAttribute(w, "height", this.height);
        XMLUtils.writeAttribute(w, "location", this.getLocalPrefabTranslation());
       
        XMLUtils.writeAttribute(w, "refaxisx", xAxisBackup);
        XMLUtils.writeAttribute(w, "refaxisy", yAxisBackup);
        XMLUtils.writeAttribute(w, "refaxisz", zAxisBackup);
        XMLUtils.writeAttribute(w, "minAngle", this.minAngle);
        XMLUtils.writeAttribute(w, "maxAngle", this.maxAngle);
        XMLUtils.writeAttribute(w, "angle", this.currentAngle);
        XMLUtils.writeAttribute(w, "chainwithchild", this.chainWithChild);
        if (chainWithChild) {
            XMLUtils.writeAttribute(w, "chainchildname", this.chainChildName);
        }
        XMLUtils.writeAttribute(w, "chainwithparent", this.chainWithParent);

        boolean hasBodyElements = false;
        for (Spatial child : this.getChildren()) {
            if (child instanceof BodyElement) {
                hasBodyElements = true;
                break;
            }
        }

        if (!hasBodyElements) {
            w.write("/>\n");
        } else {
            w.write(">\n");
            for (Spatial child : this.getChildren()) {
                if (child instanceof BodyElement) {
                    ((BodyElement) child).write(w, depth + 1);
                }
            }
            for (int i = 0; i < depth; ++i) {
                w.write('\t');
            }
            w.write("</joint>\n");
        }
    }

    /**
     * Returns an extra rotation for the gizmo.
     * @return an extra rotation for the gizmo.
     */
    @Override
    public Quaternion getGizmoRotation() {
        return visual.getLocalRotation();
    }
    
    
}