/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.gizmos;

import com.google.common.eventbus.Subscribe;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.events.RotateGizmoSpaceChangedEvent;

/**
 *
 * @author Koen Samyn
 */
public class RotateGizmo extends Node implements Gizmo {

    private RotateGizmoSpace rotateGizmoSpace = RotateGizmoSpace.LOCAL;
    private RotateGizmoSpace newSpace = RotateGizmoSpace.LOCAL;
    
    private Geometry xGizmo;
    private Geometry yGizmo;
    private Geometry zGizmo;
    
    private Material xMaterial;
    private Material yMaterial;
    private Material zMaterial;
    private Material disabledMaterial;
    
    private boolean enabled = true;

    public RotateGizmo(AssetManager manager, float axisRadius, float gizmoRadius) {
        //xMaterial.getAdditionalRenderState().setDepthTest(false);
        //yMaterial.getAdditionalRenderState().setDepthTest(false);
        //zMaterial.getAdditionalRenderState().setDepthTest(false);
        xMaterial = manager.loadMaterial("Materials/XGizmoMaterial.j3m");
        yMaterial = manager.loadMaterial("Materials/YGizmoMaterial.j3m");
        zMaterial = manager.loadMaterial("Materials/ZGizmoMaterial.j3m");
        disabledMaterial = manager.loadMaterial("Materials/DisabledGizmoMaterial.j3m");

        xGizmo = createDonut("X", xMaterial, Vector3f.UNIT_X, 24, 6, axisRadius, gizmoRadius);
        yGizmo = createDonut("Y", yMaterial, Vector3f.UNIT_Y, 24, 6, axisRadius, gizmoRadius);
        zGizmo = createDonut("Z", zMaterial, Vector3f.UNIT_Z, 24, 6, axisRadius, gizmoRadius);
        attachChild(xGizmo);
        attachChild(yGizmo);
        attachChild(zGizmo);
        this.setQueueBucket(Bucket.Translucent);

        GlobalObjects.getInstance().registerListener(this);
    }

    private Geometry createDonut(String name, Material mat, Vector3f axis, int outerSegments, int innerSegments, float axisRadius, float gizmoRadius) {
        Vector3f z = axis.normalize();
        Vector3f x, y;
        if (z.x > z.y && z.x > z.z) {
            // x component is largest so cross with unit.y
            x = z.cross(Vector3f.UNIT_Y);
            y = x.cross(z);
        } else {
            // y or z component is largest so cross with unit.x
            x = z.cross(Vector3f.UNIT_X);
            y = x.cross(z);
        }
        x.normalizeLocal();
        y.normalizeLocal();


        Vector3f donuty = axis.clone();

        x.multLocal(axisRadius);
        y.multLocal(axisRadius);

        donuty.multLocal(gizmoRadius);

        Vector3f[] vertices = new Vector3f[outerSegments * innerSegments];
        int[] indices = new int[outerSegments * innerSegments * 6];

        int index = 0;
        for (int outer = 0; outer < outerSegments; ++outer) {
            float angle = (FastMath.TWO_PI * outer) / (outerSegments);
            Vector3f center = x.mult(FastMath.cos(angle)).add(y.mult(FastMath.sin(angle)));

            for (int inner = 0; inner < innerSegments; ++inner) {
                Vector3f donutx = center.normalize().multLocal(gizmoRadius);
                float angle2 = (FastMath.TWO_PI * inner) / (innerSegments);
                Vector3f offset = donutx.mult(FastMath.cos(angle2)).add(donuty.mult(FastMath.sin(angle2)));
                vertices[index++] = center.add(offset);
            }
        }

        // create the indices
        index = 0;
        for (int outer = 0; outer < outerSegments; ++outer) {
            for (int inner = 0; inner < innerSegments; ++inner) {
                int inner1 = (inner + 1) % innerSegments;
                int outer1 = (outer + 1) % outerSegments;
                int i1 = (outer * innerSegments) + inner;
                int i2 = (outer * innerSegments) + inner1;
                int i3 = outer1 * innerSegments + inner1;
                int i4 = outer1 * innerSegments + inner;

                indices[index] = i1;
                indices[index + 1] = i2;
                indices[index + 2] = i3;
                indices[index + 3] = i1;
                indices[index + 4] = i3;
                indices[index + 5] = i4;
                index += 6;
            }

        }
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        result.updateBound();

        Geometry g = new Geometry(name, result);
        g.setMaterial(mat);
        g.setUserData("Transform", "True");
        return g;
    }

    @Override
    public void updateLogicalState(float tpf) {
        if (newSpace != this.rotateGizmoSpace) {
            rotateGizmoSpace = newSpace;
        }
        switchSpace();
        
        if ( parent instanceof Prefab ){
            Prefab pp = (Prefab)parent;
            adaptMaterials(!pp.getLocked());
        }
    }
    
    private void adaptMaterials(boolean enabled){
        if ( this.enabled == enabled)
        {
            return;
        }else{
            this.enabled = enabled;
        }
        
        if ( enabled ){
            this.xGizmo.setMaterial(xMaterial);
            this.yGizmo.setMaterial(yMaterial);
            this.zGizmo.setMaterial(zMaterial);
        }else{
            this.xGizmo.setMaterial(disabledMaterial);
            this.yGizmo.setMaterial(disabledMaterial);
            this.zGizmo.setMaterial(disabledMaterial);
        }
    }

    private void switchSpace() {  
        switch (this.rotateGizmoSpace) {
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
    public void gizmoSpaceChanged(RotateGizmoSpaceChangedEvent event) {
        this.newSpace = event.getSpace();
    }
}
