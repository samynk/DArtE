/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
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
    private float refDistance = 10.0f;
    private float maxDistance = 20.0f;
    private Material refSoundMaterial;
    private Geometry refDistanceGeometry;
    private Material maxSoundMaterial;
    private Geometry maxDistanceGeometry;
    private AssetManager assetManager;
    private AudioNode audioNode;

    public SoundEntity() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Entities/M_Sound.j3o"));
        this.setCategory("Standard");
        this.setType("Sound");
        this.setName(name);

        soundFile = extraInfo;
        assetManager = manager;
        refSoundMaterial = manager.loadMaterial("/Materials/RefSoundMaterial.j3m");
        maxSoundMaterial = manager.loadMaterial("/Materials/MaxSoundMaterial.j3m");
        adaptSoundSpheres();
    }

    private void adaptSoundSpheres() {
        Sphere s1 = new Sphere(12, 12, refDistance);
        refDistanceGeometry = new Geometry("refDistanceGizmo", s1);
        refDistanceGeometry.setMaterial(refSoundMaterial);

        Sphere s2 = new Sphere(12, 12, maxDistance);
        maxDistanceGeometry = new Geometry("refDistanceGizmo", s2);
        maxDistanceGeometry.setMaterial(maxSoundMaterial);
        
        refDistanceGeometry.setUserData("Pickable", Boolean.FALSE);
        maxDistanceGeometry.setUserData("Pickable", Boolean.FALSE);
    }

    private void detachSoundSpheres() {
        if (this.refDistanceGeometry != null) {
            refDistanceGeometry.removeFromParent();
        }
        if (this.maxDistanceGeometry != null) {
            maxDistanceGeometry.removeFromParent();
        }
    }

    private void attachSoundSpheres() {
        if (this.refDistanceGeometry != null) {
            attachChild(refDistanceGeometry);
        }
        if (this.maxDistanceGeometry != null) {
            attachChild(maxDistanceGeometry);
        }
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
        if ( audioNode != null){
            audioNode.removeFromParent();
        }
        
        if ( !soundFile.isEmpty() )
        {
            audioNode = new AudioNode(assetManager,soundFile);
            attachChild(audioNode);
        }
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        adaptAudioNode();
    }

    public float getVolume() {
        return volume;
    }

    public void setPositional(boolean pos) {
        this.positional = pos;
        adaptAudioNode();
    }

    public boolean getPositional() {
        return positional;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        adaptAudioNode();
    }

    public boolean getLooping() {
        return looping;
    }

    /**
     * @return the refDistance
     */
    public float getRefDistance() {
        return refDistance;
    }

    /**
     * @param refDistance the refDistance to set
     */
    public void setRefDistance(float refDistance) {
        this.refDistance = refDistance;
        adaptAudioNode();
        detachSoundSpheres();
        adaptSoundSpheres();
        attachSoundSpheres();
    }

    /**
     * @return the maxDistance
     */
    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * @param maxDistance the maxDistance to set
     */
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
        detachSoundSpheres();
        adaptSoundSpheres();
        attachSoundSpheres();
    }

    /**
     * Play the sound of this node.
     */
    public void playSound() {
        if (audioNode == null) {
            audioNode = new AudioNode(assetManager, this.soundFile);
        }
        adaptAudioNode();
        attachChild(audioNode);
        audioNode.play();
    }

    /**
     * When the sound entity is selected, show the distance spheres that define
     * the sound entity.
     *
     * @param selected true if the prefab is selected , false otherwise.
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            attachSoundSpheres();
        } else {
            detachSoundSpheres();
        }
    }

    private void adaptAudioNode() {
        if ( audioNode == null)
            return;
        audioNode.setVolume(volume);
        audioNode.setRefDistance(refDistance);
        audioNode.setMaxDistance(maxDistance);
        audioNode.setLooping(looping);
        audioNode.setPositional(positional);
    }
}