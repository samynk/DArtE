
package dae.prefab.io.rig;

import com.jme3.asset.AssetManager;
import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.rig.io.RigLoader;
import dae.animation.rig.io.RigWriter;
import dae.animation.skeleton.RevoluteJoint;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.io.SceneSaver;
import dae.prefab.io.TextIOTestSuite;
import dae.prefabs.types.ObjectTypeCategory;
import dae.project.Level;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

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
    
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
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
        rj.setChainChildName("revolutejoint2");
        
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
        assertEquals(loaded.getChainChildName(),rj.getChainChildName());
    }
}
