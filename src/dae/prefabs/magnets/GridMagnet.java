/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.magnets;

import com.jme3.math.Vector3f;
import dae.GlobalObjects;
import dae.prefabs.AxisEnum;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class GridMagnet extends Magnet {

    private Vector3f grid;

    public GridMagnet() {
        grid = GlobalObjects.getInstance().getGrid();
        setRotationRange(GlobalObjects.getInstance().getDefaultRotationRange());
    }

    public GridMagnet(float gridx, float gridy, float gridz) {
        grid = new Vector3f(gridx, gridy, gridz);
        setRotationRange(GlobalObjects.getInstance().getDefaultRotationRange());
    }

    /**
     * This method will always return true.
     *
     * @param location the location of the click point.
     * @return
     */
    @Override
    public boolean isInside(Vector3f local) {
        float xpos = Math.round(local.x / grid.x) * grid.x;
        float ypos = Math.round(local.y / grid.x) * grid.x;
        float zpos = Math.round(local.z / grid.x) * grid.x;

        setLocation(xpos, ypos, zpos);
        return true;
    }

    @Override
    public float calcDistance(Vector3f point, Prefab prefab) {
        Vector3f local = new Vector3f();
        local.addLocal(prefab.getOffset());
        prefab.worldToLocal(point, local);

        float xpos = Math.round(local.x / grid.x) * grid.x;
        float ypos = Math.round(local.y / grid.x) * grid.x;
        float zpos = Math.round(local.z / grid.x) * grid.x;

        setLocation(xpos, ypos, zpos);
        return getLocation().distance(local);
    }

    @Override
    public Vector3f getRotationAxis() {
        AxisEnum upAxis = GlobalObjects.getInstance().getUpAxis();
        if (upAxis == AxisEnum.Y) {
            return Vector3f.UNIT_Y;
        } else if (upAxis == AxisEnum.Z) {
            return Vector3f.UNIT_Z;
        } else {
            return Vector3f.UNIT_X;
        }
    }
}