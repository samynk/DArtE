package dae.prefab.io;

import com.jme3.audio.AudioData;
import com.jme3.util.NativeObject;

/**
 *
 * @author Koen Samyn
 */
public class MockAudioData extends AudioData{

    @Override
    public DataType getDataType() {
        return DataType.Buffer;
    }

    @Override
    public float getDuration() {
        return 1000;
    }

    @Override
    public void resetObject() {
        
    }

    @Override
    public void deleteObject(Object rendererObject) {
        
    }

    @Override
    public NativeObject createDestructableClone() {
        return null;
    }

    @Override
    public long getUniqueId() {
        return 1;
    }

}
