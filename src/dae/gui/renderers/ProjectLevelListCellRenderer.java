/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.renderers;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Koen Samyn
 */
public class ProjectLevelListCellRenderer extends DefaultListCellRenderer {

    private Icon imgLevel;

    public ProjectLevelListCellRenderer() {
        ImageLoader loader = ImageLoader.getInstance();
        imgLevel = new ImageIcon(loader.getImage("/dae/icons/level.png"));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            label.setIcon(imgLevel);
        }
        return c;
    }
}
