/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.animation.Bone;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import dae.animation.skeleton.debug.BoneVisualization;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import java.io.IOException;
import java.io.Writer;

/**
 * A Revolute joint that can rotate around two axis. This is useful when
 * constructing a skeleton object.
 *
 * @author Koen Samyn
 */
public class RevoluteJointTwoAxis extends Prefab implements BodyElement {

    /**
     * The location of the axis.
     */
    private Vector3f location;
    /**
     * The first axis.
     */
    private Vector3f axis1;// = Vector3f.UNIT_Y;
    /**
     * The label for the first axis.
     */
    private String labelAxis1;
    /**
     * The second axis.
     */
    private Vector3f axis2;// = Vector3f.UNIT_Z;
    /**
     * The lable for the second axis.
     */
    private String labelAxis2;
    /**
     * The current angle of the first axis.
     */
    private float currentAngle1 = 0.0f;
    /**
     * The current angle of the second axis.
     */
    private float currentAngle2 = 0.0f;
    /**
     * The axis system. This axis system will be rotated by the angle
     * operations.
     */
    private Vector3f xAxis = new Vector3f(1, 0, 0);
    private Vector3f yAxis = new Vector3f(0, 1, 0);
    private Vector3f zAxis = new Vector3f(0, 0, 1);
    private Vector3f xAxisBackup = new Vector3f(1, 0, 0);
    private Vector3f yAxisBackup = new Vector3f(0, 1, 0);
    private Vector3f zAxisBackup = new Vector3f(0, 0, 1);
    Matrix3f rotMatrix = new Matrix3f();
    // angle constraints
    private float minAngle1 = Float.MIN_VALUE;
    private float maxAngle1 = Float.MAX_VALUE;
    private float minAngle2 = Float.MIN_VALUE;
    private float maxAngle2 = Float.MAX_VALUE;
    // meta data
    private String group;
    // visualization
    private float radius = 0.1f;
    private float height = 0.6f;
    // the connected bone
    private Bone bone;
    // the initial frame
    private Matrix3f initialFrame = Matrix3f.IDENTITY;
    private Matrix3f initialFrameInverted = Matrix3f.IDENTITY;
    // the relative rotation;
    private Quaternion relativeBoneRotation = new Quaternion();
    // chaining transformation
    private boolean chainWithChild = false;
    private boolean chainWithParent = false;
    private String chainChildName = "";
    private Geometry cgeo1;
    private AssetManager manager;
    // maximum allowed changes
    private float maxAngle1Change;
    private float maxAngle2Change;

    /**
     * The default constructor creates a RevoluteJointTwoAxis object with the
     * first axis [0,1,0] and label Y and the second axis [0,0,1] and labl Z.
     */
    public RevoluteJointTwoAxis(Material mat, String name, String group, Vector3f location, float radius) {
        super.setName(name);
        axis1 = new Vector3f(0, 1, 0);
        labelAxis1 = "Y";
        axis2 = new Vector3f(0, 0, 1);
        labelAxis2 = "Z";

        this.setCategory("Animation");
        this.setType("RevoluteJointTwoAxis");

//        Mesh joint;
//
//        joint = new Sphere(12, 12, radius);
//        jg = new Geometry(name + "_joint", joint);
//        jg.setShadowMode(RenderQueue.ShadowMode.Cast);
//        jg.setMaterial(mat);
//
//        this.attachChild(jg);

        this.setLocalTranslation(location);
        this.group = group;

        this.radius = radius;
        this.height = 3 * radius;
    }

    /**
     * Creates a new RevoluteJointTwoAxis system with the specified axises and
     * labels.
     *
     * @param axis1 the first axis.
     * @param label1 the label of the first axis.
     * @param axis2 the second axis.
     * @param label2 the label of the second axis.
     */
    public RevoluteJointTwoAxis(Vector3f axis1, String label1, Vector3f axis2, String label2, Material mat, String name, String group, Vector3f location, float radius) {
        this.axis1 = axis1;
        this.labelAxis1 = label1;
        this.axis2 = axis2;
        this.labelAxis2 = label2;
        this.location = location;

        super.setName(name);

        this.setCategory("Animation");
        this.setType("RevoluteJointTwoAxis");

        this.setLocalTranslation(location);
        this.radius = radius;
        this.height = 6 * radius;
        this.group = group;
    }

    /**
     * Returns the group this joint belongs to.
     *
     * @return the group of this joint.
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return the currentAngle1
     */
    public float getCurrentAngle1() {
        return currentAngle1;
    }

    /**
     * @param currentAngle1 the currentAngle1 to set
     */
    public void setCurrentAngle1(float currentAngle1) {
        float angleBackup = this.currentAngle1;
        if (currentAngle1 > this.maxAngle1) {
            this.currentAngle1 = maxAngle1;
        } else if (currentAngle1 < this.minAngle1) {
            this.currentAngle1 = minAngle1;
        } else {
            this.currentAngle1 = currentAngle1;
        }
        updateTransform(this.axis1, this.currentAngle1 - angleBackup);
        //updateTransforms();
    }

    /**
     * @return the currentAngle2
     */
    public float getCurrentAngle2() {
        return currentAngle2;
    }

    /**
     * @param currentAngle2 the currentAngle2 to set
     */
    public void setCurrentAngle2(float currentAngle2) {
        float angleBackup = this.currentAngle2;
        if (currentAngle2 > this.maxAngle2) {
            this.currentAngle2 = maxAngle2;
        } else if (currentAngle2 < this.minAngle2) {
            this.currentAngle2 = minAngle2;
        } else {
            this.currentAngle2 = currentAngle2;
        }
        updateTransform(this.axis2, this.currentAngle2 - angleBackup);
        //updateTransforms();
    }

    private void updateTransforms() {
        // currentAngle1 : de volledige hoek over as 1
        // axis 1 : de as waarover gedraaid wordt, maar uitgedrukt in het originele (niet getransformeerde
        // assenstelsel van de bone.
        // quaternion q is dan de voorstelling van de rotatie van currentAngle1 graden over de lokale originele as.
        // initialFrame: de oorspronkelijke rotatiematrix van de bone (t-pose)
        // initialFrame en (xAxisBackup,yAxisBackup en zAxisBackup) zijn equivalent.
        Vector3f localAxis1 = initialFrame.mult(axis1);
        Quaternion q = AxisAngleTransform.createAxisAngleTransform(localAxis1, currentAngle1 * FastMath.DEG_TO_RAD);
        // nu wordt het originele assenstelsel (T-pose) van de bone geroteerd over dit aantal graden en 
        // opgeslagen in de drie vectoren xAxis, yAxis, zAxis, deze vormen het nieuwe lokale assenstelsel.
        q.mult(xAxisBackup, xAxis);
        q.mult(yAxisBackup, yAxis);
        q.mult(zAxisBackup, zAxis);

        // xAxis , yAxis en zAxis bepalen nu de nieuwe transformatie matrix.
        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        // axis2 : is de originele as, maar die moet nu wel uitgedrukt worden in het geroteerde system.
        Vector3f localAxis2 = rotMatrix.mult(axis2);
        // q is nu de rotatie van currentAngle2 graden over de tweede lokale as.
        q = AxisAngleTransform.createAxisAngleTransform(localAxis2, currentAngle2 * FastMath.DEG_TO_RAD);
        // lokale systeem nu verder roteren met dit quaternion.
        q.multLocal(xAxis);
        q.multLocal(yAxis);
        q.multLocal(zAxis);


        // rotMatrix maken met behulp van deze 3 nieuwe assen.
        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        this.setLocalRotation(rotMatrix);
        updateBoneTransform();
    }

    private void updateTransform(Vector3f axis, float dangle) {
        // step 0 : return if difference is too smal.
//        if (FastMath.abs(dangle) < FastMath.ZERO_TOLERANCE) {
//            return;
//        }



        // step 1 : express the axis in the current axis system.
        Vector3f localAxis = this.getLocalRotation().mult(axis);
        // step 2 : create a quaternion with this local axis and angle
        Quaternion q = AxisAngleTransform.createAxisAngleTransform(localAxis, dangle * FastMath.DEG_TO_RAD);
        // step 3 : rotate the current axis system with this quaternion.
        q.multLocal(xAxis);
        q.multLocal(yAxis);
        q.multLocal(zAxis);

        // step 4: create the rotation matrix from the axis system.
        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        rotMatrix.normalizeLocal();
        this.setLocalRotation(rotMatrix);
        // step 5: update the bone, relative to the initial matrix

        updateBoneTransform();



        // update the constraint visualization
        if (axis == axis1 && cgeo1 != null) {
            Quaternion q2 = AxisAngleTransform.createAxisAngleTransform(axis1, -dangle * FastMath.DEG_TO_RAD);
            cgeo1.rotate(q2);
        }
    }

    public void updateBoneTransform() {
        if (bone != null) {
            Matrix3f result = initialFrameInverted.mult(rotMatrix);
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

    /**
     * Returns the first axis in world space.
     *
     * @return the first axis in world space.
     */
    public Vector3f getWorldAxis1() {
        return getWorldRotation().mult(axis1);
    }

    /**
     * Returns the second axis in world space.
     *
     * @return the second axis in world space.
     */
    public Vector3f getWorldAxis2() {
        return getWorldRotation().mult(axis2);
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
                    BoneVisualization bv = new BoneVisualization(localTranslation.normalize(), 0.001f, localTranslation.length(), 12);
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

    public void reset() {
        currentAngle1 = 0;
        currentAngle2 = 0;
    }

    @Override
    public Spatial clone() {

        RevoluteJointTwoAxis copy = new RevoluteJointTwoAxis(
                axis1.clone(),
                labelAxis1,
                axis2.clone(),
                labelAxis2,
                this.getOriginalMaterial(),
                this.name,
                this.group,
                this.location.clone(),
                this.radius);
        copy.setAngleConstraints(minAngle1, maxAngle1, minAngle2, maxAngle2);
        copy.setInitialLocalFrame(xAxisBackup, yAxisBackup, zAxisBackup);
        copy.createVisualization(this.manager);
        copy.setChaining(chainWithChild, chainWithParent);
        copy.setChainChildName(chainChildName);
        for (Spatial child : this.children) {
            if (child instanceof BodyElement) {
                copy.attachBodyElement((BodyElement) child.clone());
            }
        }

        return copy;
    }

    public void setAngleConstraints(float minAngle1, float maxAngle1, float minAngle2, float maxAngle2) {
        this.minAngle1 = minAngle1;
        this.maxAngle1 = maxAngle1;
        this.minAngle2 = minAngle2;
        this.maxAngle2 = maxAngle2;
    }

    public void setInitialLocalFrame(Vector3f r) {
        Quaternion q = new Quaternion();
        q.fromAngles(r.x, r.y, r.z);
        Vector3f[] axes = new Vector3f[3];
        q.toAxes(axes);

        xAxis = axes[0];
        yAxis = axes[1];
        zAxis = axes[2];

        rotMatrix.fromAxes(xAxis, yAxis, zAxis);
        this.setLocalRotation(rotMatrix);
        this.initialFrameInverted = rotMatrix.clone();
        initialFrameInverted.invertLocal();
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
        this.initialFrameInverted = rotMatrix.clone();
        initialFrameInverted.invertLocal();

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

    /**
     * Creates a visualization of this joint.
     */
    public void createVisualization(AssetManager assetManager) {
        this.manager = assetManager;
        // create a visualization for the first rotation axis.
        // attach the axises as children
        Mesh firstJoint = createJoint(this.axis1, 12);
        Geometry geo1 = new Geometry("axis1", firstJoint); // using our custom mesh object
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geo1.setMaterial(mat);

        this.attachChild(geo1);
        // visualize the constraints of this first axis
        Mesh firstConstraint = createConstraint(axis1, height, radius / 4, minAngle1, maxAngle1, ColorRGBA.Orange, ColorRGBA.Pink, 12, new ColorRGBA(1, 0, 0, 0.2f));
        cgeo1 = new Geometry("constraint1", firstConstraint);
        Material matc = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matc.setBoolean("VertexColor", true);
        matc.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        cgeo1.setMaterial(matc);
        this.attachChild(cgeo1);

        // create a visualization for the second rotation axis.
        // attach the axises as children
        Mesh secondJoint = createJoint(this.axis2, 12);
        Geometry geo2 = new Geometry("axis2", secondJoint); // using our custom mesh object
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Green);
        geo2.setMaterial(mat2);

        this.attachChild(geo2);


    }

    /**
     * Creates a joint visualization
     */
    private Mesh createJoint(Vector3f axis, int sides) {
        Vector3f z = axis.normalize();
        Vector3f x, y;
        if (z.x > z.y && z.x > z.z) {
            // x component is largest so cross with unit.y
            x = z.cross(Vector3f.UNIT_Y);
            y = x.cross(z);
        } else {
            // y or z component is largest so cross with unit.x
            x = z.cross(Vector3f.UNIT_X);
            y = x.cross(z);
        }
        x.normalizeLocal();
        y.normalizeLocal();
        x.multLocal(radius);
        y.multLocal(radius);

        // bottom vertex 
        Vector3f bo = z.mult(-height / 2);
        Vector3f to = z.mult(height / 2);
        Vector3f[] vertices = new Vector3f[sides * 2];
        int[] indices = new int[sides * 6];

        for (int i = 0; i < sides; ++i) {
            float angle = 2 * i * (FastMath.PI / sides);
            Vector3f xo = x.mult(FastMath.cos(angle));
            Vector3f yo = y.mult(FastMath.sin(angle));
            Vector3f offset = xo.add(yo);
            vertices[i] = bo.add(offset);
            vertices[i + sides] = to.add(offset);

            // bottom vertices
            int i1 = i;
            int i2 = (i + 1) % sides;
            int i3 = i + sides;
            int i4 = i2 + sides;

            int ti = i * 6;
            indices[ti] = i3;
            indices[ti + 1] = i2;
            indices[ti + 2] = i1;

            indices[ti + 3] = i4;
            indices[ti + 4] = i2;
            indices[ti + 5] = i3;
        }

        Mesh result = new Mesh();
        result.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        result.updateBound();
        return result;
    }

    private Mesh createConstraint(Vector3f axis, float radius, float constraintWidth, float minAngle, float maxAngle, ColorRGBA minColor, ColorRGBA maxColor, int fanSides, ColorRGBA fanColor) {
        // TODO: assumption is that the direction of the bone is [1,0,0] , maybe
        // this should be made configurable.

        int[] indices = new int[6 * 3 * 2 + fanSides * 3];
        Vector3f[] vertices = new Vector3f[8 * 2 + fanSides + 1];
        float[] colors = new float[vertices.length * 4];
        // 1 create lower constraint, a box with minColor vertex colors.
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(radius, constraintWidth, constraintWidth);
        vertices[2] = new Vector3f(radius, -constraintWidth, +constraintWidth);
        vertices[3] = new Vector3f(radius, -constraintWidth, -constraintWidth);
        vertices[4] = new Vector3f(radius, constraintWidth, -constraintWidth);

        // rotate these vertices with the minAngle along the axis
        Quaternion q = new Quaternion();
        q.fromAngleAxis(minAngle * FastMath.DEG_TO_RAD, axis);

        for (int i = 0; i < 5; ++i) {
            q.multLocal(vertices[i]);
            setColor(colors, i, minColor);
        }

        // create the indices for the first constraint
        createTriangle(indices, 0, 0, 1, 2);
        createTriangle(indices, 1, 0, 2, 3);
        createTriangle(indices, 2, 0, 3, 4);
        createTriangle(indices, 3, 0, 4, 1);
        createTriangle(indices, 4, 1, 2, 3);
        createTriangle(indices, 5, 3, 2, 4);


        // 2) create upper constraint, a box with maxColor vertex colors.
        vertices[5] = new Vector3f(0, 0, 0);
        vertices[6] = new Vector3f(radius, constraintWidth, constraintWidth);
        vertices[7] = new Vector3f(radius, -constraintWidth, +constraintWidth);
        vertices[8] = new Vector3f(radius, -constraintWidth, -constraintWidth);
        vertices[9] = new Vector3f(radius, constraintWidth, -constraintWidth);

        // rotate these vertices with the minAngle along the axis
        q.fromAngleAxis(maxAngle * FastMath.DEG_TO_RAD, axis);

        for (int i = 5; i < 10; ++i) {
            q.multLocal(vertices[i]);
            setColor(colors, i, maxColor);
        }

        // create the indices for the second constraint
        createTriangle(indices, 6, 5, 6, 7);
        createTriangle(indices, 7, 5, 7, 8);
        createTriangle(indices, 8, 5, 8, 9);
        createTriangle(indices, 9, 5, 9, 6);
        createTriangle(indices, 10, 6, 7, 8);
        createTriangle(indices, 11, 8, 7, 9);

        // create the fan with the number of sides.

        float diff = (maxAngle - minAngle) / (fanSides - 1);

        int vo = 10;
        vertices[vo] = new Vector3f(0, 0, 0);
        setColor(colors, vo, fanColor);
        int vsi = 1;
        for (float angle = minAngle; angle < maxAngle; angle += diff) {
            q.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);
            vertices[vo + vsi] = new Vector3f(3 * radius / 4.0f, 0, 0);
            setColor(colors, vo + vsi, fanColor);
            q.multLocal(vertices[vo + vsi]);

            if (vsi < fanSides) {
                createTriangle(indices, 11 + vsi, vo, vo + vsi, vo + vsi + 1);
            }
            ++vsi;
        }

        Mesh result = new Mesh();
        result.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(colors));
        result.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        result.updateBound();
        return result;
    }

    private void setColor(float[] colors, int index, ColorRGBA color) {
        int si = index * 4;
        colors[si] = color.r;
        colors[si + 1] = color.g;
        colors[si + 2] = color.b;
        colors[si + 3] = color.a;
    }

    private void createTriangle(int indices[], int tindex, int i1, int i2, int i3) {
        int ti = tindex * 3;
        indices[ti] = i1;
        indices[ti + 1] = i2;
        indices[ti + 2] = i3;
    }

    public void setCurrentMaxAngle1Change(float angle) {
        this.maxAngle1Change = angle;
    }

    public float getCurrentMaxAngle1Change() {
        return maxAngle1Change;
    }

    public void setCurrentMaxAngle2Change(float angle) {
        this.maxAngle2Change = angle;
    }

    public float getCurrentMaxAngle2Change() {
        return maxAngle2Change;
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

//name="Bip01$Neck"  type="Revolute2" group="rightarm" radius="0.01"
//location="[0.204456,8.6884E-10,7.94768E-5]"
//refaxisx="[0.9991973,2.6645156E-7,-0.040059462]"
//refaxisy="[-2.6645145E-7,1.0,5.3414047E-9]"
//refaxisz="[0.040059462,5.3367843E-9,0.9991973]"
//axis1="[0,1,0]" axislabel1="Y" 
//angle1="0"  minangle1="-5" maxangle1="5" 
//axis2="[1,0,0]" axislabel2="X" 
//angle2="0" minangle2="-5" maxangle2="5"
        
        XMLUtils.writeAttribute(w, "group", this.group);
        XMLUtils.writeAttribute(w, "radius", this.radius);
        XMLUtils.writeAttribute(w, "height", this.height);
        XMLUtils.writeAttribute(w, "location", this.getLocalPrefabTranslation());
        Quaternion localRotation = this.getLocalRotation();
        Vector3f rotation = new Vector3f();
        float[] angles = new float[3];
        localRotation.toAngles(angles);
        rotation.set(angles[0], angles[1], angles[2]);
        rotation.multLocal(FastMath.RAD_TO_DEG);
        XMLUtils.writeAttribute(w, "rotation", rotation);
        XMLUtils.writeAttribute(w, "axis1", this.axis1);
        XMLUtils.writeAttribute(w, "axislabel1", this.labelAxis1);
        XMLUtils.writeAttribute(w, "minAngle1", this.minAngle1);
        XMLUtils.writeAttribute(w, "maxAngle1", this.maxAngle1);
        XMLUtils.writeAttribute(w, "angle1", this.currentAngle1);
        
        XMLUtils.writeAttribute(w, "minAngle2", this.minAngle2);
        XMLUtils.writeAttribute(w, "maxAngle2", this.maxAngle2);
        XMLUtils.writeAttribute(w, "angle2", this.currentAngle2);
        
        XMLUtils.writeAttribute(w, "chainwithchild", this.chainWithChild);
        if (chainWithChild) {
            XMLUtils.writeAttribute(w, "chainchildname", this.chainChildName);
        }
        XMLUtils.writeAttribute(w, "chainwithparen", this.chainWithParent);

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
}