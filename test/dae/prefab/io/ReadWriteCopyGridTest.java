package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.project.Grid;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyGridTest {

    public ReadWriteCopyGridTest() {
    }

    @Before
    public void setup() {
        GlobalObjects go = GlobalObjects.getInstance();
        if (go.getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
   
    @Test
    public void readWriteGridTest() {
        Grid grid = (Grid)TextIOTestSuite.createObject("Standard", "Ground", Grid.class);
        assertEquals(50, grid.getWidth(), 0.0001);
        assertEquals(25, grid.getLength(), .00001);
        
        ObjectComponent oc = (ObjectComponent)grid.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = grid.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Ground1",oc.getName());
        assertEquals("gizmo",oc.getLayer());
        
        String result = TextIOTestSuite.writeObject(grid);
        
        Level loadedLevel = TextIOTestSuite.readLevel(result,Grid.class,1);
        Grid loaded = loadedLevel.descendantMatches(Grid.class).get(0);
        
        assertEquals(loaded.getName(),grid.getName());
        assertEquals(50, loaded.getWidth(), 0.0001);
        assertEquals(25, loaded.getLength(), .00001);
               
    }
}