/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import java.util.ArrayList;

/**
 * A behaviour animates a set of targets that establish an animation in the
 * character.
 * @author Koen.Samyn
 */
public class Behaviour {
    private final String name;
    private final int fps;
    private final ArrayList<TimeLine> timeLines = new ArrayList<>();
    
    private int currentFrame = 0;
    private boolean isPlaying = false;
    
    public Behaviour(String name, int fps){
        this.name = name;
        this.fps = fps;
    }
    
    /**
     * Returns the name of the behaviour.
     * @return the name of the behaviour.
     */
    public String getName(){
        return name;
    }
    
    /**
     * Add a timeLine object to the list of timelines.
     * @param timeLine the timeline object to add.
     */
    public void addTimeLine(TimeLine timeLine){
        timeLines.add(timeLine);
    }
    
    /**
     * Play the behaviour
     */
    public void play(){
        currentFrame = 0;
        isPlaying = true;
    } 
}
