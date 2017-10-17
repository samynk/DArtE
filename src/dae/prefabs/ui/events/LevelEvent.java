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
    private Object previousParent;
    private Object currentParent;
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

    /**
     * Constructor for node removed events.
     *
     * @param level the level where the node was removed.
     * @param type the type of the event (should be NodeRemoved for this
     * constructor.
     * @param node the node that was remove.
     * @param index the index of the node.
     */
    public LevelEvent(Level level, EventType type, Node node, Object parent, int index) {
        this.level = level;
        eventType = type;
        ArrayList<Node> alNodes = new ArrayList<Node>();
        alNodes.add(node);
        nodes = alNodes;

        this.previousParent = parent;
        this.currentParent = null;
        this.previousIndex = index;
    }

    public LevelEvent(Level level, EventType type, Node node, Object previousParent, int previousIndex, Object currentParent) {
        this.level = level;
        eventType = type;
        ArrayList<Node> alNodes = new ArrayList<>();
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

    public Object getPreviousParent() {
        return previousParent;
    }

    public int getPreviousIndex() {
        return previousIndex;
    }

    public void setCurrentParent(Node parentNode) {
        this.currentParent = parentNode;
    }

    public Object getCurrentParent() {
        return currentParent;
    }

    public static LevelEvent createNodeRemovedEvent(Level level, Node toRemove, Object parent, int index) {
        return new LevelEvent(level, EventType.NODEREMOVED, toRemove, parent, index);
    }
}
