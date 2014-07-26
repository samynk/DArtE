/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui;

import com.google.common.eventbus.Subscribe;
import com.jme3.asset.AssetManager;
import com.jme3.math.Transform;
import com.jme3.scene.Node;
import dae.GlobalObjects;
import dae.gui.model.ProjectTreeModel;
import dae.gui.renderers.ProjectTreeCellRenderer;
import dae.io.SceneSaver;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.ui.classpath.FileNode;
import dae.prefabs.ui.events.AssetEvent;
import dae.prefabs.ui.events.AssetEventType;
import dae.prefabs.ui.events.InsertObjectEvent;
import dae.prefabs.ui.events.LayerEvent;
import dae.prefabs.ui.events.LayerEvent.LayerEventType;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.prefabs.ui.events.ProjectEvent;
import dae.prefabs.ui.events.ProjectEventType;
import dae.prefabs.ui.events.SelectionEvent;
import dae.project.AssetLevel;
import dae.project.Layer;
import dae.project.Level;
import dae.project.Project;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

/**
 *
 * @author Koen Samyn
 */
public class ProjectPanel extends javax.swing.JPanel implements TreeSelectionListener {

    private ProjectTreeModel treeModel;
    private Project currentProject;
    private CreateLayerDialog createLayerDialog;
    private CreateKlatchDialog createKlatchDialog;
    private final int LEVELDEPTH = 1;
    private boolean silentSelection = false;

    /**
     * Creates new form ProjectPanel
     */
    public ProjectPanel() {
        initComponents();
        GlobalObjects.getInstance().registerListener(this);
        ProjectTreeCellRenderer renderer = new ProjectTreeCellRenderer();
        projectTree.setCellRenderer(renderer);
        projectTree.setCellEditor(new DefaultTreeCellEditor(projectTree, renderer));
        projectTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                doPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                doPopup(e);
            }

            private void doPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath path = projectTree.getPathForLocation(e.getX(), e.getY());
                    projectTree.setSelectionPath(path);
                    java.awt.Rectangle pathBounds = projectTree.getUI().getPathBounds(projectTree, path);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        Object o = path.getLastPathComponent();
                        if (o instanceof Project) {
                            projectMenu.show(projectTree, pathBounds.x, pathBounds.y + pathBounds.height / 2);
                        } else if (o instanceof Level) {
                            layerMenu.show(projectTree, pathBounds.x, pathBounds.y + pathBounds.height / 2);
                        } else if (o instanceof Klatch) {
                            klatchMenu.show(projectTree, pathBounds.x, pathBounds.y + pathBounds.height / 2);
                        } else if (o instanceof Prefab) {
                            prefabMenu.show(projectTree, pathBounds.x, pathBounds.y + pathBounds.height / 2);
                        }
                    }

                }
            }
        });
        createLayerDialog = new CreateLayerDialog((Frame) this.getTopLevelAncestor(), true);
        createKlatchDialog = new CreateKlatchDialog((Frame) this.getTopLevelAncestor(), true);
    }

    @Subscribe
    public void projectSelected(final ProjectEvent event) {
        if (event.getSource() != this
                && (event.getEventType() == ProjectEventType.CREATED || event.getEventType() == ProjectEventType.SELECTED)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    currentProject = event.getProject();
                    treeModel = new ProjectTreeModel(event.getProject());
                    projectTree.setModel(treeModel);
                    projectTree.addTreeSelectionListener(ProjectPanel.this);
                    if (event.getProject().hasLevels()) {
                        Level level = event.getProject().getLevel(0);
                        if (level != null) {
                            TreePath path = new TreePath(new Object[]{event.getProject(), level});
                            projectTree.setSelectionPath(path);
                        }
                    }
                    projectTree.updateUI();
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectMenu = new javax.swing.JPopupMenu();
        mnuAddLevel = new javax.swing.JMenuItem();
        layerMenu = new javax.swing.JPopupMenu();
        mnuRemoveLevel = new javax.swing.JMenuItem();
        prefabMenu = new javax.swing.JPopupMenu();
        mnuDeletePrefab = new javax.swing.JMenuItem();
        mnuCreateKlatch = new javax.swing.JMenuItem();
        klatchMenu = new javax.swing.JPopupMenu();
        mnuDeleteKlatch = new javax.swing.JMenuItem();
        scrTreeContainer = new javax.swing.JScrollPane();
        projectTree = new javax.swing.JTree();

        mnuAddLevel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/accept.png"))); // NOI18N
        mnuAddLevel.setText("Add Level");
        mnuAddLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddLevelActionPerformed(evt);
            }
        });
        projectMenu.add(mnuAddLevel);

        mnuRemoveLevel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/delete.png"))); // NOI18N
        mnuRemoveLevel.setText("Remove Level");
        mnuRemoveLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRemoveLevelActionPerformed(evt);
            }
        });
        layerMenu.add(mnuRemoveLevel);

        mnuDeletePrefab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/delete.png"))); // NOI18N
        mnuDeletePrefab.setText("Delete object");
        mnuDeletePrefab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDeletePrefabActionPerformed(evt);
            }
        });
        prefabMenu.add(mnuDeletePrefab);

        mnuCreateKlatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/klatch.png"))); // NOI18N
        mnuCreateKlatch.setText("Create assembly ...");
        mnuCreateKlatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateKlatchActionPerformed(evt);
            }
        });
        prefabMenu.add(mnuCreateKlatch);

        mnuDeleteKlatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/delete.png"))); // NOI18N
        mnuDeleteKlatch.setText("Delete");
        mnuDeleteKlatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDeleteKlatchActionPerformed(evt);
            }
        });
        klatchMenu.add(mnuDeleteKlatch);

        setBorder(javax.swing.BorderFactory.createTitledBorder("Project"));
        setLayout(new java.awt.BorderLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Project");
        projectTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        projectTree.setEditable(true);
        scrTreeContainer.setViewportView(projectTree);

        add(scrTreeContainer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuAddLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddLevelActionPerformed
        // TODO add your handling code here:
        createLayerDialog.setLocationRelativeTo(this);
        createLayerDialog.setCurrentProject(currentProject);
        createLayerDialog.setVisible(true);
        if (createLayerDialog.getReturnStatus() == createLayerDialog.RET_OK) {
            String levelName = createLayerDialog.getLevelName();
            if (!this.currentProject.hasLevel(levelName)) {
                Level level = new Level(levelName, true);
                int index = currentProject.addLevel(level);
                treeModel.fireInsertLevelEvent(level, index);
            }
        }
    }//GEN-LAST:event_mnuAddLevelActionPerformed

    private void mnuRemoveLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRemoveLevelActionPerformed
        // TODO add your handling code here:
        if (currentProject.getNrOfLevels() > 1) {
            Object selected = projectTree.getSelectionPath().getLastPathComponent();
            if (selected instanceof Level) {
                Level l = (Level) selected;
                int index = currentProject.getIndexOfLevel(l);
                this.currentProject.removeLevel((Level) selected);
                treeModel.fireDeleteLevelEvent(l, index);

                Level newSelection = null;
                if (index > 0) {
                    newSelection = currentProject.getLevel(index - 1);
                } else {
                    newSelection = currentProject.getLevel(0);
                }
                Object[] selection = {currentProject, newSelection};
                projectTree.setSelectionPath(new TreePath(selection));
            }
        }
    }//GEN-LAST:event_mnuRemoveLevelActionPerformed

    private void mnuCreateKlatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateKlatchActionPerformed
        // TODO add your handling code here:
        if (currentProject.hasFileLocation()) {
            createKlatchDialog.setLocationRelativeTo(this);
            createKlatchDialog.setCurrentProject(this.currentProject);
            createKlatchDialog.setVisible(true);

            if (createKlatchDialog.getReturnStatus() == CreateKlatchDialog.RET_OK) {
                String path = createKlatchDialog.getAssemblyName();
                TreePath selected = projectTree.getLeadSelectionPath();
                Object o = selected.getLastPathComponent();
                if (!(o instanceof Klatch)) {
                    Prefab original = (Prefab) o;
                    Node parentNode = original.getParent();
                    Prefab copy = (Prefab) original.clone();
                    copy.setLocalTransform(Transform.IDENTITY);

                    Node sceneNode = new Node();
                    sceneNode.attachChild(copy);
                    SceneSaver.writeScene(new File(currentProject.getKlatchDirectory(), path), sceneNode);
                    // TODO ask to replace the current object with a klatch object.
                    Level currentLevel = (Level) selected.getPathComponent(LEVELDEPTH);
                    LevelEvent removeNode = new LevelEvent(currentLevel, EventType.NODEREMOVEREQUEST, original);
                    GlobalObjects.getInstance().postEvent(removeNode);

                    InsertObjectEvent ioe = new InsertObjectEvent(currentLevel, path, original.getName(), parentNode, original.getLocalTransform());
                    GlobalObjects.getInstance().postEvent(ioe);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Save the project first before creating assemblies!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_mnuCreateKlatchActionPerformed

    private void mnuDeletePrefabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeletePrefabActionPerformed
        deleteNode();
    }//GEN-LAST:event_mnuDeletePrefabActionPerformed

    private void mnuDeleteKlatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDeleteKlatchActionPerformed
        deleteNode();
    }//GEN-LAST:event_mnuDeleteKlatchActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu klatchMenu;
    private javax.swing.JPopupMenu layerMenu;
    private javax.swing.JMenuItem mnuAddLevel;
    private javax.swing.JMenuItem mnuCreateKlatch;
    private javax.swing.JMenuItem mnuDeleteKlatch;
    private javax.swing.JMenuItem mnuDeletePrefab;
    private javax.swing.JMenuItem mnuRemoveLevel;
    private javax.swing.JPopupMenu prefabMenu;
    private javax.swing.JPopupMenu projectMenu;
    private javax.swing.JTree projectTree;
    private javax.swing.JScrollPane scrTreeContainer;
    // End of variables declaration//GEN-END:variables

    public void valueChanged(TreeSelectionEvent e) {
        if ( silentSelection )
            return;
        Object selected = e.getPath().getLastPathComponent();
        if (selected instanceof Level) {
            Level l = (Level) selected;
            LevelEvent le = new LevelEvent(l);
            GlobalObjects.getInstance().postEvent(le);
        } else if (selected instanceof Layer) {
            Layer l = (Layer) selected;
            LayerEvent le = new LayerEvent(l);
            GlobalObjects.getInstance().postEvent(le);
        } else if (selected instanceof Prefab) {
            SelectionEvent se = new SelectionEvent((Prefab) selected, this);
            GlobalObjects.getInstance().postEvent(se);
        } else if (selected instanceof Project) {
            ProjectEvent pe = new ProjectEvent((Project) selected, this);
            GlobalObjects.getInstance().postEvent(pe);
        }
    }

    @Subscribe
    public void levelChanged(final LevelEvent le) {
        if (SwingUtilities.isEventDispatchThread()) {
            treeModel.levelChanged(le);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    treeModel.levelChanged(le);
                }
            });
        }
    }

    @Subscribe
    public void assetEdit(AssetEvent event) {
        if (event.getAssetEventType() == AssetEventType.EDIT) {
            FileNode asset = event.getFileNode();
            String assetLocation = asset.getFullName();

            AssetLevel aLevel = new AssetLevel(Paths.get(assetLocation));
            currentProject.addLevel(aLevel);
            treeModel.fireInsertLevelEvent(aLevel, currentProject.getIndexOfLevel(aLevel));
        }
    }

    @Subscribe
    public void layerChanged(final LayerEvent le) {
        if (le.eventType == LayerEventType.CREATED) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    treeModel.layerAdded(le);
                }
            });
        }
    }
    
    @Subscribe
    public void nodeSelected(final SelectionEvent se)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            selectNode(se.getSelectedNode());
        }else{
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    selectNode(se.getSelectedNode());
                }
            });
        }
    }
    
    private void selectNode(Prefab node){
        silentSelection = true;
        Object[] path = treeModel.createPathForNode(node);
        TreePath tp= new TreePath(path);
        projectTree.setSelectionPath(new TreePath(path));
        projectTree.scrollPathToVisible(tp);
        silentSelection = false;
    }

    private void deleteNode() {
        // TODO add your handling code here:
        TreePath selection = projectTree.getSelectionPath();
        Prefab deleted = (Prefab) selection.getLastPathComponent();
        Level level = (Level) selection.getPathComponent(1);
        LevelEvent levelEvent = new LevelEvent(level, LevelEvent.EventType.NODEREMOVEREQUEST, deleted);
        GlobalObjects.getInstance().postEvent(levelEvent);
    }
}