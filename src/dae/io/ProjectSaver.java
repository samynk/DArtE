package dae.io;

import com.google.common.io.Files;
import dae.project.Project;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static dae.io.XMLUtils.*;
import dae.io.export.OVSceneExporter;
import dae.io.writers.Exporter;
import dae.project.AssetLevel;
import dae.project.Level;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class ProjectSaver {

    private final static HashMap<String, Exporter> registeredExporters =
            new HashMap<String, Exporter>();

    static {
        registeredExporters.put("j3o", new SceneToJ3OExporter());
        registeredExporters.put("ov", new OVSceneExporter());
    }

    public static void write(Project project, File file) throws IOException {
        // check if file has a correct file extension.
        if (!"zbk".equalsIgnoreCase(Files.getFileExtension(file.getName()))) {
            file = new File(file.getPath() + ".zbk");
            project.setProjectLocation(file);
        }
        // create a klatch folder
        if (project.getKlatchDirectory().exists()) {
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
                if (l instanceof AssetLevel) {
                    writeAttribute(bw, "type", "klatch");
                }
                if (l.hasLocation()) {
                    writeAttribute(bw, "relativelocation", l.hasRelativeLocation());
                } else {
                    writeAttribute(bw, "relativelocation", true);
                }
                bw.write(">\n");
                bw.write("\t\t\t<file><![CDATA[");
                if (!l.hasLocation()) {
                    File levelFile = new File("levels/" + l.getName() + "/" + l.getName() + ".scene");
                    l.setRelativeLocation(true);
                    l.setLocation(levelFile);
                }
                if ( l instanceof AssetLevel ){
                    bw.write(l.getRelativeLocation().getPath());
                }else{
                    bw.write(l.getLocation().getPath());
                }

                bw.write("]]></file>\n");
                writeExportSettings(bw, l);
                bw.write("\t\t</level>\n");
            }
        }
        bw.write("\t</levels>\n");
        bw.write("</project>\n");

        bw.close();
    }

    private static void writeExportSettings(Writer w, Level l) {
        if (l.hasExportKeys()) {
            try {
                w.write("\t\t\t<exportsettings ");
                writeAttribute(w, "exportonsave", l.isExportOnSave());
                w.write(">\n");
                for (String key : l.getExportKeys()) {
                    File loc = l.getExportLocation(key);
                    w.write("\t\t\t\t<exportfile ");
                    writeAttribute(w, "key", key);
                    w.write("><![CDATA[");
                    w.write(loc.getPath());
                    w.write("]]></exportfile>\n");
                }
                w.write("\t\t\t</exportsettings>\n");
            } catch (IOException ex) {
                Logger.getLogger(ProjectSaver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    private static void writeLevel(File projectDir, Level l) {
        if (!l.isChanged()) {
            return;
        }
        if (l.hasLocation()) {
            if (l.hasRelativeLocation()) {
                File sceneLoc = new File(projectDir, l.getLocation().getPath());
                l.save(sceneLoc);
            } else {
                l.save(l.getLocation());
            }
        } else {
            // levels are written to separate subfolders.
            File levelDir = new File(projectDir, "levels/" + l.getName());
            levelDir.mkdirs();

            File levelFile = new File("levels/" + l.getName() + "/" + l.getName() + ".scene");
            l.setRelativeLocation(true);
            l.setLocation(levelFile);
            try {
                l.save(new File(projectDir, levelFile.getPath()));
            } catch (Exception ex) {
                Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, "Could not write level " + l.getName(), ex);
            }
        }

        if (l.isExportOnSave() && l.hasExportKeys()) {
            for (String key : l.getExportKeys()) {
                Exporter e = registeredExporters.get(key);
                File location = l.getExportLocation(key);
                if (e != null && location != null) {

                    e.writeScene(location, l.getAssetManager(), l);
                }
            }
        }
    }
}
