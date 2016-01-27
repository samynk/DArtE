/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.CameraEntity;
import dae.prefabs.standard.SoundEntity;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyCameraEntityTest {
    
    public ReadWriteCopyCameraEntityTest() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteSoundEntity() {
        CameraEntity object = (CameraEntity) TextIOTestSuite.createObject("Standard", "Camera", CameraEntity.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Camera1", oc.getName());
        assertEquals("entities", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, CameraEntity.class, 1);
        CameraEntity loaded = loadedLevel.descendantMatches(CameraEntity.class).get(0);

        assertEquals(loaded.getName(), object.getName());
        
        assertEquals(object.getStartCam(),loaded.getStartCam());
    }
}