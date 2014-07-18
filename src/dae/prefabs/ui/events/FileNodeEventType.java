/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

public enum FileNodeEventType {

    OBJECT3DOPENED("3D object opened"), NPCOBJECTOPENED("NPC Object Opened"), J3ONPCOBJECTOPENED("J3O NPC Object Openened"),
    WAYPOINTFILEOPENED("Waypoint file opened"), SOUNDOBJECTOPENED("Sound Object Opened");
    private String label;

    FileNodeEventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }
};