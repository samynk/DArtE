package dae.prefab.io;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.skeleton.RevoluteJoint;
import dae.animation.skeleton.RevoluteJointTwoAxis;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefabs.Prefab;
import dae.project.Level;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Koen.Samyn
 */
public class ReadWriteCopyRevoluteJointTwoAxisTest {

    public ReadWriteCopyRevoluteJointTwoAxisTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void readWriteRevoluteJoint() {
        RevoluteJointTwoAxis rj = (RevoluteJointTwoAxis) TextIOTestSuite.createObject("Animation", "RevoluteJointTwoAxis", RevoluteJointTwoAxis.class);
        ObjectComponent oc = (ObjectComponent) rj.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = rj.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("RevoluteJointTwoAxis1", oc.getName());
        assertEquals("default", oc.getLayer());

        //rj.setChainChildName("revolutejoint2");

        String result = TextIOTestSuite.writeObject(rj);

        Level loadedLevel = TextIOTestSuite.readLevel(result, RevoluteJointTwoAxis.class, 1);
        RevoluteJointTwoAxis loaded = loadedLevel.descendantMatches(RevoluteJointTwoAxis.class).get(0);

        assertEquals(loaded.getName(), rj.getName());
        assertEquals(loaded.getGroup(), rj.getGroup());
        
        
        // there should be only one child.
        assertEquals(loaded.getChildren().size() , 1);
        Spatial transformNode = loaded.getChild(0);
        assertTrue(transformNode instanceof Node);
        Node node = (Node)transformNode;
        
        
        List<Prefab> childPrefabs = node.descendantMatches(Prefab.class);
        assertEquals(childPrefabs.size(),1);
    }
}
