package dae.components;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.AnimationSet;
import dae.prefabs.Prefab;
import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class AnimationComponent extends PrefabComponent {

    private String animationSet;
    private String animation;
    private Prefab toAnimate;
    private AnimationSet animations;
    private AnimChannel animationChannel;
    private ArrayList<String> animationNames = new ArrayList<String>();

    @Override
    public void install(Prefab parent) {
        toAnimate = parent;
        loadAnimationSet();
    }

    /**
     * @return the animationSet
     */
    public String getAnimationSet() {
        return animationSet;
    }

    /**
     * @param animationSet the animationSet to set
     */
    public void setAnimationSet(String animationSet) {
        this.animationSet = animationSet;
        loadAnimationSet();
    }

    private void copyAnimControl(Spatial animatedMesh, AnimControl control) {
        AnimControl ac = animatedMesh.getControl(AnimControl.class);
        if (ac != null) {
            for (String animationName : control.getAnimationNames()) {
                ac.addAnim(control.getAnim(animationName));
                animationNames.add(animationName);
            }
        }
    }

    private AnimChannel initAnimation(Spatial model) {
        AnimControl ac = model.getControl(AnimControl.class);
        if (ac != null) {
            if (ac.getNumChannels() == 1) {
                return ac.getChannel(0);
            } else {
                AnimChannel channel = ac.createChannel();
                return channel;
            }
        } else {
            return null;
        }
    }

    /**
     * @return the animation
     */
    public String getAnimation() {
        return animation;
    }

    /**
     * @param animation the animation to set
     */
    public void setAnimation(String animation) {
        this.animation = animation;
        if ( animationChannel != null)
        {
            animationChannel.setAnim(animation);
        }
    }

    public Object[] getAnimations() {
        return animationNames.toArray();
    }

    private void loadAnimationSet() {
        if (animationSet == null) {
            return;
        }
        if (toAnimate != null) {
            AssetManager manager = GlobalObjects.getInstance().getAssetManager();
            animations = (AnimationSet) manager.loadAsset(animationSet);

            for (Spatial child : toAnimate.getChildren()) {
                AnimControl ac = child.getControl(AnimControl.class);
                if (ac != null) {
                    // clear the previous animations
                    ac.clearChannels();
                    animationNames.clear();
                    // set the new ones.
                    for (Spatial s : animations.getAnimations()) {
                        AnimControl controlToCopy = s.getControl(AnimControl.class);
                        copyAnimControl(child, controlToCopy);
                    }
                    animationChannel = initAnimation(child);
                    animationChannel.setAnim("idle1");
                }
            }
        }
    }
}