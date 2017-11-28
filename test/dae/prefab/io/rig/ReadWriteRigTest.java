
package dae.prefab.io.rig;

import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.skeleton.RevoluteJoint;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefab.io.TextIOTestSuite;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Koen.Samyn
 */
public class ReadWriteRigTest {
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
        Rig rig = (Rig) TextIOTestSuite.createObject("Animation", "Rig", Rig.class);
        
        
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
        
        rig.attachBodyElement(rj);
        
        
        
        
        String result = TextIOTestSuite.writeRig(rig);
        Rig loadedRig = TextIOTestSuite.readRig(result);
        RevoluteJoint loaded = loadedRig.descendantMatches(RevoluteJoint.class).get(0);

        assertEquals(loaded.getName(), rj.getName());
        
        assertEquals(loaded.getGroup(), rj.getGroup());
        assertEquals(loaded.getMinAngle(),rj.getMinAngle(),0.0001f);
        assertEquals(loaded.getMaxAngle(),rj.getMaxAngle(),0.0001f);
        assertEquals(loaded.getCurrentAngle(),rj.getCurrentAngle(),0.0001f);
        
        assertEquals(loaded.getRadius(),rj.getRadius(),0.0001f);
        assertEquals(loaded.getHeight(), rj.getHeight(),0.0001f);
        assertEquals(loaded.getCentered(),rj.getCentered());
    }
}
