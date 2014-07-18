/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.lights;

import com.jme3.shadow.AbstractShadowRenderer;

/**
 *
 * @author samyn_000
 */
public interface ShadowCastSupport {
    /**
     * Checks if this direction light casts a shadow or not.
     * @return true if this light casts a shadow, false otherwise.
     */
    public boolean getCastShadow() ;

    /**
     * Sets the shadow casting property of this light.
     * @param castShadow true if this light casts a shadow, false otherwise.
     */
    public void setCastShadow(boolean castShadow) ;
    
    /**
     * Returns the shadow renderer that will be used to cast shadows.
     * @return the AbstractShadowRenderer object.
     */
    public AbstractShadowRenderer getShadowRenderer();
}
