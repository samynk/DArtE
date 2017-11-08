package dae.animation.custom;

import java.util.HashMap;

/**
 *
 * @author Koen Samyn
 */
public class AnimationSet {

    private HashMap<String, DAEBoneAnimation> animationSet =
            new HashMap<String, DAEBoneAnimation>();
    private String name;

    public AnimationSet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addBoneAnimation(String key, DAEBoneAnimation animation) {
        animationSet.put(key, animation);
    }

    public Iterable<DAEBoneAnimation> getAnimations() {
        return animationSet.values();
    }
}
