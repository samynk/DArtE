/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae;

import com.google.common.eventbus.EventBus;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.KeyNames;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import dae.gui.SelectAssetDialog;
import dae.gui.preferences.GameKeyDefinition;
import dae.prefabs.AxisEnum;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
import dae.prefabs.standard.RotationRange;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.ui.classpath.FileNode;
import dae.project.Project;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * @author Koen Samyn
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
    private DAEFlyByCamera camera;
    /**
     * Helps to determine if an edit is significant or not
     */
    private UndoableEdit lastEdit = null;
    // the undo manager for the editor
    private UndoManager undoManager = new UndoManager();
    private Material wireMaterial;
    // The Preferences object that is used to store preferences.
    private Preferences preferences;
    private KeyNames keyNames = new KeyNames();
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
        String[] filenames;
        filenames = recentFiles.split("\0");

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
    public void installCameraKeys(DAEFlyByCamera flyCam, InputManager inputManager, boolean deleteMapping) {
        if (deleteMapping) {
            for (String mapping : cameraKeys) {
                inputManager.deleteMapping(mapping);
            }
        }


        Locale l = this.getInputLocale();
        Logger.getLogger("DArtE").log(Level.INFO, "Keyboard language : {0} , country : {1}", new Object[]{l.getLanguage(), l.getCountry()});
        ResourceBundle cameraKeyBundle = ResourceBundle.getBundle("i18n.camerakeys", l);

        GlobalObjects go = GlobalObjects.getInstance();
        boolean useCustomKeys = go.getBooleanPreference("CustomKeys", false);

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

        if (useCustomKeys) {
            inputManager.addMapping("FLYCAM_StrafeLeft", getKeyCode("FLYCAM_StrafeLeft"));
            inputManager.addMapping("FLYCAM_StrafeRight", getKeyCode("FLYCAM_StrafeRight"));
            inputManager.addMapping("FLYCAM_Forward", getKeyCode("FLYCAM_Forward"));
            inputManager.addMapping("FLYCAM_Backward", getKeyCode("FLYCAM_Backward"));
            inputManager.addMapping("FLYCAM_Rise", getKeyCode("FLYCAM_Rise"));
            inputManager.addMapping("FLYCAM_Lower", getKeyCode("FLYCAM_Lower"));
        } else {
            inputManager.addMapping("FLYCAM_StrafeLeft", getKeyCode("FLYCAM_StrafeLeft", cameraKeyBundle));
            inputManager.addMapping("FLYCAM_StrafeRight", getKeyCode("FLYCAM_StrafeRight", cameraKeyBundle));
            inputManager.addMapping("FLYCAM_Forward", getKeyCode("FLYCAM_Forward", cameraKeyBundle));
            inputManager.addMapping("FLYCAM_Backward", getKeyCode("FLYCAM_Backward", cameraKeyBundle));
            inputManager.addMapping("FLYCAM_Rise", getKeyCode("FLYCAM_Rise", cameraKeyBundle));
            inputManager.addMapping("FLYCAM_Lower", getKeyCode("FLYCAM_Lower", cameraKeyBundle));
        }

        inputManager.addListener(flyCam, cameraKeys);
        flyCam.registerWithInput(inputManager);
    }

    public void updateCameraKeys() {
        this.installCameraKeys(this.camera, inputManager, true);
    }

    /**
     * Returns the camera key as a Java camera key code.
     *
     * @param key the camera key to get the keycode for.
     * @return the virual key code as defined in KeyEvent.java
     */
    public int getJavaCameraKeyCode(String key) {
        Locale l = this.getInputLocale();
        Logger.getLogger("DArtE").log(Level.INFO, "Keyboard language : {0} , country : {1}", new Object[]{l.getLanguage(), l.getCountry()});
        ResourceBundle cameraKeyBundle = ResourceBundle.getBundle("i18n.camerakeys", l);
        String keyStrokeUp = cameraKeyBundle.getString(key);
        int keyCode = Integer.parseInt(keyStrokeUp, 16);
        return convertKeyCodeToJava(keyCode);
    }

    /**
     * Returns the input locale for the keyboard.
     *
     * @return the input locale for the keyboard.
     */
    public Locale getInputLocale() {
        InputContext ic = InputContext.getInstance();
        Locale l = ic.getLocale();
        return l;
    }

    /**
     * Returns the text for a given keycode.
     *
     * @param keyCode the integer that describes the character.
     * @return the text for the keycode.
     */
    public String getTextForKeyCode(int keyCode) {
        return keyNames.getName(keyCode);
    }

    public int convertKeyCodeToJMonkeyEngine(int keycode) {
        switch (keycode) {
            case KeyEvent.VK_0:
                return KeyInput.KEY_0;
            case KeyEvent.VK_1:
                return KeyInput.KEY_1;
            case KeyEvent.VK_2:
                return KeyInput.KEY_2;
            case KeyEvent.VK_3:
                return KeyInput.KEY_3;
            case KeyEvent.VK_4:
                return KeyInput.KEY_4;
            case KeyEvent.VK_5:
                return KeyInput.KEY_5;
            case KeyEvent.VK_6:
                return KeyInput.KEY_6;
            case KeyEvent.VK_7:
                return KeyInput.KEY_7;
            case KeyEvent.VK_8:
                return KeyInput.KEY_8;
            case KeyEvent.VK_9:
                return KeyInput.KEY_9;
            case KeyEvent.VK_A:
                return KeyInput.KEY_A;
            case KeyEvent.VK_B:
                return KeyInput.KEY_B;
            case KeyEvent.VK_C:
                return KeyInput.KEY_C;
            case KeyEvent.VK_D:
                return KeyInput.KEY_D;
            case KeyEvent.VK_E:
                return KeyInput.KEY_E;
            case KeyEvent.VK_F:
                return KeyInput.KEY_F;
            case KeyEvent.VK_G:
                return KeyInput.KEY_G;
            case KeyEvent.VK_H:
                return KeyInput.KEY_H;
            case KeyEvent.VK_I:
                return KeyInput.KEY_I;
            case KeyEvent.VK_J:
                return KeyInput.KEY_J;
            case KeyEvent.VK_K:
                return KeyInput.KEY_K;
            case KeyEvent.VK_L:
                return KeyInput.KEY_L;
            case KeyEvent.VK_M:
                return KeyInput.KEY_M;
            case KeyEvent.VK_N:
                return KeyInput.KEY_N;
            case KeyEvent.VK_O:
                return KeyInput.KEY_O;
            case KeyEvent.VK_P:
                return KeyInput.KEY_P;
            case KeyEvent.VK_Q:
                return KeyInput.KEY_Q;
            case KeyEvent.VK_R:
                return KeyInput.KEY_R;
            case KeyEvent.VK_S:
                return KeyInput.KEY_S;
            case KeyEvent.VK_T:
                return KeyInput.KEY_T;
            case KeyEvent.VK_U:
                return KeyInput.KEY_U;
            case KeyEvent.VK_V:
                return KeyInput.KEY_V;
            case KeyEvent.VK_W:
                return KeyInput.KEY_W;
            case KeyEvent.VK_X:
                return KeyInput.KEY_X;
            case KeyEvent.VK_Y:
                return KeyInput.KEY_Y;
            case KeyEvent.VK_Z:
                return KeyInput.KEY_Z;
            case KeyEvent.VK_NUMPAD0:
                return KeyInput.KEY_NUMPAD0;
            case KeyEvent.VK_NUMPAD1:
                return KeyInput.KEY_NUMPAD1;
            case KeyEvent.VK_NUMPAD2:
                return KeyInput.KEY_NUMPAD2;
            case KeyEvent.VK_NUMPAD3:
                return KeyInput.KEY_NUMPAD3;
            case KeyEvent.VK_NUMPAD4:
                return KeyInput.KEY_NUMPAD4;
            case KeyEvent.VK_NUMPAD5:
                return KeyInput.KEY_NUMPAD5;
            case KeyEvent.VK_NUMPAD6:
                return KeyInput.KEY_NUMPAD6;
            case KeyEvent.VK_NUMPAD7:
                return KeyInput.KEY_NUMPAD7;
            case KeyEvent.VK_NUMPAD8:
                return KeyInput.KEY_NUMPAD8;
            case KeyEvent.VK_NUMPAD9:
                return KeyInput.KEY_NUMPAD9;
            case KeyEvent.VK_F1:
                return KeyInput.KEY_F1;
            case KeyEvent.VK_F2:
                return KeyInput.KEY_F2;
            case KeyEvent.VK_F3:
                return KeyInput.KEY_F3;
            case KeyEvent.VK_F4:
                return KeyInput.KEY_F4;
            case KeyEvent.VK_F5:
                return KeyInput.KEY_F5;
            case KeyEvent.VK_F6:
                return KeyInput.KEY_F6;
            case KeyEvent.VK_F7:
                return KeyInput.KEY_F7;
            case KeyEvent.VK_F8:
                return KeyInput.KEY_F8;
            case KeyEvent.VK_F9:
                return KeyInput.KEY_F9;
            case KeyEvent.VK_F10:
                return KeyInput.KEY_F10;
            case KeyEvent.VK_F11:
                return KeyInput.KEY_F11;
            case KeyEvent.VK_F12:
                return KeyInput.KEY_F12;
            case KeyEvent.VK_COMMA:
                return KeyInput.KEY_COMMA;
            case KeyEvent.VK_COLON:
                return KeyInput.KEY_COLON;
            case KeyEvent.VK_SEMICOLON:
                return KeyInput.KEY_SEMICOLON;
            case KeyEvent.VK_OPEN_BRACKET:
                return KeyInput.KEY_LBRACKET;
            case KeyEvent.VK_CLOSE_BRACKET:
                return KeyInput.KEY_RBRACKET;
            case KeyEvent.VK_AT:
                return KeyInput.KEY_AT;
            case KeyEvent.VK_UP:
                return KeyInput.KEY_UP;
            case KeyEvent.VK_DOWN:
                return KeyInput.KEY_DOWN;
            case KeyEvent.VK_LEFT:
                return KeyInput.KEY_LEFT;
            case KeyEvent.VK_RIGHT:
                return KeyInput.KEY_RIGHT;
        }
        return -1;
    }

    public int convertKeyCodeToJava(int keycode) {
        switch (keycode) {
            case KeyInput.KEY_0:
                return KeyEvent.VK_0;
            case KeyInput.KEY_1:
                return KeyEvent.VK_1;
            case KeyInput.KEY_2:
                return KeyEvent.VK_2;
            case KeyInput.KEY_3:
                return KeyEvent.VK_3;
            case KeyInput.KEY_4:
                return KeyEvent.VK_4;
            case KeyInput.KEY_5:
                return KeyEvent.VK_5;
            case KeyInput.KEY_6:
                return KeyEvent.VK_6;
            case KeyInput.KEY_7:
                return KeyEvent.VK_7;
            case KeyInput.KEY_8:
                return KeyEvent.VK_8;
            case KeyInput.KEY_9:
                return KeyEvent.VK_9;
            case KeyInput.KEY_A:
                return KeyEvent.VK_A;
            case KeyInput.KEY_B:
                return KeyEvent.VK_B;
            case KeyInput.KEY_C:
                return KeyEvent.VK_C;
            case KeyInput.KEY_D:
                return KeyEvent.VK_D;
            case KeyInput.KEY_E:
                return KeyEvent.VK_E;
            case KeyInput.KEY_F:
                return KeyEvent.VK_F;
            case KeyInput.KEY_G:
                return KeyEvent.VK_G;
            case KeyInput.KEY_H:
                return KeyEvent.VK_H;
            case KeyInput.KEY_I:
                return KeyEvent.VK_I;
            case KeyInput.KEY_J:
                return KeyEvent.VK_J;
            case KeyInput.KEY_K:
                return KeyEvent.VK_K;
            case KeyInput.KEY_L:
                return KeyEvent.VK_L;
            case KeyInput.KEY_M:
                return KeyEvent.VK_M;
            case KeyInput.KEY_N:
                return KeyEvent.VK_N;
            case KeyInput.KEY_O:
                return KeyEvent.VK_O;
            case KeyInput.KEY_P:
                return KeyEvent.VK_P;
            case KeyInput.KEY_Q:
                return KeyEvent.VK_Q;
            case KeyInput.KEY_R:
                return KeyEvent.VK_R;
            case KeyInput.KEY_S:
                return KeyEvent.VK_S;
            case KeyInput.KEY_T:
                return KeyEvent.VK_T;
            case KeyInput.KEY_U:
                return KeyEvent.VK_U;
            case KeyInput.KEY_V:
                return KeyEvent.VK_V;
            case KeyInput.KEY_W:
                return KeyEvent.VK_W;
            case KeyInput.KEY_X:
                return KeyEvent.VK_X;
            case KeyInput.KEY_Y:
                return KeyEvent.VK_Y;
            case KeyInput.KEY_Z:
                return KeyEvent.VK_Z;
            case KeyInput.KEY_NUMPAD0:
                return KeyEvent.VK_NUMPAD0;
            case KeyInput.KEY_NUMPAD1:
                return KeyEvent.VK_NUMPAD1;
            case KeyInput.KEY_NUMPAD2:
                return KeyEvent.VK_NUMPAD2;
            case KeyInput.KEY_NUMPAD3:
                return KeyEvent.VK_NUMPAD3;
            case KeyInput.KEY_NUMPAD4:
                return KeyEvent.VK_NUMPAD4;
            case KeyInput.KEY_NUMPAD5:
                return KeyEvent.VK_NUMPAD5;
            case KeyInput.KEY_NUMPAD6:
                return KeyEvent.VK_NUMPAD6;
            case KeyInput.KEY_NUMPAD7:
                return KeyEvent.VK_NUMPAD7;
            case KeyInput.KEY_NUMPAD8:
                return KeyEvent.VK_NUMPAD8;
            case KeyInput.KEY_NUMPAD9:
                return KeyEvent.VK_NUMPAD9;
            case KeyInput.KEY_F1:
                return KeyEvent.VK_F1;
            case KeyInput.KEY_F2:
                return KeyEvent.VK_F2;
            case KeyInput.KEY_F3:
                return KeyEvent.VK_F3;
            case KeyInput.KEY_F4:
                return KeyEvent.VK_F4;
            case KeyInput.KEY_F5:
                return KeyEvent.VK_F5;
            case KeyInput.KEY_F6:
                return KeyEvent.VK_F6;
            case KeyInput.KEY_F7:
                return KeyEvent.VK_F7;
            case KeyInput.KEY_F8:
                return KeyEvent.VK_F8;
            case KeyInput.KEY_F9:
                return KeyEvent.VK_F9;
            case KeyInput.KEY_F10:
                return KeyEvent.VK_F10;
            case KeyInput.KEY_F11:
                return KeyEvent.VK_F11;
            case KeyInput.KEY_F12:
                return KeyEvent.VK_F12;
            case KeyInput.KEY_COMMA:
                return KeyEvent.VK_COMMA;
            case KeyInput.KEY_COLON:
                return KeyEvent.VK_COLON;
            case KeyInput.KEY_SEMICOLON:
                return KeyEvent.VK_SEMICOLON;
            case KeyInput.KEY_LBRACKET:
                return KeyEvent.VK_BRACELEFT;
            case KeyInput.KEY_RBRACKET:
                return KeyEvent.VK_BRACERIGHT;
            case KeyInput.KEY_AT:
                return KeyEvent.VK_AT;
            case KeyInput.KEY_UP:
                return KeyEvent.VK_UP;
            case KeyInput.KEY_DOWN:
                return KeyEvent.VK_DOWN;
            case KeyInput.KEY_LEFT:
                return KeyEvent.VK_LEFT;
            case KeyInput.KEY_RIGHT:
                return KeyEvent.VK_RIGHT;
        }
        return -1;
    }

    private KeyTrigger getKeyCode(String key, ResourceBundle bundle) {
        String sKeyCode = bundle.getString(key);
        return new KeyTrigger(Integer.parseInt(sKeyCode, 16));
    }
    
    /**
     * Returns a key trigger based on the information in the preferences.
     * @param key the key for the trigger.
     * @return the KeyTrigger object.
     */
    private KeyTrigger getKeyCode(String key){
        GlobalObjects go = GlobalObjects.getInstance();
        String keycodes = go.getStringPreference(key);
        GameKeyDefinition gkd = new GameKeyDefinition(keycodes);
        return new KeyTrigger(gkd.getJmeKeyCode());
    }

    /**
     * Gets a boolean preference from the preference setting.
     *
     * @param key the key for the preference.
     */
    public boolean getBooleanPreference(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Sets the boolean value for a preference.
     *
     * @param key the key for the preference
     * @param value the new value for the preference.
     */
    public void setBooleanPreference(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    /**
     * Gets a string preference from the preference setting.
     *
     * @param key the key for the preference.
     */
    public String getStringPreference(String key) {
        return preferences.get(key, "");
    }

    /**
     * Sets a string preference from the preference setting.
     *
     * @param key the key for the preference.
     * @param value the new value for the preference.
     */
    public void setStringPreference(String key, String value) {
        preferences.put(key, value);
    }

    /**
     * Sets the camera in the application.
     *
     * @param flyCam the flycame for the application.
     */
    public void setCamera(DAEFlyByCamera flyCam) {
        this.camera = flyCam;
    }

    /**
     * Returns the camera object.
     * @return the camera object.
     */
    public DAEFlyByCamera getCamera() {
        return camera;
    }
    
    private SelectAssetDialog dialog;
    
    /**
     * Select an asset with an asset panel
     * @param rootComponent the root frame component.
     * @param relative place the dialog relative to this component.
     * @param title the title for the dialog.
     * @param extensions the extensions (separated by |) that need to be displayed.
     * in the asset panel.
     */
    public FileNode selectAsset( Frame rootComponent, Component relative, Project currentProject, String title, String extensions )
    {
        if ( dialog == null ){
            dialog = new SelectAssetDialog(rootComponent, true);
        }
        dialog.setLocationRelativeTo(relative);
        dialog.setTitle(title);
        dialog.setProject(currentProject);
        dialog.setExtensions(extensions);
        
        
        dialog.setVisible(true);
        if ( dialog.getReturnStatus() == dialog.RET_OK)
        {
            return dialog.getSelectedFileNode();
        }else{
            return null;
        }
    }
}
