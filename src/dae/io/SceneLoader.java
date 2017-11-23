package dae.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.ModelKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.io.readers.DefaultPrefabImporter;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import dae.prefabs.UnresolvedReferencePrefab;
import dae.prefabs.brush.BrushBatch;
import dae.prefabs.gizmos.PivotGizmo;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.standard.J3ONPC;
import dae.prefabs.standard.NavigationMesh;
import dae.prefabs.standard.SituationEntity;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.ui.events.ErrorMessage;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Koen
 */
public class SceneLoader implements AssetLoader {

    private static DefaultPrefabImporter importer = new DefaultPrefabImporter();

    /**
     * Loads a scene from the specified location into the scene element.
     *
     * @param location the location of the scene file.
     * @param am the assetmanager that helps with loading the assets (textures &
     * meshes).
     * @param scene the root node for the scene.
     * @param objectsToCreate the object with the object and parameter definitions of prefabs.
     * @param selectionMaterial the material that is used to indicated that a prefab is selected.
     */
    public static void loadScene(String location, AssetManager am, Node scene, ObjectTypeCategory objectsToCreate, Material selectionMaterial) {
        InputStream is = am.getClass().getClassLoader().getResourceAsStream(location);
        loadScene(is, am, scene, objectsToCreate, selectionMaterial);
    }

    /**
     * Loads a scene from the specified location into the scene element.
     *
     * @param location the location of the scene file.
     * @param am the assetmanager that helps with loading the assets (textures &
     * meshes).
     * @param scene the root node for the scene.
     * @param objectsToCreate the object with the object and parameter definitions of prefabs.
     * @param selectionMaterial the material that is used to indicated that a prefab is selected.
     */
    public static void loadScene(File location, AssetManager am, Node scene, ObjectTypeCategory objectsToCreate, Material selectionMaterial) {
        InputStream is;
        try {
            if (!location.exists()) {
                return;
            }
            is = new BufferedInputStream(new FileInputStream(location));
            loadScene(is, am, scene, objectsToCreate, selectionMaterial);
        } catch (FileNotFoundException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads a scene from the specified location into the scene element.
     *
     * @param is the inputstream to load the scene from.
     * @param am the assetmanager that helps with loading the assets (textures &
     * meshes).
     * @param scene the root node for the scene.
     * @param objectsToCreate the object with the object and parameter definitions of prefabs.
     * @param selectionMaterial the material that is used to indicated that a prefab is selected.
     */
    public static void loadScene(InputStream is, AssetManager am, Node scene, ObjectTypeCategory objectsToCreate, Material selectionMaterial) {
        try {
            importer.setRootNode(scene);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            is = new BufferedInputStream(is);
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            readNodeChildren(nl, am, objectsToCreate, scene);

            // resolved unresolved references
            List<UnresolvedReferencePrefab> references = scene.descendantMatches(UnresolvedReferencePrefab.class);
            for (UnresolvedReferencePrefab prefab : references) {
                prefab.resolveReference(scene);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Prefab createJ3ONPC(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String meshFile = getAttrContent("mesh", map);
        if (meshFile == null || meshFile.length() == 0) {
            return null;
        }

        String className = getAttrContent("class", map);
        String label = getAttrContent("type", map);
        String name = getAttrContent("name", map);
        String category = getAttrContent("category", map);
        Vector3f translation = parseFloat3(getAttrContent("translation", map));
        Vector3f scale = parseFloat3(getAttrContent("scale", map));
        Vector3f offset = parseFloat3(getAttrContent("offset", map));
        Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
        String physicsMesh = getAttrContent("physicsMesh", map);
        String animation = getAttrContent("animation", map);
        String actorName = getAttrContent("actorName", map);
        String actorId = getAttrContent("actorId", map);

        ObjectType type = objectsToCreate.find(label);
        J3ONPC p = (J3ONPC) type.create(manager, name);

        p.setName(name);
        p.setType(label);
        p.setCategory(category);
        p.setPhysicsMesh(physicsMesh);
        p.setAnimation(animation);
        p.setActorName(actorName);
        p.setActorId(actorId);


        if (type != null) {
            MagnetParameter mp = (MagnetParameter) type.findParameter("magnets");
            p.setMagnets(mp);
        }
        p.setLocalRotation(rotation);
        p.setLocalTranslation(translation);
        p.setLocalScale(scale);
        p.setOffset(offset);
        return p;
    }

    public static Prefab createPrefab(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        /*
         String meshFile = getAttrContent("mesh", map);
         if (meshFile == null || meshFile.length() == 0) {
         return null;
         }
         */

        String label = getAttrContent("label", map);
        String category = getAttrContent("category", map);

        ObjectType type = objectsToCreate.getObjectType(category, label);


        if (type != null) {
            Prefab p = type.createDefault(manager, "prefab",false);
            MagnetParameter mp = (MagnetParameter) type.findParameter("magnets");
            p.setMagnets(mp);
            return p;
        } else {
            return null;
        }
    }

    public static Prefab createKlatch(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String klatchFile = getAttrContent("klatch", map);
        if (klatchFile == null || klatchFile.length() == 0) {
            return null;
        }


        String label = getAttrContent("type", map);
        String category = getAttrContent("category", map);
        
        Vector3f offset = parseFloat3(getAttrContent("offset", map));
        
        String physicsMesh = getAttrContent("physicsMesh", map);
        String shadowMode = getAttrContent("shadowmode", map);


        Klatch k = null;
        try {
            k = (Klatch) manager.loadModel(klatchFile);
        } catch (AssetNotFoundException ex) {
            k = new Klatch();
            Box b = new Box(0.25f, 0.25f, 0.25f); // create cube shape at the origin
            Geometry box = new Geometry("Box", b);
            Material boxmat = manager.loadMaterial("Materials/ErrorMaterial.j3m");
            box.setMaterial(boxmat);
            k.setPivot(new Vector3f(0, -0.25f, 0));
            k.attachChild(box);
            GlobalObjects.getInstance().postEvent(new ErrorMessage("Could not load assembly :" + klatchFile));
        }
        k.setKlatchFile(klatchFile);
        k.setType(label);
        k.setCategory(category);
        k.setPhysicsMesh(physicsMesh);

        try {
            ShadowMode sm = ShadowMode.valueOf(shadowMode);
            k.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }

        k.setOffset(offset);

        return k;
    }

    public static Vector3f parseFloat3(String float3) {
        if (float3.length() > 0) {
            String withoutBrackets = float3.substring(1, float3.length() - 1);
            String[] cs = withoutBrackets.split(",");
            if (cs.length == 3) {
                try {
                    float x = Float.parseFloat(cs[0]);
                    float y = Float.parseFloat(cs[1]);
                    float z = Float.parseFloat(cs[2]);
                    return new Vector3f(x, y, z);
                } catch (NumberFormatException ex) {

                    return new Vector3f(0, 0, 0);
                }
            } else {
                return new Vector3f(0, 0, 0);
            }
        } else {
            return Vector3f.ZERO;
        }
    }

    public static Quaternion parseQuaternion(String quat) {
        String withoutBrackets = quat.substring(1, quat.length() - 1);
        String[] cs = withoutBrackets.split(",");
        if (cs.length == 4) {
            try {
                float x = Float.parseFloat(cs[0]);
                float y = Float.parseFloat(cs[1]);
                float z = Float.parseFloat(cs[2]);
                float w = Float.parseFloat(cs[3]);
                return new Quaternion(x, y, z, w);
            } catch (NumberFormatException ex) {

                return Quaternion.IDENTITY;
            }
        } else {
            return Quaternion.IDENTITY;
        }
    }

    public static ColorRGBA parseColor(String quat) {
        String withoutBrackets = quat.substring(1, quat.length() - 1);
        String[] cs = withoutBrackets.split(",");
        if (cs.length == 4) {
            try {
                float x = Float.parseFloat(cs[0]);
                float y = Float.parseFloat(cs[1]);
                float z = Float.parseFloat(cs[2]);
                float w = Float.parseFloat(cs[3]);
                return new ColorRGBA(x, y, z, w);
            } catch (NumberFormatException ex) {

                return ColorRGBA.White;
            }
        } else {
            return ColorRGBA.Magenta;
        }
    }

    private static String getAttrContent(String key, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private static float parseFloat(String name, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(name);
        try {
            return attr != null ? Float.parseFloat(attr.getTextContent()) : 0f;
        } catch (NumberFormatException ex) {
            return 0.0f;
        }
    }

    private static int parseInt(String cellsize, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(cellsize);
        try {
            return attr != null ? Integer.parseInt(attr.getTextContent()) : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static boolean parseBoolean(String name, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(name);
        return attr != null ? Boolean.parseBoolean(attr.getTextContent()) : false;
    }

    public static void readNodeChildren(NodeList nl, AssetManager am, ObjectTypeCategory objectsToCreate, Node parentNode) throws IllegalAccessException, NumberFormatException, ClassNotFoundException, InstantiationException {
        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node n = nl.item(i);
            NamedNodeMap map = n.getAttributes();
            Prefab currentPrefab = null;
            if ("j3onpc".equals(n.getNodeName())) {
                currentPrefab = createJ3ONPC(map, am, objectsToCreate);
            } else if ("prefab".equals(n.getNodeName())) {
                currentPrefab = createPrefab(map, am, objectsToCreate);
            } else if ("klatch".equals(n.getNodeName())) {
                currentPrefab = createKlatch(map, am, objectsToCreate);
            } else if ("situation".equals(n.getNodeName())) {
                ObjectType objectType = objectsToCreate.getObjectType("Standard", "Situation");

                String name = getAttrContent("name", map);
                SituationEntity se = (SituationEntity) objectType.create(am, name);
                String eventid = getAttrContent("eventid", map);

                se.setName(name);
                se.setEventId(eventid);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                se.setLocalTranslation(loc);
                currentPrefab = se;
            } else if ("event".equals(n.getNodeName())) {
                String location = getAttrContent("location", map);
                parentNode.setUserData("eventlocation", location);

            } else if ("radar".equals(n.getNodeName())) {
                String model = getAttrContent("model", map);
                parentNode.setUserData("radarmodel", model);
            } else if ("skybox".equals(n.getNodeName())) {
                String skytex = getAttrContent("tex", map);

                if (skytex.length() > 0 && parentNode instanceof dae.project.Level) {
                    dae.project.Level level = (dae.project.Level) parentNode;
                    level.setSkyBoxTexture(skytex);
                }
            } else if ("navmesh".equals(n.getNodeName())) {
                ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "NavigationMesh");
                NavigationMesh mesh = (NavigationMesh) type.create(am, "navmesh");

                String sourceMesh = getAttrContent("sourcemesh", map);
                mesh.setSourceMesh(sourceMesh);
                String compiledMesh = getAttrContent("compiledmesh", map);
                mesh.setCompiledMesh(compiledMesh);

                float cellSize = parseFloat("cellsize", map);
                mesh.setCellSize(cellSize);
                float cellHeight = parseFloat("cellheight", map);
                mesh.setCellHeight(cellHeight);
                float minTraversableHeight = parseFloat("mintraversableheight", map);
                mesh.setMinTraversableHeight(minTraversableHeight);
                float maxTraversableSlope = parseFloat("maxtraversableslope", map);
                mesh.setMaxTraversableSlope(maxTraversableSlope);
                float maxTraversableStep = parseFloat("maxtraversablestep", map);
                mesh.setMaxTraversableStep(maxTraversableStep);
                boolean clipLedges = parseBoolean("clipledges", map);
                mesh.setClipLedges(clipLedges);
                float traversableAreaBorderSize = parseFloat("traversableareabordersize", map);
                mesh.setTraversableAreaBorderSize(traversableAreaBorderSize);
                int smoothingTreshold = parseInt("smoothingTreshold", map);
                mesh.setSmoothingThreshold(smoothingTreshold);
                boolean useConservativeExpansion = parseBoolean("useconservativeexpansion", map);
                mesh.setUseConservativeExpansion(useConservativeExpansion);
                int minUnconnectedRegionSize = parseInt("minunconnectedregionsize", map);
                mesh.setMinUnconnectedRegionSize(minUnconnectedRegionSize);
                int mergeRegionSize = parseInt("mergeregionsize", map);
                mesh.setMergeRegionSize(mergeRegionSize);
                float maxEdgeLength = parseFloat("maxedgelength", map);
                mesh.setMaxEdgeLength(maxEdgeLength);
                float edgemaxdeviation = parseFloat("edgemaxdeviation", map);
                mesh.setEdgeMaxDeviation(edgemaxdeviation);
                int maxVertsPerPoly = parseInt("maxvertsperpoly", map);
                mesh.setMaxVertsPerPoly(maxVertsPerPoly);
                float contourSampleDistance = parseFloat("contoursamplesistance", map);
                mesh.setContourSampleDistance(contourSampleDistance);
                float contourMaxDeviation = parseFloat("contourmaxdeviation", map);
                mesh.setContourMaxDeviation(contourMaxDeviation);

                currentPrefab = mesh;
            } else if ("pivot".equals(n.getNodeName())) {
                String name = getAttrContent("name", map);
                ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("MetaData", "Pivot");
                PivotGizmo prefab = (PivotGizmo) type.create(am, name);
                currentPrefab = prefab;
            } else if ("brushbatch".equals(n.getNodeName())) {
                currentPrefab = readBrushBatch(n, am);
            } else if ("component".equals(n.getNodeName())) {
                if (parentNode != null && parentNode instanceof Prefab) {
                    String id = XMLUtils.getAttribute("id", n.getAttributes());
                    Prefab prefab = (Prefab) parentNode;

                    ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(id);
                    if (ct != null) {
                        PrefabComponent pc = null;
                        if (!prefab.hasPrefabComponent(id)) {
                            pc = ct.create();
                            prefab.addPrefabComponent(pc, false);
                        }else{
                            pc = prefab.getComponent(id);
                        }
                        readComponentParameters(prefab, pc, ct, n.getChildNodes());
                    }
                }
            } else if ("parameter".equals(n.getNodeName())) {
                NamedNodeMap attrs = n.getAttributes();
                if (parentNode != null && parentNode instanceof Prefab) {
                    String id = XMLUtils.getAttribute("id", attrs);
                    String value = XMLUtils.getAttribute("value", attrs);
                    if (value.length() == 0) {
                        value = readCData(n);
                    }
                    importer.parseAndSetParameter((Prefab) parentNode, id, value);
                }
            } else if ("list".equals(n.getNodeName())) {
                NamedNodeMap attrs = n.getAttributes();
                if (parentNode != null && parentNode instanceof Prefab) {
                    String id = XMLUtils.getAttribute("id", attrs);
                    for (int ci = 0; ci < n.getChildNodes().getLength(); ++ci) {
                        org.w3c.dom.Node listNode = n.getChildNodes().item(ci);
                        if ("parameter".equals(listNode.getNodeName())) {
                            NamedNodeMap itemAttrs = listNode.getAttributes();
                            String value = XMLUtils.getAttribute("value", itemAttrs);
                            if (value.length() == 0) {
                                value = readCData(n);
                            }
                            importer.parseAndSetListParameter((Prefab) parentNode, id, value);
                        }
                    }
                }
            }
            if (currentPrefab != null) {
                parentNode.attachChild(currentPrefab);
                if (n.hasChildNodes()) {
                    readNodeChildren(n.getChildNodes(), am, objectsToCreate, currentPrefab);
                }
                currentPrefab.installAllComponents();
            }
        }
    }

    /**
     * Reads the parameters of the components.
     *
     * @param pc
     * @param childNodes
     */
    private static void readComponentParameters(Prefab prefab, PrefabComponent pc, ComponentType type, NodeList childNodes) {
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(pc.getClass());
        if (pr != null) {
            for (int i = 0; i < childNodes.getLength(); ++i) {
                org.w3c.dom.Node p = childNodes.item(i);
                if (p.getNodeName().equals("parameter")) {
                    String id = XMLUtils.getAttribute("id", p.getAttributes());
                    String value = null;
                    if (p.getAttributes().getNamedItem("value") == null) {
                        value = readCData(p);
                    } else {
                        value = XMLUtils.getAttribute("value", p.getAttributes());
                    }
                    importer.parseAndSetParameter(pc, type, id, value);
                }
            }
        }
    }

    public static String readCData(org.w3c.dom.Node node) {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node child = nl.item(i);
            if (child.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE) {
                return child.getNodeValue();
            }
        }
        return null;
    }

    private static Prefab readBrushBatch(org.w3c.dom.Node n, AssetManager am) {

        NamedNodeMap attrs = n.getAttributes();
        String name = XMLUtils.getAttribute("name", attrs);
        ObjectType type = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Terrain", "BrushBatch");
        BrushBatch bb = (BrushBatch) type.create(am, name);
        return bb;
    }

    public Object load(AssetInfo assetInfo) throws IOException {
        AssetManager manager = assetInfo.getManager();

        InputStream is = assetInfo.openStream();
        Klatch klatch = new Klatch(assetInfo.getKey().getName());
        ObjectTypeCategory cat = GlobalObjects.getInstance().getObjectsTypeCategory();
        Material selectionMaterial = manager.loadMaterial("Materials/SelectionMaterial.j3m");
        loadScene(is, manager, klatch, cat, selectionMaterial);

        for (Spatial s : klatch.getChildren()) {
            s.setUserData("klatchpart", Boolean.TRUE);
        }

        klatch.setCategory("Standard");
        klatch.setType("Klatch");
        return klatch;
    }
}