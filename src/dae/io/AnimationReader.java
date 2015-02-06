/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.scene.Spatial;
import dae.animation.AnimationSet;
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
 * @author Koen Samyn
 */
public class AnimationReader implements AssetLoader{

    /**
     * Loads the animation set.
     * @param assetInfo the location of the animation set.
     * @return a set of animations.
     * @throws IOException 
     */
    public Object load(AssetInfo assetInfo) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        AnimationSet result = null ;

        try {
            db = dbf.newDocumentBuilder();
            InputStream is = assetInfo.openStream();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            String name = root.getAttribute("name");
            result = new AnimationSet(name);
            NodeList nl = root.getChildNodes();

            for (int i = 0; i < nl.getLength(); ++i) {
                Node current = nl.item(i);
                if ("animation".equals(current.getNodeName())) {
                    NamedNodeMap map = current.getAttributes();
                    String animname = getAttrContent("name",map);
                    String file = getAttrContent("file",map);
                    
                    // file is relative.
                    String animationFile = assetInfo.getKey().getFolder() + file;
                    Spatial animation;
                    animation = assetInfo.getManager().loadModel(animationFile);
                    result.addAnimation(animname,animation);
                }
            }

            return result;
        } catch (SAXException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            return result;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            return result;
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            return result;
        }
    }
    
    private String getAttrContent(String key, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }
}
