/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefab.io.ovm;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dae.GlobalObjects;
import dae.components.MeshComponent;
import dae.components.ObjectComponent;
import dae.components.TransformComponent;
import dae.io.export.OVSceneExporter;
import dae.io.readers.OVSceneImporter;
import dae.prefab.io.TextIOTestSuite;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.project.Level;
import java.io.File;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Koen Samyn
 */
public class OVSceneExportTest {

    public OVSceneExportTest() {
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
        Object meshObject = type.create(manager, "mesh1∆Ѽ");
        assertNotNull("Could not create mesh object", meshObject);
        assertTrue("Mesh object not instanceof Mesh", meshObject instanceof MeshObject);

        MeshObject mo = (MeshObject) meshObject;
        MeshComponent mc = (MeshComponent) mo.getComponent("MeshComponent");
        assertNotNull(mc);
        TransformComponent tc = (TransformComponent) mo.getComponent("TransformComponent");
        assertNotNull(tc);
        ObjectComponent oc = (ObjectComponent) mo.getComponent("ObjectComponent");
        assertNotNull(oc);
        
        Vector3f eTrans = new Vector3f(10,20,30);
        tc.setTranslation(eTrans);
        Vector3f eScale = new Vector3f(2,3,4);
        tc.setScale(eScale);
        Quaternion q = new Quaternion();
        float xRot = FastMath.DEG_TO_RAD * 10;
        float yRot = FastMath.DEG_TO_RAD * 40;
        float zRot = FastMath.DEG_TO_RAD * 60;
        q.fromAngles(xRot,yRot,zRot);
        tc.setRotation(q);

        mc.setMeshFile("Resources/Meshes/chair.ovm");
        assertEquals("mesh1∆Ѽ", oc.getName());
        
       

        dae.project.Level l = new dae.project.Level("test", false);
        l.attachChild(mo);
        
        for ( int i = 2 ; i < 10; ++i )
        {
            MeshObject sceneObject = createMesh(i);
            l.attachChild(sceneObject);
        }

        // test output configuration
        File outputDir = new File("d:/export/ov");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File output = new File(outputDir, "scene.ovms");

        OVSceneExporter exporter = new OVSceneExporter();
        exporter.writeScene(output, manager, l);
        
        
        OVSceneImporter importer = new OVSceneImporter();
        Level readLevel = importer.readScene(output);
        assertEquals(0xDAE, importer.getMajorVersion());
        assertEquals(1,importer.getMinorVersion());
        
        List<MeshObject> meshes = readLevel.descendantMatches(MeshObject.class);
        assertEquals(9,meshes.size());
        
        MeshObject moread = meshes.get(0);
        String name = moread.getName();
        assertEquals("mesh1∆Ѽ",name);
        
        MeshComponent mcread = (MeshComponent) moread.getComponent("MeshComponent");
        assertNotNull(mcread);
        assertEquals(mc.getMeshFile(),mcread.getMeshFile());
        
        TransformComponent tcread = (TransformComponent) moread.getComponent("TransformComponent");
        assertNotNull(tcread);
        assertEquals(tc.getTranslation(), tcread.getTranslation());
        assertEquals(tc.getScale(), tcread.getScale());
        assertEquals(tc.getRotation(),tcread.getRotation());
    }
    
    private MeshObject createMesh(int id){
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
        ObjectType type = types.getObjectType("Standard", "Mesh");
        assertNotNull("Type [Standard,Mesh] not found", type);
        
        
        Object meshObject = type.create(manager, "mesh"+id);
        assertNotNull("Could not create mesh object", meshObject);
        assertTrue("Mesh object not instanceof Mesh", meshObject instanceof MeshObject);

        MeshObject mo = (MeshObject) meshObject;
        MeshComponent mc = (MeshComponent) mo.getComponent("MeshComponent");
        assertNotNull(mc);
        TransformComponent tc = (TransformComponent) mo.getComponent("TransformComponent");
        assertNotNull(tc);
        ObjectComponent oc = (ObjectComponent) mo.getComponent("ObjectComponent");
        assertNotNull(oc);
        
        Vector3f eTrans = new Vector3f(10,20,id*10);
        tc.setTranslation(eTrans);
        Vector3f eScale = new Vector3f(1,1,1);
        tc.setScale(eScale);
        Quaternion q = new Quaternion();
        float xRot = 0;
        float yRot = FastMath.DEG_TO_RAD * (5*id);
        float zRot = 0;
        q.fromAngles(xRot,yRot,zRot);
        tc.setRotation(q);

        mc.setMeshFile("Resources/Meshes/chair.ovm");
        assertEquals("mesh"+id, oc.getName());
        
        return mo;
    }
}