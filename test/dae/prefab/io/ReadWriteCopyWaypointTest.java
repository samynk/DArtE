/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.animation.custom.Waypoint;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.project.Grid;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyWaypointTest {

    public ReadWriteCopyWaypointTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }

    @Test
    public void readWriteWaypointTest() {
        Waypoint wp = (Waypoint) TextIOTestSuite.createObject("Animation", "Waypoint", Waypoint.class);


        ObjectComponent oc = (ObjectComponent) wp.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = wp.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Waypoint1", oc.getName());
        assertEquals("waypoints", oc.getLayer());

        String result = TextIOTestSuite.writeObject(wp);

        Level loadedLevel = TextIOTestSuite.readLevel(result, Waypoint.class, 1);
        Waypoint loaded = loadedLevel.descendantMatches(Waypoint.class).get(0);

        assertEquals(loaded.getName(), wp.getName());

    }
}