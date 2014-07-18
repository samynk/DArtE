/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

/**
 *
 * @author Koen Samyn
 */
public class ViewportReshapeEvent {
    private int width, height;
    
    public ViewportReshapeEvent(int width, int height){
        this.width = width;
        this.height = height;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
}
