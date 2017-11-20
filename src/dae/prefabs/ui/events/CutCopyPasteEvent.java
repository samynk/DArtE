/*
 * Digital Arts and Entertainment 
 */
package dae.prefabs.ui.events;

/**
 *
 * @author Koen.Samyn
 */
public class CutCopyPasteEvent {
    private final CutCopyPasteType type;
    
    public CutCopyPasteEvent(CutCopyPasteType type){
        this.type = type;
    }
    
    public CutCopyPasteType getType(){
        return type;
    }
}
