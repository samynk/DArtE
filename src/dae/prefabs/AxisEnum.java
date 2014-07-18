/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs;

/**
 *
 * @author Koen
 */
public enum AxisEnum {

    X("X Axis"), Y("Y Axis"), Z("Z Axis");
    String label;

    AxisEnum(String label) {
        this.label = label;
    }
}
