package dae.io.game;

import dae.project.Level;
import dae.project.Project;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This class writes the configuration file for the current level to a
 * subfolder of this project. The configuration file will the be used
 * to start the game.
 * @author Koen Samyn
 */
public class GameWriter {
    
    /**
     * Writes the configuration for the game to a configuration file.
     * @param project
     * @param currentLevel 
     */
    public static void writeGame(Project project, Level currentLevel) throws IOException
    {
        if ( !project.hasFileLocation())
            return;
        File projectDir = project.getProjectLocation().getParentFile();
        File configFile =new File(projectDir,"game/boot.cfg");
        configFile.getParentFile().mkdirs();
        Writer w = new BufferedWriter(new FileWriter(configFile));
        
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        w.write("<config>\n");
        
        // write asset folder.
        for ( File assetFolder: project.getAssetFolders())
        {
            w.write("\t<assetfolder><![CDATA[");
            w.write(assetFolder.getPath());
            w.write("]]></assetfolder>\n");
        }
        
        w.write("</config>");
        w.close();
    }
}
