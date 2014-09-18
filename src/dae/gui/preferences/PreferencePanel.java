/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.preferences;

/**
 * Interface for parts of the Preference dialog.
 * @author Koen Samyn
 */
public interface PreferencePanel {
    /**
     * Create a backup of the old situation.
     */
    public void createBackup();
    /**
     * Commit the changes in the user interface.
     */
    public void commitChanges();
    /**
     * Reverts the changes in the user interface.
     */
    public void revertChanges();
}
