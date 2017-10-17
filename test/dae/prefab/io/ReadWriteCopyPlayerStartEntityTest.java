/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.NPCLocationEntity;
import dae.prefabs.standard.PlayerStartEntity;
import dae.prefabs.standard.SoundEntity;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyPlayerStartEntityTest {
    
    public ReadWriteCopyPlayerStartEntityTest() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWritePlayerStartEntity() {
        NPCLocationEntity object = (NPCLocationEntity) TextIOTestSuite.createObject("Standard", "NPCLocation", NPCLocationEntity.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("NPCLocation1", oc.getName());
        assertEquals("entities", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, NPCLocationEntity.class, 1);
        NPCLocationEntity loaded = loadedLevel.descendantMatches(NPCLocationEntity.class).get(0);

        assertEquals(loaded.getName(), object.getName());
    }
}