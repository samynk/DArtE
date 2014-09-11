/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.animation.skeleton;

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
public class BodyWriter {

     /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param body the body to write to file.
     */
    public static void writeScene(File location, Body body) {
     FileWriter fw;
        BufferedWriter bw = null;
        try {
            if (!location.getParentFile().exists()) {
                location.getParentFile().mkdirs();
            }
            fw = new FileWriter(location);

            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<body>\n");
            bw.write("</body>\n");
        }catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
    }
}
