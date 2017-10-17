/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io.rig;

import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.skeleton.AttachmentPoint;
import dae.animation.skeleton.RevoluteJointTwoAxis;
import dae.animation.skeleton.constraints.SectorConstraint;
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
public class ReadWriteTwoAxisJointTest {
    
    public ReadWriteTwoAxisJointTest() {
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
     public void readWriteRevoluteJointTwoAxis() {
         Rig rig = (Rig) TextIOTestSuite.createObject("Animation", "Rig", Rig.class);

        RevoluteJointTwoAxis rj = (RevoluteJointTwoAxis) TextIOTestSuite.createObject("Animation", "RevoluteJointTwoAxis", RevoluteJointTwoAxis.class);
        ObjectComponent oc = (ObjectComponent) rj.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = rj.getComponent("TransformComponent");
        assertNotNull(tc);
        SectorConstraint sc = (SectorConstraint)rj.getComponent("SectorConstraint");
        assertNotNull(sc);
        assertEquals("RevoluteJointTwoAxis1", oc.getName());
        assertEquals("default", oc.getLayer());

        rig.attachBodyElement(rj);

        String result = TextIOTestSuite.writeRig(rig);
        Rig loadedRig = TextIOTestSuite.readRig(result);
        RevoluteJointTwoAxis loaded = loadedRig.descendantMatches(RevoluteJointTwoAxis.class).get(0);
       

        assertEquals(loaded.getName(), rj.getName());
        assertEquals(loaded.getLayerName(), rj.getLayerName());
     }
}
