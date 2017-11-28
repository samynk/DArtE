package dae.prefab.io;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import dae.GlobalObjects;
import dae.components.ObjectComponent;
import dae.components.TransformComponent;
import dae.prefabs.lights.DirectionalLightPrefab;
import dae.project.Level;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author samyn_000
 */
public class ReadWriteCopyDirectionalLightTest {
    
    public ReadWriteCopyDirectionalLightTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        if (GlobalObjects.getInstance().getAssetManager() == null) {
            TextIOTestSuite.createMockGame();
        }
    }
   
    @Test
    public void readWriteAmbientLight() {
        DirectionalLightPrefab al = (DirectionalLightPrefab) TextIOTestSuite.createObject("Light", "DirectionalLight", DirectionalLightPrefab.class);
        
        
        ObjectComponent oc = (ObjectComponent) al.getComponent("ObjectComponent");
        assertNotNull(oc);
        TransformComponent tc = (TransformComponent)al.getComponent("TransformComponent");
        assertNotNull(tc);
        assertEquals("DirectionalLight1", oc.getName());
        assertEquals("lights", oc.getLayer());
        
        Quaternion q = new Quaternion();
        q.fromAngles(FastMath.PI / 4, -FastMath.PI / 3, 12 * FastMath.PI / 8);
        tc.setRotation(q);
        
        String result = TextIOTestSuite.writeObject(al);
        
        Level loadedLevel = TextIOTestSuite.readLevel(result, DirectionalLightPrefab.class, 1);
        DirectionalLightPrefab loaded = loadedLevel.descendantMatches(DirectionalLightPrefab.class).get(0);
        
        assertEquals(loaded.getName(), al.getName());
        
        assertEquals(loaded.getDirectionalLightIntensity(), al.getDirectionalLightIntensity(), .0001);
        assertEquals(loaded.getDirectionalLightColor(), al.getDirectionalLightColor());
        
        TransformComponent tc2 =  (TransformComponent)al.getComponent("TransformComponent");
        Quaternion q2 = tc2.getRotation();
        float angles[] = new float[3];
        q2.toAngles(angles);
        
        assertEquals(q2.getX(),q.getX(), .0001);
        assertEquals(q2.getY(),q.getY(), .0001);
        assertEquals(q2.getZ(),q.getZ(), .0001);
        assertEquals(q2.getW(),q.getW(), .0001);
    }
}