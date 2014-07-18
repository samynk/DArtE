/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

/**
 *
 * @author Koen
 */
public interface ChannelSupport {

    public void setXYZIndexInChannel(int xindex, int yindex, int zindex);

    public void setRXRYRZIndexInChannel(int rxindex, int ryindex, int rzindex);

    public void setXIndexInChannel(int xindex);

    public void setYIndexInChannel(int yindex);

    public void setZIndexInChannel(int zindex);

    public void setRXIndexInChannel(int rxindex);

    public void setRYIndexInChannel(int ryindex);

    public void setRZIndexInChannel(int rzindex);
}
