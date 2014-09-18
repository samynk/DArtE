/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.preferences;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a game key. A game key has 3 components.
 * javaKeyCode : the keycode as defined in the java event system
 * javaExtKeyCode : the extended keycode for presentation purposes.
 * jmeKeyCode : the keycode as defined in the JMonkeyEngine event system.
 * @author Koen Samyn
 */
public class GameKeyDefinition {
    private int javaKeyCode;
    private int javaExtKeyCode;
    private int jmeKeyCode;
    
    /**
     * Parses a GameKeyDefinition from a string defined as [javaKeyCode,javaExtKeyCode,jmeKeyCode]
     * @param keycodes the string that contains the definition.
     */
    public GameKeyDefinition(String keycodes){
        if (keycodes.length() > 0) {
            String withoutBrackets = keycodes.substring(1, keycodes.length() - 1);
            String[] cs = withoutBrackets.split(",");
            if (cs.length == 3) {
                try {
                    javaKeyCode = Integer.parseInt(cs[0]);
                    javaExtKeyCode = Integer.parseInt(cs[1]);
                    jmeKeyCode = Integer.parseInt(cs[2]);
                } catch (NumberFormatException ex) {
                    Logger.getLogger("DArtE").log(Level.SEVERE, "Game key definition {0} could not be parsed!", keycodes);
                }
            } else {
                Logger.getLogger("DArtE").log(Level.SEVERE, "Wrong number of components in {0}. Format is [javaKeyCode,javaExtKeyCode,jmeKeyCode].", keycodes);
            }
        } else {
            Logger.getLogger("DArtE").log(Level.WARNING, "Game key definition is empty string!");
        }
    }

    /**
     * Creates a new GameKeyDefinition object based on the three parameters.
     * @param keyCode the java key code.
     * @param extendedKeyCode the extended java key code.
     * @param jmeKeyCode the JMonkeyEngine keycode.
     */
    public GameKeyDefinition(int keyCode, int extendedKeyCode, int jmeKeyCode) {
        this.javaKeyCode = keyCode;
        this.javaExtKeyCode = extendedKeyCode;
        this.jmeKeyCode = jmeKeyCode;
    }
    
    /**
     * Returns the java key code.
     * @return the java key code.
     */
    public int getJavaKeyCode(){
        return javaKeyCode;
    }
    
    /**
     * Returns the java extended key code.
     * @return the java extended key code.
     */
    public int getJavaExtKeyCode(){
        return javaExtKeyCode;
    }
    
    /**
     * Returns the JMonkeyEngine key code.
     * @return the JMonkeyEngine key code.
     */
    public int getJmeKeyCode(){
        return jmeKeyCode;
    }
    
    /**
     * Returns the string representation of this GameKeyCode.
     * @return this GameKeyCode formatted as [javaKeyCode,javaExtKeyCode,jmeKeyCode]
     */
    @Override
    public String toString(){
        return MessageFormat.format("[{0},{1},{2}]", javaKeyCode,javaExtKeyCode,jmeKeyCode);
    }
}
