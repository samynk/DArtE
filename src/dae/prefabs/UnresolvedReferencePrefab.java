package dae.prefabs;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.prefabs.parameters.Parameter;

/**
 * An unresolved reference is a place holder for references that cannot yet be
 * found.
 *
 * @author Koen Samyn
 */
public class UnresolvedReferencePrefab extends Prefab {

    private Object referenceParent;
    private Parameter referenceParameter;
    private String reference;

    /**
     * Sets the unresolved reference.
     *
     * @param reference the unresolved reference.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the unresolved reference.
     *
     * @return the unresolved reference.
     */
    public String getReference() {
        return reference;
    }

    public void setReference(Object parent, Parameter p, String id) {
        this.referenceParent = parent;
        this.referenceParameter = p;
        this.reference = id;
    }

    public void resolveReference(Node scene) {
        Spatial referencedNode = scene.getChild(reference);
        if (referencedNode != null) {
            referenceParameter.invokeSet(referenceParent, referencedNode);
        }else{
            // set the reference to null.
            referenceParameter.invokeSet(referenceParent, null);
        }
    }
}
