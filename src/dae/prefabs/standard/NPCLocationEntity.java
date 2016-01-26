package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class NPCLocationEntity extends Prefab {

    private String animation;
    private String npcMeshLocation;
    private AssetManager assetManager;
    private NPC npcMesh;
    private Spatial npcLoc;

    public NPCLocationEntity() {
        this.setCategory("Standard");
        this.setType("NPCLocation");
    }

    @Override
    protected void create( AssetManager manager, String extraInfo) {
        npcLoc = manager.loadModel("Entities/M_Playerstart.j3o");
        this.attachChild(npcLoc);
        assetManager = manager;
    }

    public void setNpc(String npc) {
        if (npc != null) {
            this.npcMeshLocation = npc;
            npcMesh = new NPC();
            npcMesh.create(assetManager, npc);
            npcMesh.setName(name+ "_npcmesh");
            for (Spatial s : this.getChildren()) {
                s.removeFromParent();
            }
            this.attachChild(npcMesh);
            this.attachChild(npcLoc);
        }
    }

    public String getNpc() {
        return npcMeshLocation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
        if (npcMesh != null) {
            npcMesh.activateAnimation(animation);
        }
    }

    public String getAnimation() {
        if (npcMesh != null) {
            return npcMesh.getAnimation();
        } else {
            return animation;
        }
    }
}
