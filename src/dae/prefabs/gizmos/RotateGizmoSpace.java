/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.gizmos;

/**
 * This enumeration describes the spaces that are relevant for
 * a gizmo.
 * 
 * WORLD: The gizmo translate axises will be aligned with the world.
 * LOCAL: The gizmo translate axises will be aligned with the local space.
 * PARENT: The gizmo translate axises will be aligned with the parent space.
 * @author samyn_000
 */
public enum RotateGizmoSpace {
    WORLD("World"),LOCAL("Local"),PARENT("Parent");
    
    private String label;
    RotateGizmoSpace(String label){
        this.label = label;
    }
    
    @Override
    public String toString(){
        return label;
    }
}
