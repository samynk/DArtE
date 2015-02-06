package dae.prefabs.parameters;

import dae.components.ComponentType;

/**
 * The file parameter allows the user to select a file from the asset system
 * and set it as a property of a component.
 * @author Koen Samyn
 */
public class FileParameter extends Parameter {
    private String extension;
    /* Creates a new ObjectParameter object.
     *
     * @param componentType componentType the component type of the parameter.
     * @param type the type of the parameter
     * @param id the id of the parameter.
     *
     */
    public FileParameter(ComponentType componentType, String type, String id) {
        super(componentType, type, id);
    }

    /**
     * Set the etension to load.
     * @param extension the extension to load.
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    /**
     * Gets the extension to load.
     * @return the extension to load.
     */
    public String getExtension(){
        return extension;
    }
}
