/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.standard;

import dae.animation.rig.Rig;
import dae.prefabs.Prefab;
import dae.prefabs.parameters.DictionaryParameter;
import dae.prefabs.parameters.ObjectParameter;
import dae.prefabs.standard.CrateObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Koen Samyn
 */
public class DictionaryParameterTest {

    private Rig rig;
    private DictionaryParameter parameter;

    public DictionaryParameterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        rig = new Rig();
        parameter = new DictionaryParameter("dictionary", "target", new ObjectParameter("object", "target"));

        assertEquals("Number of keys at start should be zero", rig.getNrOfTargetKeys(), 0);
        assertEquals("Property should be equal to target", parameter.getProperty(), "Target");

    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void testAddRemoveKeys() {
        parameter.addKey(rig, "Enemy");
        assertEquals("Number of keys should be 1 (direct call)", 1, rig.getNrOfTargetKeys());
        assertEquals("Number of keys should be 1 (via parameter)", 1, parameter.getNrOfKeys(rig));
        parameter.removeKey(rig, "Enemy");
        assertEquals("Number of keys should be 0 (direct call)", 0, rig.getNrOfTargetKeys());
        assertEquals("Number of keys should be 0 (via parameter)", 0, parameter.getNrOfKeys(rig));


    }

    @Test
    public void testPrefabDictionary() {
        CrateObject co1 = new CrateObject();
        co1.setName("Crate1");

        parameter.addKey(rig, "Crate1");
        parameter.setProperty(rig, "Crate1", co1);
        assertEquals("Number of keys should be 1 direct call)", 1, rig.getNrOfTargetKeys());

        Object result = parameter.getProperty(rig, "Crate1");
        assertNotNull("Result should be non zero ", result);
        assertTrue("Type should be Prefab", result instanceof Prefab);

        parameter.removeKey(rig, "Crate1");
        assertEquals("Number of keys should be 0(direct call)", 0, rig.getNrOfTargetKeys());

        CrateObject co2 = new CrateObject();
        parameter.setProperty(rig, "Crate2", result);
        assertEquals("Number of keys should be 1(direct call)", 1, rig.getNrOfTargetKeys());
    }
}