/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class SoundEntity extends Prefab {

    private String soundFile;
    private float volume;
    private boolean positional;
    private boolean looping;

    public SoundEntity() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Entities/M_Sound.j3o"));
        this.setCategory("Standard");
        this.setType("Sound");
        this.setName(name);
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    public void setPositional(boolean pos) {
        this.positional = pos;
    }

    public boolean getPositional() {
        return positional;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean getLooping() {
        return looping;
    }
}