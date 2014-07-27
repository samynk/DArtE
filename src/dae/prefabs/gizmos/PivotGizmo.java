package dae.prefabs.gizmos;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import dae.prefabs.Prefab;

/**
 * This gizmo shows the user where a pivot point is situated on an object. The
 * pivot gizmo can be used when adding metadata to a model.
 *
 * The axis offcourse uses color coding to differentiate among the different
 * axises.
 *
 * @author Koen
 */
public class PivotGizmo extends Prefab implements Gizmo{

    private float axisHeight = 0.5f;
    private float axisRadius = 0.025f;

    public PivotGizmo() {
        setCategory("Metadata");
        setType("Pivot");
    }
    
    @Override
    public void create(String name, AssetManager manager, String extraInfo) {     
        Material xMaterial = manager.loadMaterial("Materials/XPivotMaterial.j3m");
        Material yMaterial = manager.loadMaterial("Materials/YPivotMaterial.j3m");
        Material zMaterial = manager.loadMaterial("Materials/ZPivotMaterial.j3m");
        Material boxMaterial = manager.loadMaterial("Materials/BoxPivotMaterial.j3m");
        createGeometry(xMaterial, yMaterial, zMaterial,boxMaterial, axisHeight, axisRadius);
    }
    
    @Override
    public Node clone(boolean cloneMaterials){
        return (Node)this.clone();
    }
    
    @Override
    public Spatial clone(){
        PivotGizmo pg = new PivotGizmo();
        pg.setLocalPrefabRotation(this.getLocalPrefabRotation().clone());
        pg.setLocalPrefabTranslation(this.getLocalPrefabTranslation().clone());
        pg.setLocalScale(this.getLocalScale().clone());
        for(Spatial s : this.getChildren()){
            pg.attachChild(s.clone());
        }
        return pg;
    }

    private Node createArrow(float coneRatio, float axisHeight, float axisRadius, Material mat) {
        float omConeRatio = 1 - coneRatio;
        Node xAxisNode = new Node();
        Cylinder cyl = new Cylinder(6, 12, axisRadius, axisHeight * coneRatio, true, false);
        Vector2f scale = new Vector2f(1,5*(axisHeight* coneRatio));
        cyl.scaleTextureCoordinates( scale );
        Geometry xAxis1 = new Geometry("xCyl", cyl);
        xAxis1.setMaterial(mat);
        xAxis1.setLocalTranslation(0, 0, axisHeight * coneRatio / 2.0f);

        Cylinder cyl2 = new Cylinder(6, 24, 0.0f, axisRadius * 1.5f, axisHeight * omConeRatio, true, false);
        Vector2f scale2 = new Vector2f(5*(axisHeight* omConeRatio),5*(axisHeight* omConeRatio));
        cyl2.scaleTextureCoordinates(scale2);
        Geometry xAxis2 = new Geometry("xCone", cyl2);
        xAxis2.setMaterial(mat);
        xAxis2.setLocalTranslation(0, 0, axisHeight * (coneRatio + omConeRatio / 2));

        xAxisNode.attachChild(xAxis1);
        xAxisNode.attachChild(xAxis2);
        return xAxisNode;
    }

    private void createGeometry(Material xMaterial, Material yMaterial, Material zMaterial, Material boxMaterial, float axisHeight, float axisRadius) {
        float coneRatio = 0.8f;
        //xMaterial.getAdditionalRenderState().setDepthTest(false);
        //yMaterial.getAdditionalRenderState().setDepthTest(false);
        //zMaterial.getAdditionalRenderState().setDepthTest(false);;

        Node xArrow = createArrow(coneRatio, axisHeight, axisRadius, xMaterial);
        xArrow.rotate(0, FastMath.PI / 2, 0);

        Node yArrow = createArrow(coneRatio, axisHeight, axisRadius, yMaterial);
        yArrow.rotate(-FastMath.PI / 2, 0, 0);

        Node zArrow = createArrow(coneRatio, axisHeight, axisRadius, zMaterial);

        this.attachChild(xArrow);
        this.attachChild(yArrow);
        this.attachChild(zArrow);
        
        Box b = new Box(axisRadius,axisRadius,axisRadius);
        Geometry centerGeo = new Geometry("centerbox", b);
        centerGeo.setMaterial(boxMaterial);
        this.attachChild(centerGeo);

        //(0.15f * axisHeight, plane);

        setQueueBucket(Bucket.Opaque);
        xArrow.setQueueBucket(Bucket.Inherit);
        yArrow.setQueueBucket(Bucket.Inherit);
        zArrow.setQueueBucket(Bucket.Inherit);
        centerGeo.setQueueBucket(Bucket.Inherit);
    }
}
