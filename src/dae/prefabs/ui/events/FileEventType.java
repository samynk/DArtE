/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

public enum FileEventType {

    NEWSCENE("New Scene"), OPENSCENE("Open Scene"), SAVESCENE("Save Scene"),
    SCENEOPENED("Scene Opened"), SCENESAVED("Scene Saved"), OPEN3DOBJECT("Open 3D Object"),
    SCENEPREFERENCES("Preferences ..."), OPENNPCOBJECT("Open NPC Object"), ADDSITUATION("Add Sitatuation"),
    ADDCAMERA("Add Camera"), ADDSOUND("Add Sound"), ADDPLAYERSTART("Add Player Start"),
    ADDWAYPOINT("Add Waypoint"), OPENJ3ONPCOBJECT("Open J3O NPC"), ADDTRIGGERBOX("Add Trigger Box"), ADDHINGEDOBJECT("Add Hinged Object"),
    ADDNPCLOCATION("Add NPC Location"), ADDAGV("Add AGV"), ADDWAYPOINTFILE("Add Waypoint File"), ADDGROUND("Add Ground"), OPENSOUNDOBJECT("Add Sound"), EDITOBJECT("Edit Object"), ADDNAVIGATIONMESH("Add Navigation Mesh");
    private String label;

    FileEventType(String label) {
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