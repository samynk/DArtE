package dae;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import dae.gui.SandboxViewport;

/**
 *
 * @author Koen Samyn
 */
public class DAECamAppState extends AbstractAppState {

    private Application app;
    private DAEFlyByCamera flyCam;

    /**
     * This is called by SimpleApplication during initialize().
     */
    void setCamera(DAEFlyByCamera cam) {
        this.flyCam = cam;
    }

    public DAEFlyByCamera getCamera() {
        return flyCam;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;

        if (app.getInputManager() != null) {
            if (flyCam == null) {
                flyCam = new DAEFlyByCamera(app.getCamera());
                flyCam.setUpVector(Vector3f.UNIT_Y);
                flyCam.setDragToRotate(true);
                flyCam.setMoveSpeed(10.0f);
                GlobalObjects.getInstance().setCamera(flyCam);
                if ( app instanceof SandboxViewport)
                {
                    SandboxViewport sv = (SandboxViewport)app;
                    sv.setFlyByCamera(flyCam);
                }
            }
            GlobalObjects.getInstance().installCameraKeys(flyCam, app.getInputManager(),false);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        flyCam.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        flyCam.unregisterInput();
    }
}
