/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.model;

import dae.project.Project;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Koen Samyn
 */
public class ProjectAssetFolderListModel implements ListModel{

    private Project project;
    
    private ArrayList<ListDataListener> listeners = 
            new ArrayList<ListDataListener>();
    
    public ProjectAssetFolderListModel(Project project){
        this.project = project;
    }

    public int getSize() {
        return project.getNrOfAssetFolders();
    }

    public Object getElementAt(int index) {
        return project.getAssetFoldersAt(index);
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
    public void assetFolderAdded(int index){
        for ( ListDataListener listener: listeners){
            
            ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,index,index);
            listener.intervalAdded(lde);
        }
    }

    public void assetFolderRemoved(int index) {
        for ( ListDataListener listener: listeners){
            
            ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED,index,index);
            listener.intervalAdded(lde);
        }
    }
}
