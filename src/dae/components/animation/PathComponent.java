package dae.components.animation;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.animation.custom.Animatable;
import dae.animation.custom.CharacterPath;
import dae.animation.custom.PathSegment;
import dae.components.AnimationComponent;
import dae.components.PrefabComponent;
import dae.components.physics.CharacterControllerComponent;
import dae.prefabs.Prefab;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;
import mlproject.fuzzy.LeftSigmoidMemberShip;
import mlproject.fuzzy.RightSigmoidMemberShip;
import mlproject.fuzzy.SigmoidMemberShip;
import mlproject.fuzzy.SingletonMemberShip;
import mlproject.fuzzy.TrapezoidMemberShip;

/**
 * The path component places an player, npc or object (such as a camera) on a
 * path. The path can be configure by setting a path object.
 *
 * @author Koen Samyn
 */
public class PathComponent extends PrefabComponent implements Animatable {

    private CharacterPath path;
    private PathSegment currentSegment;
    private float speed = 1.0f;
    private float t = 0.0f;
    private Vector3f splineStore = new Vector3f();
    private Vector3f tangentStore = new Vector3f();
    private boolean isWalking;
    private Prefab parent;
    private int waypointId = 0;
    private FuzzySystem waypointController = new FuzzySystem("waypoints");

    public PathComponent() {
        FuzzyVariable angle = new FuzzyVariable("angle");
        angle.addMemberShip(new LeftSigmoidMemberShip(-5, -1, "farleft"));
        angle.addMemberShip(new SigmoidMemberShip(-5, -1, -0.01f, "left"));
        angle.addMemberShip(new TrapezoidMemberShip(-1, -0.01f, 0.01f, 1, "center"));
        angle.addMemberShip(new SigmoidMemberShip(0.01f, 1f, 5, "right"));
        angle.addMemberShip(new RightSigmoidMemberShip(1, 5, "farright"));

        FuzzyVariable dangle = new FuzzyVariable("dangle");
        dangle.addMemberShip(new SingletonMemberShip("turnleftfast", -150));
        dangle.addMemberShip(new SingletonMemberShip("turnleft", -120));
        dangle.addMemberShip(new SingletonMemberShip("stay", 0));
        dangle.addMemberShip(new SingletonMemberShip("turnright", 120));
        dangle.addMemberShip(new SingletonMemberShip("turnrightfast", 150));

        waypointController.addFuzzyInput(angle);
        waypointController.addFuzzyOutput(dangle);

        waypointController.addFuzzyRule("if angle is farleft then dangle is turnrightfast");
        waypointController.addFuzzyRule("if angle is left then dangle is turnright");
        waypointController.addFuzzyRule("if angle is center then dangle is stay");
        waypointController.addFuzzyRule("if angle is right then dangle is turnleft");
        waypointController.addFuzzyRule("if angle is farright then dangle is turnleftfast");




    }

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
    }

    /**
     * @return the path
     */
    public CharacterPath getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(CharacterPath path) {
        this.path = path;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void walk() {
        if (path != null) {
            path.reset();
            activateAnimation("walk");
            isWalking = true;
        }
    }
    Quaternion rot = new Quaternion();

    @Override
    public void update(float deltaTime) {
        if (isWalking && path != null) {
            if (waypointId < path.getNrOfWaypoints()) {
                float actualspeed = speed;
                Vector3f wploc = path.getWaypointLocation(waypointId);
                Vector3f actorLoc = this.getWorldTranslation();

                float dx = wploc.x - actorLoc.x;
                float dz = wploc.z - actorLoc.z;

                if (FastMath.sqrt(dx * dx + dz * dz) < 0.1) {
                    ++waypointId;
                } else {
                    Vector3f forward = this.getForwardDirection();
                    Vector3f directionToGo = wploc.subtract(actorLoc);
                    directionToGo.normalizeLocal();

                    float angle = forward.angleBetween(directionToGo) * FastMath.RAD_TO_DEG;
                    if (directionToGo.cross(forward).y < 0) {
                        angle = -angle;
                    }
                    waypointController.setFuzzyInput("angle", angle);
                    waypointController.evaluate();
                    float dangle = waypointController.getFuzzyOutput("dangle") * FastMath.DEG_TO_RAD * deltaTime;
                    rot.fromAngleAxis(dangle, Vector3f.UNIT_Y);
                    Vector3f newrot = rot.mult(forward);
                    setViewDirection(newrot);
                    setWalkDirection(newrot.mult(speed));

                }
            } else {
                setWalkDirection(Vector3f.ZERO);
                activateAnimation("idle1");
                isWalking = false;
                waypointId = 0;
            }
        }
    }

    /**
     * Called when a new frame needs to be calculated.
     */
    /*
     @Override
     public void update(float deltaTime) {
     if (isWalking && path != null) {
     if (currentSegment == null) {
     currentSegment = path.getCurrentSegment(this);
     }
     float actualspeed = speed;


     if (deltaTime > 100) {
     deltaTime = 100;
     }

     float requiredLength = actualspeed * deltaTime;
     t = currentSegment.getSegment(t, requiredLength);
     Vector3f currentWorld = parent.getLocalTranslation();
     currentSegment.interpolate(t, splineStore);

     path.localToWorld(splineStore, splineStore);



     //setLocation(splineStore);
     Vector3f walk = new Vector3f(splineStore.x - currentWorld.x, 0, splineStore.z - currentWorld.z);
     System.out.println("Required length : " + requiredLength);
     System.out.println("walk length : " + walk.length());
     System.out.println("t : " + t);
     System.out.println("Delta time : " + deltaTime);
     System.out.println("currentWorld : " + currentWorld);
     //walk.normalizeLocal().multLocal(actualspeed*deltaTime);
     setWalkDirection(walk);

     tangentStore.set(this.getForwardDirection());

     currentSegment.getTangent(t, tangentStore);
     path.getWorldRotation().mult(tangentStore, tangentStore);
     setViewDirection(tangentStore);

     if (t >= 1.0f) {
     if (path.nextSegment()) {
     currentSegment = path.getCurrentSegment(this);
     t = 0.0f;
     } else {
     activateAnimation("idle1");
     t = 0.0f;
     currentSegment = null;
     isWalking = false;
     path.reset();
     setWalkDirection(Vector3f.ZERO);
     splineStore.set(0, 0, 0);
     }
     }
     }
     }
     */
    public void activateAnimation(String animation) {
        AnimationComponent ac = (AnimationComponent) parent.getComponent("AnimationComponent");
        if (ac != null) {
            ac.setAnimation(animation);
        }
    }

    public Vector3f getForwardDirection() {
        return parent.getWorldRotation().mult(Vector3f.UNIT_Z);
    }

    public Vector3f getWorldTranslation() {
        return parent.getWorldTranslation();
    }

    public void setLocation(Vector3f location) {
        CharacterControllerComponent cc = (CharacterControllerComponent) parent.getComponent("CharacterControllerComponent");
        if (cc != null) {
            cc.warp(location);
        }
    }

    public void setViewDirection(Vector3f view) {
        CharacterControllerComponent cc = (CharacterControllerComponent) parent.getComponent("CharacterControllerComponent");
        if (cc != null) {
            cc.setViewDirection(view);
        }
    }

    public void rotateY(float angle) {
    }

    public void setCharacterPath(CharacterPath path) {
        this.path = path;
    }

    public float getLocationOnCharacterPath() {
        return t;
    }

    public boolean isOnPath() {
        return isWalking;
    }

    public void unlockMovement() {
    }

    public void lockMovement() {
    }

    private void setWalkDirection(Vector3f walk) {
        CharacterControllerComponent cc = (CharacterControllerComponent) parent.getComponent("CharacterControllerComponent");
        if (cc != null) {
            cc.setWalkDirection(walk);
        }
    }
}
