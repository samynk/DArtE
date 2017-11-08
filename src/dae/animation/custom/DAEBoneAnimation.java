package dae.animation.custom;

import com.jme3.math.Matrix4f;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class DAEBoneAnimation {

    public String name;
    private float timeInSeconds;
    private int samples;
    public ArrayList<Track> tracks = new ArrayList<Track>();

    public DAEBoneAnimation(String name, int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public void addSample(String boneName, int boneId, int sample, Matrix4f transform) {
        Track track;
        if (boneId >= tracks.size()) {
            track = new Track(boneName, boneId);
            tracks.add(boneId, track);
        } else {
            track = tracks.get(boneId);
        }
        track.addTransform(transform);
        if (sample > samples) {
            samples = sample;
            timeInSeconds = samples * 1 / 15.0f;
        }
    }

    public Matrix4f getAnimationTransform(float time, int boneId) {
        Track t = tracks.get(boneId);
        int index = (int) (time * (samples * 1.0f / timeInSeconds));

        index = index % samples;
        return t.getTransform(index);
    }

    public String getName() {
        return name;
    }
}
