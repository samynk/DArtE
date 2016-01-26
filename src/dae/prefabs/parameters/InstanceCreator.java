package dae.prefabs.parameters;

/**
 *
 * @author Koen Samyn
 */
public interface InstanceCreator {
    /**
     * Creates an instance of an object in the scenegraph.
     * @return the created object.
     */
    public Object createInstance();
}
