/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.physics;

public enum Gravity {

    Earth(9.81f), Mars(3.8f), Moon(1.622f);
    private float gravity;

    private Gravity(float gravity) {
        this.gravity = gravity;
    }

    public float getGravity() {
        return gravity;
    }
}
