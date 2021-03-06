package dae.gui.components;

import com.jme3.texture.Image.Format;
import com.jme3.texture.plugins.DDSLoader;
import dae.prefabs.ui.classpath.FileNode;
import dae.project.Project;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author Koen Samyn
 */
public class ImageInfo extends javax.swing.JPanel {
    
    private FileNode fileNode;
    private Project currentProject;
    
    private DDSLoader loader;
    
    /** Creates new form ImageInfo */
    public ImageInfo() {
        initComponents();
        loader = new DDSLoader();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nonCollapsibleHeader1 = new dae.gui.components.NonCollapsibleHeader();
        imagePreview1 = new dae.gui.components.ImagePreview();

        setLayout(new java.awt.GridBagLayout());

        nonCollapsibleHeader1.setTitle("Image");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(nonCollapsibleHeader1, gridBagConstraints);

        javax.swing.GroupLayout imagePreview1Layout = new javax.swing.GroupLayout(imagePreview1);
        imagePreview1.setLayout(imagePreview1Layout);
        imagePreview1Layout.setHorizontalGroup(
            imagePreview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );
        imagePreview1Layout.setVerticalGroup(
            imagePreview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(imagePreview1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    public void setFileNode(FileNode fileNode) throws IOException{
        InputStream is = currentProject.getAssetLoader().getResourceAsStream(fileNode.getFullName());
        if ( fileNode.getExtension().equals("dds"))
        {
            
            
            try {
                com.jme3.texture.Image image = loader.load(is);
                int[] mipmapSize = image.getMipMapSizes();
                
                for ( int i = 0 ; i < mipmapSize.length; ++i){
                    System.out.println("Width and height : " + image.getWidth() + "," + image.getHeight());
                    if ( mipmapSize[i] >= 128*128){
                        ByteBuffer data = image.getData(i);
                        int totalPixels = mipmapSize[i];
                        int mipmapWidth = totalPixels* image.getWidth() / image.getHeight();
                        int mipmapHeight = totalPixels/mipmapWidth;
                        
                        Format format = image.getFormat();
                        
                        
                        //BufferedImage bi = ImageToAwt.convert(image,false,false,i);
                        //imagePreview1.setImage(bi);
                        //imagePreview1.repaint();
                    }
                }
                
                
                
            } catch (IOException ex) {
                Logger.getLogger(ImageInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if (fileNode.getExtension().equals("png") || fileNode.getExtension().equals("jpg")){
            Image image = ImageIO.read(is);
            imagePreview1.setImage(image);
            imagePreview1.repaint();
        }
    }

    public void setProject(Project project){
        currentProject = project;
    }
    
    public Project getProject(){
        return currentProject;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dae.gui.components.ImagePreview imagePreview1;
    private dae.gui.components.NonCollapsibleHeader nonCollapsibleHeader1;
    // End of variables declaration//GEN-END:variables

}
