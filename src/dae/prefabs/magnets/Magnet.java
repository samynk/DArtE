/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.magnets;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import dae.prefabs.Prefab;
import dae.prefabs.standard.RotationRange;

/**
 * This class
 *
 * @author Koen
 */
public class Magnet {

    /**
     * The location of the attachment point in model space.
     */
    private Vector3f location;
    /**
     * The local rotation frame of the magnet.
     */
    private Matrix3f localFrame;
    /**
     * The radius of influence of the magnet object.
     */
    private float radius;
    /**
     * The type of attachment point. This can be used to create a map of types
     * that can be connected to each other.
     */
    private String type;
    /**
     * If the magnet is used for the insertion of elements then this boolean is
     * set to true.
     */
    private boolean pivotMagnet;
    /**
     * The name of the magnet (for debugging purposes).
     */
    private String name;
    /**
     * An optional are where the magnet is active.
     */
    private MagnetArea magnetArea;
    /*
     * A magnet can also define an optimal pivot point to connect with. If the optimal
     * pivot point is not found in the list of pivots of the object that is attached
     * to the magnet, the current pivot point of the attached object is used.
     */
    private String selectPivot;
    /**
     * A rotation axis for this
     */
    private Vector3f rotationAxis;
    /**
     * The possible rotation values for the rotation axis.
     */
    private RotationRange rotationRange;// = new RotationRange(-FastMath.HALF_PI, FastMath.HALF_PI, FastMath.PI / 15, 0);
    /**
     * The current rotation value.
     */
    private float rotation;
    private int currentRotationIndex = -1;
    private Matrix3f additionalRotation = new Matrix3f();

    public Magnet() {
    }
    
    /**
     * Checks if this magnet has a local frame that needs to be applied.
     * @return true if the object has a local frame, false otherwise.
     */
    public boolean hasLocalFrame(){
        return true;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public void setLocation(float xpos, float ypos, float zpos) {
        if (location != null) {
            location.set(xpos, ypos, zpos);
        } else {
            location = new Vector3f(new Vector3f(xpos, ypos, zpos));
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setPivotMagnet(boolean pivotMagnet) {
        this.pivotMagnet = pivotMagnet;
    }

    public boolean isPivotMagnet() {
        return pivotMagnet;
    }

    public void setLocalFrame(Matrix3f rotation) {
        this.localFrame = rotation;
    }

    public Matrix3f getLocalFrame() {
        if (this.rotationRange == null) {
            return localFrame;
        } else {
            //return localFrame.mult(additionalRotation);
            return additionalRotation;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasMagnetArea() {
        return magnetArea != null;
    }

    public MagnetArea getMagnetArea() {
        return magnetArea;
    }

    public void setMagnetArea(MagnetArea area) {
        this.magnetArea = area;
    }

    public void setSelectPivot(String name) {
        this.selectPivot = name;
    }

    public boolean hasSelectPivot() {
        return selectPivot != null && selectPivot.length() > 0;
    }

    public String getSelectPivot() {
        return selectPivot;
    }

    public boolean isInside(Vector3f local) {
        if (magnetArea != null) {
            return magnetArea.isInside(local);
        } else {
            return false;
        }
    }

    public void setRotationAxis(Vector3f rotationAxis) {
        this.rotationAxis = rotationAxis;
    }

    public Vector3f getRotationAxis() {
        return rotationAxis;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        if (rotationRange != null) {
            rotationRange.setCurrentValue(rotation);
        }
    }

    public float getRotation() {
        return rotation;
    }
    /*
     public void setPossibleRotationValues(float[] values) {
     this.possibleRotationValues = values;
     }*/

    public void setRotationRange(RotationRange range) {
        this.rotationRange = range;
    }

    public void nextRotationValue() {
        if (rotationRange == null) {
            return;
        }
        rotation = rotationRange.nextUp();
        this.additionalRotation.fromAngleAxis(rotation, this.getRotationAxis());
    }

    public void previousRotationValue() {
        if (rotationRange == null) {
            return;
        }
        rotation = rotationRange.nextDown();
        this.additionalRotation.fromAngleAxis(rotation, this.getRotationAxis());
    }

    public float calcDistance(Vector3f point, Prefab prefab) {
        Vector3f magnetLoc = getLocation();
        Vector3f worldLoc = new Vector3f();
        Vector3f offsetLoc = magnetLoc.subtract(prefab.getOffset());
        prefab.localToWorld(offsetLoc, worldLoc);
        return worldLoc.distance(point);
    }
}