/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import java.io.File;

/**
 *
 * @author Koen
 */
public class FileEvent {

    private FileEventType type;
    private File file;
    private String id;

    public FileEvent(FileEventType type) {
        this.type = type;
    }

    public FileEvent(FileEventType type, String id) {
        this.type = type;
        this.id = id;
    }

    public FileEvent(FileEventType type, File file) {
        this.type = type;
        this.file = file;
    }

    public FileEventType getType() {
        return type;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getId() {
        return id;
    }
}
