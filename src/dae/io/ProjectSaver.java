/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io;

import com.google.common.io.Files;
import dae.project.Project;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static dae.io.XMLUtils.*;
import dae.project.AssetLevel;
import dae.project.Level;

/**
 *
 * @author samyn_000
 */
public class ProjectSaver {

    public static void write(Project project, File file) throws IOException {
        // check if file has a correct file extension.
        if (!"zbk".equalsIgnoreCase(Files.getFileExtension(file.getName()))) {
            file = new File(file.getPath() + ".zbk");
            project.setProjectLocation(file);
        }
        // create a klatch folder
        if ( project.getKlatchDirectory().exists()){
            project.getKlatchDirectory().mkdirs();
        }
        
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("<?xml version='1.0'?>\n");
        bw.write("<project ");
        writeAttribute(bw, "name", project.getProjectName());
        bw.write(">\n");
        bw.write("\t<assetfolders>\n");
        for (File f : project.getAssetFolders()) {
            bw.write("\t\t<assetfolder>");
            bw.write("<file><![CDATA[");
            bw.write(f.getAbsolutePath());
            bw.write("]]></file>");
            bw.write("</assetfolder>\n");
        }
        bw.write("\t</assetfolders>\n");
        bw.write("\t<levels>\n");
        if (project.hasLevels()) {
            for (Level l : project.getLevels()) {
                writeLevel(file.getParentFile(), l);

                bw.write("\t\t<level ");
                writeAttribute(bw, "name", l.getName());
                bw.write(" ");
                if ( l instanceof AssetLevel ){
                    writeAttribute(bw, "type","klatch");
                }
                bw.write(">\n");
                bw.write("\t\t\t<file><![CDATA[");
                if (l.hasLocation()) {
                    bw.write(l.getRelativeLocation().getPath());
                }
                bw.write("]]></file>\n");
                bw.write("\t\t</level>\n");
            }
        }
        bw.write("\t</levels>\n");
        bw.write("</project>\n");

        bw.close();
    }

    private static void writeLevel(File projectDir, Level l) {
        if (l.hasLocation()) {
            if (l.hasRelativeLocation()) {
                File sceneLoc = new File(projectDir, l.getLocation().getPath());
                SceneSaver.writeScene(sceneLoc, l);
            } else {
                SceneSaver.writeScene(l.getLocation(), l);
            }
        } else {
            // levels are written to separate subfolders.
            File levelDir = new File(projectDir, "levels/" + l.getName());
            levelDir.mkdirs();

            File levelFile = new File("levels/" + l.getName() + "/" + l.getName() + ".scene");
            l.setRelativeLocation(true);
            l.setLocation(levelFile);

            SceneSaver.writeScene(new File(projectDir,levelFile.getPath()), l);
        }
    }
}
