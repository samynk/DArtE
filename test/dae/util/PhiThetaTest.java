/*
 * Digital Arts and Entertainment 
 */
package dae.util;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.Random;
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
public class PhiThetaTest {

    private Random r;

    public PhiThetaTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        r = new Random(System.currentTimeMillis());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRotationAnalysis() {
        Vector2f angles = new Vector2f();
        Quaternion q= new Quaternion();
        Vector3f test = new Vector3f();
        Vector3f axisRot = new Vector3f();
        for ( int i = 0; i < 1000; ++i)
        {
            Vector3f random = createRandomVector(angles);
            
            float phi = MathUtil.calculateDof1Rotation(Vector3f.UNIT_X, random, Vector3f.UNIT_Y);
            q.fromAngles(0,phi,0);
            q.mult(Vector3f.UNIT_X, test);
            q.mult(Vector3f.UNIT_Z, axisRot);
            float theta = MathUtil.calculateDof1Rotation(test,random ,axisRot);
            System.out.println("rand vector:" + random);
            q.fromAngles(0,phi,theta);
            q.mult(Vector3f.UNIT_X,test);
            System.out.println("test vector: " + test);
           
            assertEquals(test.x, random.x,0.0001f);
            assertEquals(test.y, random.y,0.0001f);
            assertEquals(test.z, random.z,0.0001f);
        }
    }

    public Vector3f createRandomVector(Vector2f angles) {
        // y rotation
        float phi = (float) (r.nextFloat() * Math.PI);
        // z rotation
        float theta = (float) (r.nextFloat() * Math.PI / 2);
        
        angles.x = phi * FastMath.RAD_TO_DEG;
        angles.y = theta * FastMath.RAD_TO_DEG;
        
        float x = FastMath.cos(phi)*FastMath.cos(theta);
        float z = FastMath.sin(phi)*FastMath.cos(theta);
        float y = FastMath.sin(theta);
        return new Vector3f(x,y,z);
    }

}
