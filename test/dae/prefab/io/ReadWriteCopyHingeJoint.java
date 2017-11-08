package dae.prefab.io;

import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.prefabs.physics.JointPrefab;
import dae.project.Level;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyHingeJoint {

    public ReadWriteCopyHingeJoint() {
    }

    @Before
    public void setup() {
        GlobalObjects go = GlobalObjects.getInstance();
        if (go.getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void readWriteHingeJointTest() {
        JointPrefab joint = (JointPrefab) TextIOTestSuite.createObject("Standard", "HingeJoint", JointPrefab.class);


        ObjectComponent oc = (ObjectComponent) joint.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = joint.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("HingeJoint1", oc.getName());

        String result = TextIOTestSuite.writeObject(joint);

        Level loadedLevel = TextIOTestSuite.readLevel(result, JointPrefab.class, 1);
        JointPrefab loaded = loadedLevel.descendantMatches(JointPrefab.class).get(0);

        assertEquals(loaded.getName(), joint.getName());

        Prefab objectA = loaded.getObjectA();
        assertNotNull(objectA);
        assertEquals("bone", objectA.getName());
        Prefab objectB = loaded.getObjectB();
        assertNotNull(objectB);
        assertEquals("bone", objectB.getName());


    }
}