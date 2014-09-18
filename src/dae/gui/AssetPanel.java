/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui;

import com.google.common.eventbus.Subscribe;
import dae.GlobalObjects;
import dae.gui.events.ApplicationStoppedEvent;
import dae.gui.model.AssetTreeModel;
import dae.gui.model.TreeTransferHandler;
import dae.gui.renderers.AssetTreeCellRenderer;
import dae.gui.watchservice.WatchServiceListener;
import dae.gui.watchservice.WatchServiceThread;
import dae.prefabs.ui.classpath.ClassPathSearcher;
import dae.prefabs.ui.classpath.FileNode;
import dae.prefabs.ui.events.AssetEvent;
import dae.prefabs.ui.events.AssetEventType;
import dae.prefabs.ui.events.ProjectEvent;
import dae.prefabs.ui.events.ProjectEventType;
import dae.project.Project;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author Koen Samyn
 */
public class AssetPanel extends javax.swing.JPanel implements WatchServiceListener {

    private ClassPathSearcher searcher = new ClassPathSearcher();
    private WatchService service;
    private WatchServiceThread thread;
    private Project currentProject;
    private FileNode baseNode;
    private AssetTreeModel treeModel;
    private Pattern filePattern;
    private ExecutorService executor;
    private boolean registerListener;

    /**
     * Creates new form AssetPanel
     */
    public AssetPanel() {
        // match j3o files and klatch files
        setFilePattern(".*\\.(?:j3o|klatch|rig)");
        initComponents();
        assetTree.setCellRenderer(new AssetTreeCellRenderer());
        assetTree.setTransferHandler(new TreeTransferHandler());

        GlobalObjects.getInstance().registerListener(this);
        try {
            service = FileSystems.getDefault().newWatchService();
            thread = new WatchServiceThread(service);
            thread.addWatchServiceListener(this);
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }

        if (registerListener) {
            GlobalObjects.getInstance().registerListener(this);
        }
        executor = Executors.newSingleThreadExecutor();
    }

    public void setFilePattern(String pattern) {
        filePattern = Pattern.compile(pattern);
    }

    public String getFilePattern() {
        return filePattern.pattern();
    }

    /**
     * Listen to project events.
     *
     * @param value true if the listener should register for project events.
     */
    public void setRegisterListener(boolean value) {
        this.registerListener = value;
    }

    /**
     * Checks if this panel listens to project events.
     *
     * @return true if this panel is registered, false otherwise.
     */
    public boolean isRegisterListener() {
        return registerListener;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        assetPopupMenu = new javax.swing.JPopupMenu();
        mnuEditObject = new javax.swing.JMenuItem();
        mnuDelete = new javax.swing.JMenuItem();
        rigPopupMenu = new javax.swing.JPopupMenu();
        mnuEditRig = new javax.swing.JMenuItem();
        mnuDeleteRig = new javax.swing.JMenuItem();
        klatchPopupMenu = new javax.swing.JPopupMenu();
        mnuEditKlatch = new javax.swing.JMenuItem();
        mnuDeleteRig1 = new javax.swing.JMenuItem();
        scrAssetPanel = new javax.swing.JScrollPane();
        assetTree = new javax.swing.JTree();
        txtSearch = new javax.swing.JTextField();
        lblSearch = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnSearch.putClientProperty("JComponent.sizeVariant", "mini");
        cboMeshFilter = new javax.swing.JToggleButton();
        cboKlatchFilter = new javax.swing.JToggleButton();
        cboRigFilter = new javax.swing.JToggleButton();

        mnuEditObject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/editobject.png"))); // NOI18N
        mnuEditObject.setText("Create Assembly ...");
        mnuEditObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditObjectActionPerformed(evt);
            }
        });
        assetPopupMenu.add(mnuEditObject);

        mnuDelete.setText("Delete Asset ...");
        mnuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDeleteActionPerformed(evt);
            }
        });
        assetPopupMenu.add(mnuDelete);

        mnuEditRig.setText("Edit Rig ...");
        mnuEditRig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditRigActionPerformed(evt);
            }
        });
        rigPopupMenu.add(mnuEditRig);

        mnuDeleteRig.setText("Delete Rig");
        rigPopupMenu.add(mnuDeleteRig);

        mnuEditKlatch.setText("Edit Assembly ...");
        mnuEditKlatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditKlatchActionPerformed(evt);
            }
        });
        klatchPopupMenu.add(mnuEditKlatch);

        mnuDeleteRig1.setText("Delete Assembly");
        klatchPopupMenu.add(mnuDeleteRig1);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Assets");
        assetTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        assetTree.setDragEnabled(true);
        assetTree.setRootVisible(false);
        assetTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                assetTreeMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                assetTreeMousePressed(evt);
            }
        });
        scrAssetPanel.setViewportView(assetTree);

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        lblSearch.setText("Search assets:");

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/zoom.png"))); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        cboMeshFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/mesh.png"))); // NOI18N
        cboMeshFilter.setSelected(true);
        cboMeshFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMeshFilterItemStateChanged(evt);
            }
        });

        cboKlatchFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/klatch.png"))); // NOI18N
        cboKlatchFilter.setSelected(true);
        cboKlatchFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKlatchFilterItemStateChanged(evt);
            }
        });

        cboRigFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/body.png"))); // NOI18N
        cboRigFilter.setSelected(true);
        cboRigFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboRigFilterItemStateChanged(evt);
            }
        });

        txtSearch.putClientProperty("JComponent.sizeVariant", "small");
        txtSearch.putClientProperty("JComponent.sizeVariant", "small");
        cboMeshFilter.putClientProperty("JComponent.sizeVariant", "mini");
        cboKlatchFilter.putClientProperty("JComponent.sizeVariant", "mini");
        cboMeshFilter.putClientProperty("JComponent.sizeVariant", "mini");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrAssetPanel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboMeshFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboKlatchFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboRigFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                        .addComponent(btnSearch))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboMeshFilter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cboKlatchFilter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cboRigFilter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrAssetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
        FileNode node = searcher.findFilesInClassLoader(filePattern);
        this.assetTree.setModel(new AssetTreeModel(node));
    }//GEN-LAST:event_txtSearchActionPerformed

    private void assetTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_assetTreeMousePressed
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            showAssetTreePopup(evt);
        }
    }//GEN-LAST:event_assetTreeMousePressed

    private void assetTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_assetTreeMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            showAssetTreePopup(evt);
        }
    }//GEN-LAST:event_assetTreeMouseReleased

    private void mnuEditObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditObjectActionPerformed
        // TODO add your handling code here:
        Object o = this.assetTree.getLastSelectedPathComponent();
        if (o != null && o instanceof FileNode) {
            FileNode fn = (FileNode) o;
            AssetEvent ae = new AssetEvent(AssetEventType.EDIT, fn);
            GlobalObjects.getInstance().postEvent(ae);
        }
    }//GEN-LAST:event_mnuEditObjectActionPerformed

    private void cboMeshFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMeshFilterItemStateChanged
        // TODO add your handling code here:
        adaptFilter();
    }//GEN-LAST:event_cboMeshFilterItemStateChanged

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        adaptFilter();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void cboKlatchFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKlatchFilterItemStateChanged
        adaptFilter();
    }//GEN-LAST:event_cboKlatchFilterItemStateChanged

    private void mnuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeleteActionPerformed
    }//GEN-LAST:event_mnuDeleteActionPerformed

    private void cboRigFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboRigFilterItemStateChanged
        adaptFilter();
    }//GEN-LAST:event_cboRigFilterItemStateChanged

    private void mnuEditRigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditRigActionPerformed
        // TODO add your handling code here:
        Object o = this.assetTree.getLastSelectedPathComponent();
        if (o != null && o instanceof FileNode) {
            FileNode fn = (FileNode) o;
            AssetEvent ae = new AssetEvent(AssetEventType.EDIT, fn);
            GlobalObjects.getInstance().postEvent(ae);
        }
    }//GEN-LAST:event_mnuEditRigActionPerformed

    private void mnuEditKlatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditKlatchActionPerformed
        // TODO add your handling code here:
        Object o = this.assetTree.getLastSelectedPathComponent();
        if (o != null && o instanceof FileNode) {
            FileNode fn = (FileNode) o;
            AssetEvent ae = new AssetEvent(AssetEventType.EDIT, fn);
            GlobalObjects.getInstance().postEvent(ae);
        }
    }//GEN-LAST:event_mnuEditKlatchActionPerformed

    private void adaptFilter() {
        int count = 0;
        boolean klatches = cboKlatchFilter.isSelected();
        count += klatches ? 1 : 0;
        boolean meshes = cboMeshFilter.isSelected();
        count += meshes ? 1 : 0;
        boolean rigs = cboRigFilter.isSelected();
        count += rigs ? 1 : 0;

        if (count == 1) {
            if (klatches) {
                filePattern = Pattern.compile(".*" + txtSearch.getText() + ".*\\.klatch");
            } else if (meshes) {
                filePattern = Pattern.compile(".*" + txtSearch.getText() + ".*\\.j3o");
            } else if (rigs) {
                filePattern = Pattern.compile(".*" + txtSearch.getText() + ".*\\.rig");
            }
        } else if (count > 1) {
            String pattern = "";
            if (klatches) {
                pattern += "klatch";
            }
            if (meshes) {
                if (pattern.length() > 0) {
                    pattern += "|";
                }
                pattern += "j3o";
            }
            if (rigs) {
                if (pattern.length() > 0) {
                    pattern += "|";
                }
                pattern += "rig";
            }
            filePattern = Pattern.compile(".*" + txtSearch.getText() + ".*\\.(?:" + pattern + ")");
        } else {
            //System.out.println("no search results");
            assetTree.setModel(new AssetTreeModel(new FileNode("no search results", false)));
            assetTree.repaint();
            return;
        }
        buildAssetTree(currentProject);
    }

    private void showAssetTreePopup(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        JTree tree = (JTree) evt.getSource();
        TreePath path = tree.getPathForLocation(x, y);
        if (path == null) {
            return;
        }
        tree.setSelectionPath(path);
        FileNode selected = (FileNode) path.getLastPathComponent();
        if (selected.isFile()) {
            String ext = selected.getExtension();
            if (ext.equalsIgnoreCase("j3o")) {
                this.assetPopupMenu.show(tree, evt.getX(), evt.getY()+5);
            } else if (ext.equalsIgnoreCase("rig")) {
                this.rigPopupMenu.show(tree, evt.getX(), evt.getY()+5);
            } else if ( ext.equalsIgnoreCase("klatch")){
                this.klatchPopupMenu.show(tree, evt.getX(), evt.getY()+5);
            }
        }
    }

    @Subscribe
    public void projectChanged(ProjectEvent pe) {
        if (pe.getEventType() == ProjectEventType.SELECTED
                || pe.getEventType() == ProjectEventType.ASSETFOLDERCHANGED
                || pe.getEventType() == ProjectEventType.CREATED) {
            currentProject = pe.getProject();
            buildAssetTree(currentProject);
        }
    }

    public void pathModified(Path path) {
        System.out.println("Path modified: " + path);
        Logger.getLogger("DArtE").log(Level.INFO, "Path modified: {0}", path.toString());
    }

    public void assetModified(Path path) {
        if (!filePattern.matcher(path.toString()).matches()) {
            return;
        }
        File assetFolder = getAssetFolderForPath(path);
        Path assetPath = assetFolder.toPath();
        Path assetLocation = path.subpath(assetPath.getNameCount(), path.getNameCount());
        FileNode current = baseNode;
        for (int i = 0; i < assetLocation.getNameCount(); ++i) {
            current = current.getChild(assetLocation.getName(i).toString());
            if (current == null) {
                break;
            }
        }
        GlobalObjects.getInstance().postEvent(new AssetEvent(AssetEventType.MODIFIED, current));
    }

    public void pathCreated(Path subDir) {
        // class path searching can take a while, so rescan
        // from the correct file node.
        File assetFolder = getAssetFolderForPath(subDir);
        Path assetPath = assetFolder.toPath();
        Path classpathDir = subDir.subpath(assetPath.getNameCount(), subDir.getNameCount());
        // running the classpath searcher from the given subdir without the service
        // watcher because the directories are allready registered.
        FileNode current = baseNode;
        FileNode parent = baseNode;
        for (int i = 0; i < classpathDir.getNameCount(); ++i) {
            parent = current;
            current = current.getChild(classpathDir.getName(i).toString());
            if (current == null) {
                break;
            }
        }

        if (current == null) {
            // path does not exist in any class path.
            FileNode newDir = parent.addDirectory(classpathDir.getName(classpathDir.getNameCount() - 1).toString());
            int indices[] = {parent.getIndexOf(newDir)};
            Object[] path = createTreePath(parent);
            Object[] child = {newDir};
            TreeModelEvent tme = new TreeModelEvent(this, path, indices, child);
            try {
                // scan items that might have been copied together with the directory.
                searcher.findResourceInDirectory(newDir, baseNode, subDir.toFile(), filePattern.matcher(""), thread);
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }

            treeModel.fireNodeAdded(tme);
        }
    }
    private ArrayList tmpObjects = new ArrayList();
    private ArrayList<Integer> tmpIndices = new ArrayList<Integer>();

    public void pathDeleted(Path subDir) {
        File assetFolder = getAssetFolderForPath(subDir);
        Path assetPath = assetFolder.toPath();
        Path classpathDir = subDir.subpath(assetPath.getNameCount(), subDir.getNameCount());

        FileNode current = baseNode;
        FileNode parent = baseNode;
        for (int i = 0; i < classpathDir.getNameCount(); ++i) {
            parent = current;
            current = current.getChild(classpathDir.getName(i).toString());
            if (current == null) {
                break;
            }
        }
        // check if the class path dir does not exist in other classpaths.
        if (current != null) {
            if (isSubDirectoryEmpty(current, assetFolder)) {
                int indices[] = {parent.getIndexOf(current)};
                Object[] path = createTreePath(parent);
                Object[] child = {current};
                parent.removeChild(current);
                TreeModelEvent tme = new TreeModelEvent(this, path, indices, child);
                treeModel.fireNodeRemoved(tme);
            } else {
                // the directory is not empty, but maybe there are
                // subdirectories / files that are not present
                // in the other classpaths that need to be removed.
                tmpObjects.clear();
                tmpIndices.clear();
                for (FileNode child : current.getChildren()) {
                    if (!isChildPresent(child, assetFolder)) {
                        tmpObjects.add(child);
                        tmpIndices.add(current.getIndexOf(child));
                    }
                }
                Object[] children = tmpObjects.toArray();
                int[] indices = new int[tmpIndices.size()];
                int count = 0;
                for (int integer : tmpIndices) {
                    indices[count++] = integer;
                }
                TreeModelEvent tme = new TreeModelEvent(this, this.createTreePath(current), indices, children);
                treeModel.fireNodeRemoved(tme);
            }
        }
    }

    private void dispatchTreeModelEvent(TreeModelEvent tme) {
    }

    public void assetCreated(Path subDir) {
        if (!filePattern.matcher(subDir.toString()).matches()) {
            return;
        }
        File assetFolder = getAssetFolderForPath(subDir);
        Path assetPath = assetFolder.toPath();
        Path classpathDir = subDir.subpath(assetPath.getNameCount(), subDir.getNameCount());

        FileNode current = baseNode;
        FileNode parent = baseNode;
        int element;
        int nameCount = classpathDir.getNameCount();
        for (element = 0; element < nameCount; ++element) {
            parent = current;
            Path p = classpathDir.getName(element);
            if (p == null) {
                Logger.getLogger("DArtE").log(Level.WARNING, "Problem getting path element {0} from {1} with {2} elements!", new Object[]{element, classpathDir, nameCount});
                break;
            } else {
                current = current.getChild(classpathDir.getName(element).toString());
                if (current == null) {
                    break;
                }
            }
        }
        if (current != null) {
            // item allready exists
            return;
        }

        // element is the start of the new FileNode objects that need to be created.
        FileNode startFileNode = null;
        FileNode newFileNode = null;
        for (int i = nameCount - 1; i >= element; --i) {
            startFileNode = new FileNode(classpathDir.getName(i).toString(), i == (nameCount - 1));
            if (newFileNode != null) {
                startFileNode.addChild(newFileNode);
            }
            newFileNode = startFileNode;
        }

        // append startFileNode to the parent
        parent.addChild(startFileNode);
        // path does not exist in any class path.

        int indices[] = {parent.getIndexOf(startFileNode)};
        Object[] path = createTreePath(parent);
        Object[] child = {startFileNode};
        TreeModelEvent tme = new TreeModelEvent(this, path, indices, child);
        treeModel.fireNodeAdded(tme);
    }

    public void assetDeleted(Path subDir) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        File assetFolder = getAssetFolderForPath(subDir);
        Path assetPath = assetFolder.toPath();
        Path classpathDir = subDir.subpath(assetPath.getNameCount(), subDir.getNameCount());

        FileNode current = baseNode;
        FileNode parent = baseNode;
        int element;
        int nameCount = classpathDir.getNameCount();
        for (element = 0; element < nameCount; ++element) {
            parent = current;
            current = current.getChild(classpathDir.getName(element).toString());
            if (current == null) {
                break;
            }
        }



        if (current != null) {
            // check if the file exists in other classpaths.
            if (!isChildPresent(current, assetFolder)) {
                // TODO - notify the sandbox that a resource is gone, possibilities
                // 1) remove objects that were loaded from this asset.
                // 2) add a placeholder with the location of the asset.


                Object[] path = createTreePath(parent);
                Object[] child = {current};
                int[] index = {parent.getIndexOf(current)};
                parent.removeChild(current);
                TreeModelEvent tme = new TreeModelEvent(this, path, index, child);
                treeModel.fireNodeRemoved(tme);
            } else {
                // todo : two (or more) assets with the same name are present
                // so it's possible that the asset needs to be reloaded.
            }
        }
    }

    private File getAssetFolderForPath(Path subDir) {
        for (File f : currentProject.getAssetFolders()) {
            Path p = f.toPath();
            if (subDir.getNameCount() < p.getNameCount()) {
                continue;
            }


            Path toCheck = subDir.subpath(0, p.getNameCount());
            toCheck = p.getRoot() != null ? p.getRoot().resolve(toCheck) : toCheck;
            if (toCheck.equals(p)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Checks if there is content present in the subdirectory in classpath
     * directories other then the folderToExclude file.
     *
     * @param toCheck the FileNode to check.
     * @param folderToExclude a file that should be skipped in the check.
     * @return false, if the subdirectory is not empty, true otherwise.
     */
    public boolean isSubDirectoryEmpty(FileNode toCheck, File folderToExclude) {
        for (File assetFolder : currentProject.getAssetFolders()) {
            if (assetFolder.equals(folderToExclude)) {
                continue;
            }
            File dirToCheck = new File(assetFolder, toCheck.getFullName());
            if (dirToCheck.exists()) {
                for (File f : dirToCheck.listFiles()) {
                    if (toCheck.hasChild(f.getName())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if a FileNode is present, except in the provided folderToExclude
     * directory.
     *
     * @param toCheck the FileNode to check.
     * @param folderToExclude the folder to skip during checks.
     * @return
     */
    public boolean isChildPresent(FileNode toCheck, File folderToExclude) {
        for (File assetFolder : currentProject.getAssetFolders()) {
            if (assetFolder.equals(folderToExclude)) {
                continue;
            }
            File fileToCheck = new File(assetFolder, toCheck.getFullName());
            if (fileToCheck.exists()) {
                return true;
            }
        }
        return false;
    }

    public Object[] createTreePath(FileNode node) {
        // count the number of elements in the path
        int count = 1;
        FileNode countNode = node;
        while (countNode.getParentNode() != null) {
            countNode = countNode.getParentNode();
            ++count;
        }

        Object[] path = new Object[count];
        for (int i = path.length - 1; i >= 0; --i) {
            path[i] = node;
            node = node.getParentNode();
        }
        return path;
    }

    @Subscribe
    public void applicationStopped(ApplicationStoppedEvent evt) {
        try {
            service.close();
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu assetPopupMenu;
    private javax.swing.JTree assetTree;
    private javax.swing.JButton btnSearch;
    private javax.swing.JToggleButton cboKlatchFilter;
    private javax.swing.JToggleButton cboMeshFilter;
    private javax.swing.JToggleButton cboRigFilter;
    private javax.swing.JPopupMenu klatchPopupMenu;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JMenuItem mnuDelete;
    private javax.swing.JMenuItem mnuDeleteRig;
    private javax.swing.JMenuItem mnuDeleteRig1;
    private javax.swing.JMenuItem mnuEditKlatch;
    private javax.swing.JMenuItem mnuEditObject;
    private javax.swing.JMenuItem mnuEditRig;
    private javax.swing.JPopupMenu rigPopupMenu;
    private javax.swing.JScrollPane scrAssetPanel;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    private void buildAssetTree(final Project project) {

        if (!thread.isAlive()) {
            thread.start();
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        executor.execute(new Runnable() {
            public void run() {
                searcher.setAssetClassLoader(project.getAssetLoader());
                thread.clearWatchService();
                baseNode = searcher.findFilesInClassLoader(filePattern, thread);
                baseNode.removeEmptyDirectories();
                treeModel = new AssetTreeModel(baseNode);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        assetTree.setModel(treeModel);
                        setCursor(Cursor.getDefaultCursor());
                    }
                });
            }
        });
    }
}
