/**
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package dae.prefabs.parameters;

/**
 * An indexed parameter defines an indexed property, in other words a property
 * that is backed by an arraylist.
 *
 * If the id of the parameter is property and the type of the property is Type,
 * then the following methods must be present.
 *
 * public Type getProperty(int index); 
 * public void setProperty(int index, Type key);
 * public int getNrOfTargetKeys(); 
 * public void addTargetKey(String key);
 * public void removeTargetKey(String key);
 *
 * @author Koen Samyn
 */
public class ListParameter extends Parameter {

    private Parameter baseType;

    /**
     * Creates a new IndexedParameter object.
     *
     * @param id the id of the parameter.
     * @param baseType the base type of the parameter.
     */
    public ListParameter(String id, Parameter baseType) {
        super(id, id);
        this.baseType = baseType;
    }
    
    /**
     * Returns the base type of this ListParameter.
     * @return the base type of this list parameter.
     */
    public Parameter getBaseType(){
        return baseType;
    }
}
