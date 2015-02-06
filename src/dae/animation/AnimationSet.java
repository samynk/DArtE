package dae.animation;

import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Maintains a list of animations.
 * @author Koen Samyn
 */
public class AnimationSet{
    // the name of the animation set.
    private String name;
    // the map with the animations.
    private ArrayList<String> animationNames = new ArrayList<String>();
    public HashMap<String,Spatial> animationMap = new HashMap<String,Spatial>();
    
    /**
     * Creates a new AnimationSet.
     */
    public AnimationSet(String name){
        this.name = name;
    }
    
    /**
     * Returns the name of the animation set.
     * @return 
     */
    public String getName(){
        return name;
    }
    
    /**
     * Adds an animation to the set of animations.
     * @param name the name of the animation.
     * @param animation the Spatial with the animation data.
     */
    public void addAnimation(String name, Spatial animation){
        animationMap.put(name, animation);
        animationNames.add(name);
    }
    
    /**
     * Returns the set of animations.
     * @return an iterable with all the animations.
     */
    public Iterable<Spatial> getAnimations(){
        return animationMap.values();
    }

    public String getFirstAnimationName() {
        if ( animationNames.size() > 0 )
        {
            return animationNames.get(0);
        }else{
            return "";
        }
    }
}