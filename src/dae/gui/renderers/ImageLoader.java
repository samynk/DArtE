/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.renderers;

import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;

public class ImageLoader {
    private MediaTracker tracker = new MediaTracker(new Label());
    private static ImageLoader instance = new ImageLoader();
    private int count=0;
    
    public static ImageLoader getInstance(){
        return instance;
    }
    
    public Image getImage(String resource){
        try{
            //System.out.println("Loading : " + resource);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(getClass().getResource(resource));
            tracker.addImage(image,count++);
            try{
                tracker.waitForID(count-1);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
             //System.out.println("After : " + resource);
             return image;
        }catch(Exception ex){
            //System.out.println("Could not find : " + resource);
        }
        return null;
    }
}
