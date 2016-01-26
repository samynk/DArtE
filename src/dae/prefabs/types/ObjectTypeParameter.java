package dae.prefabs.types;

/**
 *
 * @author Koen Samyn
 */
public class ObjectTypeParameter {
    private String id;
    private String value;
    
    public ObjectTypeParameter(String id, String value){
        this.id = id;
        this.value = value;
    }
    
    public String getId(){
        return id;
    }
    
    public String getValue(){
        return value;
    }
}
