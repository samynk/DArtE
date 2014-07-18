/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.magnets;

import com.jme3.math.Vector3f;

/**
 *
 * @author Koen
 */
public class MagnetArea {

    private Vector3f min;
    private Vector3f max;

    public MagnetArea() {
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public boolean isInside(Vector3f toCheck) {

        boolean result = toCheck.x > min.x && toCheck.x < max.x
                && toCheck.y > min.y && toCheck.y < max.y
                && toCheck.z > min.z && toCheck.z < max.z;
        //System.out.println("Checking :" + toCheck + " : " + result);
        return result;
    }
}
