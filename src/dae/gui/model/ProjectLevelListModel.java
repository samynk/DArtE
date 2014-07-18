/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.model;

import dae.project.Project;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Koen Samyn
 */
public class ProjectLevelListModel implements ListModel{
    private Project project;
    
    private ArrayList<ListDataListener> listeners = 
            new ArrayList<ListDataListener>();
    
    public ProjectLevelListModel(Project project){
        this.project = project;
    }
    
    public int getSize() {
        return project.getNrOfLevels();
    }

    public Object getElementAt(int index) {
        return project.getLevel(index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

}
