/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.lights.AmbientLightPrefab;
import dae.prefabs.lights.PointLightPrefab;
import dae.prefabs.lights.SpotLightPrefab;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyPointLightTest {

    public ReadWriteCopyPointLightTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
   
    @Test
    public void readWriteAmbientLight() {
        PointLightPrefab al = (PointLightPrefab) TextIOTestSuite.createObject("Light", "PointLight", PointLightPrefab.class);


        ObjectComponent oc = (ObjectComponent) al.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = al.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("PointLight1", oc.getName());
        assertEquals("lights", oc.getLayer());

        String result = TextIOTestSuite.writeObject(al);

        Level loadedLevel = TextIOTestSuite.readLevel(result, PointLightPrefab.class, 1);
        PointLightPrefab loaded = loadedLevel.descendantMatches(PointLightPrefab.class).get(0);

        assertEquals(loaded.getName(), al.getName());
        
        assertEquals(loaded.getPointLightIntensity(), al.getPointLightIntensity(),.0001);
        assertEquals(loaded.getRadius(), al.getRadius(),.0001);
        assertEquals(loaded.getPointLightColor(), al.getPointLightColor());
    }
}