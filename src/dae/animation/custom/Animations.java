package dae.animation.custom;

import java.util.HashMap;

/**
 *
 * @author Koen Samyn
 */
public class Animations {

    private HashMap<String, AnimationSet> animations =
            new HashMap<String, AnimationSet>();

    public void addAnimationSet(AnimationSet set) {
        animations.put(set.getName(), set);
    }

    public AnimationSet getAnimationSet(String key) {
        return animations.get(key);
    }
}
