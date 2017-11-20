package dae.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.prefabs.types.ObjectTypeCategory;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
public class ComponentReader implements AssetLoader {

    /**
     * Loads the animation set.
     *
     * @param assetInfo the location of the animation set.
     * @return a set of animations.
     * @throws IOException
     */
    public Object load(AssetInfo assetInfo) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        ArrayList<Component> result = new ArrayList<Component>();
        ObjectTypeCategory types = GlobalObjects.getInstance().getObjectsTypeCategory();
        try {
            db = dbf.newDocumentBuilder();
            InputStream is = assetInfo.openStream();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();


            NodeList nl = root.getChildNodes();

            for (int i = 0; i < nl.getLength(); ++i) {
                Node current = nl.item(i);
                if ("component".equals(current.getNodeName())) {
                    NamedNodeMap map = current.getAttributes();
                    String id = getAttrContent("id", map);
                    ComponentType c = types.getComponent(id);
                    PrefabComponent pc = c.create();
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
