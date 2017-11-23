package dae.gui.renderers;


import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;

/**
 *
 * @author Koen
 */
public class ProjectTreeCellEditor extends DefaultTreeCellEditor{
    private final Icon imgProject;
    private final Icon imgGrid;
    private final Icon imgLayer;
    private final Icon imgLevel;
    private final Icon imgBody;
    private final Icon imgPrefab;


    public ProjectTreeCellEditor(JTree tree, ProjectTreeCellRenderer renderer) {
        super(tree,renderer);
        ImageLoader loader = ImageLoader.getInstance();

        imgProject =  new ImageIcon(loader.getImage("/dae/icons/house.png"));
        imgGrid = new ImageIcon(loader.getImage("/dae/icons/grid.png"));
        imgLayer = new ImageIcon(loader.getImage("/dae/icons/layer.png"));
        imgLevel = new ImageIcon(loader.getImage("/dae/icons/level.png"));
        imgBody = new ImageIcon(loader.getImage("/dae/icons/body.png"));
        imgPrefab = new ImageIcon(loader.getImage("/dae/icons/prefab.png"));
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        Component c = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
//        if ( value instanceof Project){
//            label.setIcon(imgProject);
//        }else if ( value instanceof Grid){
//            label.setIcon(imgGrid);
//        }else if ( value instanceof Layer){
//            label.setIcon(imgLayer);
//        }else if (value instanceof Level){
//            label.setIcon(imgLevel);
//        }else if (value instanceof Body){
//            label.setIcon(imgBody);
//        }else {
//            label.setIcon(imgPrefab);
//        }
        return c;//To change body of generated methods, choose Tools | Templates.
    }
    
}
