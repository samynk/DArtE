package dae.gui.renderers;


import dae.prefabs.Klatch;
import dae.project.Grid;
import dae.project.Layer;
import dae.project.Level;
import dae.project.Project;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Koen
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {
    private final Icon imgProject;
    private final Icon imgGrid;
    private final Icon imgLayer;
    private final Icon imgLevel;
    private final Icon imgBody;
    private final Icon imgPrefab;
    
     private final Icon imgJ3O; 
     private final Icon imgKlatch; 


    public ProjectTreeCellRenderer() {
        ImageLoader loader = ImageLoader.getInstance();

        imgProject =  new ImageIcon(loader.getImage("/dae/icons/house.png"));
        imgGrid = new ImageIcon(loader.getImage("/dae/icons/grid.png"));
        imgLayer = new ImageIcon(loader.getImage("/dae/icons/layer.png"));
        imgLevel = new ImageIcon(loader.getImage("/dae/icons/level.png"));
        imgBody = new ImageIcon(loader.getImage("/dae/icons/body.png"));
        imgPrefab = new ImageIcon(loader.getImage("/dae/icons/prefab.png"));
        
        imgJ3O =  new ImageIcon(loader.getImage("/dae/icons/flat/mesh2.png"));
        imgKlatch = new ImageIcon(loader.getImage("/dae/icons/klatch.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if ( value instanceof Project){
            label.setIcon(imgProject);
        }else if ( value instanceof Grid){
            label.setIcon(imgGrid);
        }else if ( value instanceof Layer){
            label.setIcon(imgLayer);
        }else if (value instanceof Level){
            label.setIcon(imgLevel);
        }else if (value instanceof Klatch){
            label.setIcon(imgKlatch);
        }else{
            label.setIcon(imgJ3O);
        }
        //String text = label.getText();
        //FontMetrics fm = label.getFontMetrics(label.getFont());
        //Dimension d = label.getPreferredSize();
        //label.setMinimumSize(new Dimension(fm.stringWidth(text),d.height));
        return label;
    }
}
