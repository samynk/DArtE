/*
 * Digital Arts and Entertainment 
 */
package dae.animation.rig.timing.gui;

import dae.animation.rig.timing.Behaviour;
import java.awt.event.MouseEvent;

/**
 *
 * @author Koen.Samyn
 */
public class SelectTool implements TimingTool{
    private final FramePanel parent;
    
    public SelectTool(FramePanel parent){
        this.parent = parent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
        Behaviour current = parent.getBehaviour();
        if ( current != null ){
            current.setCurrentFrame(parent.mouseXToFrame(e.getX()));
            int timeLineIndex = parent.mouseYToTimeLine(e.getY());
            parent.setCurrentTimeLine(timeLineIndex);
            parent.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
}
