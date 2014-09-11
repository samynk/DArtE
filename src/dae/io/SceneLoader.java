/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.jme3.texture.Texture;
import dae.GlobalObjects;
import dae.animation.skeleton.Body;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.PivotGizmo;
import dae.prefabs.lights.AmbientLightPrefab;
import dae.prefabs.lights.DirectionalLightPrefab;
import dae.prefabs.lights.PointLightPrefab;
import dae.prefabs.lights.SpotLightPrefab;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.standard.CameraEntity;
import dae.prefabs.standard.J3ONPC;
import dae.prefabs.standard.NPCLocationEntity;
import dae.prefabs.standard.NavigationMesh;
import dae.prefabs.standard.PlayerStartEntity;
import dae.prefabs.standard.SituationEntity;
import dae.prefabs.standard.SoundEntity;
import dae.prefabs.standard.TriggerBox;
import dae.prefabs.standard.WayPointEntity;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.ui.events.ErrorMessage;
import java.io.*;
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

    /**
     * Loads a scene from the specified location into the scene element.
     *
     * @param location the location of the scene file.
     * @param am the assetmanager that helps with loading the assets (textures &
     * meshes).
     * @param scene the root node for the scene.
     */
    public static void loadScene(String location, AssetManager am, Node scene, ObjectTypeCategory objectsToCreate, Material selectionMaterial) {
        InputStream is = am.getClass().getClassLoader().getResourceAsStream(location);
        loadScene(is, am, scene, objectsToCreate, selectionMaterial);
    }

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

    public static void loadScene(InputStream is, AssetManager am, Node scene, ObjectTypeCategory objectsToCreate, Material selectionMaterial) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            is = new BufferedInputStream(is);
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            readNodeChildren(nl, am, objectsToCreate, scene);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Material createStandardMaterial(AssetManager am, String textureName, Texture.WrapMode mode, ColorRGBA color) {
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture ref = am.loadTexture(textureName);
        ref.setWrap(mode);
        mat.setTexture("ColorMap", ref);
        mat.setColor("Color", color); // purple
        return mat;
    }

    public static Prefab createBody(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String name = getAttrContent("name", map);
        String type = getAttrContent("type", map);
        String category = getAttrContent("category", map);
        Vector3f translation = parseFloat3(getAttrContent("translation", map));
        Quaternion rot = parseQuaternion(getAttrContent("rotation", map));
        Vector3f scale = parseFloat3(getAttrContent("scale", map));
        String skeletonFile = getAttrContent("skeleton", map);

        Body b = (Body) manager.loadModel(new ModelKey(skeletonFile));
        b.setType(type);
        b.setCategory(category);


        b.setName(name);
        b.setLocalRotation(rot);
        b.setLocalTranslation(translation);
        b.setLocalScale(scale);

        return b;
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

        J3ONPC p = (J3ONPC) Class.forName(className).newInstance();
        p.create(name, manager, meshFile);
        p.setType(label);
        p.setCategory(category);
        p.setPhysicsMesh(physicsMesh);
        p.setAnimation(animation);
        p.setActorName(actorName);
        p.setActorId(actorId);

        ObjectType type = objectsToCreate.find(label);
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

        String shadowMode = getAttrContent("shadowmode", map);



        Prefab p = (Prefab) Class.forName(className).newInstance();
        p.create(name, manager, meshFile);
        p.setType(label);
        p.setCategory(category);
        p.setPhysicsMesh(physicsMesh);

        ObjectType type = objectsToCreate.find(label);
        if (type != null) {
            MagnetParameter mp = (MagnetParameter) type.findParameter("magnets");
            p.setMagnets(mp);
        }

        try {
            ShadowMode sm = ShadowMode.valueOf(shadowMode);
            p.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }

        p.setLocalPrefabRotation(rotation);
        p.setLocalPrefabTranslation(translation);
        p.setLocalScale(scale);

        p.setOffset(offset);

        return p;
    }

    public static Prefab createKlatch(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String klatchFile = getAttrContent("klatch", map);
        if (klatchFile == null || klatchFile.length() == 0) {
            return null;
        }


        String label = getAttrContent("type", map);
        String category = getAttrContent("category", map);
        String name = getAttrContent("name", map);

        Vector3f translation = parseFloat3(getAttrContent("translation", map));
        Vector3f scale = parseFloat3(getAttrContent("scale", map));
        Vector3f offset = parseFloat3(getAttrContent("offset", map));
        Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
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
            k.setPivot(new Vector3f(0,-0.25f,0));
            k.attachChild(box);
            GlobalObjects.getInstance().postEvent(new ErrorMessage("Could not load assembly :" + klatchFile));
        }
        k.setName(name);
        k.setKlatchFile(klatchFile);
        k.setType(label);
        k.setCategory(category);
        k.setPhysicsMesh(physicsMesh);

        try {
            ShadowMode sm = ShadowMode.valueOf(shadowMode);
            k.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }

        k.setLocalRotation(rotation);
        k.setLocalTranslation(translation);
        k.setLocalScale(scale);

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

    private static void readNodeChildren(NodeList nl, AssetManager am, ObjectTypeCategory objectsToCreate, Node parentNode) throws IllegalAccessException, NumberFormatException, ClassNotFoundException, InstantiationException {
        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node n = nl.item(i);
            NamedNodeMap map = n.getAttributes();
            Prefab currentPrefab = null;
            if ("body".equals(n.getNodeName())) {
                currentPrefab = createBody(map, am, objectsToCreate);
            } else if ("j3onpc".equals(n.getNodeName())) {
                currentPrefab = createJ3ONPC(map, am, objectsToCreate);
            } else if ("prefab".equals(n.getNodeName())) {
                currentPrefab = createPrefab(map, am, objectsToCreate);
            } else if ("klatch".equals(n.getNodeName())) {
                currentPrefab = createKlatch(map, am, objectsToCreate);
            } else if ("situation".equals(n.getNodeName())) {
                SituationEntity se = new SituationEntity();
                String name = getAttrContent("name", map);
                String eventid = getAttrContent("eventid", map);
                se.create(name, am, null);
                se.setEventId(eventid);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                se.setLocalTranslation(loc);
                currentPrefab = se;
            } else if ("camera".equals(n.getNodeName())) {
                CameraEntity ce = new CameraEntity();
                String name = getAttrContent("name", map);
                String cameraid = getAttrContent("cameraid", map);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                boolean startCam = parseBoolean("startcam", map);
                ce.create(name, am, null);
                ce.setCameraId(cameraid);
                ce.setLocalTranslation(loc);
                ce.setLocalRotation(rotation);
                ce.setStartCam(startCam);
                currentPrefab = ce;
            } else if ("sound".equals(n.getNodeName())) {
                SoundEntity se = new SoundEntity();
                String name = getAttrContent("name", map);
                String soundFile = getAttrContent("soundfile", map);

                String sPositional = getAttrContent("positional", map);
                boolean positional = sPositional != null ? Boolean.parseBoolean(sPositional) : true;
                String sLooping = getAttrContent("looping", map);
                boolean looping = sLooping != null ? Boolean.parseBoolean(sLooping) : true;

                String sVolume = getAttrContent("volume", map);
                float volume = sVolume != null ? Float.parseFloat(sVolume) : 1.0f;

                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                se.create(name, am, null);
                se.setSoundFile(soundFile);
                se.setLocalTranslation(loc);
                se.setPositional(positional);
                se.setLooping(looping);
                se.setVolume(volume);
                currentPrefab = se;
            } else if ("playerstart".equals(n.getNodeName())) {
                PlayerStartEntity pse = new PlayerStartEntity();
                String name = getAttrContent("name", map);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                pse.create(name, am, null);
                pse.setLocalTranslation(loc);
                pse.setLocalRotation(rotation);
                currentPrefab = pse;
            } else if ("waypoint".equals(n.getNodeName())) {
                WayPointEntity we = new WayPointEntity();
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                String waypointid = getAttrContent("waypointid", map);
                we.setWaypointId(waypointid);
                we.create("waypoint_" + waypointid, am, null);
                we.setLocalTranslation(loc);
                we.setLocalRotation(rotation);

                currentPrefab = we;
            } else if ("trigger".equals(n.getNodeName())) {
                TriggerBox tb = new TriggerBox();
                tb.setCategory("Standard");
                tb.setType("Trigger");
                tb.setId(getAttrContent("id", map));
                tb.create(tb.getId(), am, null);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                tb.setLocalTranslation(loc);
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                tb.setLocalRotation(rotation);
                Vector3f dimension = parseFloat3(getAttrContent("dimension", map));
                tb.setDimension(dimension);
                currentPrefab = tb;
            } else if ("npclocation".equals(n.getNodeName())) {
                NPCLocationEntity npcloc = new NPCLocationEntity();
                String name = getAttrContent("name", map);
                Vector3f loc = parseFloat3(getAttrContent("translation", map));
                String animation = getAttrContent("animation", map);
                String mesh = getAttrContent("npcmesh", map);
                float yRot = Float.parseFloat(getAttrContent("yRotation", map));

                npcloc.create(name, am, null);
                npcloc.setLocalTranslation(loc);
                npcloc.setYRotation(yRot);
                npcloc.setNpc(mesh);
                npcloc.setAnimation(animation);
                currentPrefab = npcloc;
            } else if ("event".equals(n.getNodeName())) {
                String location = getAttrContent("location", map);
                parentNode.setUserData("eventlocation", location);

            } else if ("radar".equals(n.getNodeName())) {
                String model = getAttrContent("model", map);
                parentNode.setUserData("radarmodel", model);
            } else if ("navmesh".equals(n.getNodeName())) {
                NavigationMesh mesh = new NavigationMesh();

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

                mesh.create("navmesh", am, null);
                currentPrefab = mesh;
            } else if ("spotlight".equals(n.getNodeName())) {
                Vector3f translation = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                ColorRGBA color = parseColor(getAttrContent("spotcolor", map));
                float innerangle = parseFloat("spotinnerangle", map);
                float outerangle = parseFloat("spotouterangle", map);
                float spotrange = parseFloat("spotrange", map);
                float intensity = parseFloat("spotintensity", map);
                String name = getAttrContent("name", map);
                SpotLightPrefab prefab = new SpotLightPrefab(translation, rotation, innerangle, outerangle, spotrange, color, intensity);
                prefab.create(name, am, null);
                currentPrefab = prefab;
            } else if ("pointlight".equals(n.getNodeName())) {
                Vector3f translation = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                ColorRGBA color = parseColor(getAttrContent("color", map));
                float intensity = parseFloat("intensity", map);
                String name = getAttrContent("name", map);
                PointLightPrefab prefab = new PointLightPrefab();
                prefab.setLocalPrefabTranslation(translation);
                prefab.setLocalPrefabRotation(rotation);
                prefab.setPointLightColor(color);
                prefab.setPointLightIntensity(intensity);
                prefab.create(name, am, null);
                currentPrefab = prefab;
            } else if ("directionallight".equals(n.getNodeName())) {
                Vector3f translation = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                ColorRGBA color = parseColor(getAttrContent("color", map));
                float intensity = parseFloat("intensity", map);
                String name = getAttrContent("name", map);
                boolean castshadow = parseBoolean("castshadow", map);
                DirectionalLightPrefab prefab = new DirectionalLightPrefab();
                prefab.create(name, am, null);
                prefab.setLocalPrefabTranslation(translation);
                prefab.setLocalPrefabRotation(rotation);
                prefab.setDirectionalLightColor(color);
                prefab.setDirectionalLightIntensity(intensity);
                prefab.setCastShadow(castshadow);
                currentPrefab = prefab;
            } else if ("ambientlight".equals(n.getNodeName())) {
                Vector3f translation = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                ColorRGBA color = parseColor(getAttrContent("color", map));
                float intensity = parseFloat("intensity", map);
                String name = getAttrContent("name", map);
                AmbientLightPrefab prefab = new AmbientLightPrefab();
                prefab.create(name, am, null);
                prefab.setLocalPrefabTranslation(translation);
                prefab.setLocalPrefabRotation(rotation);
                prefab.setAmbientLightColor(color);
                prefab.setAmbientLightIntensity(intensity);

                currentPrefab = prefab;
            } else if ("pivot".equals(n.getNodeName())) {
                Vector3f translation = parseFloat3(getAttrContent("translation", map));
                Quaternion rotation = parseQuaternion(getAttrContent("rotation", map));
                String name = getAttrContent("name", map);
                PivotGizmo prefab = new PivotGizmo();
                prefab.create(name, am, null);
                prefab.setLocalPrefabTranslation(translation);
                prefab.setLocalPrefabRotation(rotation);
                currentPrefab = prefab;
            }
            if (currentPrefab != null) {
                parentNode.attachChild(currentPrefab);
                if (currentPrefab.canHaveChildren() && n.hasChildNodes()) {
                    readNodeChildren(n.getChildNodes(), am, objectsToCreate, currentPrefab);
                }
            }
        }
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