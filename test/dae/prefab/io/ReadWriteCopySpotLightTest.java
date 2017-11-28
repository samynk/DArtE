/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.lights.AmbientLightPrefab;
import dae.prefabs.lights.SpotLightPrefab;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopySpotLightTest {

    public ReadWriteCopySpotLightTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
  
    @Test
    public void readWriteAmbientLight() {
        SpotLightPrefab al = (SpotLightPrefab) TextIOTestSuite.createObject("Light", "SpotLight", SpotLightPrefab.class);


        ObjectComponent oc = (ObjectComponent) al.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = al.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("SpotLight1", oc.getName());
        assertEquals("lights", oc.getLayer());

        String result = TextIOTestSuite.writeObject(al);

        Level loadedLevel = TextIOTestSuite.readLevel(result, SpotLightPrefab.class, 1);
        SpotLightPrefab loaded = loadedLevel.descendantMatches(SpotLightPrefab.class).get(0);

        assertEquals(loaded.getName(), al.getName());
        assertEquals(loaded.getSpotInnerAngle(), al.getSpotInnerAngle(), .0001);
        assertEquals(loaded.getSpotOuterAngle(), al.getSpotOuterAngle(), .0001);
        assertEquals(loaded.getSpotRange(), al.getSpotRange(), .0001);
        assertEquals(loaded.getSpotLightIntensity(), al.getSpotLightIntensity(),.0001);
    }
}