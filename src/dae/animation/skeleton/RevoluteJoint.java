package dae.animation.skeleton;

import com.jme3.animation.Bone;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.rig.ConnectorType;
import dae.animation.rig.Joint;
import dae.animation.skeleton.debug.BoneVisualization;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.gizmos.RotateGizmo;
import dae.prefabs.shapes.HingeShape;
import dae.project.ProjectTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Koen
 */
public class RevoluteJoint extends JointPrefab implements BodyElement {

    private float minAngle, maxAngle;
    private float currentAngle;
    private float startAngle;

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
    // the bone that is attached to this RevoluteJoint
    private Bone bone;
    // visualization of the joint
    private AssetManager manager;
    private ColorRGBA jointColor = ColorRGBA.Blue;
    private HingeShape visual;
    // maximum allowed changed for the angle
    private float maxAngleChange;
    // the possible connectors for this revolute joint
    private final static ArrayList<ConnectorType> supportedInputConnectorTypes
            = new ArrayList<ConnectorType>();
    // the possible connectors for this revolute joint
    private final static ArrayList<ConnectorType> supportedOutputConnectorTypes
            = new ArrayList<ConnectorType>();

    public static ConnectorType ANGLE_TARGET_TYPE;
    public static ConnectorType ANGLE_ORIENTATION_TYPE;

    static {
        ANGLE_TARGET_TYPE = new ConnectorType("angletargetrevjoint", "Angle target",
                "This target calculates the angle between two vectors. "
                + "The first vector has the joint location as its origin and the selected attachment point as endpoint."
                + "The second vector has the joint location as its origin and the target as endpoint.",
                "dae.animation.rig.gui.AngleTargetConnectorPanel");
        supportedInputConnectorTypes.add(ANGLE_TARGET_TYPE);

        ANGLE_ORIENTATION_TYPE = new ConnectorType("angleorientationrevjoint", "Orientation target", "angleorientationrevjoint",
                "dae.animation.rig.gui.AngleOrientationConnectorPanel");
        supportedInputConnectorTypes.add(ANGLE_ORIENTATION_TYPE);

        ConnectorType oct = new ConnectorType("anglerevjoint", "Angle",
                "This connector increments the current angle of the joint with the output of the controller",
                "dae.animation.rig.gui.RevoluteJointOutputConnectorPanel");
        supportedOutputConnectorTypes.add(oct);
    }

    public RevoluteJoint() {
        currentAngle = 0;
        minAngle = -45;
        maxAngle = 45;

        radius = 0.1f;
        height = 0.4f;
        centered = false;
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        createVisualization(manager);
    }

    @Override
    public boolean hasSavableChildren() {
        return this.hasChildren();
    }

    /**
     * Return the supported input connector types of this joint.
     *
     * @return the supported input connector types.
     */
    @Override
    public List<ConnectorType> getInputConnectorTypes() {
        return supportedInputConnectorTypes;
    }

    /**
     * Return the supported output connector types of this joint.
     *
     * @return the supported input connector types.
     */
    @Override
    public List<ConnectorType> getOutputConnectorTypes() {
        return supportedOutputConnectorTypes;
    }

    /**
     * Sets the current angle for the revolute joint.
     *
     * @param angle the current angle.
     */
    final public void setCurrentAngle(float angle) {
        if (angle < minAngle) {
            currentAngle = minAngle;
        } else if (angle > maxAngle) {
            currentAngle = maxAngle;
        } else {
            this.currentAngle = angle;
        }
        updateTransform(getLocalAxis(), this.currentAngle);
    }

    @Override
    public void rotate(float angle) {
        setCurrentAngle(getCurrentAngle() + angle * FastMath.RAD_TO_DEG);
    }

    /**
     * Gets the current angle.
     *
     * @return the current angle for the rotation.
     */
    final public float getCurrentAngle() {
        return currentAngle;
    }

    

    private void updateTransform(Vector3f axis, float angle) {
        //System.out.println("rotating over: " + angle);
        Quaternion q = getTransformNode().getLocalRotation();
        q.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);
        // step 5: update the bone, relative to the initial matrix
        updateBoneTransform();
    }

    public void updateBoneTransform() {
        // todo calculate relative bone transform.
        if (bone != null) {
            // bone.setUserTransforms(Vector3f.ZERO, relativeBoneRotation, Vector3f.UNIT_XYZ);
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

    @Override
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

    @Override
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
        visual.updateLimits(-minAngle, -maxAngle);
        if (currentAngle < minAngle) {
            currentAngle = minAngle;
            this.updateTransform(getLocalAxis(), currentAngle);
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
        visual.updateLimits(-this.minAngle, -maxAngle);
        if (currentAngle > maxAngle) {
            currentAngle = maxAngle;
            this.updateTransform(getLocalAxis(), currentAngle);
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

    /**
     * Sets the render options for this revolute joint.
     *
     * @param radius the radius of this revolute joint.
     * @param height the height of this revolute joint.
     * @param centered center the revolute joint around the location.
     */
    public void setRenderOptions(float radius, float height, boolean centered) {
        this.setRadius(radius);
        this.setHeight(height);
        this.setCentered(centered);
    }

    public void createVisualization(AssetManager manager) {
        this.manager = manager;
        // create a visualization for the first rotation axis.
        // attach the axises as children
        HingeShape hs = new HingeShape(getLocalAxis(), -minAngle * FastMath.DEG_TO_RAD, -maxAngle * FastMath.DEG_TO_RAD);
        hs.create(manager);
        super.attachChild(hs);
        visual = hs;
    }

    public void setAttachedBone(Bone b) {
        this.bone = b;
        b.setUserControl(true);
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

    /**
     * Returns an extra rotation for the gizmo.
     *
     * @return an extra rotation for the gizmo.
     */
    @Override
    public Quaternion getGizmoRotation() {
        return visual.getLocalRotation();
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return the centered
     */
    public boolean getCentered() {
        return centered;
    }

    /**
     * @param centered the centered to set
     */
    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    /**
     * @return the jointColor
     */
    public ColorRGBA getJointColor() {
        return jointColor;
    }
}
