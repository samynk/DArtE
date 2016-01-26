package dae.prefabs.brush;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;

/**
 * This class describes an element of a brush. The brush can be set to 
 * orient itself relative to the contact normal.
 * 
 * All the orientations can be set to be random or not. The random orientation
 * wil be a value between 0 and 2Pi.
 * 
 * @author Koen Samyn
 */
public class Stroke extends Prefab{

    private boolean useContactNormal;
    private float weight;
    private boolean randomizeXRot;
    private boolean randomizeYRot;
    private boolean randomizeZRot;
    private String mesh;
    
    private Vector2f scaleValues = new Vector2f(1,1);
    private boolean useForRaycast = false;
    
    
    private AssetManager manager;
    private Spatial meshSpatial;
    
    public Stroke(){
        
    }

    @Override
    protected void create( AssetManager manager, String extraInfo) {
        super.create( manager, extraInfo); 
        this.manager = manager;
    }
    
    

    /**
     * @return the useContactNormal
     */
    public boolean getUseContactNormal() {
        return useContactNormal;
    }

    /**
     * @param useContactNormal the useContactNormal to set
     */
    public void setUseContactNormal(boolean useContactNormal) {
        this.useContactNormal = useContactNormal;
    }

    /**
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * @return the randomizeXRot
     */
    public boolean getRandomizeXRot() {
        return randomizeXRot;
    }

    /**
     * @param randomizeXRot the randomizeXRot to set
     */
    public void setRandomizeXRot(boolean randomizeXRot) {
        this.randomizeXRot = randomizeXRot;
    }

    /**
     * @return the randomizeYRot
     */
    public boolean getRandomizeYRot() {
        return randomizeYRot;
    }

    /**
     * @param randomizeYRot the randomizeYRot to set
     */
    public void setRandomizeYRot(boolean randomizeYRot) {
        this.randomizeYRot = randomizeYRot;
    }

    /**
     * @return the randomizeZRot
     */
    public boolean getRandomizeZRot() {
        return randomizeZRot;
    }

    /**
     * @param randomizeZRot the randomizeZRot to set
     */
    public void setRandomizeZRot(boolean randomizeZRot) {
        this.randomizeZRot = randomizeZRot;
    }

    /**
     * @return the mesh
     */
    public String getMesh() {
        return mesh;
    }

    /**
     * @param mesh the mesh to set
     */
    public void setMesh(String mesh) {
        if ( mesh!= null && !mesh.equals(this.mesh) ){
            if ( meshSpatial != null ){
                meshSpatial.removeFromParent();
            }
            this.mesh = mesh;
            meshSpatial = manager.loadModel(mesh);
            attachChild(meshSpatial);
        }
    }
    
    @Override
    public String toString(){
        return name;
    }

    /**
     * @return the scaleValues
     */
    public Vector2f getScaleValues() {
        return scaleValues;
    }

    /**
     * @param scaleValues the scaleValues to set
     */
    public void setScaleValues(Vector2f scaleValues) {
        this.scaleValues = scaleValues;
    }

    /**
     * @return the useForRaycast
     */
    public boolean getUseForRaycast() {
        return useForRaycast;
    }

    /**
     * @param useForRaycast the useForRaycast to set
     */
    public void setUseForRaycast(boolean useForRaycast) {
        this.useForRaycast = useForRaycast;
    }
}
