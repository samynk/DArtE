/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import dae.prefabs.Prefab;

/**
 * This class models a waypoint
 *
 * @author Koen
 */
public class WayPointEntity extends Prefab {

    private String waypointId;

    public WayPointEntity() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.attachChild(manager.loadModel("Entities/M_Waypoint.j3o"));
        this.setCategory("Standard");
        this.setType("Waypoint");

    }

    public void setWaypointId(String waypointid) {
        this.waypointId = waypointid;
        this.setName("waypoint_" + waypointid);
    }

    public String getWaypointId() {
        return waypointId;
    }
}
