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
public class ProjectAssetFolderListCellRenderer extends DefaultListCellRenderer{
 private Icon imgAssetFolder;

    public ProjectAssetFolderListCellRenderer() {
        ImageLoader loader = ImageLoader.getInstance();
        imgAssetFolder = new ImageIcon(loader.getImage("/dae/icons/folder.png"));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            label.setIcon(imgAssetFolder);
        }
        return c;
    }
}
