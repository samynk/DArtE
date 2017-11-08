package dae.animation.custom;

import com.jme3.math.Matrix4f;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class Track {

    public ArrayList<Matrix4f> transforms = new ArrayList<Matrix4f>();
    private String boneName;
    private int boneId;

    public Track(String boneName, int boneId) {
        this.boneName = boneName;
        this.boneId = boneId;
    }

    public void addTransform(Matrix4f transform) {
        this.transforms.add(transform);
    }

    public Matrix4f getTransform(int index) {
        return transforms.get(index);
    }
}
