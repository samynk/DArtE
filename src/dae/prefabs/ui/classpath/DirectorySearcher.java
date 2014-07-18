/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.classpath;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Searches a directory for specific patterns and subdirectories.
 *
 * @author Koen Samyn
 */
public class DirectorySearcher implements FileVisitor<Path> {

    private Path startDirectory;
    private FileNode rootNode;
    private Matcher matcher;

    public DirectorySearcher() {
    }

    public DirectorySearcher(Path startDirectory) {
        this.startDirectory = startDirectory;
    }

    public void setStartDirectory(Path startDirectory) {
        this.startDirectory = startDirectory;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public FileNode searchFiles() {
        try {
            if (startDirectory == null) {
                return new FileNode("<error>", false);
            }
            rootNode = new FileNode("", false);
            rootNode.setLabel("Assemblies");
            Files.walkFileTree(startDirectory, this);
            return rootNode;
        } catch (IOException ex) {
            Logger.getLogger(DirectorySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new FileNode("<error>", false);
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path relative = startDirectory.relativize(dir);
        createSubdirs(rootNode, relative);

        System.out.println("Path found : " + relative);
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path relative = startDirectory.relativize(file);

        String relativeName = relative.toString();
        if (relativeName.length() > 0 && matcher != null) {
            matcher.reset(relativeName);
            if (matcher.matches()) {
                FileNode node = createSubdirs(rootNode, relative.getParent());
                if (node != null) {
                    node.addChild(new FileNode(file.getFileName().toString(), true));
                }
            }
        }
        return FileVisitResult.CONTINUE;
    }

    private FileNode createSubdirs(FileNode root, Path relative) {
        for (int i = 0; i < relative.getNameCount(); ++i) {
            Path subdir = relative.getName(i);
            String subdirname = subdir.toString();
            if (subdirname.length() == 0) {
                return null;
            }
            if (root.hasChild(subdirname)) {
                root = root.getChild(subdirname);
            } else {
                FileNode newNode = new FileNode(subdirname, false);
                root.addChild(newNode);
                root = newNode;
            }
        }
        return root;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.TERMINATE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
