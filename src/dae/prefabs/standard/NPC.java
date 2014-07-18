/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import dae.animation.custom.Animatable;
import dae.animation.custom.AnimationSet;
import dae.animation.custom.CharacterPath;
import dae.animation.custom.DAESkeletonControl;
import dae.animation.custom.PathSegment;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class NPC extends Prefab implements Animatable {

    private DAESkeletonControl skeletonControl;
    private String actorName = null;
    private String actorId = null;
    private static int id = 1;
    private CharacterPath path;
    private PathSegment currentSegment;
    private float t = 0.0f;
    private float walkSpeed = 1.0f;
    private Spatial syncNode;
    private boolean synced;
    private String meshName;
    private String meshFile;
    private String currentAnimation;

    public NPC() {
    }

    public NPC(Node animatedMesh, String actorName, String actorId) {
        this(null, animatedMesh, actorName, actorId);
    }

    public NPC(AnimationSet animationSet, Node animatedMesh, String actorName, String actorId) {
        this.attachChild(animatedMesh);
        skeletonControl = animatedMesh.getControl(DAESkeletonControl.class);

        if (actorName == null) {
            this.actorName = "npc" + (id++);
        } else {
            this.actorName = actorName;
        }

        this.actorId = actorId;
        this.setUserData("walkSpeed", walkSpeed);
        this.setUserData("camOffset", new Vector3f(0, 1.0f, 0));
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        if (extraInfo != null && extraInfo.length() > 0) {
            Spatial model = manager.loadModel(extraInfo);
            super.setName(name);
            this.meshName = name;
            this.meshFile = extraInfo;

            this.attachChild(model);
            skeletonControl = model.getControl(DAESkeletonControl.class);

            this.actorName = name;
            this.actorId = name;

            this.setUserData("walkSpeed", walkSpeed);
            this.setUserData("camOffset", new Vector3f(0, 1.0f, 0));
        }
    }

    public String getMeshName() {
        return meshName;
    }

    public String getMeshFile() {
        return meshFile;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorId() {
        return actorId;
    }

    /**
     * Returns the actual name of the actor.
     *
     * @return the actual name of the actor.
     */
    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setAnimationSet(AnimationSet set) {
        if (skeletonControl != null) {
            skeletonControl.addAnimationSet(set);
            setAnimation("idle1");
        }
    }

    @Override
    public void activateAnimation(String startAnimation) {
        currentAnimation = startAnimation;
        if (skeletonControl != null) {
            skeletonControl.activateAnimation(startAnimation);
        }
    }

    public void setAnimation(String animation) {
        currentAnimation = animation;
        if (skeletonControl != null) {
            skeletonControl.activateAnimation(animation);
        }
    }

    public String getAnimation() {
        return currentAnimation;
    }

    public void createBillboard(AssetManager manager) {
        // Set position above model

        // Load font and create the Text
        BitmapFont font = manager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmText = new BitmapText(font);
        bmText.setSize(0.25f);
        bmText.setText(actorName);

        bmText.setQueueBucket(Bucket.Transparent);
        bmText.setColor(ColorRGBA.Orange);
        bmText.setBox(new Rectangle(-bmText.getLineWidth() / 2.0f - 0.2f, 0, bmText.getLineWidth() * 2.0f, bmText.getLineHeight() + 0.1f));
        bmText.setAlignment(BitmapFont.Align.Left);
        bmText.setLineWrapMode(LineWrapMode.NoWrap);
        Vector3f newPos = new Vector3f(0, 2.5f, 0);
        // Create node and add a BillboardControl so it rotates with the cam
        BillboardControl bbControl = new BillboardControl();
        bbControl.setAlignment(BillboardControl.Alignment.Screen);


        Node textNode = new Node("Node for text");
        textNode.setLocalTranslation(newPos);
        textNode.setCullHint(CullHint.Never);
        textNode.attachChild(bmText);
        bmText.addControl(bbControl);

        //Vector3f world = textNode.getWorldTranslation();
        this.attachChild(textNode);
        //System.out.println("Text node location for " + actorName + "," +world);
    }

    @Override
    public Vector3f getForwardDirection() {
        Vector3f forwardDir = new Vector3f(0, 0, -1);
        localVectorToWorld(forwardDir, forwardDir);
        return forwardDir;
    }
    private Vector3f origin = Vector3f.ZERO;
    private Vector3f to = new Vector3f();
    private Vector3f ti = new Vector3f();

    public void localVectorToWorld(Vector3f in, Vector3f out) {
        origin.set(0, 0, 0);
        localToWorld(origin, to);
        localToWorld(in, ti);

        out.set(ti.x - to.x, ti.y - to.y, ti.z - to.z);
        out.normalizeLocal();
    }

    @Override
    public void setCharacterPath(CharacterPath path) {
        this.path = path;
        t = 0.0f;
    }
    String waypointTest = "";

    public void setWaypointTest(String path) {
        this.waypointTest = path;

    }

    public String getWaypointTest() {
        return waypointTest;
    }

    public void doWaypointTest() {
        Node parentNode = getParent();
        String[] ids = waypointTest.split(";");
        CharacterPath testPath = new CharacterPath();
        testPath.setRestoreOriginalPosition(true);

        //testPath.addWayPoint(this.getLocalTranslation());
        for (String wpid : ids) {
            System.out.println("Waypoint id is : " + wpid);
            Spatial waypoint = parentNode.getChild("waypoint_" + wpid);
            if (waypoint != null) {
                Vector3f wploc = waypoint.getWorldTranslation();
                System.out.println("Adding waypoint : " + wploc);
                testPath.addWayPoint(wploc.clone());
            }
        }
        if (testPath.getNrOfWaypoints() > 0) {
            this.setCharacterPath(testPath);
        }
        this.activateAnimation("walk");
    }

    @Override
    public float getLocationOnCharacterPath() {
        return t;
    }
    private Vector3f splineStore = new Vector3f();
    private Vector3f tangentStore = new Vector3f();

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);
        if (path != null) {
            if (currentSegment == null) {
                currentSegment = path.getCurrentSegment(this);
            }
            float requiredLength = walkSpeed * tpf;
            t = currentSegment.getSegment(t, requiredLength);
            currentSegment.interpolate(t, splineStore);

            Vector3f currentWorld = getWorldTranslation();
            splineStore.y = currentWorld.y;

            setLocation(splineStore);
            tangentStore.set(this.getForwardDirection());
            currentSegment.getTangent(t, tangentStore);
            float angle = FastMath.atan2(tangentStore.z, tangentStore.x);
            rotateY(-FastMath.PI / 2 - angle);

            if (t >= 1.0f) {
                if (path.nextSegment()) {
                    currentSegment = path.getCurrentSegment(this);
                    t = 0.0f;
                } else {
                    path = null;
                    t = 0.0f;
                    currentSegment = null;
                }
            }
        }
        if (syncNode != null && !synced) {
            this.setLocalTransform(syncNode.getWorldTransform());
            synced = true;
        }
    }

    /**
     * Checks if the player is still on a path.
     *
     * @return true if the player is walking on a path, false otherwise.
     */
    @Override
    public boolean isOnPath() {
        return path != null;
    }

    @Override
    public void setLocation(Vector3f location) {
        this.setLocalTranslation(location);
    }
    private Matrix3f rotation = new Matrix3f();

    @Override
    public void rotateY(float angle) {
        rotation.fromAngleAxis(angle, Vector3f.UNIT_Y);
        setLocalRotation(rotation);
    }

    @Override
    public void unlockMovement() {
    }

    @Override
    public void lockMovement() {
    }

    public void setSyncNode(Spatial transformNode) {
        this.syncNode = transformNode;
    }
}
