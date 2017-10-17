package dae.prefab.io;

import dae.GlobalObjects;
import dae.animation.skeleton.RevoluteJoint;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.project.Level;
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
public class ReadWriteCopyRevoluteJointTest {
    
    public ReadWriteCopyRevoluteJointTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void readWriteRevoluteJoint() {
        RevoluteJoint rj = (RevoluteJoint) TextIOTestSuite.createObject("Animation", "RevoluteJoint", RevoluteJoint.class);
        ObjectComponent oc = (ObjectComponent) rj.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = rj.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("RevoluteJoint1", oc.getName());
        assertEquals("default", oc.getLayer());
        
        rj.setGroup("leftarm");
        rj.setMinAngle(-22);
        rj.setMaxAngle(89);
        rj.setCurrentAngle(7);
        
        rj.setRenderOptions(1.5f, 1.0f, false);
        rj.setChainChildName("revolutejoint2");
        
        String result = TextIOTestSuite.writeObject(rj);

        Level loadedLevel = TextIOTestSuite.readLevel(result, RevoluteJoint.class, 1);
        RevoluteJoint loaded = loadedLevel.descendantMatches(RevoluteJoint.class).get(0);

        assertEquals(loaded.getName(), rj.getName());
        
        assertEquals(loaded.getGroup(), rj.getGroup());
        assertEquals(loaded.getMinAngle(),rj.getMinAngle(),0.0001f);
        assertEquals(loaded.getMaxAngle(),rj.getMaxAngle(),0.0001f);
        assertEquals(loaded.getCurrentAngle(),rj.getCurrentAngle(),0.0001f);
        
        assertEquals(loaded.getRadius(),rj.getRadius(),0.0001f);
        assertEquals(loaded.getHeight(), rj.getHeight(),0.0001f);
        assertEquals(loaded.getCentered(),rj.getCentered());
        assertEquals(loaded.getChainChildName(),rj.getChainChildName());
    }
}
