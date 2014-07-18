/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.magnets;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class Quad {

    private float maxLength;
    private Vector3f connectorLocation;
    private Vector3f connectorDir;
    private Vector3f p1;
    private Vector3f p2;
    private Vector3f p3;
    private Vector3f p4;
    private Vector3f p1_dir1;
    private Vector3f p1_dir2;
    private Vector3f p2_dir1;
    private Vector3f p2_dir2;
    private Vector3f p3_dir1;
    private Vector3f p3_dir2;
    private Vector3f p4_dir1;
    private Vector3f p4_dir2;
    private String name;
    private boolean cw = false;
    /**
     * The triangles of the original mesh that are connected to this quad
     * object.
     */
    private ArrayList<Integer> indices = new ArrayList<Integer>();

    public Quad() {
    }

    public void setMaxLength(float maxLength) {
        this.maxLength = maxLength;
    }

    public float getMaxLength() {
        return maxLength;
    }

    public void setP1Data(Vector3f loc, Vector3f dir1, Vector3f dir2) {
        this.p1 = loc;
        this.p1_dir1 = dir1;
        this.p1_dir2 = dir2;
    }

    public void setP2Data(Vector3f loc, Vector3f dir1, Vector3f dir2) {
        this.p2 = loc;
        this.p2_dir1 = dir1;
        this.p2_dir2 = dir2;
    }

    public void setP3Data(Vector3f loc, Vector3f dir1, Vector3f dir2) {
        this.p3 = loc;
        this.p3_dir1 = dir1;
        this.p3_dir2 = dir2;
    }

    public void setP4Data(Vector3f loc, Vector3f dir1, Vector3f dir2) {
        this.p4 = loc;
        this.p4_dir1 = dir1;
        this.p4_dir2 = dir2;
    }

    public void setPData(int index, Vector3f loc, Vector3f dir1, Vector3f dir2) {
        switch (index) {
            case 0:
                setP1Data(loc, dir1, dir2);
                break;
            case 1:
                setP2Data(loc, dir1, dir2);
                break;
            case 2:
                setP3Data(loc, dir1, dir2);
                break;
            case 3:
                setP4Data(loc, dir1, dir2);
                break;
        }
    }

    public void setConnectorLocation(Vector3f connectorLoc) {
        this.connectorLocation = connectorLoc;
    }

    public Vector3f getConnectorLocation() {
        return connectorLocation;
    }

    public void setConnectorDirection(Vector3f dir) {
        this.connectorDir = dir;
    }

    public Vector3f getConnectorDirection() {
        return this.connectorDir;
    }

    public void addTriangleIndex(int index) {
        this.indices.add(index);
    }

    public Iterable<Integer> indices() {
        return indices;
    }

    public Vector3f getP1() {
        return this.p1;
    }

    public Vector3f getP2() {
        return this.p2;
    }

    public Vector3f getP3() {
        return this.p3;
    }

    public Vector3f getP4() {
        return this.p4;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClockWise() {
        this.cw = true;
    }

    public boolean isClockWise() {
        return cw;
    }

    public void setCounterClockWise() {
        this.cw = false;
    }
}
