/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import dae.animation.bhv.BHVAnimationData;

/**
 *
 * @author Koen
 */
public class RotationJoint extends Node implements BodyElement, ChannelSupport {

    private Geometry jg;
    private Vector3f currentTranslation;
    private int xindex = -1;
    private int yindex = -1;
    private int zindex = -1;
    private int rxindex = -1;
    private int ryindex = -1;
    private int rzindex = -1;

    public RotationJoint(Material mat, String name) {
        super(name);
        Sphere joint = new Sphere(6, 6, 1);
        jg = new Geometry(name + "_joint", joint);
        jg.setShadowMode(RenderQueue.ShadowMode.Cast);
        jg.setMaterial(mat);
        jg.setLocalTranslation(7, 0, 0);
        this.attachChild(jg);
    }

    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    public void reset() {
    }

    public void setXYZIndexInChannel(int xindex, int yindex, int zindex) {
        this.xindex = xindex;
        this.yindex = yindex;
        this.zindex = zindex;
    }

    public void setRXRYRZIndexInChannel(int rxindex, int ryindex, int rzindex) {
        this.rxindex = rxindex;
        this.ryindex = ryindex;
        this.rzindex = rzindex;
    }

    public void setXIndexInChannel(int xindex) {
        this.xindex = xindex;
    }

    public void setYIndexInChannel(int yindex) {
        this.yindex = yindex;
    }

    public void setZIndexInChannel(int zindex) {
        this.zindex = zindex;
    }

    public void setRXIndexInChannel(int rxindex) {
        this.rxindex = rxindex;
    }

    public void setRYIndexInChannel(int ryindex) {
        this.ryindex = ryindex;
    }

    public void setRZIndexInChannel(int rzindex) {
        this.rzindex = rzindex;
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation);
        this.currentTranslation = localTranslation;
    }

    public void updateTransform(int frameNumber, BHVAnimationData data) {
        if (xindex > -1) {
            currentTranslation.x = data.getAnimData(frameNumber, xindex);
        }
        if (yindex > -1) {
            currentTranslation.y = data.getAnimData(frameNumber, yindex);
        }
        if (zindex > -1) {
            currentTranslation.z = data.getAnimData(frameNumber, zindex);
        }
        if (rxindex > -1 && ryindex > -1 && rzindex > -1) {
            float rx = data.getAnimData(frameNumber, rxindex);
            float ry = data.getAnimData(frameNumber, ryindex);
            float rz = data.getAnimData(frameNumber, rzindex);

            float cx = FastMath.cos(rx * FastMath.DEG_TO_RAD);
            float sx = FastMath.sin(rx * FastMath.DEG_TO_RAD);
            float cy = FastMath.cos(ry * FastMath.DEG_TO_RAD);
            float sy = FastMath.sin(ry * FastMath.DEG_TO_RAD);
            float cz = FastMath.cos(rz * FastMath.DEG_TO_RAD);
            float sz = FastMath.sin(rz * FastMath.DEG_TO_RAD);
            this.setLocalTransform(Transform.IDENTITY);
            // the order has to be correct
            if (rxindex < ryindex && rxindex < rzindex) {
                if (ryindex < rzindex) {
                    // XYZ rotation
                    float c1 = cx;
                    float s1 = sx;
                    float c2 = cy;
                    float s2 = sy;
                    float c3 = cz;
                    float s3 = sz;

                    Matrix3f rotation = new Matrix3f(
                            c2 * c3, -s2, c2 * s3,
                            s1 * s3 + c1 * c3 * c2, c1 * c2, c1 * s2 * s3 - c3 * s1,
                            c3 * s1 * s2 - c1 * s3, c2 * s1, c1 * c3 + s1 * s2 * s3);
                    this.setLocalRotation(rotation);
                } else {
                    // XZY rotation
                    float c1 = cx;
                    float s1 = sx;
                    float c2 = cz;
                    float s2 = sz;
                    float c3 = cy;
                    float s3 = sy;

                    Matrix3f rotation = new Matrix3f(
                            c2 * c3, -c2 * s3, s2,
                            c1 * s3 + c3 * s1 * s2, c1 * c3 - s1 * s2 * s3, -c2 * s1,
                            s1 * s3 - c1 * c3 * s2, c3 * s1 + c1 * s2 * s3, c1 * c2);
                    this.setLocalRotation(rotation);
                }
            } else if (ryindex < rxindex && ryindex < rzindex) {
                if (rxindex < rzindex) {
                    // YXZ
                    float c1 = cy;
                    float s1 = sy;
                    float c2 = cx;
                    float s2 = sx;
                    float c3 = cz;
                    float s3 = sz;

                    Matrix3f rotation = new Matrix3f(
                            c1 * c3 + s1 * s2 * s3, c3 * s1 * s2 - c1 * s3, c2 * s1,
                            c2 * s3, c2 * c3, -s2,
                            c1 * s2 * s3 - c3 * s1, s1 * s3 + c1 * c3 * c2, c1 * c2);
                    this.setLocalRotation(rotation);
                } else {
                    // YZX
                    float c1 = cy;
                    float s1 = sy;
                    float c2 = cz;
                    float s2 = sz;
                    float c3 = cx;
                    float s3 = sx;

                    Matrix3f rotation = new Matrix3f(
                            c1 * c2, s1 * s3 - c1 * c3 * c2, c3 * s1 + c1 * s2 * s3,
                            s2, c2 * c3, -c2 * s3,
                            -c2 * s1, c1 * s3 + c3 * s1 * s2, c1 * c3 - s1 * s2 * s3);
                    this.setLocalRotation(rotation);
                }
            } else {
                if (rxindex < ryindex) {
                    // ZXY
                    //System.out.println("ZXY");
                    float c1 = cz;
                    float s1 = sz;
                    float c2 = cx;
                    float s2 = sx;
                    float c3 = cy;
                    float s3 = sy;

                    Matrix3f rotation = new Matrix3f(
                            c1 * c3 - s1 * s2 * s3, -c2 * s1, c1 * s3 + c3 * s1 * s2,
                            c3 * s1 + c1 * s2 * s3, c1 * c2, s1 * s3 - c1 * c3 * s2,
                            -c2 * s3, s2, c2 * c3);

                    Quaternion q = new Quaternion();
                    Quaternion result = q.fromRotationMatrix(rotation);


                    this.setLocalRotation(rotation);
                    this.setLocalTranslation(currentTranslation);
                    // this.setLocalRotation(rotation);

                } else {
                    System.out.println("ZYX");
                    float c1 = cz;
                    float s1 = sz;
                    float c2 = cy;
                    float s2 = sy;
                    float c3 = cx;
                    float s3 = sx;

                    Matrix3f rotation = new Matrix3f(
                            c1 * c2, c1 * s2 * s3 - c3 * s1, s1 * s3 + c1 * c3 * s2,
                            c2 * s1, c1 * c3 + s1 * s2 * s3, c3 * s1 * s2 - c1 * s3,
                            -s2, c2 * s3, c2 * c3);
                    this.setLocalRotation(rotation);
                }

            }
        }
        this.setLocalTranslation(currentTranslation);
    }
    
     @Override
    public void hideTargetObjects(){
        for (Spatial s : children) {
            if (s instanceof BodyElement) {
                ((BodyElement)s).hideTargetObjects();
            }
        }
    }
    
   
    public void showTargetObjects() {
        for( Spatial s: this.getChildren())
        {
            if ( s instanceof BodyElement ){
                ((BodyElement)s).showTargetObjects();
            }
        }
    }
}
