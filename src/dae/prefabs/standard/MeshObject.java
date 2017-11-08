package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import dae.animation.skeleton.BodyElement;
import dae.components.MeshComponent;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class MeshObject extends Prefab implements BodyElement {

    private String meshName;
    private AssetManager manager;

    public MeshObject() {
        this.canHaveChildren = true;
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        this.meshName = name;
        this.manager = manager;

    }

    @Override
    public Node clone(boolean cloneMaterials) {
        return (Node) clone();
    }


    public String getMeshName() {
        return meshName;
    }

    /**
     * Reloads the mesh, the children of the mesh are not affected.
     */
    public void reloadMesh() {
        MeshComponent mc = (MeshComponent) this.getComponent("MeshComponent");
        if (mc != null) {
            mc.deinstall();
            mc.install(this);
        }
    }

    @Override
    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    @Override
    public void reset() {
    }
}
