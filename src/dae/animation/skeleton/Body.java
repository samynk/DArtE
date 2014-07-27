/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.custom.CharacterPath;
import dae.animation.trajectory.CurveChannel;
import dae.animation.trajectory.CurveTarget;
import dae.animation.trajectory.FootStep;
import dae.animation.trajectory.TargetCurve;
import dae.prefabs.Prefab;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import mlproject.fuzzy.FuzzyRuleBlock;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;

/**
 *
 * @author Koen Samyn
 */
public class Body extends Prefab implements BodyElement {

    private HashMap<String, RevoluteJoint> revJoints = new HashMap<String, RevoluteJoint>();
    private HashMap<String, RevoluteJointTwoAxis> revJoints2 = new HashMap<String, RevoluteJointTwoAxis>();
    private HashMap<RevoluteJoint, ArrayList<Float>> samples = new HashMap<RevoluteJoint, ArrayList<Float>>();
    private HashMap<RevoluteJoint, ArrayList<Vector3f>> translationSamples = new HashMap<RevoluteJoint, ArrayList<Vector3f>>();
    private HashMap<String, ArrayList<RevoluteJoint>> controllerGroups = new HashMap<String, ArrayList<RevoluteJoint>>();
    private HashMap<String, AttachmentPoint> attachmentPoints = new HashMap<String, AttachmentPoint>();
    private ArrayList<RevoluteJoint> revJointList = new ArrayList<RevoluteJoint>();
    private FuzzySystem rightFootController;
    private FuzzySystem leftFootController;
    private FuzzySystem rightFootControllerAscent;
    private FuzzySystem leftFootControllerAscent;
    private FuzzySystem rightFootControllerDescent;
    private FuzzySystem leftFootControllerDescent;
    private Node footSteps = new Node("footSteps");
    // parameters for movement, in degrees
    private float bendAngleFlat;
    private float bendAngleFlatSupport;
    private float bendAngleUp;
    private float bendAngleDown;
    // length of limbs
    private float upperLegLength;
    private float lowerLegLength;
    private Vector3f targetKneeEndPos = new Vector3f();
    private Vector3f supportKneeEndPos = new Vector3f();
    private Vector3f hipEndPos = new Vector3f();
    private AssetManager assetManager;
    private int sampleBufferCount = 500;
    private long lastSample = -1;
    private boolean logTranslations = false;
    private float startArcDistance;
    private FuzzySystem rightHandController;
    private FuzzySystem leftHandController;
    private FuzzySystem headController;
    private FuzzySystem rightLegController;
    private FuzzySystem leftLegController;
    private Spatial rightHandTarget;
    private Material mat;
    private Spatial model;
    private String modelLocation;
    private boolean showCharacter = true;
    // targets
    private HashMap<String, String> targetNames = new HashMap<String, String>();
    // the path for the character to walk on
    private CharacterPath characterPath;
    private float footStepLength = 0.4f;
    private String skeletonFile;

    public Body() {
        System.out.println("creating a body");
        ++skelCount;
    }

    public Body(AssetManager assetInfo, Material mat) {
        super();
        this.bendAngleFlat = 5;
        this.bendAngleFlatSupport = 5;
        this.bendAngleUp = 5;
        this.bendAngleDown = 5;
        this.lowerLegLength = 5;
        this.upperLegLength = 5;
        this.assetManager = assetInfo;

        this.setCategory("Animation");
        this.setType("Skeleton");
        this.setName("skeleton" + skelCount);
        //this.setLocalScale(0.1f);
        this.mat = mat;
        rightHandController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/righttakebehaviour.fcl");
        leftHandController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/lefttakebehaviour.fcl");
        headController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/lookbehaviour.fcl");
        rightLegController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/rightwalkbehaviour.fcl");
        leftLegController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/leftwalkbehaviour.fcl");

        rightHandController.setName("rightarm");
        rightHandController.setIterativeMode(true);
        leftHandController.setName("leftarm");
        //leftHandController.setIterativeMode(true);
        headController.setName("head");
        rightLegController.setName("rightleg");
        leftLegController.setName("leftleg");
        //rightLegController.setIterativeMode(true);

        setDefaultTargetNames();
        ++skelCount;
        attachChild(footSteps);
    }

    public void setDefaultTargetNames() {
        targetNames.put("righthandtarget", "righthandtarget");
        targetNames.put("lefthandtarget", "lefthandtarget");
        targetNames.put("headtarget", "headtarget");
        targetNames.put("rightlegtarget", "rightlegtarget");
        targetNames.put("leftlegtarget", "leftlegtarget");
    }

    public Body(AssetManager assetInfo, Material mat, float bendAngleFlat, float bendAngleFlatSupport, float bendAngleUp, float bendAngleDown, float lowerLegLength, float upperLegLength) {
        super();
        this.bendAngleFlat = bendAngleFlat;
        this.bendAngleFlatSupport = bendAngleFlatSupport;
        this.bendAngleUp = bendAngleUp;
        this.bendAngleDown = bendAngleDown;
        this.lowerLegLength = lowerLegLength;
        this.upperLegLength = upperLegLength;
        this.assetManager = assetInfo;

        this.setName("skeleton" + skelCount);
        //this.setLocalScale(0.1f);
        this.mat = mat;
        rightHandController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/righttakebehaviour.fcl");
        leftHandController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/lefttakebehaviour.fcl");
        headController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/lookbehaviour.fcl");
        rightLegController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/rightwalkbehaviour.fcl");
        leftLegController = (FuzzySystem) assetManager.loadAsset("Skeleton/Cathy/Behaviours/leftwalkbehaviour.fcl");

        rightHandController.setName("rightarm");
        leftHandController.setName("leftarm");
        headController.setName("head");
        rightLegController.setName("rightleg");
        leftLegController.setName("leftleg");

        setDefaultTargetNames();
        ++skelCount;
        attachChild(footSteps);
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.assetManager = manager;
        Node n = (Node) assetManager.loadModel(extraInfo);
        attachChild(n);



    }

    /**
     * Returns the number of samples that are buffered.
     *
     * @return
     */
    public int getSampleBufferCount() {
        return sampleBufferCount;
    }

    public void attachBodyElement(BodyElement node) {
        if (node instanceof Node) {
            this.attachChild((Node) node);
        }
    }

    public void createControllers() {
        createControllers(this);
    }

    @Override
    public void notifyLoaded() {
        createControllers(this);
    }

    private void calculateBodyAngleLH(FuzzyVariable bodyAngleLH) {
        RevoluteJoint rjLH = revJoints.get("leftHipJointX");
        rjLH.getWorldAxis(worldAxisOrigin, worldAxis);
        Quaternion q = this.getWorldRotation();
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f rotated = q.mult(up);

        this.project(rotated, worldAxis);
        float angle = FastMath.acos(rotated.dot(up)) * FastMath.RAD_TO_DEG;
        Vector3f checkVector = up.cross(rotated);
        if (checkVector.dot(worldAxis) < 0) {
            angle = -angle;
        }
        bodyAngleLH.setCurrentValue(angle);
        //System.out.println("Current angle : " + angle);
    }

    private void calculateBodyAngleRH(FuzzyVariable bodyAngleRH) {
        RevoluteJoint rjLH = revJoints.get("rightHipJointX");
        rjLH.getWorldAxis(worldAxisOrigin, worldAxis);
        Quaternion q = this.getWorldRotation();
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f rotated = q.mult(up);

        this.project(rotated, worldAxis);
        float angle = FastMath.acos(rotated.dot(up)) * FastMath.RAD_TO_DEG;
        Vector3f checkVector = up.cross(rotated);
        if (checkVector.dot(worldAxis) < 0) {
            angle = -angle;
        }
        bodyAngleRH.setCurrentValue(angle);
        //System.out.println("Current angle : " + angle);
    }

    private float calculateAngleOfIntersection(Vector3f p1, float r0, Vector3f p0, float r1) {
        Vector3f p1_p0 = p1.subtract(p0);
        float d = p1_p0.length();
        //System.out.println("d : "+d);
        float a = (r0 * r0 - r1 * r1 + d * d) / (2 * d);
        return FastMath.acos(a / r0);
    }

    private float calculateArcDistance(FootStep current) {
        Vector3f axis;
        Vector3f knee;
        Vector3f rotationAxis;

        if ("left".equals(current.getType())) {
            RevoluteJoint leftHip = revJoints.get("leftHipJointX");
            RevoluteJoint leftKnee = revJoints.get("leftKneeJointX");

            axis = leftHip.getWorldTranslation();
            knee = leftKnee.getWorldTranslation();
            rotationAxis = leftHip.getWorldRotationAxis();
        } else {
            RevoluteJoint rightHip = revJoints.get("rightHipJointX");
            RevoluteJoint rightKnee = revJoints.get("rightKneeJointX");

            axis = rightHip.getWorldTranslation();
            knee = rightKnee.getWorldTranslation();
            rotationAxis = rightHip.getWorldRotationAxis();
        }

        Vector3f target = current.getLocation();

        float angle = this.calculateAngleOfIntersection(axis, upperLegLength, target, lowerLegLength);

        Vector3f kneeAxis = knee.subtract(axis).normalizeLocal();
        Vector3f targetAxis = target.subtract(axis).normalizeLocal();

        float angle2 = FastMath.acos(targetAxis.dot(kneeAxis));


        Vector3f checkVector = kneeAxis.cross(targetAxis);
//        if ( "left".equals(current.getType())){
//            if (checkVector.dot(rotationAxis) > 0.0f) {
//                angle2 = -angle2;
//            }
//        }else{
        if (checkVector.dot(rotationAxis) < 0.0f) {
            angle2 = -angle2;
        }
        //}
        return (angle2 + angle);
    }

    private void calculateArcDistance(FuzzyVariable arcDistance, FootStep current) {
        float halfStartArcDist = startArcDistance / 2;
        float arcDist = calculateArcDistance(current);
        float value = (arcDist - halfStartArcDist) * (-1 / halfStartArcDist);
        arcDistance.setCurrentValue(value);
    }

    /**
     * After adding all the elements, create a hashmap of elements
     */
    private void createControllers(Node node) {
        for (Spatial s : node.getChildren()) {
            if (s instanceof RevoluteJoint) {
                RevoluteJoint rj = (RevoluteJoint) s;
                revJoints.put(rj.getName(), rj);
                revJoints.put(rj.getName().toLowerCase(), rj);
                revJointList.add(rj);
                String group = rj.getGroup();
                if (group != null) {
                    ArrayList<RevoluteJoint> jointGroup = controllerGroups.get(group);
                    if (jointGroup == null) {
                        jointGroup = new ArrayList<RevoluteJoint>();
                        controllerGroups.put(group, jointGroup);
                    }
                    System.out.println("putting : " + rj.getName() + " into " + group);
                    jointGroup.add(rj);
                }
            }
            if (s instanceof RevoluteJointTwoAxis) {
                RevoluteJointTwoAxis rj = (RevoluteJointTwoAxis) s;
                revJoints2.put(rj.getName(), rj);
                revJoints2.put(rj.getName().toLowerCase(), rj);
            }
            if (s instanceof AttachmentPoint) {
                attachmentPoints.put(s.getName(), (AttachmentPoint) s);
                System.out.println("Found attachment point :" + s.getName());
            }

            if (s instanceof Node) {
                createControllers((Node) s);
            }
        }
    }

    public FuzzySystem getCurrentController() {
        return this.currentController;
    }

    public void setRightFootController(FuzzySystem controller) {
        this.rightFootController = controller;
    }

    public void setLeftFootController(FuzzySystem controller) {
        this.leftFootController = controller;
    }

    public void setRightFootControllerAscent(FuzzySystem controller) {
        this.rightFootControllerAscent = controller;
    }

    public void setLeftFootControllerAscent(FuzzySystem controller) {
        this.leftFootControllerAscent = controller;
    }

    public void setRightFootControllerDescent(FuzzySystem rfdcontroller) {
        this.rightFootControllerDescent = rfdcontroller;
    }

    public void setLeftFootControllerDescent(FuzzySystem lfdcontroller) {
        this.leftFootControllerDescent = lfdcontroller;
    }

    /*
     public void setFootSteps(FootSteps footSteps) {
     this.footSteps = footSteps;
     this.calculateKneeTargets(this.supportKneeEndPos, targetKneeEndPos, this.hipEndPos);
     }*/
    private FuzzySystem currentController;
    private AttachmentPoint currentAp;

    public void determineControllers(FootStep current) {
        if ("left".equals(current.getType())) {
            currentAp = this.attachmentPoints.get("leftFootAP");
            Vector3f currentRightLocation = this.attachmentPoints.get("rightFootAP").getWorldTranslation();
            float heightDiff = current.getLocation().y - currentRightLocation.y;

            System.out.println("current footstep : " + current.getLocation());
            System.out.println("current right foot loc : " + currentRightLocation);
            if (FastMath.abs(heightDiff) < 0.25f) {
                System.out.println("heightDiff :" + heightDiff);
                System.out.println("Level walking towards left");

                currentController = this.leftFootController;
            } else if (heightDiff > 0.25f) {
                System.out.println("Ascent towards left");
                currentController = this.leftFootControllerAscent;
            } else {
                System.out.println("Descent towards left");
                currentController = this.leftFootControllerDescent;
            }
        } else {
            currentAp = this.attachmentPoints.get("rightFootAP");
            Vector3f currentLeftLocation = this.attachmentPoints.get("leftFootAP").getWorldTranslation();
            float heightDiff = current.getLocation().y - currentLeftLocation.y;

            System.out.println("current footstep : " + current.getLocation());
            System.out.println("current left foot loc : " + currentLeftLocation);
            if (FastMath.abs(heightDiff) < 0.25f) {
                System.out.println("heightDiff :" + heightDiff);
                System.out.println("Level walking towards right");
                currentController = this.rightFootController;
            } else if (heightDiff > 0.25f) {
                System.out.println("Ascent towards right");
                currentController = this.rightFootControllerAscent;
            } else {
                System.out.println("Descent towards right");
                currentController = this.rightFootControllerDescent;
            }
        }
    }

    private void updateSamples() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSample;
        if (timeDiff > 50) {
            for (RevoluteJoint rj : this.revJointList) {
                if (rj.logsRotations()) {
                    float rotation = (rj.getCurrentRotation() + rj.getLogOffset()) * rj.getLogPostScale();
                    ArrayList<Float> sampleList = this.samples.get(rj);
                    if (sampleList == null) {
                        sampleList = new ArrayList<Float>();
                        this.samples.put(rj, sampleList);
                    }
                    sampleList.add(rotation);
                    if (sampleList.size() > this.sampleBufferCount) {
                        sampleList.remove(0);
                    }
                }
            }
            //
        }
        if (timeDiff > 50) {
            if (this.logTranslations) {
                for (RevoluteJoint rj : this.revJointList) {
                    if (rj.logsTranslations()) {
                        Vector3f sample = rj.getWorldTranslation().clone();
                        ArrayList<Vector3f> sampleList = this.translationSamples.get(rj);
                        if (sampleList == null) {
                            sampleList = new ArrayList<Vector3f>();
                            this.translationSamples.put(rj, sampleList);
                        }
                        sampleList.add(sample);
                    }
                }
            }
            lastSample = currentTime;
        }
    }
    private Vector3f worldAxis = new Vector3f();
    private Vector3f worldLocation = new Vector3f();

    public void calculateDirectionInputAngleValues(FootStep current, Vector3f ap, Vector3f targetLoc, Vector3f normal,
            RevoluteJoint rj,
            FuzzyVariable inputAngle,
            FuzzyVariable inputAngle2,
            FuzzyVariable inputDistance) {
        Vector3f targetLocation = current.getLocation();

        rj.getWorldAxis(worldLocation, worldAxis);

        Vector3f currentVector;

        Vector3f limbToMoveWorldLoc = ap;
        currentVector = limbToMoveWorldLoc.subtract(worldLocation);


        float limbDistance = currentVector.length();
        //System.out.println(rj.getName() + normal);
        Vector3f groundVector = normal.cross(worldAxis);
        groundVector.normalizeLocal();
        // System.out.println("ground vector : "  + rj.getName() + groundVector);

        project(currentVector, worldAxis);
        currentVector.normalizeLocal();

        float angle = FastMath.acos(groundVector.dot(currentVector)) * FastMath.RAD_TO_DEG;

        Vector3f checkVector = currentVector.cross(groundVector);
        if (checkVector.dot(worldAxis) < 0) {
            angle = -angle;
        }
        //System.out.println(rj.getName()  +" angle:" + angle);
        if (inputAngle != null) {
            inputAngle.setCurrentValue(angle);
        }
        if (inputDistance != null) {
            Vector3f directionVector = targetLocation.subtract(worldLocation);
            float targetDistance = directionVector.length();
            inputDistance.setCurrentValue(targetDistance - limbDistance);
        }
    }

    public void calculateLocationInputAngleValues(FootStep current, Vector3f ap, Vector3f targetLoc, Vector3f normal,
            RevoluteJoint rj,
            FuzzyVariable inputAngle,
            FuzzyVariable inputAngle2,
            FuzzyVariable inputDistance) {
        // get the joint that is 
        Vector3f targetLocation = targetLoc;

        rj.getWorldAxis(worldLocation, worldAxis);

        Vector3f directionVector = targetLocation.subtract(worldLocation);

        float targetDistance = directionVector.length();
        project(directionVector, worldAxis);
        directionVector.normalizeLocal();

        Vector3f limbToMoveWorldLoc = ap;

        Vector3f currentVector = limbToMoveWorldLoc.subtract(worldLocation);
        float limbDistance = currentVector.length();
        project(currentVector, worldAxis);
        currentVector.normalizeLocal();

        float dot = directionVector.dot(currentVector);

        if (dot > 1.0f) {
            dot = 1.0f;
        } else if (dot < -1.0f) {
            dot = -1.0f;
        }

        float angle = FastMath.acos(dot) * FastMath.RAD_TO_DEG;

        Vector3f checkVector = currentVector.cross(directionVector);
        checkVector.normalizeLocal();

        if (checkVector.dot(worldAxis) < 0) {
            angle = -angle;
        }

        if (inputAngle != null) {
            inputAngle.setCurrentValue(angle);
        }
        if (inputDistance != null) {
            inputDistance.setCurrentValue(targetDistance - limbDistance);
        }
    }
    private Vector3f worldAxisOrigin = new Vector3f();
    private Vector3f bodyLocalAxisOrigin = new Vector3f();
    private Vector3f bodyTempLocalAxis = new Vector3f();
    private Vector3f bodyLocalAxis = new Vector3f();

    private void counterRotate(RevoluteJoint currentRevoluteJoint, float amount) {
        if (Float.isNaN(amount)) {
            System.out.println("oh oh");
        }
        currentRevoluteJoint.getWorldAxis(worldAxisOrigin, worldAxis);
        //System.out.println("worldAxisOrigin" + worldAxisOrigin);


        this.worldToLocal(worldAxisOrigin, bodyLocalAxisOrigin);
        bodyTempLocalAxis.set(worldAxisOrigin.x + worldAxis.x, worldAxisOrigin.y + worldAxis.y, worldAxisOrigin.z + worldAxis.z);
        this.worldToLocal(bodyTempLocalAxis, bodyLocalAxis);
        bodyLocalAxis.setX(bodyLocalAxis.x - bodyLocalAxisOrigin.x);
        bodyLocalAxis.setY(bodyLocalAxis.y - bodyLocalAxisOrigin.y);
        bodyLocalAxis.setZ(bodyLocalAxis.z - bodyLocalAxisOrigin.z);

        Transform t1 = new Transform();
        t1.setTranslation(-worldAxisOrigin.x, -worldAxisOrigin.y, -worldAxisOrigin.z);

        Quaternion q = new Quaternion();
        q.fromAngleAxis(amount * FastMath.DEG_TO_RAD, worldAxis);
        Transform r1 = new Transform(q);

        Transform t2 = new Transform();
        t2.setTranslation(worldAxisOrigin);


        Transform b1 = this.getLocalTransform().combineWithParent(t1);
        Transform b2 = b1.combineWithParent(r1);
        Transform finalT = b2.combineWithParent(t2);
        this.setLocalTransform(finalT);

    }
    CollisionResults results = new CollisionResults();
    Vector3f dir = new Vector3f(0, -1, 0);

    private Vector3f findGroundNormal(FootStep current) {
        results.clear();
        Vector3f start = current.getWorldTranslation();
        Vector3f corrected = new Vector3f(start.x, start.y + 0.1f, start.z);
        Node rootNode = this.getParent();
        Ray ray = new Ray(corrected, dir);
        // Collect intersections between ray and all nodes in results list.
        rootNode.collideWith(ray, results);
        for (CollisionResult result : results) {
            if (!this.hasChild(result.getGeometry())) {
                return result.getContactNormal();
            }
        }
        return new Vector3f(0, 1, 0);
    }

    private void project(Vector3f toProject, Vector3f axis) {
        float dot = axis.dot(toProject);
        toProject.x = toProject.x - dot * axis.x;
        toProject.y = toProject.y - dot * axis.y;
        toProject.z = toProject.z - dot * axis.z;
    }

//    private float findGroundDistance(AttachmentPoint ap) {
//        FootStep current = footSteps.getCurrentFootStep();
//        return ap.getWorldTranslation().y - current.getWorldTranslation().y;
//        /*
//         * results.clear(); Vector3f start = ap.getWorldTranslation();
//         *
//         * Node rootNode = this.getParent(); Ray ray = new Ray(start, dir); //
//         * Collect intersections between ray and all nodes in results list.
//         * rootNode.collideWith(ray, results); for (CollisionResult result :
//         * results) { if (!this.hasChild(result.getGeometry())) { return
//         * result.getDistance(); } }
//         */
//        //return 0.0f;
//    }
//    
//    private float findGroundDistance(RevoluteJoint rj) {
//        results.clear();
//        Vector3f start = rj.getWorldTranslation();
//        Node rootNode = this.getParent();
//        Ray ray = new Ray(start, dir);
//        // Collect intersections between ray and all nodes in results list.
//        rootNode.collideWith(ray, results);
//        for (CollisionResult result : results) {
//            if (!this.hasChild(result.getGeometry())) {
//                return result.getDistance();
//            }
//        }
//        return 0.0f;
//    }
    public Vector3f getSupportKneeEndPos() {
        return this.supportKneeEndPos;
    }

    public Vector3f getTargetKneeEndPos() {
        return this.targetKneeEndPos;
    }

    public Vector3f getHipEndPos() {
        return this.hipEndPos;
    }

//    private void calculateKneeTargets(Vector3f supportTarget, Vector3f motionTarget, Vector3f hipTarget) {
//        Vector3f moveDirection = new Vector3f(0, 0, -1);
//        moveDirection = this.getWorldRotation().mult(moveDirection);
//
//        Vector3f supportLoc;
//        Vector3f targetLoc;
//        // three different situations
//        // 1) current support foot position and target foot position are approximately at same height (= y position)
//        FootStep current = footSteps.getCurrentFootStep();
//        if ("left".equals(current.getType())) {
//            supportLoc = getChild("rightFootRollJointX").getWorldTranslation().clone();
//            targetLoc = current.getWorldTranslation().clone();
//        } else {
//            targetLoc = current.getWorldTranslation().clone();
//            supportLoc = getChild("leftFootRollJointX").getWorldTranslation().clone();
//        }
//        //supportLoc.z += 0.8f;
//        float heightDif = targetLoc.y - supportLoc.y;
//
//        // project on sagittal plane
//        Vector3f upVector = new Vector3f(0, 1, 0);
//        Vector3f sagVector = moveDirection.cross(upVector);
//        sagVector.normalizeLocal();
//
//        this.project(supportLoc, sagVector);
//        this.project(targetLoc, sagVector);
//
//        Vector3f step = targetLoc.subtract(supportLoc);
//        float stepSize = step.length();
//
//        Vector3f halfStep = step.mult(0.5f);
//
//        float length = (this.lowerLegLength + this.upperLegLength);
//        if (Math.abs(heightDif) < 0.5f) {
//            calculateFlatWalk(supportLoc, supportTarget, targetLoc, motionTarget, hipTarget, sagVector);
//        } else if (heightDif > 0.5f) {
//            // uphill
//            //System.out.println("height > 0.5");
//            supportTarget.set(supportLoc.x, supportLoc.y + this.lowerLegLength, supportLoc.z);
//
//            hipTarget.set(supportTarget);
//            hipTarget.y += this.upperLegLength;
//
//            Vector3f hip = new Vector3f(supportTarget);
//            hip.y += this.upperLegLength;
//
//            //from http://paulbourke.net/geometry/2circle/
//            Vector3f p1_p0 = targetLoc.subtract(hip);
//
//            float d = p1_p0.length();
//            //System.out.println("d : "+d);
//            float r0 = upperLegLength;
//            float r1 = upperLegLength;
//            float a = (r0 * r0 - r1 * r1 + d * d) / (2 * d);
//            //System.out.println("a " + a);
//            float h = FastMath.sqrt(r0 * r0 - a * a);
//            //System.out.println("h :"+ h);
//            Vector3f p2 = hip.add(p1_p0.mult(a / d));
//
//            Vector3f p3dir = sagVector.cross(p1_p0);
//            p3dir.normalizeLocal();
//
//            Vector3f p3sol1 = p2.add(p3dir.mult(h));
//            Vector3f p3sol2 = p2.add(p3dir.mult(-h));
//
//            motionTarget.set(p3sol1);
//        } else if (heightDif < -0.5f) {
//            // downhill
//            motionTarget.set(targetLoc.x, targetLoc.y + this.lowerLegLength, targetLoc.z);
//            hipTarget.set(motionTarget);
//            hipTarget.y += this.upperLegLength;
//
//            Vector3f p1_p0 = hipTarget.subtract(supportLoc);
//
//            float d = p1_p0.length();
//            float r0 = lowerLegLength;
//            float r1 = upperLegLength;
//            float a = (r0 * r0 - r1 * r1 + d * d) / (2 * d);
//            float h = FastMath.sqrt(r0 * r0 - a * a);
//            Vector3f p2 = supportLoc.add(p1_p0.mult(a / d));
//
//            Vector3f p3dir = sagVector.cross(p1_p0);
//            p3dir.normalizeLocal();
//
//            //Vector3f p3sol1 = p2.add(p3dir.mult(h));
//            Vector3f p3sol2 = p2.add(p3dir.mult(-h));
//            supportTarget.set(p3sol2);
//        }
//    }
    private void calculateFlatWalk(Vector3f supportLoc, Vector3f supportTarget,
            Vector3f targetLoc, Vector3f motionTarget, Vector3f hipTarget,
            Vector3f sagVector) {
        float slackLL = lowerLegLength - 0.1f;
        float lll2 = slackLL * slackLL;
        float ull2 = upperLegLength * upperLegLength;
        float bendLegLength = FastMath.sqrt(lll2 + ull2
                - 2 * slackLL * upperLegLength * FastMath.cos((180 - bendAngleFlatSupport) * FastMath.DEG_TO_RAD));
        Vector3f p1_p0 = supportLoc.subtract(targetLoc);
        float d = p1_p0.length();
        float r1 = slackLL + upperLegLength;
        float r0 = bendLegLength;
        float a = (r0 * r0 - r1 * r1 + d * d) / (2 * d);
        float h = FastMath.sqrt(r0 * r0 - a * a);
        Vector3f p2 = supportLoc.add(p1_p0.mult(a / d));

        Vector3f p3dir = sagVector.cross(p1_p0);
        p3dir.normalizeLocal();

        //Vector3f p3sol1 = p2.add(p3dir.mult(h));
        Vector3f p3sol2 = p2.add(p3dir.mult(-h));
        hipTarget.set(p3sol2);

        // calculate motion target
        Vector3f diff = hipTarget.subtract(targetLoc);
        diff.multLocal(slackLL / (slackLL + upperLegLength));
        diff.addLocal(targetLoc);
        motionTarget.set(diff);
        // calculate support target
        diff = hipTarget.subtract(supportLoc);
        diff.multLocal(slackLL / bendLegLength);
        float angle = FastMath.acos(lll2 + bendLegLength * bendLegLength - ull2 / (2 * bendLegLength * slackLL));
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, sagVector);
        Vector3f rotated = q.mult(diff);
        rotated.addLocal(supportLoc);
        supportTarget.set(rotated);
    }

    public void reset() {
        //this.footSteps.reset();
        this.setLocalTranslation(new Vector3f(0, 0, 0));
        for (Spatial s : this.getChildren()) {
            if (s instanceof BodyElement) {
                ((BodyElement) s).reset();
            }
        }
        //calculateKneeTargets(this.supportKneeEndPos, this.targetKneeEndPos, this.hipEndPos);
    }

    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    public HashMap<RevoluteJoint, ArrayList<Float>> getRotationSamples() {
        return this.samples;
    }

    public HashMap<RevoluteJoint, ArrayList<Vector3f>> getTranslationSamples() {
        return this.translationSamples;
    }

    public Iterable<RevoluteJoint> getRevJoints() {
        return this.revJointList;
    }

    public void setLogTranslations(boolean logTranslations) {
        this.logTranslations = logTranslations;
    }

    public void clearTranslationLogs() {
        this.translationSamples.clear();
    }
    private int xindex = -1;
    private int yindex = -1;
    private int zindex = -1;
    private int rxindex = -1;
    private int ryindex = -1;
    private int rzindex = -1;

    public void setXYZIndexInChannel(int xindex, int yindex, int zindex) {
        this.xindex = xindex;
        this.yindex = yindex;
        this.zindex = zindex;
    }

    public void setRXRYRZIndexInChannel(int rxindex, int ryindex, int rzindex) {
        this.rxindex = rxindex;
        this.ryindex = ryindex;
        this.rzindex = rzindex;
    }
    float angle = 0;
    Quaternion q = new Quaternion();
    int frame = 0;

    @Override
    public void updateLogicalState(float tpf) {

        super.updateLogicalState(tpf);
        //Spatial character = this.getChild("charactermesh");
        if (model != null) {

            SkeletonControl sc = model.getControl(SkeletonControl.class);
            Skeleton sk = sc.getSkeleton();
            if (frame == 0) {
                for (RevoluteJointTwoAxis rj2 : revJoints2.values()) {
                    String bname = rj2.getName();
                    bname = bname.replace("$", " ");
                    Bone b = sc.getSkeleton().getBone(bname);
                    if (b != null) {
                        rj2.setAttachedBone(b);
                    }
                }
                for (RevoluteJoint rj : revJointList) {
                    String bname = rj.getName();
                    bname = bname.replace("$", " ");
                    Bone b = sc.getSkeleton().getBone(bname);
                    if (b != null) {
                        rj.setAttachedBone(b);
                    }
                }
            }
            Bone b = sc.getSkeleton().getBone("Bip01 L Toe0");
            if (b != null && frame == 0) {
                Quaternion q = b.getLocalRotation();
                float[] r = new float[3];
                System.out.println("Before any influence");
                q.toAngles(r);
                System.out.println("rotation=[" + r[0] + "," + r[1] + "," + r[2] + "]");

                Vector3f l = b.getLocalPosition();
                System.out.println("location=\"[" + l.x + "," + l.y + "," + l.z + "]\"");
                Vector3f[] axes = new Vector3f[3];
                q.toAxes(axes);
                printVector3f("refaxisx=\"[", axes[0]);
                System.out.println("]\"");
                printVector3f("refaxisy=\"[", axes[1]);
                System.out.println("]\"");
                printVector3f("refaxisz=\"[", axes[2]);
                System.out.println("]\"");
            }
        }
        updateRightHand(tpf);
        updateHead(tpf);
        updateLeftHand(tpf);
        updateRightLeg(tpf);
        updateLeftLeg(tpf);
        ++frame;
    }

    public void printVector3f(String label, Vector3f value) {
        System.out.print(label + value.x + "," + value.y + "," + value.z);
    }

    // right hand controller stuff.
    /**
     * Sets the controller for the right hand.
     *
     * @param rightHandController the right hand controller to set.
     */
    public void setRightHandController(FuzzySystem rightHandController) {
        this.rightHandController = rightHandController;
    }

    public void updateRightHand(float tpf) {
        //  System.out.println("updating right hand");
        String rightHandTargetName = this.targetNames.get("righthandtarget");
        Spatial target = this.getChild(rightHandTargetName);
        if (target == null) {
            target = this.getParent().getChild(rightHandTargetName);
        }
        if (target instanceof TargetCurve) {
            TargetCurve tc = (TargetCurve) target;
            CurveChannel cc = tc.getCurveChannel(rightHandController.getName());
            AttachmentPoint ap = this.attachmentPoints.get(rightHandController.getName());

            Vector3f worldAp = ap.getWorldTranslation();
            CurveTarget current = cc.getCurrentTarget();

            Vector3f worldTarget = tc.localToWorld(current.getLocalTranslation(), null);

            if (worldAp.distance(worldTarget) < tc.getTolerance()) {
                if (cc.hasNextTarget()) {
                    cc.nextTarget();
                    current = cc.getCurrentTarget();
                }
            }

            evaluateController(rightHandController, tpf, tc.getHandle(current.getName()), false);
        } else {
            evaluateController(rightHandController, tpf, target, false);
        }
    }

    public void updateHead(float tpf) {
        String headTarget = this.targetNames.get("headtarget");
        Spatial target = this.getParent().getChild(headTarget);
        evaluateController(headController, tpf, target, false);
    }

    public void updateLeftHand(float tpf) {
        String leftHandTarget = this.targetNames.get("lefthandtarget");
        Spatial target = this.getChild(leftHandTarget);
        if (target == null) {
            target = this.getParent().getChild(leftHandTarget);
        }
        evaluateController(leftHandController, tpf, target, false);
    }

    public void updateRightLeg(float tpf) {
        Spatial target = null;
        if (useInternalLegTargets) {
            target = this.rightLegTarget;
        } else {
            String rightLegTarget = this.targetNames.get("rightlegtarget");
            target = this.getParent().getChild(rightLegTarget);
        }
        evaluateController(rightLegController, tpf, target, true);
    }

    public void updateLeftLeg(float tpf) {
        Spatial target = null;
        if (useInternalLegTargets) {
            target = this.leftLegTarget;
        } else {
            String leftLegTarget = this.targetNames.get("leftlegtarget");
            target = this.getParent().getChild(leftLegTarget);
        }
        evaluateController(leftLegController, tpf, target, false);
    }

    private void evaluateController(FuzzySystem controller, float tpf, Spatial target, boolean printDistance) {
        if (controller != null && target != null) {
            AttachmentPoint ap = this.attachmentPoints.get(controller.getName());


            int iteration = 0;
            float distance = target.getWorldTranslation().distance(ap.getWorldTranslation());
            while (distance > 0.0005f
                    && ((controller.isIterativeMode() && iteration < controller.getMaxIterations())
                    || iteration == 0)) {
                FuzzyRuleBlock[] blocks = controller.getFuzzyRuleBlocks();
                for (int i = 0; i < blocks.length; ++i) {
                    FuzzyRuleBlock currentBlock = blocks[i];
                    if (currentBlock == null) {
                        continue;
                    }
                    for (FuzzyVariable input : currentBlock.getInputs()) {
                        String inputName = input.getName();
                        if (inputName.startsWith("angle_target") || inputName.startsWith("angle_alignment")) {
                            calculateAndSetAngle(input, target, controller.getName());
                        } else if (inputName.startsWith("angle_current")) {
                            setCurrentAngle(input, target, controller.getName());
                        } else if (inputName.startsWith("distance")) {
                            calculateAndSetDistance(input, target, controller.getName());
                        }
                    }
                    currentBlock.evaluate();
                    for (FuzzyVariable output : currentBlock.getOutputs()) {
                        String outputName = output.getName();
                        if (outputName.startsWith("dangle")) {
                            float outputVal = output.getOutputValue() * tpf;
                            if (FastMath.abs(outputVal) < FastMath.ZERO_TOLERANCE) {
                                continue;
                            }
                            String jointName = outputName.substring(outputName.lastIndexOf("_") + 1);
                            if (jointName.contains("@")) {
                                int atIndex = jointName.indexOf('@');
                                int index = Integer.parseInt(jointName.substring(atIndex + 1));
                                jointName = jointName.substring(0, atIndex);
                                RevoluteJointTwoAxis rj2 = this.revJoints2.get(jointName);
                                if (rj2 != null) {
                                    if (index == 1) {
                                        if (FastMath.abs(outputVal) > FastMath.abs(rj2.getCurrentMaxAngle1Change())) {
                                            outputVal = rj2.getCurrentMaxAngle1Change() * .9f;
                                        }
                                        rj2.setCurrentAngle1(rj2.getCurrentAngle1() + outputVal);
                                    } else {
                                        if (FastMath.abs(outputVal) > FastMath.abs(rj2.getCurrentMaxAngle2Change())) {
                                            outputVal = rj2.getCurrentMaxAngle2Change() * .9f;
                                        }
                                        rj2.setCurrentAngle2(rj2.getCurrentAngle2() + outputVal);
                                    }
                                } else {
                                    System.out.println("could not find:" + jointName);
                                }
                            } else {
                                RevoluteJoint rj = this.revJoints.get(jointName);
                                if (FastMath.abs(outputVal) > FastMath.abs(rj.getCurrentMaxAngleChange())) {
                                    outputVal = rj.getCurrentMaxAngleChange() * .9f;
                                }
                                rj.setCurrentAngle(rj.getCurrentAngle() + outputVal);
                            }
                        }
                    }
                }

                ++iteration;
            }
        }
    }

    public void calculateAndSetAngle(FuzzyVariable input, Spatial rightHandTarget, String apName) {
        String inputName = input.getName();
        String jointName = inputName.substring(inputName.lastIndexOf("_") + 1);
        if (jointName.contains("@")) {
            int atIndex = jointName.lastIndexOf('@');
            String sindex = jointName.substring(atIndex + 1);
            int index = Integer.parseInt(sindex);
            jointName = jointName.substring(0, atIndex);
            RevoluteJointTwoAxis rj2 = revJoints2.get(jointName);
            if (rj2 == null) {
                System.out.println("could not find : " + jointName);
            } else {
                CalculateAndSetAngleRevoluteJointTwoAxis(rj2, index, inputName, rightHandTarget, input, apName);
            }
        } else {
            RevoluteJoint rj = revJoints.get(jointName);
            if (rj == null) {
                //System.out.println("Could not find : " + jointName);
                return;
            }
            CalculateAndSetAngleRevoluteJoint(rj, inputName, rightHandTarget, input, apName);
        }
    }

    private void setCurrentAngle(FuzzyVariable input, Spatial target, String name) {
        String inputName = input.getName();
        String jointName = inputName.substring(inputName.lastIndexOf("_") + 1);
        if (jointName.contains("@")) {
            int atIndex = jointName.lastIndexOf('@');
            String sindex = jointName.substring(atIndex + 1);
            int index = Integer.parseInt(sindex);
            jointName = jointName.substring(0, atIndex);
            RevoluteJointTwoAxis rj2 = revJoints2.get(jointName);
            if (rj2 == null) {
                System.out.println("could not find : " + jointName);
            } else {
                if (index == 1) {
                    input.setCurrentValue(rj2.getCurrentAngle1());
                    rj2.setCurrentMaxAngle1Change(1000.0f);
                } else if (index == 2) {
                    input.setCurrentValue(rj2.getCurrentAngle2());
                    rj2.setCurrentMaxAngle2Change(1000.0f);
                }
            }
        } else {
            RevoluteJoint rj = revJoints.get(jointName);
            if (rj == null) {
                System.out.println("Could not find : " + jointName);
            } else {
                input.setCurrentValue(rj.getCurrentAngle());
                rj.setCurrentMaxAngleChange(1000.0f);
            }
        }
    }

    private void CalculateAndSetAngleRevoluteJointTwoAxis(RevoluteJointTwoAxis rj2, int index, String inputName, Spatial rightHandTarget, FuzzyVariable input, String apName) {
        if (inputName.contains("target")) {
            //String groupName = rj2.getGroup();
            AttachmentPoint ap = this.attachmentPoints.get(apName);
            if (ap != null) {
                // calculate the projected angle between the center of the joint,
                // the target and the attachment point in world space.
                Vector3f axis = Vector3f.UNIT_X;
                if (index == 1) {
                    axis = rj2.getWorldAxis1();
                } else if (index == 2) {
                    axis = rj2.getWorldAxis2();
                }
                axis.normalizeLocal();
                Vector3f origin = rj2.getWorldTranslation();

                Vector3f apLoc = ap.getWorldTranslation();
                Vector3f targetLoc = rightHandTarget.getWorldTranslation();

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


                if (index == 1) {
                    rj2.setCurrentMaxAngle1Change(angle);
                } else {
                    rj2.setCurrentMaxAngle2Change(angle);
                }
                //System.out.println("Angle is : " + angle);
                input.setCurrentValue(angle);
            }
        } else if (inputName.contains("alignment")) {
            //String groupName = rj2.getGroup();
            AttachmentPoint ap = this.attachmentPoints.get(apName);
            if (ap != null) {
                Vector3f axis = Vector3f.UNIT_X;
                if (index == 1) {
                    axis = rj2.getWorldAxis1();
                } else if (index == 2) {
                    axis = rj2.getWorldAxis2();
                }
                axis.normalizeLocal();

                Vector3f vector1, vector2;
                vector1 = null;
                vector2 = Vector3f.UNIT_Y;
                if (inputName.contains("alignment1")) {
                    vector1 = ap.getWorldAxis1();
                    if (rightHandTarget instanceof Handle) {
                        vector2 = ((Handle) rightHandTarget).getAxis1();
                    }
                } else {
                    vector1 = ap.getWorldAxis2();
                    if (rightHandTarget instanceof Handle) {
                        vector2 = ((Handle) rightHandTarget).getAxis2();
                    }
                }
                vector2 = rightHandTarget.getWorldRotation().mult(vector2);

                this.project(vector1, axis);
                this.project(vector2, axis);

                vector1.normalizeLocal();
                vector2.normalizeLocal();
                float angle = vector1.angleBetween(vector2) * FastMath.RAD_TO_DEG;
                if (vector1.cross(vector2).dot(axis) > 0) {
                    angle = -angle;
                }
                input.setCurrentValue(angle);

                if (index == 1) {
                    rj2.setCurrentMaxAngle1Change(angle);
                } else {
                    rj2.setCurrentMaxAngle2Change(angle);
                }
            }
        }
    }

    private void CalculateAndSetAngleRevoluteJoint(RevoluteJoint rj, String inputName, Spatial rightHandTarget, FuzzyVariable input, String apName) {

        if (inputName.contains("target")) {
            //String groupName = rj.getGroup();
            AttachmentPoint ap = this.attachmentPoints.get(apName);
            if (ap != null) {
                // calculate the projected angle between the center of the joint,
                // the target and the attachment point in world space.
                Vector3f axis = rj.getWorldRotationAxis();
                axis.normalizeLocal();
                Vector3f origin = rj.getWorldTranslation();

                Vector3f apLoc = ap.getWorldTranslation();
                Vector3f targetLoc = rightHandTarget.getWorldTranslation();

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
                //System.out.println("Angle is : " + angle);
                input.setCurrentValue(angle);
                rj.setCurrentMaxAngleChange(angle);
            }
        } else if (inputName.contains("alignment")) {
            //String groupName = rj.getGroup();
            AttachmentPoint ap = this.attachmentPoints.get(apName);
            if (ap != null) {
                Vector3f axis = rj.getWorldRotationAxis();
                axis.normalizeLocal();

                Vector3f vector1, vector2;
                vector1 = null;
                vector2 = Vector3f.UNIT_Y;
                if (inputName.contains("alignment1")) {
                    vector1 = ap.getWorldAxis1();
                    if (rightHandTarget instanceof Handle) {
                        vector2 = ((Handle) rightHandTarget).getAxis1();
                    }
                } else {
                    vector1 = ap.getWorldAxis2();
                    if (rightHandTarget instanceof Handle) {
                        vector2 = ((Handle) rightHandTarget).getAxis2();
                    }
                }
                vector2 = rightHandTarget.getWorldRotation().mult(vector2);

//                if (inputName.contains(name)) {
//                    System.out.println("Projection axis:" + axis);
//                    System.out.println("vector1:" + vector1);
//                    System.out.println("vector2:" + vector2);
//                }

                this.project(vector1, axis);
                this.project(vector2, axis);

//                System.out.println("After projection:");
//                System.out.println("vector1:" + vector1);
//                System.out.println("vector2:" + vector2);

                vector1.normalizeLocal();
                vector2.normalizeLocal();
                float angle = vector1.angleBetween(vector2) * FastMath.RAD_TO_DEG;
                if (vector1.cross(vector2).dot(axis) > 0) {
                    angle = -angle;
                }
//                System.out.println("Alignment Angle is : " + angle);
                input.setCurrentValue(angle);
                rj.setCurrentMaxAngleChange(angle);
            }
        }
    }

    public void calculateAndSetDistance(FuzzyVariable input, Spatial rightHandTarget, String apName) {
        String inputName = input.getName();
        String jointName = inputName.substring(inputName.lastIndexOf("_") + 1);

        Vector3f origin;
        RevoluteJoint rj = revJoints.get(jointName);
        if (rj == null) {
            RevoluteJointTwoAxis rj2 = revJoints2.get(jointName);
            origin = rj2.getWorldTranslation();

        } else {
            origin = rj.getWorldTranslation();

        }

        //String groupName = rj.getGroup();
        AttachmentPoint ap = this.attachmentPoints.get(apName);
        if (ap != null) {

            Vector3f apLoc = ap.getWorldTranslation();
            Vector3f targetLoc = rightHandTarget.getWorldTranslation();

            Vector3f vector1 = apLoc.subtract(origin);
            Vector3f vector2 = targetLoc.subtract(origin);
            //this.project(vector1, axis);
            //this.project(vector2, axis);
            float diff = vector1.length() - vector2.length();
            //System.out.println("Difference is : " + diff);
            input.setCurrentValue(diff);
        }
    }
    static int skelCount = 0;

    @Override
    public Spatial clone() {
        Body b = new Body(this.assetManager, this.getOriginalMaterial());
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                Spatial clone = s.clone();
                b.attachBodyElement((BodyElement) clone);
            } else {
                Spatial modelClone = s.clone();
                b.attachChild(modelClone);
                if (s == model) {
                    b.model = modelClone;
                }
            }
        }
        b.setSkeletonFile(this.skeletonFile);
        b.createControllers();
        b.frame = 0;
        return b;

    }

    public void attachCharacter(Spatial model) {
        this.model = model;
        this.attachChild(model);
    }

    public void attachCharacter(String modelLocation, Spatial model) {
        this.model = model;
        this.attachChild(model);
        this.modelLocation = modelLocation;
    }

    /**
     * @return the showCharacter
     */
    public boolean getShowCharacter() {
        return showCharacter;
    }

    /**
     * @param showCharacter the showCharacter to set
     */
    public void setShowCharacter(boolean showCharacter) {
        if (!showCharacter && model != null) {
            model.removeFromParent();
        } else if (showCharacter) {
            this.attachCharacter(model);
        }
        this.showCharacter = showCharacter;
    }

    /**
     * @return the characterPath
     */
    public CharacterPath getCharacterPath() {
        return characterPath;
    }

    /**
     * @param characterPath the characterPath to set
     */
    public void setCharacterPath(CharacterPath characterPath) {

        this.characterPath = characterPath;
        if (characterPath != null) {
            System.out.println("Setting body on character path");
            characterPath.setCharacter(this);
            calculateFootSteps();
        }
    }

    /**
     * Get the prefab that represents the left hand target.
     *
     * @return the prefab that represents the left hand target.
     */
    public Prefab getLeftHandTarget() {
        String lefthandtargetName = this.targetNames.get("lefthandtarget");
        return (Prefab) this.getParent().getChild(lefthandtargetName);
    }

    /**
     * Sets the left hand target for this body.
     *
     * @param p the left hand target.
     */
    public void setLeftHandTarget(Prefab p) {
        this.targetNames.put("lefthandtarget", p.getName());
        leftHandController.setIterativeMode(false);
    }

    /**
     * Get the prefab that represents the left hand target.
     *
     * @return the prefab that represents the left hand target.
     */
    public Prefab getRightHandTarget() {
        String righthandtargetName = this.targetNames.get("righthandtarget");
        Prefab target = (Prefab) this.getChild(righthandtargetName);
        if (target == null) {
            target = (Prefab) this.getParent().getChild(righthandtargetName);
        }
        return target;
    }

    /**
     * Sets the left hand target for this body.
     *
     * @param p the left hand target.
     */
    public void setRightHandTarget(Prefab p) {
        this.targetNames.put("righthandtarget", p.getName());
    }

    public Vector3f getForwardWorldDirection() {
        return this.getWorldRotation().mult(Vector3f.UNIT_Z);
    }

    private void calculateFootSteps() {
        // run throught the character 
        this.footSteps.detachAllChildren();
        characterPath.createPathFootsteps();

    }

    public void addFootStep(Spatial footStep) {
        this.footSteps.attachChild(footStep);
    }

    public float getFootStepLength() {
        return footStepLength;
    }

    @Override
    public void setXTranslation(float x) {
        super.setXTranslation(x); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setYTranslation(float y) {
        super.setYTranslation(y); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setZTranslation(float z) {
        super.setZTranslation(z); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setXRotation(float xRot) {
        super.setXRotation(xRot); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setYRotation(float yRot) {
        super.setYRotation(yRot); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setZRotation(float zRot) {
        super.setZRotation(zRot); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }

    public void clearFootSteps() {
        this.footSteps.detachAllChildren();
    }

    @Override
    public void setLocalRotation(Quaternion quaternion) {
        super.setLocalRotation(quaternion); //To change body of generated methods, choose Tools | Templates.
        if (characterPath != null) {
            characterPath.createPathMesh();
        }
    }
    /**
     * Two targets that can be used to control the walk cycles.
     */
    private boolean useInternalLegTargets = false;
    private Handle rightLegTarget;
    private Handle leftLegTarget;

    /**
     * Make the character walk on the character path.
     */
    public void walk() {

        if (footSteps.getChildren().size() > 1) {
            if (rightLegTarget == null) {
                this.rightLegTarget = new Handle();
                rightLegTarget.create("rightLegTarget", assetManager, null);
                attachChild(rightLegTarget);
            }
            if (leftLegTarget == null) {
                this.leftLegTarget = new Handle();
                leftLegTarget.create("leftLegTarget", assetManager, null);
                attachChild(leftLegTarget);
            }


            useInternalLegTargets = true;
            Spatial firstRight = footSteps.getChild(0);
            rightLegTarget.setLocalTransform(firstRight.getLocalTransform());
            Spatial firstLeft = footSteps.getChild(1);
            leftLegTarget.setLocalTransform(firstLeft.getLocalTransform());

        }
    }

    public String getSkeletonFile() {
        return skeletonFile;
    }

    void setSkeletonFile(String skeletonFile) {
        this.skeletonFile = skeletonFile;
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

    public void write(Writer w, int depth) {
    }
}