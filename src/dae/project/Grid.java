package dae.project;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import dae.GlobalObjects;
import dae.prefabs.AxisEnum;
import dae.prefabs.Prefab;
import dae.prefabs.magnets.GridMagnet;
import dae.prefabs.magnets.MagnetParameter;

/**
 *
 * @author Koen
 */
public class Grid extends Prefab {

    private float width = 100.0f;
    private float length = 100.0f;
    private GridMagnet magnet;
    private AxisEnum currentUpAxis;
    private Geometry yGeometry;
    private Geometry zGeometry;
    private Geometry currentGeometry;

    public Grid() {
        this.setName("Ground");
        this.setCategory("Standard");
        this.setType("Ground");
    }

    /**
     * Constructs a new Ground object
     *
     * @param width the width of the ground object.
     * @param length the length of the ground object;
     */
    public Grid(float width, float length, Material groundMaterial) {
        this.setName("Ground");
        this.setCategory("Standard");
        this.setType("Ground");
        this.width = width;
        this.length = length;

        initialize(groundMaterial);
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.setCategory("Standard");
        this.setType("Ground");

        Material mat = createStandardMaterial(manager, "Textures/refPattern.png", Texture.WrapMode.Repeat, ColorRGBA.White);
        initialize(mat);
        
        
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setUpAxis(AxisEnum upAxis) {
        if (upAxis != currentUpAxis) {
            currentUpAxis = upAxis;
            currentGeometry.removeFromParent();

            if (currentUpAxis == AxisEnum.Z) {
                attachChild(zGeometry);
                magnet.setRotationAxis(Vector3f.UNIT_Z);
                currentGeometry = zGeometry;
            } else {
                attachChild(yGeometry);
                magnet.setRotationAxis(Vector3f.UNIT_Y);
                currentGeometry = yGeometry;
            }
        }
    }

    private void initialize(Material groundMaterial) {
        GlobalObjects go = GlobalObjects.getInstance();
//        currentUpAxis = go.getUpAxis();
        currentUpAxis = AxisEnum.Y;


        Box zUpBox = new Box(width, length, 0.01f);
        zUpBox.scaleTextureCoordinates(new Vector2f(width, length));// create cube shape at the origin
        Box yUpBox = new Box(width, 0.01f, length);
        yUpBox.scaleTextureCoordinates(new Vector2f(width, length));// create cube shape at the origin

        yGeometry = new Geometry("GroundPlane", yUpBox);  // create cube geometry from the shape
        yGeometry.setMaterial(groundMaterial);
        yGeometry.setLocalTranslation(0, -0.01f, 0);

        zGeometry = new Geometry("GroundPlane", zUpBox);  // create cube geometry from the shape
        zGeometry.setMaterial(groundMaterial);
        zGeometry.setLocalTranslation(0, 0, -0.01f);

        this.setOriginalMaterial(groundMaterial);

        // set the cube's material
        magnet = new GridMagnet();
        BoxCollisionShape shape;
        if (currentUpAxis == AxisEnum.Z) {
            attachChild(zGeometry);
            magnet.setRotationAxis(Vector3f.UNIT_Z);
            currentGeometry = zGeometry;
            shape = new BoxCollisionShape(new Vector3f(width,length,0.01f));
        } else {
            attachChild(yGeometry);
            magnet.setRotationAxis(Vector3f.UNIT_Y);
            currentGeometry = yGeometry;
            shape = new BoxCollisionShape(new Vector3f(width,0.01f,length));
        }


        magnet.setLocalFrame(Matrix3f.IDENTITY);
        magnet.setRotationRange(GlobalObjects.getInstance().getDefaultRotationRange());
        MagnetParameter mp = new MagnetParameter("grid", "grid");
        mp.addMagnet(magnet);
        this.setMagnets(mp);
        
        shape.setMargin(0.1f);
        this.addControl(new RigidBodyControl(shape,0.0f));
    }
}