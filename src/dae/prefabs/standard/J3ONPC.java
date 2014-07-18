/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.custom.Animatable;
import dae.animation.custom.CharacterPath;
import dae.prefabs.Prefab;
import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class J3ONPC extends Prefab implements Animatable {

    private static int id = 0;
    private String actorName;
    private String actorId;
    private float walkSpeed = 1.0f;
    private String meshName;
    private String meshFile;
    private AnimChannel animationChannel;
    private String currentAnimation;
    private HashMap<String, String> animationMap = new HashMap<String, String>();

    public J3ONPC() {
    }

    public J3ONPC(Node animatedMesh, String actorName, String actorId) {
        this.attachChild(animatedMesh);


        if (actorName == null) {
            this.actorName = "npc" + (id++);
        } else {
            this.actorName = actorName;
        }

        this.actorId = actorId;
        this.setUserData("walkSpeed", walkSpeed);
        this.setUserData("camOffset", new Vector3f(0, 1.0f, 0));


        animatedMesh.setLocalTranslation(0, -0.7f, 0);
        animatedMesh.rotate(0, FastMath.PI, 0);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.3f, 1.0f, 1);

        CharacterControl character_phys = new CharacterControl(capsuleShape, 0.4f);
        character_phys.setJumpSpeed(5);
        character_phys.setFallSpeed(2);
        character_phys.setGravity(4);
        character_phys.setUseViewDirection(false);
        character_phys.setMaxSlope(45 * FastMath.DEG_TO_RAD);
        // Attach physical properties to model and PhysicsSpace
        this.addControl(character_phys);
        initAnimation(animatedMesh);

    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        Spatial model = manager.loadModel(extraInfo);
        super.setName(name);
        this.meshName = name;
        this.meshFile = extraInfo;

        this.attachChild(model);
        model.rotate(0, FastMath.PI, 0);

        this.actorName = name;
        this.actorId = name;

        this.setUserData("walkSpeed", walkSpeed);
        this.setUserData("camOffset", new Vector3f(0, 1.0f, 0));


        initAnimation(model);
    }

    @Override
    public void activateAnimation(String animation) {

        if (animationChannel != null) {
            String realName = animationMap.get(animation);
            System.out.println("Activating : " + animation + "," + realName);
            if (realName != null) {

                animationChannel.setAnim(realName, 1.0f);
            }
        }

    }

    public void setAnimation(String animation) {
        currentAnimation = animation;
        activateAnimation(animation);
    }

    public String getAnimation() {
        return currentAnimation;
    }

    @Override
    public Vector3f getForwardDirection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Vector3f getWorldTranslation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLocation(Vector3f location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotateY(float angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCharacterPath(CharacterPath path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getLocationOnCharacterPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isOnPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unlockMovement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lockMovement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getMeshFile() {
        return meshFile;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    private void initAnimation(Spatial model) {
        AnimControl ac = model.getControl(AnimControl.class);
        if (ac != null) {
            for (String animationName : ac.getAnimationNames()) {
                int fuIndex = animationName.indexOf('_');
                if (fuIndex > -1) {
                    String mapName = animationName.substring(0, fuIndex);
                    animationMap.put(mapName, animationName);
                } else {
                    animationMap.put(animationName, animationName);
                }
            }
            animationChannel = ac.createChannel();
        }
    }
}
