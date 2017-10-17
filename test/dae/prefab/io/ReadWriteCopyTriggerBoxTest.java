/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.standard.TriggerBox;
import dae.prefabs.standard.SoundEntity;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyTriggerBoxTest {
    
    public ReadWriteCopyTriggerBoxTest() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteTrigger() {
        TriggerBox object = (TriggerBox) TextIOTestSuite.createObject("Standard", "Trigger", TriggerBox.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("Trigger1", oc.getName());
        assertEquals("physics", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, TriggerBox.class, 1);
        TriggerBox loaded = loadedLevel.descendantMatches(TriggerBox.class).get(0);

        assertEquals(loaded.getName(), object.getName());
        assertEquals(loaded.getWidth(),object.getWidth(),.0001);
        assertEquals(loaded.getHeight(),object.getHeight(),.0001);
        assertEquals(loaded.getLength(),object.getLength(),.0001);
        
    }
}