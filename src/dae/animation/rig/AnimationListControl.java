/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Koen Samyn
 */
public class AnimationListControl implements Control {

    private ArrayList<AnimationController> controllers =
            new ArrayList<AnimationController>();
    private Rig rig;

    public Control cloneForSpatial(Spatial spatial) {
        AnimationListControl controller = new AnimationListControl();
        for (AnimationController ac : controllers) {
            controller.addAnimationController(ac.clone());
        }
        spatial.addControl(controller);
        return controller;
    }

    public int addAnimationController(AnimationController ac) {
        controllers.add(ac);
        return controllers.indexOf(ac);
    }

    public int removeAnimationController(AnimationController ac) {
        int index = controllers.indexOf(ac);
        controllers.remove(ac);
        return index;
    }

    public List<AnimationController> getAnimationControllers() {
        return controllers;
    }

    public int getNrOfAnimationControllers() {
        return controllers.size();
    }

    public Object getAnimationControllerAt(int index) {
        return controllers.get(index);
    }

    public void setSpatial(Spatial spatial) {
        if (spatial instanceof Rig) {
            this.rig = (Rig) spatial;
            for (AnimationController ac : controllers) {
                ac.initialize(rig);
            }
        }
    }

    public void update(float tpf) {
        for (AnimationController ac : controllers) {
            ac.update(tpf);
        }
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public void write(JmeExporter ex) throws IOException {
    }

    public void read(JmeImporter im) throws IOException {
    }

    public String generateNewName(String controller) {
        int index = 1;
        String newName = controller + index;
        while (hasName(newName)) {
            ++index;
            newName = controller + index;
        }
        return newName;
    }

    private boolean hasName(String name) {
        for (AnimationController ac : controllers) {
            if (ac.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void initialize() {
        for (AnimationController ac : controllers) {
            ac.initialize(rig);
        }
    }
}
