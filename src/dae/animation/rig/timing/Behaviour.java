/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import com.jme3.scene.Spatial;
import dae.animation.rig.Rig;
import java.util.ArrayList;
import java.util.HashMap;

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
    private final HashMap<Spatial, TimeLine> timeLineMap = new HashMap<>();

    private int currentFrame = 0;
    private boolean isPlaying = false;
    private float currentTime = 0;

    public Behaviour(String name, int fps) {
        this.name = name;
        this.fps = fps;
    }

    public void cloneForRig(Rig rig) {
        Behaviour copy = new Behaviour(name, fps);
        for (TimeLine tl : this.timeLines) {
            String targetName = tl.getTarget().getName();
            Spatial target = rig.getChild(targetName);
            TimeLine copytl = new TimeLine(target);
            copytl.addAllKeys(tl);
            copy.addTimeLine(copytl);
        }
        rig.addBehaviour(copy);
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

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    /**
     * Return the current animation time for this behaviour.
     *
     * @return the current animation time.
     */
    public float getCurrentTime() {
        return currentTime;
    }

    /**
     * Adds an amount of time to the current time.
     *
     * @param time the amount to add.
     */
    public void addTime(float time) {
        currentTime += time;
    }

    /**
     * Returns the current frame as a float. This can be used to interpolate
     * between frames.
     *
     * @return the current frame as a float.
     */
    public float getCurrentPlayFrame() {
        float frame = (currentTime * fps) % getMaxFrameNumber();
        //System.out.println("Current time is: " + currentTime);
        //System.out.println("frame is : " + frame);
        return frame;
    }

    /**
     * Add a timeLine object to the list of timelines.
     *
     * @param timeLine the timeline object to add.
     */
    public void addTimeLine(TimeLine timeLine) {
        timeLines.add(timeLine);
        timeLineMap.put(timeLine.getTarget(), timeLine);
    }

    /**
     * Returns the timelines of this behaviour.
     *
     * @return the timeline.
     */
    public Iterable<TimeLine> getTimeLines() {
        return timeLines;
    }

    public TimeLine getTimeLine(Spatial n) {
        TimeLine tl = timeLineMap.get(n);
        if (tl == null) {
            tl = new TimeLine(n);
            addTimeLine(tl);
        }
        return tl;
    }

    /**
     * Return the number of timelines.
     *
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
        currentTime = 0;
        isPlaying = true;
    }

    public void pause() {
        isPlaying = false;
    }

    /**
     * Check if the behaviour is playing or not.
     *
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
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

    public TimeLine getTimeLine(int index) {
        if ( index != -1 && index < timeLines.size()){
            return timeLines.get(index);
        }else{
            return null;
        }
    }

}
