package dae.components;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
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
    private Spatial toAnimate;
    private AnimationSet animations;
    private AnimChannel animationChannel;
    private ArrayList<String> animationNames = new ArrayList<String>();

    @Override
    public void install(Prefab parent) {
        toAnimate = parent;
        loadAnimationSet();
    }

    @Override
    public void installGameComponent(Spatial parent) {
        toAnimate = parent;
        loadAnimationSet();
        if (animation != null) {
            setAnimation(animation);
        }
    }

    /**
     * Removes this AnimationComponent from the parent.
     *
     * @param parent the parent to remove the AnimationComponent from.
     */
    @Override
    public void deinstall() {
        if (animationSet == null || toAnimate == null) {
            return;
        }
        AnimControl ac = toAnimate.getControl(AnimControl.class);
        if (ac != null) {
            ac.clearChannels();
        } else if (toAnimate instanceof Node) {
            Node parent = (Node) toAnimate;
            for (Spatial child : parent.getChildren()) {
                AnimControl cac = child.getControl(AnimControl.class);
                if (cac != null) {
                    cac.clearChannels();
                }
            }
        }
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
        if (animationChannel != null) {
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

            AnimControl ac = toAnimate.getControl(AnimControl.class);
            if (ac != null) {
                initializeAnimationChannel(ac, toAnimate);
            } else if (toAnimate instanceof Node) {
                Node parent = (Node) toAnimate;
                for (Spatial child : parent.getChildren()) {
                    AnimControl cac = child.getControl(AnimControl.class);
                    if (cac != null) {
                        initializeAnimationChannel(cac, child);
                    }
                }
            }
        }
    }

    private void initializeAnimationChannel(AnimControl ac, Spatial child) {
        // clear the previous animations
        ac.clearChannels();
        animationNames.clear();
        // set the new ones.
        for (Spatial s : animations.getAnimations()) {
            AnimControl controlToCopy = s.getControl(AnimControl.class);
            copyAnimControl(child, controlToCopy);
        }
        animationChannel = initAnimation(child);
        setAnimation("idle1");
    }
}