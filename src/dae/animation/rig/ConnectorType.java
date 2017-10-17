package dae.animation.rig;

/**
 * Describes the connector type (a name and a description) and also allows the
 * specify a panel that can configure the connector.
 *
 * @author Koen Samyn
 */
public class ConnectorType {
    /**
     * A unique id for this connector type.
     */
    private String id;
    /**
     * The descriptive name for this connector type.
     */
    private String name;
    /**
     * The description for the connector type.
     */
    private String description;
    /**
     * The customizer panel for the connector type.
     */
    private String customizerPanelClass;
    
    /**
     * Creates a new InputConnectorType object for use in the connector customizer.
     * @param id The unique id for this connector type.
     * @param name The descriptive name for this connector type.
     * @param description The description for the connector type.
     * @param customizerPanelClass The customizer panel for the connector type.
     */
    public ConnectorType(String id, String name, String description, String customizerPanelClass)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.customizerPanelClass = customizerPanelClass;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getCustomizerPanelClass(){
        return customizerPanelClass;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
