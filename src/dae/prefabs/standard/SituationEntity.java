/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class SituationEntity extends Prefab {

    private String eventid;

    public SituationEntity() {
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Entities/Entity_Situation.gmf"));
        this.setCategory("Standard");
        this.setType("Sound");
    }

    public void setEventId(String eventid) {
        this.eventid = eventid;
    }

    public String getEventId() {
        return eventid;
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);
        this.getChild(0).rotate(0, 0.6f * tpf, 0);
    }
}
