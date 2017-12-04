package dae.animation.bhv;

/**
 *
 * @author Koen
 */
public class BHVAnimationData {

    public float[][] animData;
    private float frameTime;

    public BHVAnimationData(int nrOfFrames, int nrOfChannels) {
        animData = new float[nrOfFrames][nrOfChannels];
    }

    public void setAnimData(int frameNumber, int channelNumber, float value) {
        animData[frameNumber][channelNumber] = value;
    }

    public float getAnimData(int frameNumber, int channelNumber) {
        return animData[frameNumber][channelNumber];
    }

    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }

    public int getNrOfFrames() {
        return animData.length;
    }

    public float getFrameTime() {
        return frameTime;
    }
}
