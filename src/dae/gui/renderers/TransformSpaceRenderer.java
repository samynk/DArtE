package dae.gui.renderers;

import dae.prefabs.gizmos.RotateGizmoSpace;
import dae.prefabs.gizmos.TranslateGizmoSpace;
import static dae.prefabs.gizmos.TranslateGizmoSpace.LOCAL;
import static dae.prefabs.gizmos.TranslateGizmoSpace.PARENT;
import static dae.prefabs.gizmos.TranslateGizmoSpace.WORLD;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Koen Samyn
 */
public class TransformSpaceRenderer extends JLabel
        implements ListCellRenderer {
    
    private Icon imgWorld;
    private Icon imgParent;
    private Icon imgLocal;

    public TransformSpaceRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        

        ImageLoader loader = ImageLoader.getInstance();

        imgWorld = new ImageIcon(loader.getImage("/dae/icons/world.png"));
        imgParent = new ImageIcon(loader.getImage("/dae/icons/parent.png"));
        imgLocal = new ImageIcon(loader.getImage("/dae/icons/local.png"));
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        //Set the icon and text.  If icon was null, say so.
        if (value instanceof TranslateGizmoSpace) {
            TranslateGizmoSpace space = (TranslateGizmoSpace) value;
            switch (space) {
                case WORLD:
                    setIcon(imgWorld);break;
                case PARENT:
                    setIcon(imgParent);break;
                case LOCAL:
                    setIcon(imgLocal);break;
            }
        }else if (value instanceof RotateGizmoSpace){
            RotateGizmoSpace space = (RotateGizmoSpace) value;
            switch (space) {
                case WORLD:
                    setIcon(imgWorld);break;
                case PARENT:
                    setIcon(imgParent);break;
                case LOCAL:
                    setIcon(imgLocal);break;
            }
        }
        return this;
    }
}