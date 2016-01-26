/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io.game;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.components.TransformComponent;
import static dae.io.SceneLoader.readCData;
import dae.io.XMLUtils;
import dae.io.readers.DefaultPrefabImporter;
import dae.prefabs.PropertyReflector;
import dae.prefabs.ReflectionManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jme3tools.optimize.GeometryBatchFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Loads a scene as a game scene, in other words , no visual debugging
 * information is created, and the node structure is flattened.
 *
 * @author Koen Samyn
 */
public class GameSceneLoader {

    private static DefaultPrefabImporter importer = new DefaultPrefabImporter();

    /**
     * Loads a scene from the specified location into the scene element.
     *
     * @param location the location of the scene file.
     * @param am the assetmanager that helps with loading the assets (textures &
     * meshes).
     * @param scene the root node for the scene.
     */
    public static void loadScene(String location, AssetManager am, Node scene) {
        InputStream is = am.getClass().getClassLoader().getResourceAsStream(location);
        loadScene(is, am, scene);
    }

    public static void loadScene(File location, AssetManager am, Node scene) {
        InputStream is;
        try {
            if (!location.exists()) {
                return;
            }
            is = new BufferedInputStream(new FileInputStream(location));
            loadScene(is, am, scene);
        } catch (FileNotFoundException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
    }

    public static void loadScene(InputStream is, AssetManager am, Node scene) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            is = new BufferedInputStream(is);
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            readNodeChildren(nl, am, scene);
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

    public static Spatial createPrefab(NamedNodeMap map, AssetManager manager) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String meshFile = getAttrContent("mesh", map);
        if (meshFile == null || meshFile.length() == 0) {
            return null;
        }
        String name = getAttrContent("name", map);
        String shadowMode = getAttrContent("shadowmode", map);
        try {
            Spatial result = manager.loadModel(meshFile);
            result.setName(name);
            result.setShadowMode(ShadowMode.valueOf(shadowMode));
            return result;
        } catch (AssetNotFoundException ex) {
            return null;
        }
    }

    public static Node createKlatch(NamedNodeMap map, AssetManager manager) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String klatchFile = getAttrContent("klatch", map);
        if (klatchFile == null || klatchFile.length() == 0) {
            return null;
        }
        AssetInfo info = manager.locateAsset(new AssetKey(klatchFile));
        Node klatch = new Node();
        GameSceneLoader.loadScene(info.openStream(), manager, klatch);

        String name = getAttrContent("name", map);
        String shadowMode = getAttrContent("shadowmode", map);

        try {
            klatch.setName(name);
            RenderQueue.ShadowMode sm = RenderQueue.ShadowMode.valueOf(shadowMode);
            klatch.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }
        return klatch;
    }

    public static SpotLight createSpotLight(NamedNodeMap map) {
        String name = getAttrContent("name", map);
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        Quaternion rotation = XMLUtils.parseQuaternion(getAttrContent("rotation", map));
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("spotcolor", map));
        float innerangle = XMLUtils.parseFloat("spotinnerangle", map);
        float outerangle = XMLUtils.parseFloat("spotouterangle", map);
        float spotrange = XMLUtils.parseFloat("spotrange", map);
        float intensity = XMLUtils.parseFloat("spotintensity", map);

        SpotLight spotLight = new SpotLight();
        spotLight.setName(name);
        spotLight.setPosition(translation);
        Vector3f spotLightDir = Vector3f.UNIT_Y.mult(-1.0f);
        spotLight.setDirection(rotation.mult(spotLightDir));
        spotLight.setColor(color.mult(intensity));
        spotLight.setSpotInnerAngle(innerangle * FastMath.DEG_TO_RAD);
        spotLight.setSpotOuterAngle(outerangle * FastMath.DEG_TO_RAD);
        spotLight.setSpotRange(spotrange);
        return spotLight;
    }

    private static PointLight createPointLight(NamedNodeMap map) {
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("color", map));
        float intensity = XMLUtils.parseFloat("intensity", map);
        float radius = XMLUtils.parseFloat("radius", map);
        String name = getAttrContent("name", map);

        PointLight pointlight = new PointLight();
        pointlight.setName(name);
        pointlight.setPosition(translation);
        pointlight.setRadius(radius);
        pointlight.setColor(color.mult(intensity));
        return pointlight;
    }

    private static DirectionalLight createDirectionalLight(NamedNodeMap map) {
        String name = getAttrContent("name", map);
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("color", map));
        float intensity = XMLUtils.parseFloat("intensity", map);
        boolean castshadow = XMLUtils.parseBoolean("castshadow", map);

        DirectionalLight light = new DirectionalLight();
        light.setName(name);
        light.setColor(color.mult(intensity));
        return light;
    }

    private static Light createAmbientLight(NamedNodeMap map) {
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("color", map));
        float intensity = XMLUtils.parseFloat("intensity", map);
        String name = getAttrContent("name", map);

        AmbientLight light = new AmbientLight();
        light.setName(name);
        light.setColor(color.mult(intensity));
        return light;
    }

    private static String getAttrContent(String key, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private static void readNodeChildren(NodeList nl, AssetManager am, Spatial parentNode) throws IllegalAccessException, NumberFormatException, ClassNotFoundException, InstantiationException {

        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node n = nl.item(i);
            NamedNodeMap map = n.getAttributes();
            Spatial currentSpatial = null;
            if ("prefab".equals(n.getNodeName())) {
                currentSpatial = createPrefab(map, am);
            }else if ("skybox".equals(n.getNodeName())){
                currentSpatial = createSkybox(map,am);
            } else if ("terrain".equals(n.getNodeName())) {
                currentSpatial = createTerrain(map, am);
            }else if ( "brushbatch".equals(n.getNodeName())){
                currentSpatial = createBrushBatch(map,am);
            }else if ("camera".equals(n.getNodeName())){
                currentSpatial = createCamera(map,am);
            } else if ("klatch".equals(n.getNodeName())) {
                currentSpatial = createKlatch(map, am);
            } else if ("spotlight".equals(n.getNodeName())) {
                SpotLight sl = createSpotLight(map);
                parentNode.addLight(sl);
                readSpotLightComponents(sl, n);
            } else if ("pointlight".equals(n.getNodeName())) {
                parentNode.addLight(createPointLight(map));
            } else if ("directionallight".equals(n.getNodeName())) {
                DirectionalLight dl = createDirectionalLight(map);
                parentNode.addLight(dl);
                readDirectionalLightComponents(dl, n);
            } else if ("ambientlight".equals(n.getNodeName())) {
                parentNode.addLight(createAmbientLight(map));
            } else if ("component".equals(n.getNodeName())) {
                readComponent(parentNode, n);
            }
            if (currentSpatial != null) {
                if (parentNode instanceof Node) {
                    ((Node) parentNode).attachChild(currentSpatial);
                } else if (parentNode instanceof Spatial) {
                    Node structural = new Node("structural");
                    Transform backup = parentNode.getLocalTransform();
                    parentNode.setLocalTransform(Transform.IDENTITY);
                    structural.setLocalTransform(backup);

                    Node parent = parentNode.getParent();
                    parent.attachChild(structural);
                    structural.attachChild(parentNode);
                    parentNode = structural;
                    structural.attachChild(currentSpatial);
                }

                if (n.hasChildNodes()) {
                    readNodeChildren(n.getChildNodes(), am, currentSpatial);
                }
                if (n.getNodeName().equals("brushbatch") ){
                    // remove all the physics meshes if present.
                    Node batch = (Node)currentSpatial;
                    batch.depthFirstTraversal(new SceneGraphVisitor(){

                        public void visit(Spatial spatial) {
                            if ( spatial.getName().equals("physics")){
                                spatial.removeFromParent();
                            }
                        }
                    });
                    
                    Spatial result = GeometryBatchFactory.optimize((Node)currentSpatial);
                    ((Node)parentNode).detachChild(currentSpatial);
                    ((Node)parentNode).attachChild(result);
                    
                }
            }
        }
    }

    /**
     * Reads a component and instantiates the correct control for the spatial.
     *
     * @param parentNode the parent node to add the control to.
     * @param n the node that contains the information about the component.
     */
    private static void readComponent(Spatial parentNode, org.w3c.dom.Node n) {
        NamedNodeMap attributes = n.getAttributes();
        String id = XMLUtils.getAttribute("id", attributes);
        if (id != null && id.length() > 0 && parentNode != null) {
            ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(id);
            if (ct != null) {
                PrefabComponent pc = ct.create();
                readComponentParameters(pc, ct, n.getChildNodes());
                pc.installGameComponent(parentNode);
            }
        }
    }

    private static void readComponentParameters( PrefabComponent pc, ComponentType type, NodeList childNodes) {
        PropertyReflector pr = ReflectionManager.getInstance().getPropertyReflector(pc.getClass());
        if (pr != null) {
            for (int i = 0; i < childNodes.getLength(); ++i) {
                org.w3c.dom.Node p = childNodes.item(i);
                if (p.getNodeName().equals("parameter")) {
                    String id = XMLUtils.getAttribute("id", p.getAttributes());
                    String value;
                    if (p.getAttributes().getNamedItem("value") == null) {
                        value = readCData(p);
                    } else {
                        value = XMLUtils.getAttribute("value", p.getAttributes());
                    }
                    importer.parseAndSetParameter( pc, type, id, value);
                }
            }
        }
    }

    private static void readSpotLightComponents(SpotLight sl, org.w3c.dom.Node n) {
        // light should only have a transform component.
        NodeList components = n.getChildNodes();
        for (int i = 0; i < components.getLength(); ++i) {
            org.w3c.dom.Node c = components.item(i);
            if (c.getNodeName().equals("component")) {
                String id = XMLUtils.getAttribute("id", c.getAttributes());
                if ("TransformComponent".equals(id) && sl != null) {
                    ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(id);
                    if (ct != null) {
                        PrefabComponent pc = ct.create();
                        readComponentParameters(pc, ct, c.getChildNodes());
                        if (pc instanceof TransformComponent) {
                            TransformComponent tc = (TransformComponent) pc;
                            sl.setPosition(tc.getTranslation());
                            Vector3f dir = tc.getRotation().mult(Vector3f.UNIT_Y).mult(-1.0f);
                            sl.setDirection(dir);
                            sl.setDirection(Vector3f.ZERO);
                        }
                    }
                }
            }
        }
    }

    private static void readDirectionalLightComponents(DirectionalLight dl, org.w3c.dom.Node n) {
        NodeList components = n.getChildNodes();
        for (int i = 0; i < components.getLength(); ++i) {
            org.w3c.dom.Node c = components.item(i);
            if (c.getNodeName().equals("component")) {
                String id = XMLUtils.getAttribute("id", c.getAttributes());
                if ("TransformComponent".equals(id) && dl != null) {
                    ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(id);
                    if (ct != null) {
                        PrefabComponent pc = ct.create();
                        readComponentParameters( pc, ct, c.getChildNodes());
                        if (pc instanceof TransformComponent) {
                            TransformComponent tc = (TransformComponent) pc;
                            Vector3f dir = tc.getRotation().mult(Vector3f.UNIT_X);
                            dl.setDirection(dir);
                        }
                    }
                }
            }
        }

    }

    private static Spatial createTerrain(NamedNodeMap map, AssetManager am) {
        int patchSize = 65;

        String name = XMLUtils.getAttribute("name", map);

        String heightMap = XMLUtils.getAttribute("heightmap", map);
        String alphaMap = XMLUtils.getAttribute("alphamap", map);
        String texture1 = XMLUtils.getAttribute("texture1", map);
        float tex1Scale = XMLUtils.parseFloat("tex1scale", map);

        String texture2 = XMLUtils.getAttribute("texture2", map);
        float tex2Scale = XMLUtils.parseFloat("tex2scale", map);

        String texture3 = XMLUtils.getAttribute("texture3", map);
        float tex3Scale = XMLUtils.parseFloat("tex3scale", map);

        Texture texhm = am.loadTexture(heightMap);
        ImageBasedHeightMap loadedHeightMap = new ImageBasedHeightMap(texhm.getImage());
        loadedHeightMap.load();


        Material terrainMaterial = new Material(am,
                "Common/MatDefs/Terrain/Terrain.j3md");

        Texture tex1 = am.loadTexture(texture1);
        tex1.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("Tex1", tex1);
        terrainMaterial.setFloat("Tex1Scale", tex1Scale);

        Texture tex2 = am.loadTexture(texture2);
        tex2.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("Tex2", tex2);
        terrainMaterial.setFloat("Tex2Scale", tex2Scale);

        Texture tex3 = am.loadTexture(texture3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("Tex3", tex3);
        terrainMaterial.setFloat("Tex3Scale", tex3Scale);

        Texture texAlpha = am.loadTexture(alphaMap);
        terrainMaterial.setTexture("Alpha", texAlpha);

        try {
            TerrainQuad terrain = new TerrainQuad(name, patchSize, loadedHeightMap.getSize() + 1, loadedHeightMap.getHeightMap());
            terrain.setMaterial(terrainMaterial);
            return terrain;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*TerrainLodControl control = new TerrainLodControl(terrain, GlobalObjects.getInstance().getCamera().getCamera());
         terrain.addControl(control);*/

        return null;
    }

    /**
     * Creates a spatial that contains the data of a camera.
     * @param map the attributes of the camera node.
     * @param am the assetmanager.
     * @return 
     */
    private static Spatial createCamera(NamedNodeMap map, AssetManager am) {
        Node cameraNode = new Node();
        boolean startCam = XMLUtils.parseBoolean("startcam", map);
        cameraNode.setName("camera_" + XMLUtils.getAttribute("name", map));
        cameraNode.setUserData("startcam", startCam);
        return cameraNode;
    }

    private static Spatial createSkybox(NamedNodeMap map, AssetManager am) {
        String tex = XMLUtils.getAttribute("tex",map);
        return SkyFactory.createSky(am, tex, false);
    }

    private static Spatial createBrushBatch(NamedNodeMap map, AssetManager am) {
        String name = XMLUtils.getAttribute("name", map);
        Node bn = new Node(name);
        return bn;
    }
}