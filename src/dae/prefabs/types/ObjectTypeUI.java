/*
 * Digital Arts and Entertainment 
 */
package dae.prefabs.types;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class describes a customized UI for a prefab.
 *
 * @author Koen.Samyn
 */
public class ObjectTypeUI {

    private final String uiClass;
    private final String location;
    private final String label;
    private final boolean replace;
    private final boolean remove;
    
    private ObjectTypePanel panel;

    public ObjectTypeUI(String uiClass, String loc, String label, boolean replace, boolean remove) {
        this.uiClass = uiClass;
        this.location = loc;
        this.label = label;
        this.replace = replace;
        this.remove = remove;
    }
    
    public ObjectTypePanel getPanel(){
        if ( panel == null ){
            try {
                Class uiClazz = Class.forName(uiClass);
                if ( ObjectTypePanel.class.isAssignableFrom(uiClazz)){
                    panel = (ObjectTypePanel)uiClazz.newInstance();
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ObjectTypeUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return panel;
    }

    /**
     * @return the uiClass
     */
    public String getUiClass() {
        return uiClass;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the replace
     */
    public boolean isReplace() {
        return replace;
    }

    /**
     * @return the remove
     */
    public boolean isRemove() {
        return remove;
    }
}
