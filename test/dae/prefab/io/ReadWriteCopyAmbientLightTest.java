package dae.prefab.io;

import com.jme3.math.Quaternion;
import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.lights.AmbientLightPrefab;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Koen Samyn
 */
public class ReadWriteCopyAmbientLightTest {

    public ReadWriteCopyAmbientLightTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
   
    @Test
    public void readWriteAmbientLight() {
        AmbientLightPrefab al = (AmbientLightPrefab) TextIOTestSuite.createObject("Light", "AmbientLight", AmbientLightPrefab.class);


        ObjectComponent oc = (ObjectComponent) al.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = al.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("AmbientLight1", oc.getName());
        assertEquals("lights", oc.getLayer());
        
       
        al.setLocalPrefabRotation(Quaternion.IDENTITY);

        String result = TextIOTestSuite.writeObject(al);

        Level loadedLevel = TextIOTestSuite.readLevel(result, AmbientLightPrefab.class, 1);
        AmbientLightPrefab loaded = loadedLevel.descendantMatches(AmbientLightPrefab.class).get(0);

        assertEquals(loaded.getName(), al.getName());
    }
}