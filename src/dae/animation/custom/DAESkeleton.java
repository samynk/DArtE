/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.math.Matrix4f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Koen
 */
public class DAESkeleton {

    private DAEBone rootBone;
    private ArrayList<DAEBone> bones = new ArrayList<DAEBone>();
    private Matrix4f[] skinningMatrices;
    private HashMap<String, DAEBone> boneMap = new HashMap<String, DAEBone>();
    private HashMap<String, DAEBoneAnimation> animations = new HashMap<String, DAEBoneAnimation>();
    private DAEBoneAnimation currentAnimation;
    float animTime = 0;
    static int index = 0;

    public DAESkeleton(DAEBone rootBone) {
        bones.add(rootBone.getBoneId(), rootBone);
        boneMap.put(rootBone.getName(), rootBone);
        createBoneList(rootBone);
        this.rootBone = rootBone;
        rootBone.calculateModelBindPose();
        skinningMatrices = new Matrix4f[bones.size()];
        for (int i = 0; i < bones.size(); ++i) {
            skinningMatrices[i] = new Matrix4f();
            skinningMatrices[i].set(Matrix4f.IDENTITY);
        }

    }

    public int getNumberOfBones() {
        return bones.size();
    }

    @Override
    public Object clone() {
        DAESkeleton clone = new DAESkeleton(rootBone);
        clone.animations = this.animations;
        clone.currentAnimation = this.currentAnimation;
        return clone;
    }

    public void addAnimation(DAEBoneAnimation animation) {
        //System.out.println("Adding animation : " + animation.getName());
        animations.put(animation.getName(), animation);
    }

    public void activateAnimation(String animation) {
        if (currentAnimation == null || !(currentAnimation.name.equals(animation))) {

            currentAnimation = animations.get(animation);
            //System.out.println("Activating : "+currentAnimation.getName());
            //System.out.println("Skeleton : " + this);
            animTime = 0;
        }
    }

    public boolean isAnimationActive() {
        return currentAnimation != null;
    }

    public final void createBoneList(DAEBone parentBone) {
        for (DAEBone bone : parentBone.getChildren()) {
            //bones.ensureCapacity(bone.getBoneId() + 1);
            bones.add(bone.getBoneId(), bone);
            boneMap.put(bone.getName(), bone);
            createBoneList(bone);
        }

    }

    public Matrix4f[] computeSkinningMatrices(float tpf) {
        // TODO calculate matrices
        animTime += tpf * 2;
        if (isAnimationActive()) {
            //System.out.println(currentAnimation.getName());
            //System.out.println("Skeleton " + this);
            for (int i = 0; i < bones.size(); ++i) {
                DAEBone bone = bones.get(i);
                Matrix4f t = currentAnimation.getAnimationTransform(animTime, bone.getBoneId());
                bone.setLocalBoneAnimationTransform(t);

            }
            this.rootBone.calculateAnimationPose();
            this.rootBone.calculateSkinningTransform();
            for (int i = 0; i < bones.size(); ++i) {
                DAEBone bone = bones.get(i);
                Matrix4f result = bone.getFinalSkinningTransform();
                bone.getFinalSkinningTransform(skinningMatrices[bone.getBoneId()]);
                skinningMatrices[bone.getBoneId()] = result;
            }
        } else {
            System.out.println("No animation active !!");
        }
        return skinningMatrices;
    }

    public DAEBoneAnimation getCurrentAnimation() {
        return currentAnimation;
    }
}
