/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.SoundEntity;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Koen Samyn
 */
public class ReadWriteCopySoundEntityTest {
    
    
    
    public ReadWriteCopySoundEntityTest() {
    }
    
    
    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteSoundEntity() {
        SoundEntity al = (SoundEntity) TextIOTestSuite.createObject("Standard", "Sound", SoundEntity.class);


        ObjectComponent oc = (ObjectComponent) al.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = al.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Sound1", oc.getName());
        assertEquals("entities", oc.getLayer());

        String result = TextIOTestSuite.writeObject(al);

        Level loadedLevel = TextIOTestSuite.readLevel(result, SoundEntity.class, 1);
        SoundEntity loaded = loadedLevel.descendantMatches(SoundEntity.class).get(0);

        assertEquals(loaded.getName(), al.getName());
        
        assertEquals(loaded.getLooping(), al.getLooping());
        assertEquals(loaded.getMaxDistance(), al.getMaxDistance(),.0001);
        assertEquals(loaded.getPositional(), al.getPositional());
        assertEquals(loaded.getRefDistance(),al.getRefDistance(),.0001);
        assertEquals(loaded.getSoundFile(),al.getSoundFile());
        assertEquals(loaded.getVolume(),al.getVolume(),.0001);
    }
}