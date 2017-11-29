/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import dae.animation.rig.Rig;
import java.io.IOException;

/**
 *
 * @author Koen.Samyn
 */
public class BehaviourControl implements Control {

    private Rig rig;
    private int lastFrame = -1;

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BehaviourControl bc =  new BehaviourControl();
        bc.setSpatial(spatial);
        spatial.addControl(bc);
        return bc;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial instanceof Rig) {
            rig = (Rig) spatial;
        }
    }

    @Override
    public void update(float tpf) {
        Behaviour current;
        if (rig != null && (current = rig.getCurrentBehaviour()) != null) {
            float frame = 0;
            if (current.isPlaying()) {
                // update current frame
                current.addTime(tpf);
                frame = current.getCurrentPlayFrame();

            } else {
                if ( current.getCurrentFrame() == lastFrame ){
                    // don't update if frame did not change.
                    // otherwise the target could not be reset.
                    return;
                }
                lastFrame = current.getCurrentFrame();
                frame = lastFrame;
            }
            for (TimeLine tl : current.getTimeLines()) {
                tl.interpolateAndSet(frame);
            }
        }
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        
    }

}
