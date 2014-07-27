package dae.prefabs.ui.classpath;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Koen
 */
public class FileNode implements Transferable {

    
    private String name;
    private String label;
    private String extension = "";
    private FileNode parentNode;
    private ArrayList<FileNode> children;
    private boolean isLeaf;

    public FileNode(String name, boolean isLeaf) {
        this.name = name;
        this.label = name;
        this.isLeaf = isLeaf;
        if (isLeaf) {
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > - 1) {
                this.extension = name.substring(dotIndex + 1);
            }
        }
    }
    
    public void setLabel(String label){
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileNode) {
            return this.name.equals(((FileNode) obj).name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public void removeEmptyDirectories() {
        if (!isLeaf) {
            if (children != null) {
                ArrayList<FileNode> copy = (ArrayList<FileNode>) this.children.clone();
                for (Iterator<FileNode> it = copy.iterator(); it.hasNext();) {
                    FileNode node = it.next();
                    node.removeEmptyDirectories();
                }
            }
            if (children == null || children.isEmpty()) {
                if (parentNode != null) {
                    parentNode.removeChild(this);
                }
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        String result = this.getName();
        FileNode pn = parentNode;
        while (pn != null) {
            if (pn.getName().length() > 0) {
                result = pn.getName() + "/" + result;
            }
            pn = pn.getParentNode();
        }
        return result;
    }

    public boolean isFile() {
        return isLeaf;
    }

    public boolean isDirectory() {
        return !isLeaf;
    }

    public void addChild(FileNode node) {
        if (children == null) {
            children = new ArrayList<FileNode>();
            isLeaf = false;
        }
        children.add(node);
        node.parentNode = this;
    }

    public void removeChild(FileNode node) {
        children.remove(node);
        //node.parentNode = null;
    }

    public FileNode getParentNode() {
        return parentNode;
    }

    public Iterable<FileNode> getChildren() {
        return children;
    }

    public Iterable<FileNode> getDirectories() {
        ArrayList<FileNode> directories = new ArrayList<FileNode>();
        if (children != null) {
            for (FileNode node : children) {
                if (node.isDirectory()) {
                    directories.add(node);
                }
            }
        }
        return directories;
    }

    public Iterable<FileNode> getFiles() {
        ArrayList<FileNode> directories = new ArrayList<FileNode>();
        if (children != null) {
            for (FileNode node : children) {
                if (node.isFile()) {
                    directories.add(node);
                }
            }
        }
        return directories;
    }

    @Override
    public String toString() {
        return label;
    }

    public FileNode addDirectory(String name) {
        if (children != null) {
            for (FileNode node : children) {
                if (node.isDirectory()) {
                    if (node.getName().contains(name)) {
                        return node;
                    }
                }
            }
        }
        FileNode newNode = new FileNode(name, false);
        this.addChild(newNode);
        return newNode;
    }

    public Object getChildAt(int index) {
        if (children != null) {
            return children.get(index);
        } else {
            return null;
        }
    }

    public int getChildSize() {
        if (children != null) {
            return children.size();
        } else {
            return 0;
        }
    }

    public int getIndexOf(FileNode child) {
        return children.indexOf(child);
    }
    private DataFlavor[] flavors = {DataFlavor.stringFlavor};

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DataFlavor.stringFlavor;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.getFullName();
    }

    public String getExtension() {
        return extension;
    }

    public FileNode getChild(String name) {
        if (children == null) {
            return null;
        } else {
            for (FileNode fn : this.children) {
                if (fn.getName().equals(name)) {
                    return fn;
                }
            }
            return null;
        }

    }

    public void removeAll() {
        if (children != null) {
            children.clear();
        }
    }

    public boolean hasChild(String name) {
        if (children == null) {
            return false;
        } else {
            for (FileNode fn : this.children) {
                if (fn.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int getDepth() {
        FileNode parent = this.getParentNode();
        int count = 1;
        while(parent != null){
            ++count;
            parent = parent.getParentNode();
        }
        return count;
    }
    
    /**
     * Creates a FileNode starting from a path.
     * @param rigLocation the location of the file.
     * @return a new filenode.
     */
    public static FileNode createFromPath(String rigLocation) {
        Path p = Paths.get(rigLocation);
        FileNode current = null;
        for ( int i = 0; i < p.getNameCount();++i)
        {
            Path currentPath = p.getName(i);
            FileNode newNode = new FileNode(currentPath.toString(),false);
            if ( current != null){
                current.addChild(newNode);
            }
            current = newNode;
        }
        return current; 
    }

}
