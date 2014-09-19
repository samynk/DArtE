package dae.prefabs.ui.events;

import dae.project.Level;
import dae.project.Project;

/**
 * An event class that indicates that the project has changed.
 *
 * @author Koen Samyn
 */
public class ProjectEvent {

    private Project project;
    private ProjectEventType eventType = ProjectEventType.SELECTED;
    private Object source;
    private Level level;

    public ProjectEvent(Project project, Object source) {
        this.project = project;
        this.source = source;
    }
    
    public ProjectEvent(Project project, ProjectEventType type, Object source){
        this.project = project;
        this.source = source;
        this.eventType = type;
    }

    public Project getProject() {
        return project;
    }
    
    public ProjectEventType getEventType(){
        return eventType;
    }

    public Object getSource() {
        return source;
    }

    /**
     * Sets the level that was added or removed (only for ProjectEventType.LEVELADDED).
     * @param newLevel 
     */
    public void setLevel(Level newLevel) {
        this.level = newLevel;
    }

    /**
     * @return the level that was added or removed (only for ProjectEventType.LEVELADDED).
     */
    public Level getLevel() {
        return level;
    }
}
