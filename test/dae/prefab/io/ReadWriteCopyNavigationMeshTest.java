package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.NavigationMesh;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyNavigationMeshTest {
    
    public ReadWriteCopyNavigationMeshTest() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteNavigationMesh() {
        NavigationMesh object = (NavigationMesh) TextIOTestSuite.createObject("Standard", "NavigationMesh", NavigationMesh.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("NavigationMesh1", oc.getName());
        assertEquals("entities", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, NavigationMesh.class, 1);
        NavigationMesh loaded = loadedLevel.descendantMatches(NavigationMesh.class).get(0);

        assertEquals(loaded.getName(), object.getName());
    }
}