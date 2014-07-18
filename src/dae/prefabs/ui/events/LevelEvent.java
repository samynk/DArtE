/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import com.jme3.scene.Node;
import dae.project.Level;
import java.util.ArrayList;

/**
 *
 * @author Koen Samyn
 */
public class LevelEvent {

    private Level level;

    

    public enum EventType {
        LEVELSELECTED, NODEREMOVED, NODEADDED, NODEMOVED, NODEREMOVEREQUEST
    };
    private EventType eventType;
    private Iterable<Node> nodes;
    private Node previousParent;
    private Node currentParent;
    private int previousIndex;

    public LevelEvent(Level level) {
        this.level = level;
        this.eventType = EventType.LEVELSELECTED;
    }

    public LevelEvent(Level level, EventType type, Node node) {
        this.level = level;
        eventType = type;
        ArrayList<Node> alNodes = new ArrayList<Node>();
        alNodes.add(node);
        nodes = alNodes;
    }

    public LevelEvent(Level level, EventType type, Node node, Node previousParent, int previousIndex, Node currentParent) {
        this.level = level;
        eventType = type;
        ArrayList<Node> alNodes = new ArrayList<Node>();
        alNodes.add(node);
        nodes = alNodes;

        this.previousParent = previousParent;
        this.currentParent = currentParent;
        this.previousIndex = previousIndex;
    }

    public LevelEvent(Level level, EventType type, Iterable<Node> nodeList) {
        this.level = level;
        eventType = type;
        
        nodes = nodeList;
    }

    public Level getLevel() {
        return level;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Iterable<Node> getNodes() {
        return nodes;
    }

    public Node getPreviousParent() {
        return previousParent;
    }

    public int getPreviousIndex() {
        return previousIndex;
    }
    
    public void setCurrentParent(Node parentNode) {
        this.currentParent = parentNode;
    }

    public Node getCurrentParent() {
        return currentParent;
    }
}
