/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.rig.io;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.rig.Rig;
import dae.animation.skeleton.BodyElement;
import dae.io.SceneSaver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class RigWriter {

     /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param body the body to write to file.
     */
    public static void writeRig(File location, Rig rig) {
     FileWriter fw;
        BufferedWriter bw = null;
        try {
            if (!location.getParentFile().exists()) {
                location.getParentFile().mkdirs();
            }
            fw = new FileWriter(location);

            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<rig>\n");
            int depth = 0;
            for ( Spatial child : rig.getChildren())
            {
                if ( child instanceof BodyElement ){
                    BodyElement be = (BodyElement)child;
                    be.write(bw, depth+1);
                }
            }
            bw.write("</rig>\n");
        }catch (IOException ex) {
            Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
