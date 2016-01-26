package dae.prefabs.ui.events;

/**
 *
 * @author Koen Samyn
 */
public class PlayEvent {
    private PlayEventType type;
    
    public PlayEvent(PlayEventType type)
    {
        this.type = type;
    }
    
    public PlayEventType getType(){
        return type;
    }
}
