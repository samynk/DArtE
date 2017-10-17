/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.Terrain;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyTerrainTest {
    
    public ReadWriteCopyTerrainTest() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteTerrain() {
        Terrain object = (Terrain) TextIOTestSuite.createObject("Terrain", "Terrain", Terrain.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Terrain1", oc.getName());
        assertEquals("terrain", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, Terrain.class, 1);
        Terrain loaded = loadedLevel.descendantMatches(Terrain.class).get(0);

        assertEquals(loaded.getName(), object.getName());
    }
}