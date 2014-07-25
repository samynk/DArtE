/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import dae.GlobalObjects;
import dae.animation.event.TransformListener;
import dae.animation.event.TransformType;
import dae.prefabs.gizmos.Axis;
import dae.prefabs.gizmos.Gizmo;
import dae.prefabs.gizmos.RotateGizmo;
import dae.prefabs.magnets.FillerParameter;
import dae.prefabs.magnets.Magnet;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.magnets.Quad;
import dae.prefabs.prefab.undo.UndoPrefabPropertyEdit;
import dae.prefabs.standard.ConnectorPrefab;
import dae.prefabs.standard.UpdateObject;
import dae.prefabs.ui.events.PrefabChangedEvent;
import dae.prefabs.ui.events.PrefabChangedEventType;
import dae.project.ProjectTreeNode;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class Prefab extends Node implements ProjectTreeNode {

    private String prefix = "prefab";
    private HashMap<String, Method> getMethods =
            new HashMap<String, Method>();
    private HashMap<String, Method> setMethods =
            new HashMap<String, Method>();
    private final ArrayList<UpdateObject> workList =
            new ArrayList<UpdateObject>();
    private boolean selected = false;
    private Material originalMaterial;
    private float[] angles = new float[3];
    private Vector3f offset;
    private String type;
    private String category;
    private String physicsMesh;
    private String layerName = "default";
    private ArrayList<TransformListener> transformListeners;
    private Vector3f pivot = Vector3f.ZERO;
    private boolean changed = false;
    /**
     * Defines if this prefab can have children.
     */
    protected boolean canHaveChildren = false;
    /**
     * When a prefab is locked, its translation/rotation and scale cannot be
     * changed anymore.
     */
    private boolean locked;
    /**
     * Field for editing purposes. For examples it is sometimes easier to view a
     * building in wireframe mode.
     */
    private boolean wireframe;
    /**
     * To simplify construction of complex objects, such as houses.
     */
    private MagnetParameter magnets;
    /**
     * The name of the closest magnet.
     */
    private Magnet attachMagnet;
    /**
     * The fillerparameter defines how filler geometry will be generated.
     */
    private FillerParameter fillers;

    /**
     * Creates a new empty prefab object.
     */
    public Prefab() {
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Returns the name of the layer.
     *
     * @return the name of the layer.
     */
    public String getLayerName() {
        return layerName;
    }

    /**
     * Sets the layerName of the layer.
     *
     * @param layerName the name of the layer.
     */
    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The category of this prefab type.
     *
     * @return the category of this prefab type.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of this prefab type.
     *
     * @param category the category of this type.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public void setOriginalMaterial(Material material) {
        this.originalMaterial = material;
    }

    public Material getOriginalMaterial() {
        return originalMaterial;
    }

    public String getPrefix() {
        return prefix;
    }

    public void create(String name, AssetManager manager, String extraInfo) {
    }

    public void setParameter(String property, Object value, boolean undoableEdit) {
        if (locked && !"locked".equals(property)) {
            return;
        }

        this.addUpdateObject(new UpdateObject(property, value, undoableEdit));
    }

    private void setParameterFromUpdateThread(UpdateObject uo) {
        String property = uo.getProperty();
        if (uo.hasParameter()) {
            Object value = uo.getValue();
            Method m = setMethods.get(property);
            if (m == null) {
                // get declared method
                Method[] methods = this.getClass().getMethods();
                String p = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
                for (int i = 0; i < methods.length; ++i) {

                    if (methods[i].getName().equals(p) && methods[i].getParameterTypes().length == 1) {
                        Class pc = methods[i].getParameterTypes()[0];
                        Class vc = getPrimitiveType(value.getClass());
                        if (pc.equals(vc) || pc.isAssignableFrom(vc)) {
                            m = methods[i];
                            setMethods.put(property, m);
                            break;
                        }
                    }
                }
            }
            boolean undoableEdit = uo.isUndoableEdit();

            if (m != null) {
                try {
                    Object oldValue = getParameter(property);
                    oldValue = this.clone(oldValue);
                    boolean equal = value.equals(oldValue);
                    if (!equal) {
                        m.invoke(this, value);
                        //System.out.println("Setting property " + property + " from  " + oldValue + " to  " + value);
                        if (undoableEdit) {
                            GlobalObjects go = GlobalObjects.getInstance();
                            //System.out.println("Adding UndoPrefabPropertyEdit :" + this.getName() + " : " + oldValue + "," + value);
                            go.addEdit(new UndoPrefabPropertyEdit(this, property, oldValue, value));
                            pcs.firePropertyChange(property, oldValue, value);
                        }
                        changed = true;
                    }
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Problem setting " + property + ", on prefab" + this.getName() + ":" + value);
                    System.out.println("type of value is : " + value.getClass().getName());
                    System.out.println("Method is : " + m.getName());
                    System.out.println("Type is :" + m.getParameterTypes()[0].getName());
                    Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            Method m;
            try {
                m = this.getClass().getMethod(property);
                if (m != null) {
                    m.invoke(this);
                }
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Class getPrimitiveType(Class clazz) {
        if (clazz.equals(Boolean.class)) {
            return boolean.class;
        } else if (clazz.equals(Float.class)) {
            return float.class;
        } else if (clazz.equals(Double.class)) {
            return double.class;
        } else if (clazz.equals(Integer.class)) {
            return int.class;
        } else if (clazz.equals(Short.class)) {
            return short.class;
        } else if (clazz.equals(Long.class)) {
            return long.class;
        } else if (clazz.equals(Byte.class)) {
            return byte.class;
        } else if (clazz.equals(Character.class)) {
            return char.class;
        } else {
            return clazz;
        }
    }

    private Object clone(Object toClone) {
        if (toClone instanceof Cloneable) {
            try {
                Method clone = toClone.getClass().getMethod("clone");
                if (clone != null && Modifier.isPublic(clone.getModifiers())) {
                    return clone.invoke(toClone);
                } else {
                    return toClone;
                }
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return toClone;
    }

    public Object getParameter(String property) {
        Method m = getMethods.get(property);
        if (m == null) {
            try {
                String p = Character.toUpperCase(property.charAt(0)) + property.substring(1);
                m = this.getClass().getMethod("get" + p);
                getMethods.put(property, m);
            } catch (NoSuchMethodException ex) {
                //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                //Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (m != null) {
            try {
                return m.invoke(this);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Prefab.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Problem getting " + property + ", on prefab" + this.getName());
                System.out.println("type of return value is : " + m.getReturnType().getName());
                System.out.println("Method is : " + m.getName());
            }
        }
        return null;

    }

    public void call(String methodName) {
        this.addUpdateObject(new UpdateObject(methodName, false));
    }

    public void addUpdateObject(UpdateObject uo) {
        synchronized (workList) {
            workList.add(uo);
        }
    }

    @Override
    public void updateLogicalState(float tpf) {
        synchronized (workList) {
            for (UpdateObject uo : this.workList) {
                //System.out.println("setting property : " + uo.getProperty() + "," + uo.getValue());
                this.setParameterFromUpdateThread(uo);
            }
            workList.clear();
        }
        super.updateLogicalState(tpf);
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        /*
         if (this.getChildren().size() > 0) {
         Spatial s = this.getChildren().get(0);
         if (s instanceof Geometry) {
         Geometry g = (Geometry) s;
         if (selected) {
         System.out.println("Setting selected to : " + selectionMaterial);
         g.setMaterial(selectionMaterial);
         } else {
         //System.out.println("setting material back to original");
         g.setMaterial(originalMaterial);
         }
         }
         }
         * */
    }

    /**
     * Checks if this prefab is changed.
     *
     * @return true if the prefab is changed, false otherwise.
     */
    public boolean isChanged() {
        return changed;
    }

    public boolean isChanged(boolean recursively) {
        if (changed || !recursively) {
            return changed;
        } else {
            for (Spatial s : this.children) {
                if (s instanceof Prefab) {
                    boolean changed = ((Prefab) s).isChanged(recursively);
                    if (changed) {
                        return changed;
                    }
                }
            }
            return changed;
        }
    }

    /**
     * Sets the changed property of the prefab recursively.
     *
     * @param changed true if the prefab is changed, false otherwise.
     * @param recursively also mark the children as changed.
     */
    public void setChanged(boolean changed, boolean recursively) {
        this.changed = changed;
        for (Spatial s : this.children) {
            if (s instanceof Prefab) {
                ((Prefab) s).setChanged(changed, recursively);
            }
        }
    }

    public void setDiffuseColor(ColorRGBA color) {
        if (originalMaterial != null) {
            MatParam mp = originalMaterial.getParam("m_Diffuse");
            if (mp != null) {
                mp.setValue(color);
            }
        }
    }

    public ColorRGBA getDiffuseColor() {
        if (originalMaterial != null) {
            MatParam mp = originalMaterial.getParam("m_Diffuse");
            if (mp != null) {
                return (ColorRGBA) mp.getValue();
            } else {
                return ColorRGBA.White;
            }
        } else {
            return ColorRGBA.White;
        }
    }

    public void setXRotation(float xRot) {
        this.getLocalRotation().toAngles(angles);
        Quaternion q = new Quaternion();
        q.fromAngles(FastMath.DEG_TO_RAD * xRot, angles[1], angles[2]);
        this.setLocalRotation(q);
    }

    public float getXRotation() {
        this.getLocalRotation().toAngles(angles);
        float angle = angles[0];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }

    public void setYRotation(float yRot) {
        this.getLocalRotation().toAngles(angles);
        Quaternion q = new Quaternion();
        q.fromAngles(angles[0], FastMath.DEG_TO_RAD * yRot, angles[2]);
        this.setLocalRotation(q);
    }

    public float getYRotation() {
        this.getLocalRotation().toAngles(angles);
        float angle = angles[1];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }

    public void setZRotation(float zRot) {
        this.getLocalRotation().toAngles(angles);
        Quaternion q = new Quaternion();
        q.fromAngles(angles[0], angles[1], FastMath.DEG_TO_RAD * zRot);
        this.setLocalRotation(q);
    }

    public float getZRotation() {
        this.getLocalRotation().toAngles(angles);
        float angle = angles[2];
        if (angle < 0) {
            angle += FastMath.TWO_PI;
        }
        return angle * FastMath.RAD_TO_DEG;
    }

    public float getXTranslation() {
        return this.getLocalTranslation().x;
    }

    public void setXTranslation(float x) {
        float y = this.getLocalTranslation().y;
        float z = this.getLocalTranslation().z;
        this.setLocalTranslation(x, y, z);
    }

    public float getYTranslation() {
        return this.getLocalTranslation().y;
    }

    public void setYTranslation(float y) {
        float x = this.getLocalTranslation().x;
        float z = this.getLocalTranslation().z;
        this.setLocalTranslation(x, y, z);
    }

    public float getZTranslation() {
        return this.getLocalTranslation().z;
    }

    public void setZTranslation(float z) {
        float y = this.getLocalTranslation().y;
        float x = this.getLocalTranslation().x;
        this.setLocalTranslation(x, y, z);
    }

    public void setOffset(Vector3f offset) {
        if (offset == null) {
            this.offset = Vector3f.ZERO;
        } else {
            this.offset = offset;
        }
        // this.setLocalTranslation(offset);
        // add the offset as local translation of the children.
        /*
         for (Spatial s : this.getChildren()) {
         //System.out.println("children to offset : " + s.getName());
         Vector3f lt = s.getLocalTranslation();
         s.setLocalTranslation(lt.x-this.offset.x, lt.y-this.offset.y, lt.z-this.offset.z);
         }
         */
    }

    /**
     * Sets the pivot point of this prefab.
     *
     * @param pivot the new pivot point of the prefab.
     */
    public void setPivot(Vector3f pivot) {
        this.pivot = pivot.clone();
    }

    /**
     * Returns the pivot point of the prefab.
     *
     * @return the pivot point of the prefab.
     */
    public Vector3f getPivot() {
        return pivot;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public boolean hasMagnets() {
        return magnets != null && magnets.hasMagnets();
    }

    public void setMagnets(MagnetParameter mp) {
        this.magnets = mp;
        if (hasMagnets()) {
            Magnet magnet = mp.getPivotMagnet();

//            for (Magnet m : magnets.iterate()) {
//                MagnetObject mo = new MagnetObject(m);
//                
//                attachChild(mo);
//            }
            this.setOffset(magnet.getLocation());
        }
    }

    public Iterable<Magnet> getMagnets() {
        return magnets.iterate();
    }

    public void cyclePivot() {
        Magnet pivot = magnets.cyclePivotMagnet();
        this.setOffset(pivot.getLocation());
        this.setLocalRotation(pivot.getLocalFrame());
    }

    public void selectPivot(String pivotName) {
        Magnet pivot = magnets.selectPivot(pivotName);
        //System.out.println("Selecting pivot : " + pivotName +", actual : " + pivot.getName());
        this.setOffset(pivot.getLocation());
        this.setLocalRotation(pivot.getLocalFrame());
    }

    public void nextRotationValue() {
        if (attachMagnet != null) {
            attachMagnet.nextRotationValue();
        }
    }

    public void previousRotationValue() {
        if (attachMagnet != null) {
            attachMagnet.previousRotationValue();
        }
    }

    public void setAttachMagnet(Magnet magnet) {
        this.attachMagnet = magnet;
    }

    public Magnet getAttachMagnet() {
        return attachMagnet;
    }

    @Override
    public String toString() {
        //String result = "Prefab (" + this.getType() + ") : " + this.getName() + "\n";
        //result += "Current magnet : " + magnets.getPivotMagnet().getName() + "\n";
        return this.getName();
    }

    public void setFillers(FillerParameter fp) {
        this.fillers = fp;
    }
    private static CollisionResults results = new CollisionResults();

    /**
     * Tries to find objects in the scene to connect with.
     *
     * @param sceneElements
     */
    public void connect(Node sceneElements) {
        if (fillers != null) {
            for (Quad q : fillers.iterable()) {
                Vector3f start = q.getConnectorLocation();
                Vector3f startWithOffset = start.subtract(offset);
                Vector3f dir = q.getConnectorDirection();

                Vector3f startWorld = new Vector3f();
                this.localToWorld(startWithOffset, startWorld);
                //this.getWorldRotation().
                Vector3f dirWorld = this.getWorldRotation().mult(dir);

//                Sphere sphere0 = new Sphere(6, 6, .05f);
//                Geometry trianglepoint0 = new Geometry("tr1", sphere0);
//                trianglepoint0.setMaterial(originalMaterial);
//                trianglepoint0.move(startWorld);
//                //sceneElements.attachChild(trianglepoint0);

                Ray ray = new Ray(startWorld, dirWorld);
                //ray.setLimit(q.getMaxLength());
                results = new CollisionResults();
                sceneElements.collideWith(ray, results);
                System.out.println("Start of ray : " + startWorld);
                System.out.println("Direction of ray : " + dirWorld);
                CollisionResult cr = results.getClosestCollision();

                if (cr != null) {
                    System.out.println("cr.getDistance :" + cr.getDistance());
                } else {
//                    Sphere sphere0 = new Sphere(6, 6, .01f);
//                    Geometry raystart= new Geometry("tr1", sphere0);
//                    raystart.setMaterial(originalMaterial);
//                    raystart.move(startWorld);
//                    
//                    Sphere sphere1 = new Sphere(6, 6, .01f);
//                    Geometry rayend = new Geometry("tr2", sphere1);
//                    rayend.setMaterial(originalMaterial);
//                    rayend.move(startWorld.add(dirWorld));
//                    
//                    sceneElements.attachChild(raystart);
//                    sceneElements.attachChild(rayend);
                    System.out.println("Raycasting failed");
                }
                if (cr != null && cr.getDistance() < q.getMaxLength()) {
                    Prefab other = findPrefabParent(cr.getGeometry(), sceneElements);
                    if (other == null) {
                        System.out.println("no prefab parent!");
                        return;
                    }
                    FillerParameter otherfp = other.getFillerParameter();
                    if (otherfp == null) {
                        System.out.println("No filler found!");
                        continue;
                    }
                    Quad otherQuad = otherfp.getQuad(cr.getTriangleIndex());
                    if (otherQuad == null) {
                        System.out.println("No quad found for triangle index : " + cr.getTriangleIndex());
                        continue;
                    }
                    System.out.println("Connecting " + q.getName() + " to " + otherQuad.getName());
                    Vector3f lps[] = new Vector3f[8];

                    lps[0] = q.getP1();
                    lps[1] = q.getP2();
                    lps[2] = q.getP3();
                    lps[3] = q.getP4();

                    lps[4] = otherQuad.getP1();
                    lps[5] = otherQuad.getP2();
                    lps[6] = otherQuad.getP3();
                    lps[7] = otherQuad.getP4();
                    // 
                    Vector3f wps[] = new Vector3f[8];
                    for (int i = 0; i < 4; ++i) {
                        wps[i] = new Vector3f();
                        this.localToWorldWithOffset(lps[i], wps[i]);
                    }

                    for (int i = 4; i < 8; ++i) {
                        wps[i] = new Vector3f();
                        other.localToWorldWithOffset(lps[i], wps[i]);
                    }

                    // check angle, if the angle is too large compared to the direction of the normal , do not connect, but 
                    // extrude the points according to the measured raycast distance.
                    Vector3f diff = wps[4].subtract(wps[0]);
                    diff.normalizeLocal();
                    // angle of 45 degrees
                    // todo , make it configurable

                    boolean extruded = false;
                    if (Math.abs(diff.dot(dirWorld)) < 0.707) {
                        System.out.println("extruding the raycast quad :" + cr.getDistance());
                        dirWorld.multLocal(cr.getDistance());
                        for (int i = 0; i < 4; ++i) {
                            wps[i + 4] = dirWorld.add(wps[i]);
                        }
                        extruded = true;
                    }


                    ConnectorPrefab cp = new ConnectorPrefab();
                    cp.setPoints(wps, q.isClockWise());

                    sceneElements.attachChild(cp);
                }
            }
        }
    }

    private void localToWorldWithOffset(Vector3f local, Vector3f world) {
        Vector3f l = local.subtract(this.getOffset());
        this.localToWorld(l, world);
    }

    private Prefab findPrefabParent(Geometry g, Node sceneElements) {
        Node pnode = g.getParent();
        while (!(pnode instanceof Prefab) && pnode != sceneElements && pnode != null) {
            pnode = pnode.getParent();
        }
        if (pnode != null && pnode instanceof Prefab) {
            return (Prefab) pnode;
        } else {
            return null;
        }
    }

    private FillerParameter getFillerParameter() {
        return this.fillers;
    }

    protected Material createStandardMaterial(AssetManager am, String textureName, Texture.WrapMode mode, ColorRGBA color) {
        Material mat = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        Texture ref = am.loadTexture(textureName);
        ref.setWrap(mode);
        mat.setTexture("DiffuseMap", ref);
        return mat;
    }

    /**
     * @return the physicsMesh
     */
    public String getPhysicsMesh() {
        return physicsMesh;
    }

    /**
     * @param physicsMesh the physicsMesh to set
     */
    public void setPhysicsMesh(String physicsMesh) {
        this.physicsMesh = physicsMesh;
    }

    public boolean hasPhysicsMesh() {
        return physicsMesh != null && physicsMesh.length() > 0;
    }

    public Prefab duplicate(AssetManager assetManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the locked
     */
    public boolean getLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return the wireframe
     */
    public boolean getWireframe() {
        return wireframe;
    }

    /**
     * @param wireframe the wireframe to set
     */
    public void setWireframe(boolean wireframe) {
        if (this.wireframe == wireframe) {
            return;
        }
        setWireFrameRecursively(this, wireframe);
        this.wireframe = wireframe;
    }

    private void setWireFrameRecursively(Node parent, boolean wireframe) {
        for (Spatial child : parent.getChildren()) {
            if (child instanceof Gizmo) {
                continue;
            }
            if (child instanceof Geometry) {
                Geometry g = (Geometry) child;
                if (wireframe) {
                    Material original = g.getMaterial();
                    g.setUserData("OriginalMaterial", original);
                    g.setMaterial(GlobalObjects.getInstance().getWireFrameMaterial());
                } else {
                    Material original = g.getUserData("OriginalMaterial");
                    if (original != null) {
                        g.setMaterial(original);
                    }
                }
            } else if (child instanceof Node) {
                setWireFrameRecursively((Node) child, wireframe);
            }
        }
    }

    public int getNumParentLayers() {
        int numberOfDots = 0;
        int startIndex = -1;
        while ((startIndex = layerName.indexOf('.', startIndex + 1)) > 0) {
            ++numberOfDots;
        }
        return numberOfDots + 1;
    }

    public void notifyLoaded() {
    }

    // Events
    public void addTransformListener(TransformListener listener) {
        if (this.transformListeners == null) {
            transformListeners = new ArrayList<TransformListener>();
        }
        transformListeners.add(listener);
    }

    public void removeTransformListener(TransformListener listener) {
        if (this.transformListeners != null) {
            transformListeners.remove(listener);
        }
    }

    private void notifyListeners(TransformType type) {
        if (transformListeners != null) {
            for (TransformListener listener : transformListeners) {
                listener.transformChanged(this, type);
            }
        }
        PrefabChangedEvent pe = null;
        switch (type) {
            case ROTATION:
                pe = new PrefabChangedEvent(this, PrefabChangedEventType.ROTATION);
                break;
            case TRANSLATION:
                pe = new PrefabChangedEvent(this, PrefabChangedEventType.TRANSLATION);
                break;
            case SCALE:
                pe = new PrefabChangedEvent(this, PrefabChangedEventType.SCALE);
                break;
        }
        if (pe != null) {
            GlobalObjects.getInstance().postEvent(pe);
        }
    }

    @Override
    public void setLocalRotation(Matrix3f rotation) {
        super.setLocalRotation(rotation);
        notifyListeners(TransformType.ROTATION);
        rotationChanged();
    }

    @Override
    public void setLocalRotation(Quaternion quaternion) {
        super.setLocalRotation(quaternion);
        notifyListeners(TransformType.ROTATION);//To change body of generated methods, choose Tools | Templates.
        rotationChanged();
    }

    public void setLocalPrefabRotation(Quaternion quaternion) {
        //To change body of generated methods, choose Tools | Templates.
        if (locked) {
            return;
        }

        Vector3f origPivot = null;
        if (pivot != Vector3f.ZERO) {
            origPivot = getLocalPrefabRotation().mult(pivot);
        }

        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null && rbc.isEnabled()) {
            rbc.setPhysicsRotation(parent.getWorldRotation().mult(quaternion));
            // handle pivot.
        } else {
            this.setLocalRotation(quaternion);
        }
        if (pivot != Vector3f.ZERO) {
            Vector3f newPivot = quaternion.mult(pivot);
            Vector3f diff = newPivot.subtract(origPivot);
            Vector3f newt = this.getLocalPrefabTranslation().subtract(diff);
            this.setLocalPrefabTranslation(newt);
        }
    }

    public Quaternion getLocalPrefabRotation() {
        //To change body of generated methods, choose Tools | Templates.
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null && rbc.isEnabled()) {
            Quaternion q = rbc.getPhysicsRotation();
            Quaternion world = getParent().getWorldRotation();
            return world.inverse().mult(q);

        } else {
            return getLocalRotation();
        }

    }

    @Override
    public void setLocalScale(float localScale) {
        if (locked) {
            return;
        }
        super.setLocalScale(localScale); //To change body of generated methods, choose Tools | Templates.
        notifyListeners(TransformType.SCALE);
        scaleChanged();
    }

    @Override
    public void setLocalScale(float x, float y, float z) {
        if (locked) {
            return;
        }
        super.setLocalScale(x, y, z); //To change body of generated methods, choose Tools | Templates.
        notifyListeners(TransformType.SCALE);
        scaleChanged();
    }

    @Override
    public void setLocalScale(Vector3f localScale) {
        if (locked) {
            return;
        }
        super.setLocalScale(localScale); //To change body of generated methods, choose Tools | Templates.
        notifyListeners(TransformType.SCALE);
        scaleChanged();
    }

    public void setLocalPrefabTranslation(Vector3f localTranslation) {
        if (locked) {
            return;
        }
        //To change body of generated methods, choose Tools | Templates.
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null && rbc.isEnabled()) {
            Vector3f pt = getParent().localToWorld(localTranslation, null);
            rbc.setPhysicsLocation(pt);
        } else {
            this.setLocalTranslation(localTranslation);
        }
        notifyListeners(TransformType.TRANSLATION);
        translationChanged();
    }

    public Vector3f getLocalPrefabTranslation() {
        //To change body of generated methods, choose Tools | Templates.
        RigidBodyControl rbc = this.getControl(RigidBodyControl.class);
        if (rbc != null && rbc.isEnabled()) {
            Vector3f world = rbc.getPhysicsLocation();
            return getParent().worldToLocal(world, null);
        } else {
            return getLocalTranslation();
        }

    }

    /**
     * Hides the target objects of this object.
     */
    public void hideTargetObjects() {
    }

    public void showTargetObjects() {
    }

    /**
     * Checks if a prefab has children that need to be saved when the prefab is
     * written to file. First the state of the canHaveChildren is checked, next
     * a check will be performed to see if the object has savable children.
     */
    public boolean hasSavableChildren() {
        if (!canHaveChildren) {
            return false;
        } else {
            for (Spatial s : this.getChildren()) {
                if (s instanceof Prefab) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean canHaveChildren() {
        return canHaveChildren;
    }

    public Object getPrefabChildAt(int index) {
        int pindex = 0;
        for (Spatial s : this.getChildren()) {
            if (s instanceof Prefab) {
                if (pindex == index) {
                    return s;
                }
                ++pindex;
            }
        }
        return 0;
    }

    public int getPrefabChildCount() {
        int pindex = 0;
        for (Spatial s : this.getChildren()) {
            if (s instanceof Prefab) {
                ++pindex;
            }
        }
        return pindex;
    }

    public int indexOfPrefab(Prefab prefab) {
        int pindex = 0;
        for (Spatial s : this.getChildren()) {
            if (s == prefab) {
                return pindex;

            }
            ++pindex;
        }
        return 0;
    }

    public void translationChanged() {
    }

    public void rotationChanged() {
    }

    public void scaleChanged() {
    }

    public void addPhysics(PhysicsSpace space) {
    }

    public void addPhysics(PhysicsSpace space, float mass) {
    }

    public void disablePhysics() {
        PhysicsControl pc = this.getControl(PhysicsControl.class);
        if (pc != null) {
            pc.setEnabled(false);
        }
    }

    public void enablePhysics() {
        PhysicsControl pc = this.getControl(PhysicsControl.class);
        if (pc != null) {
            pc.setEnabled(true);
        }
    }

    @Override
    public int attachChild(Spatial child) {
        if (child instanceof Axis || child instanceof RotateGizmo) {
            child.setLocalTranslation(this.pivot);
        }
        return super.attachChild(child); //To change body of generated methods, choose Tools | Templates.
    }
    private PropertyChangeSupport pcs;

    /**
     * Adds a property change listener for a specific property.
     *
     * @param property the property to listen for
     * @param pcl the property change listener to add.
     */
    public void addPropertyListener(String property, PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(property, pcl);
    }

    public boolean hasChildren() {
        return this.getPrefabChildCount() > 0;
    }

    public ProjectTreeNode getProjectChild(int index) {
        return (ProjectTreeNode) this.getPrefabChildAt(index);
    }

    public int getIndexOfChild(ProjectTreeNode object) {
        int pindex = 0;
        for (Spatial s : this.getChildren()) {
            if (s instanceof Prefab) {
                if (s == object) {
                    return pindex;
                }
                ++pindex;
            }
        }
        return -1;
    }

    public ProjectTreeNode getProjectParent() {
        Node p = this.getParent();

        if (p instanceof dae.project.Level) {
            dae.project.Level l = (dae.project.Level) p;
            return l.getLayer(layerName);
        } else if (p instanceof ProjectTreeNode) {
            return (ProjectTreeNode) p;
        } else {
            return null;
        }
    }

    public boolean isLeaf() {
        return getPrefabChildCount() == 0;
    }
}