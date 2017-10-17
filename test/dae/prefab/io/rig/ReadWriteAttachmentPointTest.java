package dae.prefab.io.rig;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.skeleton.AttachmentPoint;
import dae.animation.skeleton.RevoluteJoint;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.prefab.io.TextIOTestSuite;
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
public class ReadWriteAttachmentPointTest {

    public ReadWriteAttachmentPointTest() {
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
    public void readWriteAttachmentPoint() {
        Rig rig = (Rig) TextIOTestSuite.createObject("Animation", "Rig", Rig.class);

        AttachmentPoint rj = (AttachmentPoint) TextIOTestSuite.createObject("Animation", "AttachmentPoint", AttachmentPoint.class);
        ObjectComponent oc = (ObjectComponent) rj.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = rj.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("AttachmentPoint1", oc.getName());
        assertEquals("default", oc.getLayer());

        rig.attachBodyElement(rj);

        String result = TextIOTestSuite.writeRig(rig);
        Rig loadedRig = TextIOTestSuite.readRig(result);
        AttachmentPoint loaded = loadedRig.descendantMatches(AttachmentPoint.class).get(0);
       

        assertEquals(loaded.getName(), rj.getName());
        assertEquals(loaded.getLayerName(), rj.getLayerName());

    }
}
