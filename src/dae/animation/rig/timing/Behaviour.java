/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import java.util.ArrayList;

/**
 * A behaviour animates a set of targets that establish an animation in the
 * character.
 *
 * @author Koen.Samyn
 */
public class Behaviour {

    private final String name;
    private int fps;
    private final ArrayList<TimeLine> timeLines = new ArrayList<>();

    private int currentFrame = 0;
    private boolean isPlaying = false;

    public Behaviour(String name, int fps) {
        this.name = name;
        this.fps = fps;
    }

    /**
     * Returns the name of the behaviour.
     *
     * @return the name of the behaviour.
     */
    public String getName() {
        return name;
    }

    public int getFPS() {
        return fps;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }
    
    public int getCurrentFrame(){
        return currentFrame;
    }
    
    public void setCurrentFrame(int currentFrame){
        this.currentFrame = currentFrame;
    }

    /**
     * Add a timeLine object to the list of timelines.
     *
     * @param timeLine the timeline object to add.
     */
    public void addTimeLine(TimeLine timeLine) {
        timeLines.add(timeLine);
    }

    /**
     * Returns the timelines of this behaviour.
     *
     * @return the timeline.
     */
    public Iterable<TimeLine> getTimeLines() {
        return timeLines;
    }

    /**
     * Return the number of timelines.
     * @return the number of timelines.
     */
    public int getNumberOfTimeLines() {
        return timeLines.size();
    }

    public int getMaxFrameNumber() {
        int max = 0;
        for (TimeLine tl : this.timeLines) {
            if (max < tl.getMaxFrameNumber()) {
                max = tl.getMaxFrameNumber();
            }
        }
        return max;
    }

    /**
     * Play the behaviour
     */
    public void play() {
        currentFrame = 0;
        isPlaying = true;
    }

    /**
     * Returns a string representation of this behaviour.
     *
     * @return the name of the behaviour.
     */
    @Override
    public String toString() {
        return name;
    }
}
