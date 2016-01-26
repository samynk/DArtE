/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.animation.custom.CharacterPath;
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
public class ReadWriteCopyCharacterPath {

    public ReadWriteCopyCharacterPath() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }

    @Test
    public void readWriteCharacterPathTest() {
        CharacterPath cp = (CharacterPath) TextIOTestSuite.createObject("Animation", "CharacterPath", CharacterPath.class);


        ObjectComponent oc = (ObjectComponent) cp.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = cp.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("CharacterPath1", oc.getName());
        assertEquals("waypoints", oc.getLayer());

        String result = TextIOTestSuite.writeObject(cp);

        Level loadedLevel = TextIOTestSuite.readLevel(result, CharacterPath.class, 1);
        CharacterPath loaded = loadedLevel.descendantMatches(CharacterPath.class).get(0);

        assertEquals(loaded.getName(), cp.getName());
    }
}