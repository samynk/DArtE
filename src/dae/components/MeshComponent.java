package dae.components;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class MeshComponent extends PrefabComponent {

    private String meshFile;
    private Spatial mesh;
    private Node parent;

    public MeshComponent(){
        super.setId("MeshComponent");
    }
    
    @Override
    public void install(Prefab parent) {
        if( parent == null )
        {
            return;
        }
        this.parent = parent;
        
        if (meshFile != null && meshFile.length() > 0) {
            reloadMesh(parent);
        }
    }

    @Override
    public void deinstall() {
        if (mesh != null){
            mesh.removeFromParent();
        }
    }

    @Override
    public void installGameComponent(Spatial parent) {
        if ( parent instanceof Node){
            this.parent = (Node)parent;
        }
    }

    /**
     * @return the meshFile
     */
    public String getMeshFile() {
        return meshFile;
    }

    /**
     * @param meshFile the meshFile to set
     */
    public void setMeshFile(String meshFile) {
        if (meshFile != null && !meshFile.equals(this.meshFile)) {
            this.meshFile = meshFile;
        }
        if ( parent != null){
            deinstall();
            reloadMesh(parent);
        }
    }

    private void reloadMesh(Node parent) {
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        mesh = am.loadModel(meshFile);
        parent.attachChild(mesh);
    }
}
