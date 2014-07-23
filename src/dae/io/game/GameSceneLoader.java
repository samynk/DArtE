/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io.game;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.io.XMLUtils;
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
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(dae.io.SceneLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Spatial createPrefab(NamedNodeMap map, AssetManager manager) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        String meshFile = getAttrContent("mesh", map);
        if (meshFile == null || meshFile.length() == 0) {
            return null;
        }

        String name = getAttrContent("name", map);
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        Vector3f scale = XMLUtils.parseFloat3(getAttrContent("scale", map));
        //Vector3f offset = parseFloat3(getAttrContent("offset", map));
        Quaternion rotation = XMLUtils.parseQuaternion(getAttrContent("rotation", map));
        String physicsMesh = getAttrContent("physicsMesh", map);
        String shadowMode = getAttrContent("shadowmode", map);

        try {
            Spatial result = manager.loadModel(meshFile);
            result.setName(name);
            result.setLocalTranslation(translation);
            result.setLocalScale(scale);
            result.setLocalRotation(rotation);
            result.setShadowMode(ShadowMode.valueOf(shadowMode));
            // todo load physics mesh.
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
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        Vector3f scale = XMLUtils.parseFloat3(getAttrContent("scale", map));
        Quaternion rotation = XMLUtils.parseQuaternion(getAttrContent("rotation", map));
        // String physicsMesh = getAttrContent("physicsMesh", map);
        // todo load physics mesh
        String shadowMode = getAttrContent("shadowmode", map);

        try {
            klatch.setName(name);
            klatch.setLocalRotation(rotation);
            klatch.setLocalTranslation(translation);
            klatch.setLocalScale(scale);
            RenderQueue.ShadowMode sm = RenderQueue.ShadowMode.valueOf(shadowMode);
            klatch.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }
        return klatch;
    }

    public static Light createSpotLight(NamedNodeMap map) {
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
        spotLight.setSpotInnerAngle(innerangle);
        spotLight.setSpotOuterAngle(outerangle);
        spotLight.setSpotRange(spotrange);
        return spotLight;
    }
    
    private static PointLight createPointLight(NamedNodeMap map) {
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("color", map));
        float intensity = XMLUtils.parseFloat("intensity", map);
        float radius = XMLUtils.parseFloat("radius",map);
        String name = getAttrContent("name", map);
        
        PointLight pointlight = new PointLight();
        pointlight.setName(name);
        pointlight.setPosition(translation);
        pointlight.setRadius(radius);
        pointlight.setColor(color.mult(intensity));
        return pointlight;
    }
    
     private static Light createDirectionalLight(NamedNodeMap map) {
        String name = getAttrContent("name", map);
        Vector3f translation = XMLUtils.parseFloat3(getAttrContent("translation", map));
        Quaternion rotation = XMLUtils.parseQuaternion(getAttrContent("rotation", map));
        ColorRGBA color = XMLUtils.parseColor(getAttrContent("color", map));
        float intensity = XMLUtils.parseFloat("intensity", map);
        boolean castshadow = XMLUtils.parseBoolean("castshadow", map);
        
        DirectionalLight light = new DirectionalLight();
        light.setName(name);
        light.setColor(color.mult(intensity));
        Vector3f dir = rotation.mult(Vector3f.UNIT_X);
        light.setDirection(dir);
        light.setDirection(translation);
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
            } else if ("klatch".equals(n.getNodeName())) {
                currentSpatial = createKlatch(map, am);
            } else if ("spotlight".equals(n.getNodeName())) {
                if ( currentSpatial != null )
                {
                    parentNode.addLight(createSpotLight(map));
                }
            } else if ("pointlight".equals(n.getNodeName())) {
                if  (currentSpatial != null )
                {
                    parentNode.addLight(createPointLight(map));
                }
            } else if ("directionallight".equals(n.getNodeName())) {
                if ( currentSpatial != null)
                {
                    parentNode.addLight(createDirectionalLight(map));
                }
            } else if ("ambientlight".equals(n.getNodeName())) {
                if  (currentSpatial != null)
                {
                    parentNode.addLight(createAmbientLight(map));
                }
            }
            if (currentSpatial != null) {
                if ( parentNode instanceof Spatial){
                    Node structural = new Node();
                    Transform backup = parentNode.getLocalTransform();
                    parentNode.setLocalTransform(Transform.IDENTITY);
                    structural.setLocalTransform(backup);
                    
                    Node parent = parentNode.getParent();
                    parent.attachChild(structural);
                    structural.attachChild(parentNode);
                    parentNode = structural;
                }else if ( parentNode instanceof Node){
                    ((Node)parentNode).attachChild(currentSpatial);
                }
                
                if ( n.hasChildNodes()) {
                    readNodeChildren(n.getChildNodes(), am, currentSpatial);
                }
            }
        }
    }
}