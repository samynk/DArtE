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
public class FillerParameter extends Parameter {

    private ArrayList<Quad> quads = new ArrayList<Quad>();
    private Quad[] triangleToQuad = new Quad[10];

    public FillerParameter(String label, String id) {
        super(label, id);
    }

    public void addQuad(Quad q) {
        quads.add(q);
        for (Integer index : q.indices()) {
            if (index < triangleToQuad.length) {
                triangleToQuad[index] = q;
            } else {
                Quad[] temp = new Quad[index + 1];
                System.arraycopy(triangleToQuad, 0, temp, 0, triangleToQuad.length);
                triangleToQuad = temp;
                triangleToQuad[index] = q;
            }
        }
    }

    public Iterable<Quad> iterable() {
        return quads;
    }

    public Quad getQuad(int triangleIndex) {
        try {
            return triangleToQuad[triangleIndex];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
}
