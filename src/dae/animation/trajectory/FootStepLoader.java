/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.trajectory;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Koen
 */
public class FootStepLoader implements AssetLoader {

    private Material leftFootMaterial;
    private Material rightFootMaterial;

    public Object load(AssetInfo assetInfo) throws IOException {
        try {
            FootSteps steps = new FootSteps();

            InputStream is = assetInfo.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            leftFootMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            leftFootMaterial.setColor("Color", ColorRGBA.White);
            leftFootMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/lfoot.png"));

            rightFootMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            rightFootMaterial.setColor("Color", ColorRGBA.White);
            rightFootMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/rfoot.png"));

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if ("moveTo".equals(n.getNodeName())) {
                    NamedNodeMap map = n.getAttributes();
                    String sloc = getAttrContent("location", map);
                    Vector3f loc = this.parseVector3f(sloc);
                    steps.setStartLoc(loc);
                } else if ("repeatstep".equals(n.getNodeName())) {
                    //<repeatstep startType="right" stepWidth="1" stepLenght="-5" stepHeight="0" direction="[0,0,-1]" size="10"/> 
                    NamedNodeMap map = n.getAttributes();
                    String sStartType = getAttrContent("startType", map);
                    String sStepWidth = getAttrContent("stepWidth", map);
                    String sStepLength = getAttrContent("stepLength", map);
                    String sStepHeight = getAttrContent("stepHeight", map);
                    String sDirection = getAttrContent("direction", map);
                    String sSize = getAttrContent("size", map);

                    float stepWidth = Float.parseFloat(sStepWidth);
                    float stepLength = Float.parseFloat(sStepLength);
                    float stepHeight = Float.parseFloat(sStepHeight);
                    Vector3f direction = parseVector3f(sDirection);
                    int size = Integer.parseInt(sSize);

                    steps.addSteps(leftFootMaterial, rightFootMaterial, sStartType, stepWidth, stepLength, stepHeight, direction, size);
                }
                if ("footstep".equals(n.getNodeName())) {
                    NamedNodeMap map = n.getAttributes();
                    String stype = getAttrContent("type", map);
                    String sloc = getAttrContent("location", map);
                    String sdir = getAttrContent("direction", map);

                    Vector3f loc = this.parseVector3f(sloc);
                    Vector3f dir = this.parseVector3f(sdir);

                    if ("left".equalsIgnoreCase(stype)) {
                        FootStep step = new FootStep(leftFootMaterial, stype, loc, dir);
                        steps.addFootStep(step);
                    } else {
                        FootStep step = new FootStep(rightFootMaterial, stype, loc, dir);
                        steps.addFootStep(step);
                    }
                }
            }

            return steps;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getAttrContent(String key, NamedNodeMap map) {
        Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private Vector3f parseVector3f(String vector3f) {
        if (vector3f == null) {
            return new Vector3f();
        }
        int startIndex = vector3f.indexOf('[');
        int endIndex = vector3f.lastIndexOf(']');
        if (startIndex < 0 || endIndex < 0) {
            return new Vector3f();
        }

        String[] xyz = vector3f.substring(startIndex + 1, endIndex).split(",");
        if (xyz.length != 3) {
            return new Vector3f();
        } else {
            float x = Float.parseFloat(xyz[0]);
            float y = Float.parseFloat(xyz[1]);
            float z = Float.parseFloat(xyz[2]);
            Vector3f result = new Vector3f(x, y, z);
            return result;
        }
    }
}
