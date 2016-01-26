package dae.components;

import com.jme3.scene.Spatial;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen Samyn
 */
public class ObjectComponent extends PrefabComponent {

    private Prefab parent;
    private String layer = "default";
    private String name;
    
    public ObjectComponent(){
        super.setId("ObjectComponent");
    }

    @Override
    public void install(Prefab parent) {
        this.parent = parent;
    }

    @Override
    public void deinstall() {
        
    }

    @Override
    public void installGameComponent(Spatial parent) {
        parent.setName(name);
    }

    /**
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
        if ( parent != null)
        {
            parent.setName(name);
        }
    }
}
