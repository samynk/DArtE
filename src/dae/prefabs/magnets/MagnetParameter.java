/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.magnets;

import dae.prefabs.parameters.Parameter;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class MagnetParameter extends Parameter {

    private ArrayList<Magnet> magnets = new ArrayList<Magnet>();
    private Magnet pivotMagnet;

    public MagnetParameter(String label, String id) {
        super(label, id);
    }

    public void addMagnet(Magnet magnet) {
        // if no pivot magnet is defined, the first magnet is taken.
        if (magnets.isEmpty() || magnet.isPivotMagnet()) {
            pivotMagnet = magnet;
        }
        magnets.add(magnet);
    }

    public void removeMagnet(Magnet magnet) {
        magnets.remove(magnet);
    }

    public boolean hasMagnets() {
        return magnets.size() > 0;
    }

    public Iterable<Magnet> iterate() {
        return magnets;
    }

    public Magnet getPivotMagnet() {
        return pivotMagnet;
    }

    public void setActivePivotMagnet(Magnet magnet) {
        pivotMagnet = magnet;
    }

    public Magnet cyclePivotMagnet() {
        int indexOfCurrent = this.magnets.indexOf(pivotMagnet);
        if (indexOfCurrent > -1) {
            // search for next pivot magnet.
            for (int i = 1; i < magnets.size(); ++i) {
                int index = (indexOfCurrent + i) % magnets.size();
                Magnet m = magnets.get(index);
                if (m.getType().contains("pivot")) {
                    pivotMagnet = m;
                }
            }
        }
        return pivotMagnet;
    }

    public Magnet selectPivot(String pivotName) {
        for (Magnet m : magnets) {
            if (m.getName().equals(pivotName)) {
                pivotMagnet = m;
                return m;
            }
        }
        return pivotMagnet;
    }

    public Magnet getMagnet(String attachMagnet) {
        for (Magnet m : magnets) {
            if (m.getName().equals(attachMagnet)) {
                return m;
            }
        }
        return null;
    }
}
