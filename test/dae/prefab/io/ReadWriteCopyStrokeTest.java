/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.PrefabComponent;
import dae.io.ObjectTypeReader;
import dae.io.SceneLoader;
import dae.io.SceneSaver;
import dae.prefabs.Prefab;
import dae.prefabs.brush.Stroke;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ReadWriteCopyStrokeTest {

    private Stroke stroke;
 

    public ReadWriteCopyStrokeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if ( GlobalObjects.getInstance().getAssetManager() == null)
        {
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
    public void loadObjectTypesTest() {
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        ObjectType type = types.getObjectType("Terrain", "Stroke");
        assertNotNull("Type [Terrain,Stroke] not found", type);
        Object strokeObject = type.create(manager, "stroke1");
        assertNotNull("Could not create strokeObject", strokeObject);
        assertTrue("Stroke object not instanceof Stroke", strokeObject instanceof Stroke);

        stroke = (Stroke) strokeObject;
        assertEquals("stroke1", stroke.getName());
        stroke.setRandomizeXRot(true);
        stroke.setRandomizeYRot(false);
        stroke.setRandomizeZRot(true);
        stroke.setScaleValues(new Vector2f(.5f, 2.0f));
        stroke.setMesh("/Models/Terrain/Nettle.j3o");
        stroke.setUseContactNormal(true);
        stroke.setUseForRaycast(true);

        PrefabComponent oc = stroke.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = stroke.getComponent("TransformComponent");
        assertNotNull(tc);

        dae.project.Level l = new dae.project.Level("test", false);
        l.attachChild(stroke);

        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        try {
            SceneSaver.writeScene(bw, l);
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteCopyStrokeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String result = sw.getBuffer().toString();
        Logger.getLogger("DArtE").log(Level.INFO, "Result of writing the object");
        Logger.getLogger("DartE").log(Level.INFO, result);

        InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        dae.project.Level loadedLevel = new dae.project.Level("test2", false);
        SceneLoader.loadScene(stream, manager, loadedLevel, types, null);
        List<Stroke> childElements = loadedLevel.descendantMatches(Stroke.class);
        assertEquals("One child element expected", 1, childElements.size());
        Stroke first = childElements.get(0);
        assertEquals(first.getRandomizeXRot(), stroke.getRandomizeXRot());
        assertEquals(first.getRandomizeYRot(), stroke.getRandomizeYRot());
        assertEquals(first.getRandomizeZRot(), stroke.getRandomizeZRot());
       
        assertEquals(first.getScaleValues().x, stroke.getScaleValues().x, .000001f);
        assertEquals(first.getScaleValues().y, stroke.getScaleValues().y, .000001f);
        
        assertEquals(first.getMesh(),stroke.getMesh());
        assertEquals(first.getUseForRaycast(),stroke.getUseForRaycast());
        assertEquals(first.getUseContactNormal(),stroke.getUseContactNormal());
        
        //assertNotNull("No child element with the correct name found in level.", strokePrefab);



    }
}