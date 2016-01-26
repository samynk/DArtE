package dae.io.game;

import dae.io.SceneToJ3OExporter;
import dae.project.Project;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts up a vm and listens to the events that stop the application.
 * @author Koen Samyn
 */
public class GameBuilder {

    public GameBuilder(){
        
    }
    
    public void startGame(){
        ProcessBuilder builder = new ProcessBuilder();

        String classPath = System.getProperty("java.class.path");
        builder.command("java", "-classpath",classPath, "org.dae.game.DAEGame");
        builder.directory(new File(System.getProperty("user.dir")));
        builder.redirectOutput(ProcessBuilder.Redirect.to(new File("d:/game_output.log")));
        builder.redirectError(ProcessBuilder.Redirect.to(new File("d:/game_error_output.log")));
        try {
            builder.start();
        } catch (IOException ex) {
            Logger.getLogger(GameBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startGame(Project project, dae.project.Level level) {
        ProcessBuilder builder = new ProcessBuilder();

        String classPath = System.getProperty("java.class.path");
        
        StringBuilder assetFolderPath = new StringBuilder();
        assetFolderPath.append("\"");
        for ( File f: project.getAssetFolders()){
            assetFolderPath.append(f.getPath());
            assetFolderPath.append(";");
        }
        File projectFile = project.getProjectLocation();
        File projectDir = projectFile.getParentFile();
        File gameAssetsDir = new File(projectDir, "game/assets");
        gameAssetsDir.mkdirs();
        
        assetFolderPath.append(gameAssetsDir.getPath());
        assetFolderPath.append("\"");
        
        SceneToJ3OExporter exporter = new SceneToJ3OExporter();
        
        String sceneMesh = level.getName()+".j3o";
        exporter.writeScene(new  File(gameAssetsDir,sceneMesh), level.getAssetManager(), level);
        
        builder.command("java", "-classpath",classPath, "org.dae.game.DAEGame", assetFolderPath.toString(), sceneMesh);
        builder.directory(new File(System.getProperty("user.dir")));
        builder.redirectOutput(ProcessBuilder.Redirect.to(new File("d:/game_output.log")));
        builder.redirectError(ProcessBuilder.Redirect.to(new File("d:/game_error_output.log")));
        try {
            builder.start();
        } catch (IOException ex) {
            Logger.getLogger(GameBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
