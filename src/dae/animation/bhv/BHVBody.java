package dae.animation.bhv;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.BodyElement;
import dae.animation.skeleton.RotationJoint;
import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class BHVBody extends Node implements BodyElement {

    private BHVAnimationData animData;
    private int currentFrame = 0;
    private float currentFrameTime = 0;
    private float totalAnimTime = 0;
    private ArrayList<RotationJoint> rotationJoints =
            new ArrayList<RotationJoint>();

    public BHVBody() {
    }

    public void setAnimationData(BHVAnimationData data) {
        if (data != null) {
            this.animData = data;
        }
    }

    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    public void addRotationJoint(RotationJoint rj) {
        rotationJoints.add(rj);
    }

    public void reset() {
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        if (animData != null) {
            totalAnimTime = animData.getFrameTime() * animData.getNrOfFrames();
            currentFrameTime += (tpf / 10);
            if (currentFrameTime > totalAnimTime) {
                currentFrameTime -= totalAnimTime;
            }
            currentFrame = (int) (currentFrameTime / animData.getFrameTime());
            //System.out.println("currentFrame : " + currentFrame);
        }
        for (RotationJoint rj : rotationJoints) {
            rj.updateTransform(currentFrame, animData);
        }
    }
    
     @Override
    public void hideTargetObjects(){
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }
    
   
    @Override
    public void showTargetObjects() {
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }
}
