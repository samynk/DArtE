/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae;

import com.google.common.eventbus.EventBus;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dae.prefabs.AxisEnum;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
import dae.prefabs.standard.RotationRange;
import dae.prefabs.types.ObjectTypeCategory;
import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Koen
 */
public class GlobalObjects {

    private AssetManager manager;
    private InputManager inputManager;
    private EventBus eventBus;
    private ObjectTypeCategory objectsToCreate;
    private RotationRange defaultRotationRange;
    private static GlobalObjects instance = new GlobalObjects();
    private AxisEnum upAxis;
    private Vector3f grid;
    private boolean selectionEnabled = true;
    private HashMap<String, ArrayList> itemLists = new HashMap<String, ArrayList>();
    /**
     * Helps to determine if an edit is significant or not
     */
    private UndoableEdit lastEdit = null;
    // the undo manager for the editor
    private UndoManager undoManager = new UndoManager();
    private Material wireMaterial;
    // The Preferences object that is used to store preferences.
    private Preferences preferences;
    private static int MAXRECENTFILES = 10;

    private GlobalObjects() {
        eventBus = new EventBus();
        preferences = Preferences.userRoot().node("DAE/Zandbak");
        float gridx = preferences.getFloat("GRIDX", 0.02f);
        float gridy = preferences.getFloat("GRIDY", 0.02f);
        float gridz = preferences.getFloat("GRIDZ", 0.02f);
        grid = new Vector3f(gridx, gridy, gridz);
        //Preferences.userRoot().
        float rotationSnap = preferences.getFloat("ANGLESNAP", FastMath.PI / 16);
        defaultRotationRange = new RotationRange(rotationSnap, 0);

        String supAxis = preferences.get("UPAXIS", "Y");
        upAxis = AxisEnum.Y;

    }

    /*
     public UndoManager getUndoManager(){
     return undoManager;
     }
     */
    public void addEdit(UndoableEdit edit) {
        if (lastEdit != null) {
            // determine significance of edit
            if (lastEdit instanceof UndoPrefabPropertyEdit && edit instanceof UndoPrefabPropertyEdit) {
                UndoPrefabPropertyEdit base = (UndoPrefabPropertyEdit) lastEdit;
                UndoPrefabPropertyEdit toCompare = (UndoPrefabPropertyEdit) edit;

                boolean significant = !base.compareEdit(toCompare);
                toCompare.setSignificant(significant);
            }
        } else {
            if (edit instanceof UndoPrefabPropertyEdit) {
                ((UndoPrefabPropertyEdit) edit).setSignificant(true);
            }
        }
        undoManager.addEdit(edit);
        lastEdit = edit;
    }

    public void undo() throws CannotUndoException {
        lastEdit = null;
        try {
            undoManager.undo();
        } catch (CannotUndoException ex) {
        }
    }

    public void redo() throws CannotRedoException {
        undoManager.redo();
    }

    public void setObjectTypeCategory(ObjectTypeCategory objectsToCreate) {
        this.objectsToCreate = objectsToCreate;
    }

    public ObjectTypeCategory getObjectsTypeCategory() {
        return objectsToCreate;
    }

    public RotationRange getDefaultRotationRange() {
        return defaultRotationRange;
    }

    public float getAngleSnap() {
        return defaultRotationRange.getStep();
    }

    public void setAngleSnap(float snap) {
        preferences.putFloat("ANGLESNAP", snap);
        defaultRotationRange.setStep(snap);
    }

    public AxisEnum getUpAxis() {
        return upAxis;
    }

    public void setUpAxis(AxisEnum upAxis) {
        this.upAxis = upAxis;
        //Preferences.userRoot().put("UPAXIS", upAxis.name());
//        if (camera != null) {
//            if (upAxis == AxisEnum.Y) {
//                camera.setUpVector(Vector3f.UNIT_Y);
//            } else if (upAxis == AxisEnum.Z) {
//                camera.setUpVector(Vector3f.UNIT_Z);
//            }
//        }
    }

    public Vector3f getGrid() {
        return grid;
    }

    public void setGrid(Vector3f grid) {
        this.grid = grid;
        preferences.putFloat("GRIDX", grid.x);
        preferences.putFloat("GRIDY", grid.y);
        preferences.putFloat("GRIDZ", grid.z);
    }

    public void addRecentFile(File file) {
        String recentFiles = preferences.get("RecentFiles", "");
        // remove the nonexistant files.
        // StringBuilder sb = new StringBuilder();
        String[] filenames = recentFiles.split("\0");
        boolean addFile = true;
        for (String filename : filenames) {
            File existingFile = new File(filename);
            if (file.equals(existingFile)) {
                addFile = false;
            }
        }

        if (addFile) {
            StringBuilder sb = new StringBuilder();

            sb.append(file.getAbsolutePath());
            sb.append('\0');
            int numFile = 1;
            for (int i = 0; i < filenames.length; ++i) {
                File checkedFile = new File(filenames[i]);
                if (checkedFile.exists()) {
                    sb.append(filenames[i]);
                    sb.append('\0');
                    ++numFile;
                }

                if (numFile == MAXRECENTFILES) {
                    break;
                }
            }

            preferences.put("RecentFiles", sb.toString());
        }
    }

    public ArrayList<File> getRecentFiles() {
        String recentFiles = preferences.get("RecentFiles", "");
        String[] filenames = recentFiles.split("\0");

        ArrayList<File> files = new ArrayList<File>();
        for (String filename : filenames) {
            File file = new File(filename);
            if (file.exists()) {
                files.add(file);
            }
        }

        return files;
    }

    public void clearRecentFileList() {
        preferences.put("RecentFiles", "");
    }

    public void disableSelection() {
        selectionEnabled = false;
    }

    public void enableSelection() {
        selectionEnabled = true;
    }

    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }

    public static GlobalObjects getInstance() {
        return instance;
    }

    public AssetManager getAssetManager() {
        return manager;
    }

    public void setAssetManager(AssetManager manager) {
        if (manager != null) {
            wireMaterial = new Material(manager, "/Common/MatDefs/Misc/Unshaded.j3md");

            wireMaterial.getAdditionalRenderState().setWireframe(true);

            wireMaterial.setColor("Color", ColorRGBA.White);
        }
        this.manager = manager;
    }

    public Material getWireFrameMaterial() {
        return wireMaterial;
    }

    public void registerListener(Object o) {
        eventBus.register(o);
    }

    public void unregisterListener(Object o) {
        eventBus.unregister(o);
    }

    public void postEvent(Object o) {
        eventBus.post(o);

    }

    public ArrayList<Object> getList(String listId) {
        return itemLists.get(listId);
    }

    public void addToList(String listId, Collection items) {
        ArrayList list = itemLists.get(listId);
        if (list == null) {
            ArrayList copy = new ArrayList();
            copy.addAll(items);
            itemLists.put(listId, copy);
        } else {
            list.addAll(items);
        }
    }

    /**
     * Returns the input manager for the jmonkey application
     *
     * @return the input manager.
     */
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * Sets the input manager for the jmonkey application.
     *
     * @param inputManager the input manager.
     */
    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }
    String[] cameraKeys = {"FLYCAM_Left",
        "FLYCAM_Right",
        "FLYCAM_Up",
        "FLYCAM_Down",
        "FLYCAM_StrafeLeft",
        "FLYCAM_StrafeRight",
        "FLYCAM_Forward",
        "FLYCAM_Backward",
        "FLYCAM_Rise",
        "FLYCAM_Lower",
        "FLYCAM_ZoomIn",
        "FLYCAM_ZoomOut",
        "FLYCAM_RotateDrag"};

    /**
     * Installs the defines camera keys for the application.
     *
     * @param flyCam the flyby camera to install the keys for.
     * @param inputManager the inputmanager of the sandbox application.
     */
    public void installCameraKeys(FlyByCamera flyCam, InputManager inputManager) {
        InputContext ic = InputContext.getInstance();
        Locale l = ic.getLocale();
        Logger.getLogger("DArtE").log(Level.INFO, "Keyboard language : {0} , country : {1}", new Object[]{l.getLanguage(), l.getCountry()});
        ResourceBundle cameraKeyBundle = ResourceBundle.getBundle("i18n.camerakeys", ic.getLocale());
        if (cameraKeyBundle == null) {
            cameraKeyBundle = ResourceBundle.getBundle("camerakeys");
        }

        String keyStrokeLeft = cameraKeyBundle.getString("FLYCAM_Left");
        int leftKeyCode = Integer.parseInt(keyStrokeLeft, 16);
        inputManager.addMapping("FLYCAM_Left", new MouseAxisTrigger(0, true),
                new KeyTrigger(leftKeyCode));

        String keyStrokeRight = cameraKeyBundle.getString("FLYCAM_Right");
        int rightKeyCode = Integer.parseInt(keyStrokeRight, 16);
        inputManager.addMapping("FLYCAM_Right", new MouseAxisTrigger(0, false),
                new KeyTrigger(rightKeyCode));

        String keyStrokeUp = cameraKeyBundle.getString("FLYCAM_Up");
        int upKeyCode = Integer.parseInt(keyStrokeUp, 16);
        inputManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(1, false),
                new KeyTrigger(upKeyCode));

        String keyStrokeDown = cameraKeyBundle.getString("FLYCAM_Down");
        int downKeyCode = Integer.parseInt(keyStrokeDown, 16);
        inputManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(1, true),
                new KeyTrigger(downKeyCode));

        inputManager.addMapping("FLYCAM_ZoomIn", new MouseAxisTrigger(2, false));
        inputManager.addMapping("FLYCAM_ZoomOut", new MouseAxisTrigger(2, true));
        inputManager.addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(0));

        inputManager.addMapping("FLYCAM_StrafeLeft", getKeyCode("FLYCAM_StrafeLeft", cameraKeyBundle));
        inputManager.addMapping("FLYCAM_StrafeRight", getKeyCode("FLYCAM_StrafeRight", cameraKeyBundle));
        inputManager.addMapping("FLYCAM_Forward", getKeyCode("FLYCAM_Forward", cameraKeyBundle));
        inputManager.addMapping("FLYCAM_Backward", getKeyCode("FLYCAM_Backward", cameraKeyBundle));
        inputManager.addMapping("FLYCAM_Rise", getKeyCode("FLYCAM_Rise", cameraKeyBundle));
        inputManager.addMapping("FLYCAM_Lower", getKeyCode("FLYCAM_Lower", cameraKeyBundle));

        inputManager.addListener(flyCam, cameraKeys);
        flyCam.registerWithInput(inputManager);
    }

    private KeyTrigger getKeyCode(String key, ResourceBundle bundle) {
        String sKeyCode = bundle.getString(key);
        return new KeyTrigger(Integer.parseInt(sKeyCode, 16));
    }
}
