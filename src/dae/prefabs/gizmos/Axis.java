package dae.prefabs.gizmos;

import com.google.common.eventbus.Subscribe;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.events.GizmoSpaceChangedEvent;

/**
 * This class draws an axis that is pickable. The Axis allows the user to see
 * where an object will be dropped. The Axis has 7 parts : - X axis : allows the
 * user to change the x coordinate of an object. - Y axis : allows the user to
 * change the y coordinate of an object. - Z axis : allows the user to change
 * the z coordinate of an object. - XY plane : allows the user to drag the
 * object across the xy plane. - YZ plane : allows the user to drag the object
 * across the yz plane. - XZ plane : allows the user to drag the object across
 * the xz plane. The sandbox editor uses shadow mapping to give the user even
 * more clues about the location of an object that is being dragged.
 *
 * The axis offcourse uses color coding to differentiate among the different
 * axises.
 *
 * @author Koen
 */
public class Axis extends Node implements Gizmo {

    private TranslateGizmoSpace currentSpace = TranslateGizmoSpace.LOCAL;
    private TranslateGizmoSpace newSpace = TranslateGizmoSpace.LOCAL;
    private Material xMaterial;
    private Material yMaterial;
    private Material zMaterial;
    private Material planeMaterial;
    private Material disabledMaterial;
    private Material disabledPlaneMaterial;
    
    private Node xArrow,yArrow,zArrow;
    private Geometry xyGeometry, xzGeometry, yzGeometry;
    
    private boolean enabled = true;

    public Axis(AssetManager manager, float axisHeight, float axisRadius) {
        float coneRatio = 0.8f;

        this.xMaterial = manager.loadMaterial("Materials/XGizmoMaterial.j3m");
        this.yMaterial = manager.loadMaterial("Materials/YGizmoMaterial.j3m");
        this.zMaterial = manager.loadMaterial("Materials/ZGizmoMaterial.j3m");
        this.planeMaterial = manager.loadMaterial("Materials/PlaneGizmoMaterial.j3m");
        this.disabledMaterial = manager.loadMaterial("Materials/DisabledGizmoMaterial.j3m");
        this.disabledPlaneMaterial = manager.loadMaterial("Materials/DisabledPlaneGizmoMaterial.j3m");
        
        xArrow = createArrow(coneRatio, axisHeight, axisRadius, xMaterial, "translate_X");
        xArrow.rotate(0, FastMath.PI / 2, 0);

        yArrow = createArrow(coneRatio, axisHeight, axisRadius, yMaterial, "translate_Y");
        yArrow.rotate(-FastMath.PI / 2, 0, 0);

        zArrow = createArrow(coneRatio, axisHeight, axisRadius, zMaterial, "translate_Z");

        this.attachChild(xArrow);
        this.attachChild(yArrow);
        this.attachChild(zArrow);

        //(0.15f * axisHeight, plane);

        setQueueBucket(Bucket.Translucent);
        xArrow.setQueueBucket(Bucket.Inherit);
        yArrow.setQueueBucket(Bucket.Inherit);
        zArrow.setQueueBucket(Bucket.Inherit);

        

        createPlaneControllers(manager, 0.5f, axisRadius, planeMaterial);

        GlobalObjects.getInstance().registerListener(this);
    }

    private Node createArrow(float coneRatio, float axisHeight, float axisRadius, Material mat, String transform) {
        float omConeRatio = 1 - coneRatio;
        Node xAxisNode = new Node();
        Cylinder cyl = new Cylinder(6, 12, axisRadius, axisHeight * coneRatio, true, false);
        Geometry xAxis1 = new Geometry("xCyl", cyl);
        xAxis1.setMaterial(mat);
        xAxis1.setLocalTranslation(0, 0, axisHeight * coneRatio / 2.0f);

        Cylinder cyl2 = new Cylinder(6, 24, 0.0f, axisRadius * 1.5f, axisHeight * omConeRatio, true, false);
        Geometry xAxis2 = new Geometry("xCone", cyl2);
        xAxis2.setMaterial(mat);
        xAxis2.setLocalTranslation(0, 0, axisHeight * (coneRatio + omConeRatio / 2));

        xAxis1.setUserData("Transform", transform);
        xAxis2.setUserData("Transform", transform);

        xAxisNode.attachChild(xAxis1);
        xAxisNode.attachChild(xAxis2);
        return xAxisNode;
    }

    private void createPlaneControllers(AssetManager assetManager, float size, float axisRadius, Material planeMaterial) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        mat.setColor("Color", ColorRGBA.Orange);

        Box xyBox = new Box(size, size, 0.01f);
        xyGeometry = new Geometry("translate_XY", xyBox);
        xyGeometry.setMaterial(planeMaterial);
        xyGeometry.setLocalTranslation(size + axisRadius, size + axisRadius, 0);
        this.attachChild(xyGeometry);
        xyGeometry.setUserData("Transform", "translate_XY");

        Geometry g = new Geometry("wireframe_xy", new WireBox(size, size, 0.01f));
        g.setMaterial(mat);
        g.setLocalTranslation(size + axisRadius, size + axisRadius, 0);
        this.attachChild(g);

        xyGeometry.setQueueBucket(Bucket.Inherit);
        g.setQueueBucket(Bucket.Inherit);


        Box xzBox = new Box(size, 0.01f, size);
        xzGeometry = new Geometry("translate_XZ", xzBox);
        xzGeometry.setMaterial(planeMaterial);
        xzGeometry.move(size + axisRadius, 0, size + axisRadius);
        this.attachChild(xzGeometry);
        xzGeometry.setUserData("Transform", "translate_XZ");

        Geometry g2 = new Geometry("wireframe_xz", new WireBox(size, 0.01f, size));
        g2.setMaterial(mat);
        g2.setLocalTranslation(size + axisRadius, 0, size + axisRadius);
        this.attachChild(g2);

        xzGeometry.setQueueBucket(Bucket.Inherit);
        g2.setQueueBucket(Bucket.Inherit);

        Box yzBox = new Box(0.01f, size, size);
        yzGeometry = new Geometry("translate_YZ", yzBox);
        yzGeometry.setMaterial(planeMaterial);
        yzGeometry.move(0, size + axisRadius, size + axisRadius);
        this.attachChild(yzGeometry);
        yzGeometry.setUserData("Transform", "translate_YZ");

        Geometry g3 = new Geometry("wireframe_yz", new WireBox(0.01f, size, size));
        g3.setMaterial(mat);
        g3.setLocalTranslation(0, size + axisRadius, size + axisRadius);
        this.attachChild(g3);

        yzGeometry.setQueueBucket(Bucket.Inherit);
        g3.setQueueBucket(Bucket.Inherit);
    }

    @Override
    public void updateLogicalState(float tpf) {
        if (newSpace != this.currentSpace) {
            currentSpace = newSpace;
            switchSpace();
        }
        
        if ( parent instanceof Prefab ){
            Prefab pp = (Prefab)parent;
            adaptMaterials(!pp.getLocked());
        }
    }

    @Override
    protected void setParent(Node parent) {
        super.setParent(parent);
        switchSpace();
    }
    
    private void adaptMaterials(boolean enabled){
        if ( this.enabled == enabled){
            return;
        }else{
            this.enabled = enabled;
        }
        for( Spatial s : xArrow.getChildren()){
            if (enabled){
                s.setMaterial(xMaterial);
            }else{
                s.setMaterial(disabledMaterial);
            }
        }
        
        for( Spatial s : yArrow.getChildren()){
            if (enabled){
                s.setMaterial(yMaterial);
            }else{
                s.setMaterial(disabledMaterial);
            }
        }
        
        for( Spatial s : zArrow.getChildren()){
            if (enabled){
                s.setMaterial(zMaterial);
            }else{
                s.setMaterial(disabledMaterial);
            }
        }
        if ( enabled ){
            xyGeometry.setMaterial(this.planeMaterial);
            yzGeometry.setMaterial(this.planeMaterial);
            xzGeometry.setMaterial(this.planeMaterial);
        }else{
            xyGeometry.setMaterial(this.disabledPlaneMaterial);
            yzGeometry.setMaterial(this.disabledPlaneMaterial);
            xzGeometry.setMaterial(this.disabledPlaneMaterial);
        }
    }

    private void switchSpace() {
        switch (this.currentSpace) {
            case LOCAL:
                 if (getParent() instanceof Prefab){
                    Prefab thisPrefab = (Prefab)getParent();
                    Quaternion extraQuat = thisPrefab.getGizmoRotation();
                    if (  extraQuat != null){
                        this.setLocalRotation(extraQuat);
                    }else{
                        this.setLocalRotation(Matrix3f.IDENTITY);
                    }
                }
                this.setLocalRotation(Matrix3f.IDENTITY);
                break;
            case PARENT:
                if (getParent() != null) {
                    // get the parent of the parent (not always possible).
                    Quaternion inverseLocalParentRot = getParent().getLocalRotation().inverse();
                    this.setLocalRotation(inverseLocalParentRot);
                }
                break;

            case WORLD:
                if (getParent() != null) {
                    Quaternion inverseWorldParentRot = parent.getWorldRotation().inverse();
                    this.setLocalRotation(inverseWorldParentRot);
                }
                break;
        }
    }

    @Subscribe
    public void gizmoSpaceChanged(GizmoSpaceChangedEvent event) {
        this.newSpace = event.getSpace();
    }
}
