/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import java.util.HashMap;

/**
 *
 * @author Koen
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
