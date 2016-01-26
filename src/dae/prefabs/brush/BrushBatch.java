package dae.prefabs.brush;

import com.jme3.asset.AssetManager;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class BrushBatch extends Prefab{
    private AssetManager manager;
    private boolean createGeometryBatch;
    
    public BrushBatch(){
        // default terrain.
        
        setLayerName("terrain");
        canHaveChildren = true;
    }
    
    @Override
    protected void create( AssetManager manager, String extraInfo) {
        super.create(manager, extraInfo); 
        this.manager = manager;
    }
    
    public void setCreateGeometryBatch(boolean batch){
        this.createGeometryBatch = batch;
    }
    
    public boolean getCreateGeometryBatch(){
        return createGeometryBatch;
    }
}
