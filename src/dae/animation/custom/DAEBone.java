package dae.animation.custom;

import com.jme3.math.Matrix4f;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Koen
 */
public class DAEBone {

    private Matrix4f localBoneBindPoseTransform;
    private Matrix4f modelBoneBindPoseTransform;
    private Matrix4f inverseBoneBindPoseTransform;
    private Matrix4f localBoneAnimationTransform;
    private Matrix4f modelBoneAnimationTransform;
    private Matrix4f finalSkinningTransform;
    private ArrayList<DAEBone> children = new ArrayList<DAEBone>();
    private int boneId;
    private String name;
    public DAEBone parent;

    public DAEBone(Matrix4f transform, String name, int boneId) {
        this.localBoneBindPoseTransform = transform;
        this.boneId = boneId;
        this.name = name;
    }

    public DAEBone getParent() {
        return parent;
    }

    public int getBoneId() {
        return boneId;
    }

    public String getName() {
        return name;
    }

    public void setParent(DAEBone bone) {
        this.parent = bone;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void addChild(DAEBone bone) {
        bone.setParent(this);
        children.add(bone);
    }

    public Iterable<DAEBone> getChildren() {
        return children;
    }

    public Matrix4f getModelBoneBindPoseTransform() {
        return this.modelBoneBindPoseTransform;
    }

    public Matrix4f getModelBoneAnimationTransform() {
        return this.modelBoneAnimationTransform;
    }

    public void calculateModelBindPose() {
        if (isRoot()) {
            modelBoneBindPoseTransform = new Matrix4f(this.localBoneBindPoseTransform);
        } else {
            Matrix4f parentTransform = parent.getModelBoneBindPoseTransform();
            this.modelBoneBindPoseTransform = parentTransform.mult(localBoneBindPoseTransform);

        }
        inverseBoneBindPoseTransform = modelBoneBindPoseTransform.invert();
        for (DAEBone bone : this.getChildren()) {
            bone.calculateModelBindPose();
        }
    }

    public void calculateAnimationPose() {
        if (isRoot()) {
            modelBoneAnimationTransform = new Matrix4f(this.localBoneAnimationTransform);
        } else {
            Matrix4f parentAnimationTransform = parent.getModelBoneAnimationTransform();
            this.modelBoneAnimationTransform = parentAnimationTransform.mult(localBoneAnimationTransform);
        }
        for (DAEBone bone : this.getChildren()) {
            bone.calculateAnimationPose();
        }
    }

    public void setLocalBoneAnimationTransform(Matrix4f transform) {
        this.localBoneAnimationTransform = transform;
    }

    public void calculateSkinningTransform() {
        this.finalSkinningTransform = modelBoneAnimationTransform.mult(inverseBoneBindPoseTransform);
        for (DAEBone bone : this.getChildren()) {
            bone.calculateSkinningTransform();
        }
    }

    public Matrix4f getFinalSkinningTransform() {
        return finalSkinningTransform;
    }

    public void getFinalSkinningTransform(Matrix4f toCopy) {
        toCopy.set(this.finalSkinningTransform);
    }
}
