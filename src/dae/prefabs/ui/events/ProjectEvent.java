/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

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
}
