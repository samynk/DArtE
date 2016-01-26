/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import dae.GlobalObjects;
import dae.components.PrefabComponent;
import dae.io.SceneLoader;
import dae.io.SceneSaver;
import dae.prefabs.brush.CastMethod;
import dae.prefabs.brush.Stroke;
import dae.prefabs.brush.TerrainBrush;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class ReadWriteCopyBrushTest {
    
    public ReadWriteCopyBrushTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        GlobalObjects go = GlobalObjects.getInstance();
        if ( go.getAssetManager() == null ){
            TextIOTestSuite.createMockGame();
        }
    }
    
    @After
    public void tearDown() {
    }
   
    @Test
    public void readWriteBrushTest() {
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        ObjectType type = types.getObjectType("Terrain", "Brush");
        assertNotNull("Type [Terrain,Brush] not found", type);
        Object brushObject = type.create(manager, "brush1");
        assertNotNull("Could not create strokeObject", brushObject);
        assertTrue("Stroke object not instanceof Stroke", brushObject instanceof TerrainBrush);
        
        TerrainBrush brush = (TerrainBrush)brushObject;
        assertEquals("brush1", brush.getName());
        
        brush.setCastMethod(CastMethod.RAYCASTING);
        brush.setRadius(3.2f);
        brush.addStroke( createStroke(types,manager,"stroke1"));
        brush.addStroke( createStroke(types,manager,"stroke2"));
        
        brush.setBrushLayer("brushbatches");
        brush.setBrushNode("brush1node");
        
        PrefabComponent oc = brush.getComponent("ObjectComponent");
        assertNotNull(oc);
        PrefabComponent tc = brush.getComponent("TransformComponent");
        assertNotNull(tc);
        
        dae.project.Level l = new dae.project.Level("test", false);
        l.attachChild(brush);

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
        List<TerrainBrush> childElements = loadedLevel.descendantMatches(TerrainBrush.class);
        assertEquals("One child element expected", 1, childElements.size());
        
        TerrainBrush loadedBrush = childElements.get(0);
        assertEquals(brush.getCastMethod(),loadedBrush.getCastMethod());
        assertEquals(brush.getBrushLayer(),loadedBrush.getBrushLayer());
        assertEquals(brush.getBrushNode(),loadedBrush.getBrushNode());
        assertEquals(brush.getRadius(),loadedBrush.getRadius(),0.0001);
        assertEquals(brush.getName(),loadedBrush.getName());
        
        PrefabComponent loc = brush.getComponent("ObjectComponent");
        assertNotNull(loc);
        PrefabComponent ltc = brush.getComponent("TransformComponent");
        assertNotNull(ltc);
        
        assertEquals(brush.getStrokeListSize(),loadedBrush.getStrokeListSize());
        for ( int i = 0 ; i <loadedBrush.getStrokeListSize(); ++i)
        {
            Stroke s = loadedBrush.getStrokeAt(i);
            assertNotNull(s);
        }
    }
    
    private Stroke createStroke(ObjectTypeCategory types, AssetManager manager, String name){
        ObjectType type = types.getObjectType("Terrain", "Stroke");
        assertNotNull("Type [Terrain,Stroke] not found", type);
        Object strokeObject = type.create(manager,name);
        assertNotNull("Could not create strokeObject", strokeObject);
        assertTrue("Stroke object not instanceof Stroke", strokeObject instanceof Stroke);

        Stroke stroke = (Stroke) strokeObject;
        assertEquals(name, stroke.getName());
        stroke.setRandomizeXRot(true);
        stroke.setRandomizeYRot(false);
        stroke.setRandomizeZRot(true);
        stroke.setScaleValues(new Vector2f(.5f, 2.0f));
        stroke.setMesh("/Models/Terrain/Nettle.j3o");
        stroke.setUseContactNormal(true);
        stroke.setUseForRaycast(true);
        
        return stroke;
    }
}