package dae.prefabs.ui.classpath;

import dae.gui.watchservice.WatchServiceThread;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class searches the class path for a specific file pattern. If different
 * entries in the classpath have the same directories, the directories are
 * merged.
 *
 * @author Koen Samyn
 */
public class ClassPathSearcher {

    private ClassLoader classLoader;
    // keep a list of wacthkey so they can be canceled if necessary.

    public FileNode findFilesInClassPath(Pattern pattern) {
        Matcher m = pattern.matcher("");
        FileNode rootNode = new FileNode("", false);
        String classPath = System.getProperty("java.class.path");
        String[] pathElements = classPath.split(System
                .getProperty("path.separator"));
        for (String element : pathElements) {
            //System.out.println(element);
            try {
                File newFile = new File(element);
                if (newFile.isDirectory()) {
                    findResourceInDirectory(rootNode, rootNode, newFile, m);
                } else {
                    //findResourceInFile(rootNode, newFile, fileNamePattern);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());

            }
        }
        return rootNode;
    }

    public void setAssetClassLoader(ClassLoader loader) {
        this.classLoader = loader;
    }

    public FileNode findFilesInClassLoader(Pattern fileNamePattern) {
        Matcher matcher = fileNamePattern.matcher("");
        FileNode rootNode = new FileNode("", false);

        if (classLoader == null) {
            return rootNode;
        }
        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

        for (URL url : urlClassLoader.getURLs()) {

            System.out.println(url.getPath());
            try {
                File newFile = new File(url.getPath());
                if (newFile.isDirectory()) {
                    findResourceInDirectory(rootNode, rootNode, newFile, matcher);
                } else {
                    //findResourceInFile(rootNode, newFile, fileNamePattern);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());

            }
        }
        return rootNode;
    }

    private void findResourceInFile(FileNode rootNode, File resourceFile,
           Matcher matcher) throws IOException {

        if (resourceFile.canRead()
                && resourceFile.getAbsolutePath().endsWith(".jar")) {
            System.out.println("jar file found: " + resourceFile.getAbsolutePath());
            JarFile jarFile = new JarFile(resourceFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry singleEntry = entries.nextElement();
                //System.out.println("jar entry: " + singleEntry.getName());
                matcher.reset(singleEntry.getName());
                if (matcher.matches()) {
                    String components[] = singleEntry.getName().split("/");
                    FileNode parentNode = rootNode;
                    for (int i = 0; i < components.length; ++i) {
                        if (i < components.length - 1) {
                            FileNode childNode = new FileNode(components[i], false);
                            parentNode.addChild(childNode);
                            parentNode = childNode;
                        } else {
                            FileNode childNode = new FileNode(components[i], true);
                            parentNode.addChild(childNode);
                        }
                    }
                }
            }
        }

    }

    public void findResourceInDirectory(FileNode parentNode, FileNode rootNode, File directory,
            Matcher matcher) throws IOException {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        for (File currentFile : files) {
            matcher.reset(currentFile.getAbsolutePath());
            if (matcher.matches()) {
                FileNode matchedFile = new FileNode(currentFile.getName(), true);
                parentNode.addChild(matchedFile);
                //System.out.println("current file name: " + currentFile.getAbsolutePath());
            } else if (currentFile.isDirectory()) {
                FileNode matchedFile = parentNode.addDirectory(currentFile.getName());
                // parentNode.addChild(matchedFile);
                findResourceInDirectory(matchedFile, rootNode, currentFile, matcher);
            } else {
                findResourceInFile(rootNode, currentFile, matcher);
            }
        }
    }

    public FileNode findFilesInClassLoader(Pattern pattern, WatchServiceThread serviceThread) {
        FileNode rootNode = new FileNode("", false);
        Matcher matcher = pattern.matcher("");
        if (classLoader == null) {
            return rootNode;
        }
        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

        for (URL url : urlClassLoader.getURLs()) {

            System.out.println(url.getPath());
            try {
                File newFile = new File(url.getPath());
                if (newFile.isDirectory()) {
                    Path dir = newFile.toPath();
                    serviceThread.register(dir);
                    findResourceInDirectory(rootNode, rootNode, newFile, matcher, serviceThread);
                } else {
                    //findResourceInFile(rootNode, newFile, fileNamePattern);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());

            }
        }
        return rootNode;
    }

    public void findResourceInDirectory(FileNode parentNode, FileNode rootNode, File directory,
            Matcher matcher, WatchServiceThread service) throws IOException {
        File[] files = directory.listFiles();
        for (File currentFile : files) {
            matcher.reset(currentFile.getAbsolutePath());
            if (matcher.matches()) {
                FileNode matchedFile = new FileNode(currentFile.getName(), true);
                parentNode.addChild(matchedFile);
                //System.out.println("current file name: " + currentFile.getAbsolutePath());
            } else if (currentFile.isDirectory()) {
                Path toWatch = currentFile.toPath();
                service.register(toWatch);
                FileNode matchedFile = parentNode.addDirectory(currentFile.getName());
                // parentNode.addChild(matchedFile);
                findResourceInDirectory(matchedFile, rootNode, currentFile, matcher, service);
            } else {
                findResourceInFile(rootNode, currentFile, matcher);
            }
        }
    }
}
