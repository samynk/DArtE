/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import dae.GlobalObjects;
import dae.components.MeshComponent;
import dae.components.ObjectComponent;
import dae.components.TransformComponent;
import dae.io.SceneLoader;
import dae.io.SceneSaver;
import dae.prefabs.brush.TerrainBrush;
import dae.prefabs.standard.MeshObject;
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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyMeshTest {

    public ReadWriteCopyMeshTest() {
        GlobalObjects go = GlobalObjects.getInstance();
        if (go.getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }

    @Test
    public void readWriteMeshTest() {
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        ObjectType type = types.getObjectType("Standard", "Mesh");
        assertNotNull("Type [Standard,Mesh] not found", type);
        Object meshObject = type.create(manager, "mesh1");
        assertNotNull("Could not create mesh object", meshObject);
        assertTrue("Mesh object not instanceof Mesh", meshObject instanceof MeshObject);

        MeshObject mo = (MeshObject) meshObject;
        MeshComponent mc = (MeshComponent) mo.getComponent("MeshComponent");
        assertNotNull(mc);
        TransformComponent tc = (TransformComponent) mo.getComponent("TransformComponent");
        assertNotNull(tc);
        ObjectComponent oc = (ObjectComponent) mo.getComponent("ObjectComponent");
        assertNotNull(oc);

        mc.setMeshFile("/Models/Cabin/Cabin.j3o");

        assertEquals("mesh1", oc.getName());

        dae.project.Level l = new dae.project.Level("test", false);
        l.attachChild(mo);

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
        List<MeshObject> childElements = loadedLevel.descendantMatches(MeshObject.class);
        assertEquals("One child element expected", 1, childElements.size());
        
        MeshObject loadedMesh = childElements.get(0);
        
        MeshComponent lmc = (MeshComponent)loadedMesh.getComponent("MeshComponent");
        assertNotNull( lmc );
        String lfile = lmc.getMeshFile();
        assertEquals( lfile, "/Models/Cabin/Cabin.j3o");
        
        String lname = loadedMesh.getName();
        assertEquals(lname,"mesh1");
        
        boolean wireframe = loadedMesh.getWireframe();
        assertEquals(wireframe,false);
        assertEquals(loadedMesh.getLocked(),false);
        assertEquals(loadedMesh.getShadowMode(),ShadowMode.Off);
        
    }
}