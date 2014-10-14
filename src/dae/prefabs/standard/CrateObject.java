package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class CrateObject extends Prefab {

    private Vector3f bottom;
    private Vector3f top;
    private Geometry box;
    
    private Vector3f dimension;
    
    private AssetManager manager;
    private PhysicsSpace physicsSpace;

    public CrateObject(Vector3f bottom, Vector3f top)
    {
        
        this.bottom = new Vector3f(bottom);
        this.top = new Vector3f(top);
        dimension = new Vector3f();
        dimension.x = top.x - bottom.x;
        dimension.y = top.y - bottom.y;
        dimension.z = top.z - bottom.z;
        
        
    }
    
    public CrateObject(Vector3f dimension){
        this.bottom = dimension.mult(-0.5f);
        this.top = dimension.mult(0.5f);
        this.dimension = new Vector3f(dimension);
        setPivot(new Vector3f(0,-dimension.y,0));
    }
    
    public CrateObject() {
        dimension =new Vector3f(1.0f,1.0f,1.0f);
        top = new Vector3f(0.5f, 0.5f, 0.5f);
        bottom = new Vector3f(-0.5f, -0.5f, -0.5f);
        setPivot(new Vector3f(0, -0.5f, 0));
    }

    @Override
    public final void create(String name, AssetManager manager, String extraInfo) {
        this.setName(name);
        this.manager = manager;
        recreate(manager);
    }
    
    @Override
    public Prefab duplicate(AssetManager manager)
    {
        CrateObject co = new CrateObject(bottom,top);
        co.physicsSpace = physicsSpace;
        co.setPivot(getPivot());
        co.create( name, manager, null) ;
        co.setType( this.getType() );
        co.setCategory( this.getCategory() );
        return co;
    }
    
    private void recreate(AssetManager manager)
    {
        if ( box != null ){
            box.removeFromParent();
        }
        Box b = new Box(bottom, top); // create cube shape at the origin
        box = new Geometry("Box", b);  // create cube geometry from the shape

        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);

        Material cube1Mat = new Material(manager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex = manager.loadTexture(
                "Textures/boxPattern.png");
        cube1Mat.setTexture("ColorMap", cube1Tex);
        box.setMaterial(cube1Mat);

        setOriginalMaterial(mat);
        //box.setMaterial(mat);
        attachChild(box);
        
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.setEnabled(false);
            removeControl(rbc);
        }
        if (physicsSpace != null) {
            rbc = new RigidBodyControl( new BoxCollisionShape(dimension.mult(0.5f)),1.0f );
            addControl(rbc);
            physicsSpace.add(rbc);
        }
    }

    @Override
    public String getPrefix() {
        return "Crate";
    }

    /**
     * @return the dimension
     */
    public Vector3f getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(Vector3f dimension) {
        bottom.set(dimension.mult(-0.5f));
        top.set(dimension.mult(0.5f));
        this.dimension.set(dimension);
        setPivot(new Vector3f(0,-dimension.y/2,0));
        recreate(manager);
    }

    public void drop() {
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null) {
            rbc.activate();
        }
    }

    @Override
    public void addPhysics(PhysicsSpace space) {
        addPhysics(space,1.0f);
    }
    
    @Override
    public void addPhysics(PhysicsSpace space,float mass) {
        this.physicsSpace = space;
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc == null) {
            rbc = new RigidBodyControl(new BoxCollisionShape(dimension.mult(0.5f)),mass);
            addControl(rbc);
            space.add(rbc);
        }
    }
}
