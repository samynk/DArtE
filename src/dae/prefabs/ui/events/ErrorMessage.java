/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.prefabs.ui.events;

/**
 *
 * @author Koen Samyn
 */
public class ErrorMessage {
    private String text;
    
    public ErrorMessage(String text){
        this.text = text;
    }
    
    public String getText(){
        return text;
    }
}
