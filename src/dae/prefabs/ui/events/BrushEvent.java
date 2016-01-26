package dae.prefabs.ui.events;

import dae.prefabs.brush.Brush;

/**
 * 
 * @author Koen Samyn
 */
public class BrushEvent {
    private Brush brush;
    private BrushEventType type;
    
    public BrushEvent(BrushEventType type, Brush brush){
        this.brush = brush;
        this.type = type;
    }
    
    public Brush getBrush(){
        return brush;
    }
    
    public BrushEventType getType(){
        return type;
    }
}
