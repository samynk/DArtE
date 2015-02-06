package dae.prefabs.magnets;

import dae.components.ComponentType;
import dae.prefabs.parameters.Parameter;
import java.util.ArrayList;

/**
 *
 * @author Koen
 */
public class FillerParameter extends Parameter {

    private ArrayList<Quad> quads = new ArrayList<Quad>();
    private Quad[] triangleToQuad = new Quad[10];

    /**
     * Creates a new filler parameter.
     *
     * @param componentType the component type of the action parameter.
     * @param type the type of the parameter (string, int, float, color, ...)
     * @param id the id of the property of the prefab or component.
     */
    public FillerParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
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
