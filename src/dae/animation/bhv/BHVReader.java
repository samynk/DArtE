package dae.animation.bhv;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import dae.animation.skeleton.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 *
 * @author Koen
 */
public class BHVReader implements AssetLoader {

    private enum ParseState {

        HIERARCHY, ANIMATION, IDLE
    };
    ParseState state = ParseState.IDLE;

    public Object load(AssetInfo assetInfo) throws IOException {

        Material apMaterial = new Material(assetInfo.getManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        apMaterial.setColor("Color", ColorRGBA.Green);
        apMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));
        BHVBody body = new BHVBody();

        Material limbMaterial = new Material(assetInfo.getManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        limbMaterial.setColor("Color", ColorRGBA.Orange);
        limbMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));

        Material revJointMaterial = new Material(assetInfo.getManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");
        revJointMaterial.setColor("Color", ColorRGBA.Green);
        revJointMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));


        BufferedReader br = new BufferedReader(new InputStreamReader(assetInfo.openStream()));

        Stack<BodyElement> hierarchyStack = new Stack<BodyElement>();

        String line;
        BodyElement currentParent = body;
        int currentChannelIndex = 0;
        int nrOfFrames = 0;
        float frameTime = 0;
        int frameNumber = 0;
        BHVAnimationData animData = null;
        while ((line = br.readLine()) != null) {

            line = line.trim();
            if (state == ParseState.IDLE || state == ParseState.HIERARCHY) {
                if (line.startsWith("HIERARCHY")) {
                    state = ParseState.HIERARCHY;
                } else if (line.startsWith("ROOT")) {
                    RotationJoint rootJoint = new RotationJoint(limbMaterial, "root");
                    currentParent.attachBodyElement(rootJoint);
                    System.out.println("Pushing root joint");
                    hierarchyStack.push(rootJoint);
                    currentParent = rootJoint;
                    body.addRotationJoint(rootJoint);
                    //body.attachBodyElement(rootJoint);
                } else if (line.startsWith("OFFSET")) {
                    Vector3f location = readVector(line.substring(6));
                    currentParent.setLocalTranslation(location);
                } else if (line.startsWith("CHANNELS")) {
                    if (currentParent instanceof ChannelSupport) {
                        currentChannelIndex = readChannels((ChannelSupport) currentParent, currentChannelIndex, line.substring(8));
                    }
                } else if (line.startsWith("JOINT") || line.startsWith("End Site")) {
                    String[] components = line.split("\\s+");
                    //System.out.println("Rotation joint : " + components[1]);
                    RotationJoint rj = new RotationJoint(revJointMaterial, components[1]);
                    body.addRotationJoint(rj);
                    currentParent.attachBodyElement(rj);
                    //body.attachBodyElement(rj)
                    System.out.println("Pushing joint :" + components[1]);;
                    hierarchyStack.push(rj);
                    currentParent = rj;

                } else if (line.startsWith("}")) {
                    hierarchyStack.pop();
                    if (hierarchyStack.size() > 0) {
                        currentParent = hierarchyStack.peek();
                    } else {
                        currentParent = body;
                    }
                    System.out.println("Popped : " + currentParent);
                } else if (line.startsWith("MOTION")) {
                    System.out.println("hierarchyStack size :" + hierarchyStack.size());
                    state = ParseState.ANIMATION;
                }
            } else if (state == ParseState.ANIMATION) {
                if (line.startsWith("Frames")) {
                    int indexOfColumn = line.indexOf(':');
                    nrOfFrames = Integer.parseInt(line.substring(indexOfColumn + 1).trim());
                    animData = new BHVAnimationData(nrOfFrames, currentChannelIndex);
                    body.setAnimationData(animData);
                } else if (line.startsWith("Frame Time")) {
                    int indexOfColumn = line.indexOf(':');
                    frameTime = Float.parseFloat(line.substring(indexOfColumn + 1).trim());
                    if (animData != null) {
                        animData.setFrameTime(frameTime);
                    }
                } else {
                    String[] components = line.split("\\s+");
                    //System.out.println(components.length);
                    for (int channelIndex = 0; channelIndex < components.length; ++channelIndex) {
                        float v = Float.parseFloat(components[channelIndex]);
                        animData.setAnimData(frameNumber, channelIndex, v);
                    }
                    frameNumber++;
                }
            }
        }
        br.close();
        return body;
    }

    public Vector3f readVector(String line) {
        String[] components = line.trim().split("\\s+");
        System.out.println("Reading vector :" + line);
        float x = Float.parseFloat(components[0]);
        float y = Float.parseFloat(components[1]);
        float z = Float.parseFloat(components[2]);
        return new Vector3f(x, y, z);
    }

    public int readChannels(ChannelSupport cs, int currentIndex, String line) {
        String[] components = line.trim().split("\\s+");
        int numChannels = Integer.parseInt(components[0]);
        for (int i = 0; i < numChannels; ++i) {
            String c = components[i + 1];
            if ("xposition".equalsIgnoreCase(c)) {
                cs.setXIndexInChannel(currentIndex + i);
            } else if ("yposition".equalsIgnoreCase(c)) {
                cs.setYIndexInChannel(currentIndex + i);
            } else if ("zposition".equalsIgnoreCase(c)) {
                cs.setZIndexInChannel(currentIndex + i);
            } else if ("xrotation".equalsIgnoreCase(c)) {
                cs.setRXIndexInChannel(currentIndex + i);
            } else if ("yrotation".equalsIgnoreCase(c)) {
                cs.setRYIndexInChannel(currentIndex + i);
            } else if ("zrotation".equalsIgnoreCase(c)) {
                cs.setRZIndexInChannel(currentIndex + i);
            }
        }
        return currentIndex + numChannels;
    }
}
