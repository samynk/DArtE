/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.renderers;


import dae.prefabs.ui.classpath.FileNode;
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
public class AssetTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon imgJ3O;
    private Icon imgKlatch;
    private Icon imgRig;
    private Icon imgSound;
    private Icon imgAnimationSet;
    


    public AssetTreeCellRenderer() {
        ImageLoader loader = ImageLoader.getInstance();

        imgJ3O =  new ImageIcon(loader.getImage("/dae/icons/mesh.png"));
        imgKlatch = new ImageIcon(loader.getImage("/dae/icons/klatch.png"));
        imgRig = new ImageIcon(loader.getImage("/dae/icons/body.png"));
        imgSound = new ImageIcon(loader.getImage("/dae/icons/sound.png"));
        imgAnimationSet = new ImageIcon(loader.getImage("/dae/icons/animationset.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if ( value instanceof FileNode){
            FileNode node = (FileNode)value;
            if (node.isFile()){
                if ( node.getExtension().equals("j3o")){
                    label.setIcon(imgJ3O);
                }else if ( node.getExtension().equals("klatch")){
                    label.setIcon(imgKlatch);
                }else if (node.getExtension().equals("rig")){
                    label.setIcon(imgRig);
                }else if (node.getExtension().equals("ogg") || node.getExtension().equals("wav")){
                    label.setIcon(imgSound);
                }else if (node.getExtension().equals("animset")){
                    label.setIcon(imgAnimationSet);
                }
            }
        }
        return label;
    }
}
