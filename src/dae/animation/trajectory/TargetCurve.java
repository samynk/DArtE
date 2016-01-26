/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.trajectory;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.event.TransformListener;
import dae.animation.event.TransformType;
import dae.animation.skeleton.Handle;
import dae.prefabs.Prefab;
import dae.prefabs.types.ObjectType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a curve that must be followed by a target.
 *
 * @author Koen Samyn
 */
public class TargetCurve extends Prefab implements TransformListener{

    /**
     * The tolerance for the target.
     */
    private float tolerance = 0.1f;
    /**
     * The start handle for the target curve.
     */
    private Handle start;
    /**
     * The end handle for the target curve.
     */
    private Handle end;
    private ArrayList<CurveChannel> channels = new ArrayList<CurveChannel>();
    private HashMap<String,CurveChannel> channelMap = new HashMap<String,CurveChannel>();
    
    private Node debugNode = new Node();
    private AssetManager manager;

    /**
     * Creates an empty target cuve.
     */
    public TargetCurve() {
    }
    
    /**
     * Creates a new TargetCurve
     *
     * @param start the start of the curve.
     * @param startRotation the start rotation of the curve.
     * @param end the end location of the curve
     * @param endRotation the end rotation of the curve.
     */
    public TargetCurve(Handle start, Handle end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void create( AssetManager manager, String extraInfo) {
        // create two handles to manipulate.
        ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Animation", "TwoAxisHandle");
        start = (Handle)type.create(manager,"start");
        start.setName("start");
        this.attachChild(start);

        end =  (Handle)type.create(manager,"end");
        end.setLocalTranslation(2,0,0);
        this.attachChild(end);
        
        this.attachChild(manager.loadModel("Skeleton/Helpers/Handle.j3o"));
       
        // listen to the changes in the handles
        attachChild(debugNode);
        this.manager = manager;
    }
    
    /**
     * Gets the correct curve channel.
     * @param name the name of the channel.
     */
    public CurveChannel getCurveChannel(String name){
        return channelMap.get(name);
    }

    

    /**
     * Add a CurveChannel object to the list of channels.
     *
     * @param channel the channel object to add.
     */
    public void addCurveChannel(CurveChannel channel) {
        channels.add(channel);
        channelMap.put(channel.getAttachmentPointName(),channel);
    }

    /**
     * @return the start
     */
    public Handle getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Handle start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Handle getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Handle end) {
        this.end = end;
    }

    /**
     * @return the tolerance
     */
    public float getTolerance() {
        return tolerance;
    }

    /**
     * @param tolerance the tolerance to set
     */
    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Called when the transform of one of the handles has changed.
     * @param source the prefab that is the source of the event.
     * @param type the type of transform.
     */
    public void transformChanged(Prefab source, TransformType type) {
        recreateTargetCurve();
    }
    
    /**
     * Recreates the target curve.
     */
    public void recreateTargetCurve(){
        
    }
    
    protected void createHandle(String name, Vector3f location, Quaternion rotation)
    {
        ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Animation", "TwoAxisHandle");
        Handle h = (Handle)type.create(manager,name);
        h.setName(name);
        h.setLocalTranslation(location);
        h.setLocalRotation(rotation);
        debugNode.attachChild(h);
    }
    
    /**
     * Clears the debugging handles
     */
    protected void clearDebugNode(){
        debugNode.detachAllChildren();
    }

    public Spatial getHandle(String name) {
        return this.getChild(name);
    }
}
