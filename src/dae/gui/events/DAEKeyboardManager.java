/*
 * Digital Arts and Entertainment 
 */
package dae.gui.events;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

/**
 *
 * @author Koen.Samyn
 */
public class DAEKeyboardManager implements KeyEventDispatcher {

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if ((e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
            if (e.getKeyCode() == KeyEvent.VK_F1) {
                System.out.println("Control F1 pressed     ");
                return true;
            }
        }
        return false;
    }

}
