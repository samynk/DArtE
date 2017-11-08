package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
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
public class ReadWriteCopyNPCLocationEntity {
    
    public ReadWriteCopyNPCLocationEntity() {
    }
     @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    
    @Test
    public void readWriteNPCLocationEntity() {
        PlayerStartEntity object = (PlayerStartEntity) TextIOTestSuite.createObject("Standard", "PlayerStart", PlayerStartEntity.class);


        ObjectComponent oc = (ObjectComponent) object.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = object.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("PlayerStart1", oc.getName());
        assertEquals("entities", oc.getLayer());

        String result = TextIOTestSuite.writeObject(object);

        Level loadedLevel = TextIOTestSuite.readLevel(result, PlayerStartEntity.class, 1);
        PlayerStartEntity loaded = loadedLevel.descendantMatches(PlayerStartEntity.class).get(0);

        assertEquals(loaded.getName(), object.getName());
    }
}