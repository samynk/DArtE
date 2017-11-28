package dae.gui;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import dae.GlobalObjects;
import dae.animation.rig.Rig;
import dae.animation.rig.io.RigWriter;
import dae.components.MeshComponent;
import dae.gui.events.ApplicationStoppedEvent;
import dae.gui.events.DAEKeyboardManager;
import dae.gui.renderers.TransformSpaceRenderer;
import dae.io.ProjectLoader;
import dae.io.ProjectSaver;
import dae.io.SceneLoader;
import dae.prefabs.AxisEnum;
import dae.prefabs.Prefab;
import dae.prefabs.brush.Brush;
import dae.prefabs.gizmos.RotateGizmoSpace;
import dae.prefabs.gizmos.TranslateGizmoSpace;
import dae.prefabs.gizmos.events.AutoGridEvent;
import dae.prefabs.gizmos.events.GizmoSpaceChangedEvent;
import dae.prefabs.gizmos.events.RotateGizmoSpaceChangedEvent;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.types.ObjectTypePanel;
import dae.prefabs.types.ObjectTypeUI;
import dae.prefabs.ui.classpath.FileNode;
import dae.prefabs.ui.events.AssetEvent;
import dae.prefabs.ui.events.AssetEventType;
import dae.prefabs.ui.events.BrushEvent;
import dae.prefabs.ui.events.BrushEventType;
import dae.prefabs.ui.events.ComponentEvent;
import dae.prefabs.ui.events.CreateObjectEvent;
import dae.prefabs.ui.events.CutCopyPasteEvent;
import dae.prefabs.ui.events.CutCopyPasteType;
import dae.prefabs.ui.events.GizmoEvent;
import dae.prefabs.ui.events.GizmoType;
import dae.prefabs.ui.events.LevelEvent;
import dae.prefabs.ui.events.LevelEvent.EventType;
import dae.prefabs.ui.events.PlayEvent;
import dae.prefabs.ui.events.PlayEventType;
import dae.prefabs.ui.events.ProjectEvent;
import dae.prefabs.ui.events.ProjectEventType;
import dae.prefabs.ui.events.SelectionEvent;
import dae.prefabs.ui.events.ViewportReshapeEvent;
import dae.prefabs.ui.events.ZoomEvent;
import dae.prefabs.ui.events.ZoomEventType;
import dae.project.Project;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Koen Samyn
 */
public class SandboxFrame extends javax.swing.JFrame implements DropTargetListener {

    private final SandboxViewport viewport;
    private final CreateProjectDialog createProjectDialog;
    private final CreateKlatchDialog createObjectDialog;
    private RemoveComponentDialog removeComponentDialog;
    private final FileNameExtensionFilter sandboxFilter = new FileNameExtensionFilter("Sandbox Files", "zbk");
    private final FileNameExtensionFilter sceneFilter = new FileNameExtensionFilter("Scene files", "scene");
    /**
     * The current project.
     */
    private Project currentProject;
    /**
     * The currently selected prefab.
     */
    private Prefab selectedPrefab;
    /**
     * This boolean is true when the viewport has been loaded.
     */
    private boolean viewportLoaded = false;

    /**
     * Creates new form SandboxFrame
     */
    public SandboxFrame() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(new DAEKeyboardManager());
        initComponents();
        cboTranslateSpace.setModel(new DefaultComboBoxModel(TranslateGizmoSpace.values()));
        cboRotateSpace.setModel(new DefaultComboBoxModel(RotateGizmoSpace.values()));

        cboTranslateSpace.setSelectedItem(TranslateGizmoSpace.LOCAL);
        cboRotateSpace.setSelectedItem(RotateGizmoSpace.LOCAL);

        cboTranslateSpace.setRenderer(new TransformSpaceRenderer());
        cboRotateSpace.setRenderer(new TransformSpaceRenderer());

        SwingUtilities.updateComponentTreeUI(this);
        GlobalObjects.getInstance().registerListener(this);

        AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(480);
        settings.setVSync(true);

        //settings.set
        viewport = new SandboxViewport();
        viewport.setSettings(settings);

        viewport.createCanvas(); // create canvas!
        JmeCanvasContext ctx = (JmeCanvasContext) viewport.getContext();
        ctx.setSystemListener(viewport);
        Dimension dim = new Dimension(640, 480);
        ctx.getCanvas().setPreferredSize(dim);
        ctx.getCanvas().setMinimumSize(new Dimension(320, 240));
        DropTarget dropTarget = new DropTarget(ctx.getCanvas(), this);
        viewport.startCanvas();

        pnlViewPort.setLeftComponent(ctx.getCanvas());
        propertiesPanel1.validate();

        createProjectDialog = new CreateProjectDialog(this, true);
        createObjectDialog = new CreateKlatchDialog(this, true);
        createObjectDialog.setExtension("rig");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sceneChooser = new javax.swing.JFileChooser();
        gizmoButtonGroup = new javax.swing.ButtonGroup();
        jToggleButton1 = new javax.swing.JToggleButton();
        pnlMainSplitPane = new javax.swing.JSplitPane();
        pnlProjectSplit = new javax.swing.JSplitPane();
        projectPanel1 = new dae.gui.ProjectPanel();
        assetPanel2 = new dae.gui.AssetPanel();
        pnlOutputSplit = new javax.swing.JSplitPane();
        pnlTabOutputs = new javax.swing.JTabbedPane();
        outputPanel1 = new dae.gui.OutputPanel();
        pnlToolbarViewport = new javax.swing.JPanel();
        pnlViewPort = new javax.swing.JSplitPane();
        scrProperties = new javax.swing.JScrollPane();
        propertiesPanel1 = new dae.prefabs.ui.PropertiesPanel();
        pnlToolbar = new javax.swing.JPanel();
        gizmoToolbar = new javax.swing.JToolBar();
        btnLink = new javax.swing.JToggleButton();
        btnMove = new javax.swing.JToggleButton();
        pnlTranslateSpaceChoice = new javax.swing.JPanel();
        cboTranslateSpace = new javax.swing.JComboBox();
        btnRotate = new javax.swing.JToggleButton();
        cboRotateSpace = new javax.swing.JComboBox();
        zoomToolbar = new javax.swing.JToolBar();
        btnZoomExtents = new javax.swing.JButton();
        snapToolbar = new javax.swing.JToolBar();
        toggleSnap = new javax.swing.JToggleButton();
        toggleAutogrid = new javax.swing.JToggleButton();
        brushToolbar = new javax.swing.JToolBar();
        btnToggleBrush = new javax.swing.JToggleButton();
        cboBrushes = new javax.swing.JComboBox();
        playToolbar = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JToggleButton();
        mnuSandboxMenu = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuNewProject = new javax.swing.JMenuItem();
        mnuOpenScene = new javax.swing.JMenuItem();
        mnuImportScene = new javax.swing.JMenuItem();
        openSeparator = new javax.swing.JPopupMenu.Separator();
        mnuSaveProject = new javax.swing.JMenuItem();
        mnuExit = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenu();
        mnuUndo = new javax.swing.JMenuItem();
        mnuRedo = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuCutAction = new javax.swing.JMenuItem();
        mnuPaste = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuPreferences = new javax.swing.JMenuItem();
        mnuComponents = new javax.swing.JMenu();
        mnuAddPhysicsBox = new javax.swing.JMenuItem();
        mnuConvexShape = new javax.swing.JMenuItem();
        mnuAddCollider = new javax.swing.JMenuItem();
        mnuPhysicsTerrain = new javax.swing.JMenuItem();
        mnuCharacterController = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuAddBoxComponent = new javax.swing.JMenuItem();
        mnuAddCylinderComponent = new javax.swing.JMenuItem();
        mnuAddSphereComponent = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuAddAnimationComponent = new javax.swing.JMenuItem();
        mnuAddPath = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnuAddPersonalityComponent = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnuRemoveComponent = new javax.swing.JMenuItem();
        mnuStandardObjects = new javax.swing.JMenu();
        mnuTerrain = new javax.swing.JMenuItem();
        mnuCreateTerrainBrush = new javax.swing.JMenuItem();
        mnuEntities = new javax.swing.JMenu();
        mnuAddCamera = new javax.swing.JMenuItem();
        mnuAddSound = new javax.swing.JMenuItem();
        mnuAddNPC = new javax.swing.JMenuItem();
        mnuAdd = new javax.swing.JMenu();
        mnuCreateRig = new javax.swing.JMenuItem();
        mnuAddRevoluteJoint = new javax.swing.JMenuItem();
        mnuAddFreeJoint = new javax.swing.JMenuItem();
        mnuAddTarget = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuAddEye = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuAddHandle = new javax.swing.JMenuItem();
        mnuAdd2HandleAxis = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        munAddFootcurve = new javax.swing.JMenuItem();
        mnuHandCurve = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuAddCharacterPath = new javax.swing.JMenuItem();
        mnuAddWaypoint = new javax.swing.JMenuItem();
        mnuLights = new javax.swing.JMenu();
        mnuAddAmbientLight = new javax.swing.JMenuItem();
        mnuAddSpotLight = new javax.swing.JMenuItem();
        mnuAddDirectionalLight = new javax.swing.JMenuItem();
        mnuSpotLight = new javax.swing.JMenuItem();
        mnuPhysics = new javax.swing.JMenu();
        mnuAddCrate = new javax.swing.JMenuItem();
        mnuAddSphere = new javax.swing.JMenuItem();
        mnuAddCylinder = new javax.swing.JMenuItem();
        mnuAddTriggerBox = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuAddHingeJoint = new javax.swing.JMenuItem();
        mnuMetaData = new javax.swing.JMenu();
        mnuAddPivot = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuGettingStarted = new javax.swing.JMenuItem();

        sceneChooser.setAcceptAllFileFilterUsed(false);
        sceneChooser.setFileFilter(new FileNameExtensionFilter("Sandbox files","zbk"));

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DAE Sandbox v1.0");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlProjectSplit.setDividerLocation(200);
        pnlProjectSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pnlProjectSplit.setResizeWeight(0.5);
        pnlProjectSplit.setLeftComponent(projectPanel1);
        pnlProjectSplit.setRightComponent(assetPanel2);

        pnlMainSplitPane.setLeftComponent(pnlProjectSplit);

        pnlOutputSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pnlOutputSplit.setResizeWeight(0.5);

        pnlTabOutputs.addTab("Output", outputPanel1);

        pnlOutputSplit.setRightComponent(pnlTabOutputs);

        pnlToolbarViewport.setLayout(new java.awt.BorderLayout());

        pnlViewPort.setResizeWeight(0.6);

        propertiesPanel1.setMinimumSize(null);
        propertiesPanel1.setPreferredSize(null);
        scrProperties.setViewportView(propertiesPanel1);

        pnlViewPort.setRightComponent(scrProperties);

        pnlToolbarViewport.add(pnlViewPort, java.awt.BorderLayout.CENTER);

        pnlToolbar.setMinimumSize(new java.awt.Dimension(70, 40));
        pnlToolbar.setPreferredSize(new java.awt.Dimension(70, 50));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT);
        flowLayout1.setAlignOnBaseline(true);
        pnlToolbar.setLayout(flowLayout1);

        gizmoToolbar.setRollover(true);

        gizmoButtonGroup.add(btnLink);
        btnLink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/link.png"))); // NOI18N
        btnLink.setFocusable(false);
        btnLink.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLink.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLink.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnLinkItemStateChanged(evt);
            }
        });
        gizmoToolbar.add(btnLink);

        gizmoButtonGroup.add(btnMove);
        btnMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/flat/translate.png"))); // NOI18N
        btnMove.setFocusable(false);
        btnMove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMove.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnMoveItemStateChanged(evt);
            }
        });
        gizmoToolbar.add(btnMove);

        pnlTranslateSpaceChoice.setLayout(new javax.swing.BoxLayout(pnlTranslateSpaceChoice, javax.swing.BoxLayout.X_AXIS));
        gizmoToolbar.add(pnlTranslateSpaceChoice);

        cboTranslateSpace.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboTranslateSpaceItemStateChanged(evt);
            }
        });
        gizmoToolbar.add(cboTranslateSpace);

        gizmoButtonGroup.add(btnRotate);
        btnRotate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/flat/rotate.png"))); // NOI18N
        btnRotate.setFocusable(false);
        btnRotate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRotate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRotate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnRotateItemStateChanged(evt);
            }
        });
        gizmoToolbar.add(btnRotate);

        cboRotateSpace.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboRotateSpaceItemStateChanged(evt);
            }
        });
        gizmoToolbar.add(cboRotateSpace);

        pnlToolbar.add(gizmoToolbar);

        zoomToolbar.setRollover(true);

        btnZoomExtents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/zoom_extend.png"))); // NOI18N
        btnZoomExtents.setFocusable(false);
        btnZoomExtents.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomExtents.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomExtents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomExtentsActionPerformed(evt);
            }
        });
        zoomToolbar.add(btnZoomExtents);

        pnlToolbar.add(zoomToolbar);

        snapToolbar.setRollover(true);

        toggleSnap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/enablesnaptool.png"))); // NOI18N
        toggleSnap.setFocusable(false);
        toggleSnap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleSnap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        snapToolbar.add(toggleSnap);

        toggleAutogrid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/autogrid.png"))); // NOI18N
        toggleAutogrid.setFocusable(false);
        toggleAutogrid.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleAutogrid.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleAutogrid.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleAutogridItemStateChanged(evt);
            }
        });
        snapToolbar.add(toggleAutogrid);

        pnlToolbar.add(snapToolbar);

        brushToolbar.setRollover(true);

        gizmoButtonGroup.add(btnToggleBrush);
        btnToggleBrush.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/flat/brush.png"))); // NOI18N
        btnToggleBrush.setFocusable(false);
        btnToggleBrush.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnToggleBrush.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnToggleBrush.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnToggleBrush.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnToggleBrushItemStateChanged(evt);
            }
        });
        brushToolbar.add(btnToggleBrush);

        cboBrushes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboBrushesItemStateChanged(evt);
            }
        });
        brushToolbar.add(cboBrushes);

        pnlToolbar.add(brushToolbar);

        playToolbar.setRollover(true);

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/flat/play.png"))); // NOI18N
        btnPlay.setFocusable(false);
        btnPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlay.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/dae/icons/flat/pause.png"))); // NOI18N
        btnPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlay.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnPlayItemStateChanged(evt);
            }
        });
        playToolbar.add(btnPlay);

        pnlToolbar.add(playToolbar);

        pnlToolbarViewport.add(pnlToolbar, java.awt.BorderLayout.NORTH);

        pnlOutputSplit.setTopComponent(pnlToolbarViewport);

        pnlMainSplitPane.setRightComponent(pnlOutputSplit);

        mnuFile.setText("File");

        mnuNewProject.setText("New Project ...");
        mnuNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewProjectActionPerformed(evt);
            }
        });
        mnuFile.add(mnuNewProject);

        mnuOpenScene.setText("Open Project ...");
        mnuOpenScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenSceneActionPerformed(evt);
            }
        });
        mnuFile.add(mnuOpenScene);

        mnuImportScene.setText("Import Scene ...");
        mnuImportScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportSceneActionPerformed(evt);
            }
        });
        mnuFile.add(mnuImportScene);
        mnuFile.add(openSeparator);

        mnuSaveProject.setText("Save Project");
        mnuSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveProjectActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSaveProject);

        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        mnuSandboxMenu.add(mnuFile);

        mnuEdit.setText("Edit");
        mnuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditActionPerformed(evt);
            }
        });

        mnuUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        mnuUndo.setText("Undo");
        mnuUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUndoActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuUndo);

        mnuRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        mnuRedo.setText("Redo");
        mnuRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRedoActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuRedo);
        mnuEdit.add(jSeparator9);

        mnuCutAction.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuCutAction.setText("Cut");
        mnuCutAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCutActionActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuCutAction);

        mnuPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        mnuPaste.setText("Paste");
        mnuPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPasteActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuPaste);
        mnuEdit.add(jSeparator10);

        mnuPreferences.setText("Preferences ...");
        mnuPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPreferencesActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuPreferences);

        mnuSandboxMenu.add(mnuEdit);

        mnuComponents.setText("Components");

        mnuAddPhysicsBox.setText("Add Physics Box");
        mnuAddPhysicsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddPhysicsBoxActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddPhysicsBox);

        mnuConvexShape.setText("Add Physics Mesh");
        mnuConvexShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConvexShapeActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuConvexShape);

        mnuAddCollider.setText("Add Physics Collider");
        mnuAddCollider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddColliderActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddCollider);

        mnuPhysicsTerrain.setText("Add Physics Terrain");
        mnuPhysicsTerrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPhysicsTerrainActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuPhysicsTerrain);

        mnuCharacterController.setText("Add Character Controller");
        mnuCharacterController.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCharacterControllerActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuCharacterController);
        mnuComponents.add(jSeparator5);

        mnuAddBoxComponent.setText("Add Box");
        mnuAddBoxComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddBoxComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddBoxComponent);

        mnuAddCylinderComponent.setText("Add Cylinder");
        mnuAddCylinderComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddCylinderComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddCylinderComponent);

        mnuAddSphereComponent.setText("Add Sphere");
        mnuAddSphereComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddSphereComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddSphereComponent);
        mnuComponents.add(jSeparator8);

        mnuAddAnimationComponent.setText("Add Animation Control");
        mnuAddAnimationComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddAnimationComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddAnimationComponent);

        mnuAddPath.setText("Add Path Control");
        mnuAddPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddPathActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddPath);
        mnuComponents.add(jSeparator6);

        mnuAddPersonalityComponent.setText("Add Personality Component");
        mnuAddPersonalityComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddPersonalityComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuAddPersonalityComponent);
        mnuComponents.add(jSeparator7);

        mnuRemoveComponent.setText("Remove Component ...");
        mnuRemoveComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRemoveComponentActionPerformed(evt);
            }
        });
        mnuComponents.add(mnuRemoveComponent);

        mnuSandboxMenu.add(mnuComponents);

        mnuStandardObjects.setText("Create");

        mnuTerrain.setText("Terrain");
        mnuTerrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTerrainActionPerformed(evt);
            }
        });
        mnuStandardObjects.add(mnuTerrain);

        mnuCreateTerrainBrush.setText("Terrain Brush");
        mnuCreateTerrainBrush.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateTerrainBrushActionPerformed(evt);
            }
        });
        mnuStandardObjects.add(mnuCreateTerrainBrush);

        mnuSandboxMenu.add(mnuStandardObjects);

        mnuEntities.setText("Entities");

        mnuAddCamera.setText("Add Camera");
        mnuAddCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddCameraActionPerformed(evt);
            }
        });
        mnuEntities.add(mnuAddCamera);

        mnuAddSound.setText("Add Sound");
        mnuAddSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddSoundActionPerformed(evt);
            }
        });
        mnuEntities.add(mnuAddSound);

        mnuAddNPC.setText("Add NPC");
        mnuAddNPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddNPCActionPerformed(evt);
            }
        });
        mnuEntities.add(mnuAddNPC);

        mnuSandboxMenu.add(mnuEntities);

        mnuAdd.setText("Animation");

        mnuCreateRig.setText("Create Rig ...");
        mnuCreateRig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCreateRigActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuCreateRig);

        mnuAddRevoluteJoint.setText("Add Revolute Joint");
        mnuAddRevoluteJoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddRevoluteJointActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddRevoluteJoint);

        mnuAddFreeJoint.setText("Add Free Joint");
        mnuAddFreeJoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddFreeJointActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddFreeJoint);

        mnuAddTarget.setText("Add Target");
        mnuAddTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddTargetActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddTarget);
        mnuAdd.add(jSeparator4);

        mnuAddEye.setText("Add Eye");
        mnuAddEye.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddEyeActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddEye);
        mnuAdd.add(jSeparator1);

        mnuAddHandle.setText("Add Handle");
        mnuAddHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddHandleActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddHandle);

        mnuAdd2HandleAxis.setText("Add Handle2Axis");
        mnuAdd2HandleAxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAdd2HandleAxisActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAdd2HandleAxis);

        jMenuItem1.setText("Add Handshake");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuAdd.add(jMenuItem1);

        munAddFootcurve.setText("Add Footcurve");
        munAddFootcurve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                munAddFootcurveActionPerformed(evt);
            }
        });
        mnuAdd.add(munAddFootcurve);

        mnuHandCurve.setText("Add Handcurve");
        mnuHandCurve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHandCurveActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuHandCurve);
        mnuAdd.add(jSeparator2);

        mnuAddCharacterPath.setText("Add Character Path");
        mnuAddCharacterPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddCharacterPathActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddCharacterPath);

        mnuAddWaypoint.setText("Add Waypoint");
        mnuAddWaypoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddWaypointActionPerformed(evt);
            }
        });
        mnuAdd.add(mnuAddWaypoint);

        mnuSandboxMenu.add(mnuAdd);

        mnuLights.setText("Lights");

        mnuAddAmbientLight.setText("Add Ambient Light");
        mnuAddAmbientLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddAmbientLightActionPerformed(evt);
            }
        });
        mnuLights.add(mnuAddAmbientLight);

        mnuAddSpotLight.setText("Add Pointlight");
        mnuAddSpotLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddSpotLightActionPerformed(evt);
            }
        });
        mnuLights.add(mnuAddSpotLight);

        mnuAddDirectionalLight.setText("Add Directional Light");
        mnuAddDirectionalLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddDirectionalLightActionPerformed(evt);
            }
        });
        mnuLights.add(mnuAddDirectionalLight);

        mnuSpotLight.setText("Add Spotlight");
        mnuSpotLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSpotLightActionPerformed(evt);
            }
        });
        mnuLights.add(mnuSpotLight);

        mnuSandboxMenu.add(mnuLights);

        mnuPhysics.setText("Physics");
        mnuPhysics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPhysicsActionPerformed(evt);
            }
        });

        mnuAddCrate.setText("Add Crate");
        mnuAddCrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddCrateActionPerformed(evt);
            }
        });
        mnuPhysics.add(mnuAddCrate);

        mnuAddSphere.setText("Add Sphere");
        mnuAddSphere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddSphereActionPerformed(evt);
            }
        });
        mnuPhysics.add(mnuAddSphere);

        mnuAddCylinder.setText("Add Cylinder");
        mnuAddCylinder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddCylinderActionPerformed(evt);
            }
        });
        mnuPhysics.add(mnuAddCylinder);

        mnuAddTriggerBox.setText("Add Triggerbox");
        mnuAddTriggerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddTriggerBoxActionPerformed(evt);
            }
        });
        mnuPhysics.add(mnuAddTriggerBox);
        mnuPhysics.add(jSeparator3);

        mnuAddHingeJoint.setText("Add Hinge Joint");
        mnuAddHingeJoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddHingeJointActionPerformed(evt);
            }
        });
        mnuPhysics.add(mnuAddHingeJoint);

        mnuSandboxMenu.add(mnuPhysics);

        mnuMetaData.setText("Metadata");

        mnuAddPivot.setText("Add Pivot");
        mnuAddPivot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAddPivotActionPerformed(evt);
            }
        });
        mnuMetaData.add(mnuAddPivot);

        mnuSandboxMenu.add(mnuMetaData);

        mnuHelp.setText("Help");

        mnuGettingStarted.setText("Getting started");
        mnuGettingStarted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGettingStartedActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuGettingStarted);

        mnuSandboxMenu.add(mnuHelp);

        setJMenuBar(mnuSandboxMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1063, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuOpenSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenSceneActionPerformed
        String recentFiles = Preferences.userRoot().get("RecentFiles", "");
        int colonIndex = recentFiles.lastIndexOf('\0');
        String lastFile = recentFiles.substring(colonIndex + 1);
        File file = new File(lastFile);
        if (file.exists()) {
            sceneChooser.setCurrentDirectory(file.getParentFile());
        }
        sceneChooser.setFileFilter(this.sandboxFilter);
        int choice = sceneChooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File projectFile = sceneChooser.getSelectedFile();
            this.loadProject(projectFile);

            GlobalObjects.getInstance().addRecentFile(projectFile);
        }
    }//GEN-LAST:event_mnuOpenSceneActionPerformed

    private void mnuNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewProjectActionPerformed
        createProjectDialog.setLocationRelativeTo(this);
        createProjectDialog.setVisible(true);

        createProjectDialog.clear();
        if (createProjectDialog.getReturnStatus() == CreateProjectDialog.RET_OK) {
            try {
                Project newProject = createProjectDialog.getResult();
                File projectFile = new File(newProject.getProjectLocation(), newProject.getProjectName() + ".zbk");
                ProjectSaver.write(newProject, projectFile);
                setCurrentProject(newProject);

                GlobalObjects.getInstance().addRecentFile(projectFile);
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_mnuNewProjectActionPerformed

    private void mnuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditActionPerformed
    }//GEN-LAST:event_mnuEditActionPerformed

    private void mnuPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPreferencesActionPerformed
        PreferencesDialog dialog = new PreferencesDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_mnuPreferencesActionPerformed

    private void mnuAddCrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddCrateActionPerformed
        createObject("Standard", "Crate");
    }//GEN-LAST:event_mnuAddCrateActionPerformed

    private void mnuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUndoActionPerformed
        GlobalObjects.getInstance().undo();
    }//GEN-LAST:event_mnuUndoActionPerformed

    private void mnuRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRedoActionPerformed
        GlobalObjects.getInstance().redo();
    }//GEN-LAST:event_mnuRedoActionPerformed

    private void mnuSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveProjectActionPerformed
        saveProject();
    }//GEN-LAST:event_mnuSaveProjectActionPerformed

    private void btnLinkItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnLinkItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GlobalObjects.getInstance().postEvent(new GizmoEvent(this, GizmoType.LINK));
        }

    }//GEN-LAST:event_btnLinkItemStateChanged

    private void btnMoveItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnMoveItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GlobalObjects.getInstance().postEvent(new GizmoEvent(this, GizmoType.TRANSLATE));
        }
    }//GEN-LAST:event_btnMoveItemStateChanged

    private void btnRotateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnRotateItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GlobalObjects.getInstance().postEvent(new GizmoEvent(this, GizmoType.ROTATE));
        }
    }//GEN-LAST:event_btnRotateItemStateChanged

    private void mnuSpotLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSpotLightActionPerformed
        createObject("Light", "SpotLight");
    }//GEN-LAST:event_mnuSpotLightActionPerformed

    private void mnuPhysicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPhysicsActionPerformed

    }//GEN-LAST:event_mnuPhysicsActionPerformed

    private void mnuAddSphereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddSphereActionPerformed
        createObject("Standard", "Sphere");
    }//GEN-LAST:event_mnuAddSphereActionPerformed

    private void mnuAddCylinderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddCylinderActionPerformed
        createObject("Standard", "Cylinder");
    }//GEN-LAST:event_mnuAddCylinderActionPerformed

    private void mnuAddHingeJointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddHingeJointActionPerformed
        createObject("Standard", "HingeJoint");
    }//GEN-LAST:event_mnuAddHingeJointActionPerformed

    private void mnuAddPivotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddPivotActionPerformed
        createObject("Metadata", "Pivot");
    }//GEN-LAST:event_mnuAddPivotActionPerformed

    private void btnZoomExtentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomExtentsActionPerformed
        ZoomEvent ze = new ZoomEvent(ZoomEventType.EXTENTS_SELECTED);
        GlobalObjects.getInstance().postEvent(ze);
    }//GEN-LAST:event_btnZoomExtentsActionPerformed

    private void mnuAddSpotLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddSpotLightActionPerformed
        createObject("Light", "PointLight");
    }//GEN-LAST:event_mnuAddSpotLightActionPerformed

    private void mnuAddDirectionalLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddDirectionalLightActionPerformed
        createObject("Light", "DirectionalLight");
    }//GEN-LAST:event_mnuAddDirectionalLightActionPerformed

    private void mnuAddAmbientLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddAmbientLightActionPerformed
        createObject("Light", "AmbientLight");
    }//GEN-LAST:event_mnuAddAmbientLightActionPerformed

    private void cboTranslateSpaceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTranslateSpaceItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            TranslateGizmoSpace tgc = (TranslateGizmoSpace) evt.getItem();
            GlobalObjects.getInstance().postEvent(new GizmoSpaceChangedEvent(tgc));
        }
    }//GEN-LAST:event_cboTranslateSpaceItemStateChanged

    private void cboRotateSpaceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboRotateSpaceItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            RotateGizmoSpace tgc = (RotateGizmoSpace) evt.getItem();
            GlobalObjects.getInstance().postEvent(new RotateGizmoSpaceChangedEvent(tgc));
        }
    }//GEN-LAST:event_cboRotateSpaceItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!currentProject.hasFileLocation() || !currentProject.getSaved()) {
            int option = JOptionPane.showConfirmDialog(this, "<html><h3>Save changes to " + currentProject.getProjectName() + " before closing?</h3>"
                    + "If you close without saving your changes will be discarded.", "Save project", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveProject();
            }
        }
        GlobalObjects.getInstance().postEvent(new ApplicationStoppedEvent());
    }//GEN-LAST:event_formWindowClosing

    private void toggleAutogridItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toggleAutogridItemStateChanged
        AutoGridEvent age = new AutoGridEvent(evt.getStateChange() == ItemEvent.SELECTED, AxisEnum.Y);
        GlobalObjects.getInstance().postEvent(age);

    }//GEN-LAST:event_toggleAutogridItemStateChanged

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuGettingStartedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGettingStartedActionPerformed
        File indexFile = new File(System.getProperty("user.dir"), "docs/html/index.html");
        try {
            Desktop.getDesktop().browse(indexFile.toURI());
        } catch (IOException ex) {
            Logger.getLogger(SandboxFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuGettingStartedActionPerformed

    private void mnuAddCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddCameraActionPerformed
        createObject("Standard", "Camera");
    }//GEN-LAST:event_mnuAddCameraActionPerformed

    private void mnuImportSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportSceneActionPerformed
        if (currentProject.hasFileLocation()) {
            sceneChooser.setFileFilter(sceneFilter);
            int option = this.sceneChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File sceneFile = sceneChooser.getSelectedFile();
                System.out.println("selected file : " + sceneFile);
                String levelName = Files.getNameWithoutExtension(sceneFile.getName());
                if (!currentProject.hasLevel(levelName)) {
                    dae.project.Level newLevel = new dae.project.Level(levelName, false);
                    File projectLocation = currentProject.getProjectLocation();
                    File levelDir = new File(projectLocation.getParentFile(), "levels/" + levelName);
                    if (!levelDir.exists()) {
                        levelDir.mkdir();
                    }

                    File dest = new File(levelDir, sceneFile.getName());
                    try {
                        Files.copy(sceneFile, dest);
                        File relativeLocation = new File("levels/" + levelName + "/" + sceneFile.getName());
                        newLevel.setLocation(relativeLocation);
                        newLevel.setRelativeLocation(true);
                        AssetManager manager = GlobalObjects.getInstance().getAssetManager();
                        SceneLoader.loadScene(sceneFile, manager, newLevel, GlobalObjects.getInstance().getObjectsTypeCategory(),
                                manager.loadMaterial("Materials/SelectionMaterial.j3m"));
                        currentProject.addLevel(newLevel);
                        newLevel.setChanged();
                        ProjectEvent pe = new ProjectEvent(currentProject, ProjectEventType.LEVELADDED, this);
                        pe.setLevel(newLevel);
                        GlobalObjects.getInstance().postEvent(pe);
                    } catch (IOException ex) {
                        Logger.getLogger("DArtE").log(Level.SEVERE, "Could not copy {0} to {1}", new Object[]{sceneFile.getPath(), dest.getPath()});
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Save the project first before you import a scene!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_mnuImportSceneActionPerformed

    private void mnuAddSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddSoundActionPerformed
        createObject("Standard", "Sound");
    }//GEN-LAST:event_mnuAddSoundActionPerformed

    private void mnuAddTriggerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddTriggerBoxActionPerformed
        createObject("Standard", "Trigger");
    }//GEN-LAST:event_mnuAddTriggerBoxActionPerformed

    private void mnuAddNPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddNPCActionPerformed
        createObject("Standard", "J3ONPC", "Characters/Male/Character1/character1.j3o");
    }//GEN-LAST:event_mnuAddNPCActionPerformed

    private void mnuAddPhysicsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddPhysicsBoxActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PhysicsBoxComponent"));
    }//GEN-LAST:event_mnuAddPhysicsBoxActionPerformed

    private void mnuAddAnimationComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddAnimationComponentActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("AnimationComponent"));
    }//GEN-LAST:event_mnuAddAnimationComponentActionPerformed

    private void mnuTerrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTerrainActionPerformed
        createObject("Terrain", "Terrain");
    }//GEN-LAST:event_mnuTerrainActionPerformed

    private void mnuPhysicsTerrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPhysicsTerrainActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PhysicsTerrainComponent"));
    }//GEN-LAST:event_mnuPhysicsTerrainActionPerformed

    private void mnuCharacterControllerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCharacterControllerActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("CharacterControllerComponent"));
    }//GEN-LAST:event_mnuCharacterControllerActionPerformed

    private void mnuAddPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddPathActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PathComponent"));
    }//GEN-LAST:event_mnuAddPathActionPerformed

    private void mnuConvexShapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConvexShapeActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PhysicsConvexComponent"));
    }//GEN-LAST:event_mnuConvexShapeActionPerformed

    private void mnuAddColliderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddColliderActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PhysicsConcaveComponent"));
    }//GEN-LAST:event_mnuAddColliderActionPerformed

    private void mnuAddPersonalityComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddPersonalityComponentActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("PersonalityComponent"));
    }//GEN-LAST:event_mnuAddPersonalityComponentActionPerformed

    private void btnToggleBrushItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnToggleBrushItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GlobalObjects.getInstance().postEvent(new GizmoEvent(this, GizmoType.BRUSH));
        }
    }//GEN-LAST:event_btnToggleBrushItemStateChanged

    private void btnPlayItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnPlayItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            GlobalObjects.getInstance().postEvent(new PlayEvent(PlayEventType.PLAY));
        } else {
            GlobalObjects.getInstance().postEvent(new PlayEvent(PlayEventType.PAUSE));
        }
    }//GEN-LAST:event_btnPlayItemStateChanged

    private void mnuCreateTerrainBrushActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateTerrainBrushActionPerformed
        createObject("Terrain", "Brush", null);

    }//GEN-LAST:event_mnuCreateTerrainBrushActionPerformed

    private void cboBrushesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboBrushesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Brush selected = (Brush) evt.getItem();
            GlobalObjects.getInstance().postEvent(new BrushEvent(BrushEventType.SELECTED, selected));
        }
    }//GEN-LAST:event_cboBrushesItemStateChanged

    private void mnuRemoveComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRemoveComponentActionPerformed
        // select a component and remove it.
        if (selectedPrefab != null) {
            if (removeComponentDialog == null) {
                removeComponentDialog = new RemoveComponentDialog(this, true);
            }
            removeComponentDialog.setPrefab(selectedPrefab);
            removeComponentDialog.setVisible(true);
        }
    }//GEN-LAST:event_mnuRemoveComponentActionPerformed

    private void mnuAddWaypointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddWaypointActionPerformed
        createObject("Animation", "Waypoint");
    }//GEN-LAST:event_mnuAddWaypointActionPerformed

    private void mnuAddCharacterPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddCharacterPathActionPerformed
        createObject("Animation", "CharacterPath");
    }//GEN-LAST:event_mnuAddCharacterPathActionPerformed

    private void mnuHandCurveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHandCurveActionPerformed
        createObject("Animation", "Handcurve");
    }//GEN-LAST:event_mnuHandCurveActionPerformed

    private void munAddFootcurveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_munAddFootcurveActionPerformed
        createObject("Animation", "Footcurve");
    }//GEN-LAST:event_munAddFootcurveActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        createObject("Animation", "Handshake");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mnuAdd2HandleAxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAdd2HandleAxisActionPerformed
        createObject("Animation", "TwoAxisHandle");
    }//GEN-LAST:event_mnuAdd2HandleAxisActionPerformed

    private void mnuAddHandleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddHandleActionPerformed
        createObject("Animation", "Handle");
    }//GEN-LAST:event_mnuAddHandleActionPerformed

    private void mnuAddTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddTargetActionPerformed
        createObject("Animation", "AttachmentPoint");
    }//GEN-LAST:event_mnuAddTargetActionPerformed

    private void mnuAddFreeJointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddFreeJointActionPerformed
        createObject("Animation", "RevoluteJointTwoAxis", null);
    }//GEN-LAST:event_mnuAddFreeJointActionPerformed

    private void mnuAddRevoluteJointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddRevoluteJointActionPerformed
        createObject("Animation", "RevoluteJoint");
    }//GEN-LAST:event_mnuAddRevoluteJointActionPerformed

    private void mnuCreateRigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCreateRigActionPerformed
        if (currentProject.hasFileLocation()) {
            createObjectDialog.setTitle("Create Rig");
            createObjectDialog.setCurrentProject(this.currentProject);
            createObjectDialog.setVisible(true);
            if (createObjectDialog.getReturnStatus() == CreateKlatchDialog.RET_OK) {
                String rigLocation = createObjectDialog.getAssemblyName();
                // Create a default body.
                ObjectTypeCategory otc = viewport.getObjectsToCreate();
                ObjectType ot = otc.getObjectType("Animation", "Rig");
                if (ot != null) {
                    Rig rig;
                    rig = (Rig) ot.createDefault(viewport.getAssetManager(), "rig", true);

                    File klatchDir = currentProject.getKlatchDirectory();
                    File rigFile = new File(klatchDir, rigLocation);
                    RigWriter.writeRig(rigFile, rig);

                    AssetEvent ae = new AssetEvent(AssetEventType.EDIT, FileNode.createFromPath(rigLocation));
                    GlobalObjects.getInstance().postEvent(ae);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "<html>You must save the project before<br> you can create a rig!", "Project not saved", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_mnuCreateRigActionPerformed

    private void mnuAddEyeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddEyeActionPerformed

        createObject("Animation", "Eye");
    }//GEN-LAST:event_mnuAddEyeActionPerformed

    private void mnuAddCylinderComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddCylinderComponentActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("CylinderComponent"));
    }//GEN-LAST:event_mnuAddCylinderComponentActionPerformed

    private void mnuAddBoxComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddBoxComponentActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("BoxComponent"));
    }//GEN-LAST:event_mnuAddBoxComponentActionPerformed

    private void mnuAddSphereComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAddSphereComponentActionPerformed
        GlobalObjects.getInstance().postEvent(new ComponentEvent("SphereComponent"));
    }//GEN-LAST:event_mnuAddSphereComponentActionPerformed

    private void mnuCutActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCutActionActionPerformed
        GlobalObjects.getInstance().postEvent(new CutCopyPasteEvent(CutCopyPasteType.CUT));
    }//GEN-LAST:event_mnuCutActionActionPerformed

    private void mnuPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPasteActionPerformed
        GlobalObjects.getInstance().postEvent(new CutCopyPasteEvent(CutCopyPasteType.PASTE));
    }//GEN-LAST:event_mnuPasteActionPerformed

    private void createObject(String category, String type) {
        createObject(category, type, null);
    }

    private void createObject(String category, String type, String extraInfo) {
        ObjectTypeCategory otc = viewport.getObjectsToCreate();
        ObjectType ot = otc.getObjectType(category, type);
        if (ot != null) {
            CreateObjectEvent coe = new CreateObjectEvent(ot.getObjectToCreate(), extraInfo, ot);
            ot.setExtraInfo(extraInfo);
            GlobalObjects.getInstance().postEvent(coe);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger("DArtE").log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Logger logger = Logger.getLogger("DArtE");
                FileHandler fh;

                try {
                    File logDir = new File(System.getProperty("user.dir") + "/logs");
                    if (!logDir.exists()) {
                        logDir.mkdir();
                    }
                    // This block configure the logger with handler and formatter  
                    fh = new FileHandler(System.getProperty("user.dir") + "/logs/darte.log");

                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();
                    fh.setFormatter(formatter);

                    // the following statement is used to log any messages  
                    logger.setUseParentHandlers(false);

                } catch (IOException ex) {
                    Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
                }

                new SandboxFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dae.gui.AssetPanel assetPanel2;
    private javax.swing.JToolBar brushToolbar;
    private javax.swing.JToggleButton btnLink;
    private javax.swing.JToggleButton btnMove;
    private javax.swing.JToggleButton btnPlay;
    private javax.swing.JToggleButton btnRotate;
    private javax.swing.JToggleButton btnToggleBrush;
    private javax.swing.JButton btnZoomExtents;
    private javax.swing.JComboBox cboBrushes;
    private javax.swing.JComboBox cboRotateSpace;
    private javax.swing.JComboBox cboTranslateSpace;
    private javax.swing.ButtonGroup gizmoButtonGroup;
    private javax.swing.JToolBar gizmoToolbar;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JMenu mnuAdd;
    private javax.swing.JMenuItem mnuAdd2HandleAxis;
    private javax.swing.JMenuItem mnuAddAmbientLight;
    private javax.swing.JMenuItem mnuAddAnimationComponent;
    private javax.swing.JMenuItem mnuAddBoxComponent;
    private javax.swing.JMenuItem mnuAddCamera;
    private javax.swing.JMenuItem mnuAddCharacterPath;
    private javax.swing.JMenuItem mnuAddCollider;
    private javax.swing.JMenuItem mnuAddCrate;
    private javax.swing.JMenuItem mnuAddCylinder;
    private javax.swing.JMenuItem mnuAddCylinderComponent;
    private javax.swing.JMenuItem mnuAddDirectionalLight;
    private javax.swing.JMenuItem mnuAddEye;
    private javax.swing.JMenuItem mnuAddFreeJoint;
    private javax.swing.JMenuItem mnuAddHandle;
    private javax.swing.JMenuItem mnuAddHingeJoint;
    private javax.swing.JMenuItem mnuAddNPC;
    private javax.swing.JMenuItem mnuAddPath;
    private javax.swing.JMenuItem mnuAddPersonalityComponent;
    private javax.swing.JMenuItem mnuAddPhysicsBox;
    private javax.swing.JMenuItem mnuAddPivot;
    private javax.swing.JMenuItem mnuAddRevoluteJoint;
    private javax.swing.JMenuItem mnuAddSound;
    private javax.swing.JMenuItem mnuAddSphere;
    private javax.swing.JMenuItem mnuAddSphereComponent;
    private javax.swing.JMenuItem mnuAddSpotLight;
    private javax.swing.JMenuItem mnuAddTarget;
    private javax.swing.JMenuItem mnuAddTriggerBox;
    private javax.swing.JMenuItem mnuAddWaypoint;
    private javax.swing.JMenuItem mnuCharacterController;
    private javax.swing.JMenu mnuComponents;
    private javax.swing.JMenuItem mnuConvexShape;
    private javax.swing.JMenuItem mnuCreateRig;
    private javax.swing.JMenuItem mnuCreateTerrainBrush;
    private javax.swing.JMenuItem mnuCutAction;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuEntities;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuGettingStarted;
    private javax.swing.JMenuItem mnuHandCurve;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuImportScene;
    private javax.swing.JMenu mnuLights;
    private javax.swing.JMenu mnuMetaData;
    private javax.swing.JMenuItem mnuNewProject;
    private javax.swing.JMenuItem mnuOpenScene;
    private javax.swing.JMenuItem mnuPaste;
    private javax.swing.JMenu mnuPhysics;
    private javax.swing.JMenuItem mnuPhysicsTerrain;
    private javax.swing.JMenuItem mnuPreferences;
    private javax.swing.JMenuItem mnuRedo;
    private javax.swing.JMenuItem mnuRemoveComponent;
    private javax.swing.JMenuBar mnuSandboxMenu;
    private javax.swing.JMenuItem mnuSaveProject;
    private javax.swing.JMenuItem mnuSpotLight;
    private javax.swing.JMenu mnuStandardObjects;
    private javax.swing.JMenuItem mnuTerrain;
    private javax.swing.JMenuItem mnuUndo;
    private javax.swing.JMenuItem munAddFootcurve;
    private javax.swing.JPopupMenu.Separator openSeparator;
    private dae.gui.OutputPanel outputPanel1;
    private javax.swing.JToolBar playToolbar;
    private javax.swing.JSplitPane pnlMainSplitPane;
    private javax.swing.JSplitPane pnlOutputSplit;
    private javax.swing.JSplitPane pnlProjectSplit;
    private javax.swing.JTabbedPane pnlTabOutputs;
    private javax.swing.JPanel pnlToolbar;
    private javax.swing.JPanel pnlToolbarViewport;
    private javax.swing.JPanel pnlTranslateSpaceChoice;
    private javax.swing.JSplitPane pnlViewPort;
    private dae.gui.ProjectPanel projectPanel1;
    private dae.prefabs.ui.PropertiesPanel propertiesPanel1;
    private javax.swing.JFileChooser sceneChooser;
    private javax.swing.JScrollPane scrProperties;
    private javax.swing.JToolBar snapToolbar;
    private javax.swing.JToggleButton toggleAutogrid;
    private javax.swing.JToggleButton toggleSnap;
    private javax.swing.JToolBar zoomToolbar;
    // End of variables declaration//GEN-END:variables

    /**
     * Sets the project as the current project. Following actions will be
     * performed: 1. The name of the window is changed to the name of the
     * project. 2. If only one level is present, that level will be opened. If
     * more the one level is present in the project, the last opened level will
     * be opened, if there is no metadata about the last opened level, the first
     * level in the list will be opened.
     *
     * @param newProject the current project to set.
     */
    private void setCurrentProject(Project newProject) {
        currentProject = newProject;
        this.setTitle("DAE Sandbox - " + newProject.getProjectName());
        GlobalObjects.getInstance().postEvent(new ProjectEvent(newProject, this));
    }

    private void loadProject(File file) {
        ProjectLoader pl = new ProjectLoader();
        Project p = pl.load(file, this.viewport.getAssetManager());
        setCurrentProject(p);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // something was dropped on the application
        //System.out.println("dtde:" + dtde.getDropAction());
        try {
            String asset = dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();
            int dotIndex = asset.lastIndexOf('.');
            if (dotIndex > 0) {
                String extension = asset.substring(dotIndex + 1).toLowerCase();
                if (extension.equals("j3o") || extension.equals("ovm")) {
                    ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Mesh");
                    CreateObjectEvent event = new CreateObjectEvent("dae.prefabs.standard.MeshObject", asset, ot);
                    MeshComponent mc = new MeshComponent();
                    mc.setMeshFile(asset);
                    event.addPrefabComponent(mc);
                    viewport.onObjectCreation(event);
                } else if (extension.equals("klatch")) {
                    ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Klatch");
                    ot.setExtraInfo(asset);
                    CreateObjectEvent event = new CreateObjectEvent("dae.prefabs.Klatch", asset, ot);
                    viewport.onObjectCreation(event);
                } else if (extension.equals("rig")) {
                    ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Animation", "Rig");
                    ot.setExtraInfo(asset);
                    CreateObjectEvent event = new CreateObjectEvent("dae.animation.rig.Rig", asset, ot);
                    viewport.onObjectCreation(event);
                } else if (extension.equals("wav") || extension.equals("ogg")) {
                    ObjectType ot = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Sound");
                    ot.setExtraInfo(asset);
                    CreateObjectEvent event = new CreateObjectEvent("dae.prefabs.standard.SoundEntity", asset, ot);
                    viewport.onObjectCreation(event);
                }

            }
        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }

    @Subscribe
    public void viewPortSizeChange(ViewportReshapeEvent event) {
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                if (!viewportLoaded) {
                    ArrayList<File> projectFiles = GlobalObjects.getInstance().getRecentFiles();
                    if (projectFiles.size() > 0) {
                        // load the last projectFile.
                        loadProject(projectFiles.get(0));
                    } else {
                        Project p = new Project();
                        setCurrentProject(p);
                    }
                    viewportLoaded = true;
                }
            }
        });
    }

    @Subscribe
    public void gizmoChanged(GizmoEvent event) {
        if (event.getSource() != this) {
            GizmoType type = event.getType();
            switch (type) {
                case TRANSLATE:
                    if (!btnMove.isSelected()) {
                        this.btnMove.setSelected(true);
                    }
                    break;
                case ROTATE:
                    if (!btnRotate.isSelected()) {
                        this.btnRotate.setSelected(true);
                    }
                    this.btnRotate.setSelected(true);
                    break;
                case LINK:
                    if (!btnLink.isSelected()) {
                        this.btnMove.setSelected(true);
                    }
                    this.btnLink.setSelected(true);
                    break;
                case PICK:
                default:
                    this.gizmoButtonGroup.clearSelection();
            }
        }
    }

    @Subscribe
    public void levelSelected(LevelEvent le) {
        dae.project.Level current = le.getLevel();
        if (le.getEventType() == EventType.LEVELSELECTED) {
            // get all the brushes.  
            List<Brush> brushes = current.descendantMatches(Brush.class);
            cboBrushes.setModel(new DefaultComboBoxModel(brushes.toArray()));

            if (brushes.size() > 0) {
                System.out.println("Setting selected brush to : " + brushes.get(0));
                GlobalObjects.getInstance().postEvent(
                        new BrushEvent(BrushEventType.SELECTED, brushes.get(0)));
            }
        }
        if (le.getEventType() == EventType.NODEADDED || le.getEventType() == EventType.NODEREMOVED) {
            for (Node node : le.getNodes()) {
                if (node instanceof Brush) {
                    List<Brush> brushes = current.descendantMatches(Brush.class);
                    cboBrushes.setModel(new DefaultComboBoxModel(brushes.toArray()));

                    Brush b = (Brush) node;
                    cboBrushes.setSelectedItem(b);
                    GlobalObjects.getInstance().postEvent(
                            new BrushEvent(BrushEventType.SELECTED, b));
                }
            }
        }
    }

    @Subscribe
    public void nodeSelected(final SelectionEvent se) {
        if (se.getSource() == this) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            adjustUI(se);
        } else {
            SwingUtilities.invokeLater(() -> {
                adjustUI(se);
            });
        }
    }

    private void adjustUI(SelectionEvent se) {
        Prefab selected = se.getSelectedNode();
        if (selected != selectedPrefab) {
            selectedPrefab = selected;
            ObjectType type = selectedPrefab.getObjectType();
            if (type.hasObjectTypeUI()) {
                ObjectTypeUI ui = type.getObjectTypeUI();
                ObjectTypePanel panel = ui.getPanel();
                panel.setPrefab(selected);
                if (panel instanceof JComponent) {
                    switch (ui.getLocation()) {
                        case "horizontalTabs":
                            JComponent c = (JComponent) panel;
                            pnlTabOutputs.add(ui.getLabel(), (JComponent) panel);
                            pnlTabOutputs.setSelectedComponent(c);
                            break;
                    }
                }
            }
        }
    }

    private void saveProject() throws HeadlessException {
        if (currentProject != null) {
            if (currentProject.hasFileLocation()) {
                try {
                    ProjectSaver.write(currentProject, currentProject.getProjectLocation());
                } catch (IOException ex) {
                    Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
                }
            } else {
                sceneChooser.setFileFilter(sandboxFilter);
                int result = sceneChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selected = sceneChooser.getSelectedFile();
                    currentProject.setProjectLocation(selected);
                    File klatchDirectory = currentProject.getKlatchDirectory();
                    klatchDirectory.mkdirs();
                    currentProject.addAssetFolder(currentProject.getKlatchDirectory());
                    ProjectEvent pe = new ProjectEvent(currentProject, ProjectEventType.ASSETFOLDERCHANGED, this);
                    GlobalObjects.getInstance().postEvent(pe);
                    try {
                        ProjectSaver.write(currentProject, selected);
                        GlobalObjects.getInstance().addRecentFile(selected);
                    } catch (IOException ex) {
                        Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
