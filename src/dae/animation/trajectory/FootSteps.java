/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.trajectory;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class FootSteps extends Node {

    private Vector3f startLoc;
    private ArrayList<FootStep> footSteps = new ArrayList<FootStep>();
    private int currentIndex = 0;

    public FootSteps() {
    }

    public void addFootStep(FootStep step) {
        footSteps.add(step);
        this.attachChild(step);
    }

    public FootStep getCurrentFootStep() {
        if (currentIndex >= footSteps.size()) {
            currentIndex = footSteps.size() - 1;
        }
        return footSteps.get(currentIndex);
    }

    public boolean next() {
        currentIndex++;
        return currentIndex < footSteps.size();
    }

    public void reset() {
        currentIndex = 0;
    }

    public void setStartLoc(Vector3f loc) {
        this.startLoc = loc;
    }

    void addSteps(Material left, Material right, String sStartType, float stepWidth, float stepLength, float stepHeight, Vector3f direction, int size) {
        Vector3f upVector = new Vector3f(0, 1, 0);
        Vector3f sideVector = direction.cross(upVector);
        sideVector.normalizeLocal();

        String currentType = sStartType;
        for (int i = 0; i < size; ++i) {
            Vector3f stepLoc = new Vector3f(startLoc);
            if (currentType.equals("right")) {
                stepLoc.addLocal(sideVector.mult(stepWidth));
                FootStep step = new FootStep(right, currentType, stepLoc, direction);
                addFootStep(step);
            } else {
                stepLoc.addLocal(sideVector.mult(-stepWidth));
                FootStep step = new FootStep(left, currentType, stepLoc, direction);
                addFootStep(step);
            }

            if (currentType.equals("right")) {
                currentType = "left";
            } else {
                currentType = "right";
            }

            startLoc.addLocal(direction.mult(stepLength));
            startLoc.addLocal(upVector.mult(stepHeight));
        }
    }
}