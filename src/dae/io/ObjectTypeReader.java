/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io;

import dae.prefabs.parameters.EnumListParameter;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import dae.prefabs.magnets.FillerParameter;
import dae.prefabs.magnets.GridMagnet;
import dae.prefabs.magnets.Magnet;
import dae.prefabs.magnets.MagnetArea;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.magnets.Quad;
import dae.prefabs.parameters.ActionParameter;
import dae.prefabs.parameters.BooleanParameter;
import dae.prefabs.parameters.ChoiceParameter;
import dae.prefabs.parameters.ColorParameter;
import dae.prefabs.parameters.ConnectorParameter;
import dae.prefabs.parameters.DefaultSection;
import dae.prefabs.parameters.DictionaryParameter;
import dae.prefabs.parameters.Float3Parameter;
import dae.prefabs.parameters.FloatParameter;
import dae.prefabs.parameters.FuzzyParameter;
import dae.prefabs.parameters.IntParameter;
import dae.prefabs.parameters.ListParameter;
import dae.prefabs.parameters.ObjectParameter;
import dae.prefabs.parameters.Parameter;
import dae.prefabs.parameters.ParameterSection;
import dae.prefabs.parameters.RangeParameter;
import dae.prefabs.parameters.TextParameter;
import dae.prefabs.standard.RotationRange;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.prefabs.types.ObjectTypeCollection;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Koen
 */
public class ObjectTypeReader implements AssetLoader {

    private ResourceBundle translations;

    public ObjectTypeReader() {
        translations = ResourceBundle.getBundle("i18n/ui");
    }

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;

        ObjectTypeCategory result = new ObjectTypeCategory();

        try {
            db = dbf.newDocumentBuilder();
            InputStream is = assetInfo.openStream();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();

            for (int i = 0; i < nl.getLength(); ++i) {
                Node current = nl.item(i);
                if ("collection".equals(current.getNodeName())) {
                    NamedNodeMap map = current.getAttributes();
                    ObjectTypeCollection newCollection =
                            new ObjectTypeCollection(getAttrContent("name", map));
                    result.addObjectTypeCollection(newCollection);
                    readObjects(current, newCollection);
                }
            }

            return result;
        } catch (SAXException ex) {
            Logger.getLogger(ObjectTypeReader.class.getName()).log(Level.SEVERE, null, ex);
            return result;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ObjectTypeReader.class.getName()).log(Level.SEVERE, null, ex);
            return result;
        } catch (IOException ex) {
            Logger.getLogger(ObjectTypeReader.class.getName()).log(Level.SEVERE, null, ex);
            return result;
        }
    }

    private void readObjects(Node parentNode, ObjectTypeCollection parent) {
        NodeList objects = parentNode.getChildNodes();
        for (int i = 0; i < objects.getLength(); ++i) {
            Node current = objects.item(i);
            if ("object".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String label = getAttrContent("label", map);
                String className = getAttrContent("class", map);
                String extraInfo = getAttrContent("mesh", map);
                boolean defaultLoader = parseBoolean("defaultloader", map);
                ObjectType ot = new ObjectType(parent.getName(), label, className, extraInfo, defaultLoader);
                parent.addObjectType(ot);

                readParameterSections(current, ot);
            }
        }
    }

    private void readParameterSections(Node parentNode, ObjectType parent) {
        NodeList sections = parentNode.getChildNodes();
        for (int i = 0; i < sections.getLength(); ++i) {
            Node current = sections.item(i);
            if ("parameters".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String section = getAttrContent("section", map);

                ParameterSection ps = new ParameterSection(section);
                parent.addParameterSection(ps);

                readParameters(current, ps);
            } else if ("defaults".equals(current.getNodeName())) {
                DefaultSection ds = new DefaultSection();
                parent.setDefaultSection(ds);

                readDefaults(current, ds);
            }
        }
    }

    private void readParameters(Node parentNode, ParameterSection parent) {
        NodeList parameters = parentNode.getChildNodes();
        for (int i = 0; i < parameters.getLength(); ++i) {
            Node current = parameters.item(i);
            if ("parameter".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String type = getAttrContent("type", map);
                String id = getAttrContent("id", map);
                String collectionType = getAttrContent("collectiontype",map);
                
                String label;
                String converter = getAttrContent("converter",map);
                try {
                    label = translations.getString(id);
                } catch (MissingResourceException ex) {
                    label = id;
                }
                Parameter p = null;
                if ("float3".equals(type)) {
                    p = new Float3Parameter(type, id);
                } else if ("string".equals(type)) {
                    p = new TextParameter(type, id);
                } else if ("choice".equals(type)) {
                    ChoiceParameter cp = new ChoiceParameter(type, id);
                    readChoices(current, cp);
                    p = cp;
                } else if ("color".equals(type)) {
                    ColorParameter cp = new ColorParameter(type, id);
                    p = cp;
                } else if ("float".equals(type)) {
                    FloatParameter fp = new FloatParameter(type, id);
                    p = fp;
                } else if ("integer".equals(type)) {
                    IntParameter ip = new IntParameter(type, id);
                    p = ip;
                } else if ("magnets".equals(type)) {
                    MagnetParameter mp = new MagnetParameter(type, id);
                    readMagnets(current, mp);
                    p = mp;
                } else if ("filler".equals(type)) {
                    FillerParameter fp = new FillerParameter(type, id);
                    readFiller(current, fp);
                    p = fp;
                } else if ("range".equals(type)) {
                    RangeParameter rp = new RangeParameter(type, id);
                    float min = parseFloat("min", map);
                    float max = parseFloat("max", map);
                    float step = parseFloat("step", map);
                    rp.setMin(min);
                    rp.setMax(max);
                    rp.setStep(step);
                    p = rp;
                } else if ("object".equals(type)) {
                    ObjectParameter op = new ObjectParameter(type, id);
                    p = op;
                } else if ("sound".equals(type)) {
                    ObjectParameter op = new ObjectParameter(type, id);
                    p = op;
                } else if ("gmf_object".equals(type)) {
                    ObjectParameter op = new ObjectParameter(type, id);
                    p = op;
                } else if ("action".equals(type)) {
                    ActionParameter ap = new ActionParameter(type, id);
                    String method = this.getAttrContent("method", map);
                    ap.setMethodName(method);
                    p = ap;
                } else if ("boolean".equals(type)) {
                    BooleanParameter bp = new BooleanParameter(type, id);
                    p = bp;
                } else if ("enumlist".equals(type)) {
                    EnumListParameter elp = new EnumListParameter(type, id);
                    String enumClass = this.getAttrContent("enumclass", map);
                    elp.setEnumClass(enumClass);
                    p = elp;
                }else if ("fuzzy".equals(type)){
                    FuzzyParameter frp = new FuzzyParameter(type,id);
                    p = frp;
                    
                }else if ("connector".equals(type)){
                    ConnectorParameter frp = new ConnectorParameter(type,id);
                    p = frp;
                    
                }
                if (p != null) {
                    if ( "list".equals(collectionType)){
                        ListParameter lp = new ListParameter(type,p);
                        parent.addParameter(lp);
                    }else if ( "dictionary".equals(collectionType)){
                        DictionaryParameter dp = new DictionaryParameter(collectionType,id, p);
                        parent.addParameter(dp);
                    }else{
                        parent.addParameter(p);
                    }
                    p.setLabel(label);
                    p.setConverter(converter);
                }
            }
        }
    }

    private void readDefaults(Node parentNode, DefaultSection parent) {
        NodeList parameters = parentNode.getChildNodes();
        for (int i = 0; i < parameters.getLength(); ++i) {
            Node current = parameters.item(i);
            if ("parameter".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String type = getAttrContent("type", map);
                String id = getAttrContent("id", map);
                String value = getAttrContent("value", map);
                if ("float3".equals(type)) {
                    Vector3f v = parseFloat3(value);
                    parent.addParameter(new Float3Parameter(type, id, v));
                } else if ("string".equals(type)) {
                    parent.addParameter(new TextParameter(type, id));
                } else if ("choice".equals(type)) {
                    ChoiceParameter cp = new ChoiceParameter(type, id);
                    readChoices(current, cp);
                    parent.addParameter(cp);
                } else if ("color".equals(type)) {
                    ColorParameter cp = new ColorParameter(type, id);
                    parent.addParameter(cp);
                } else if ("float".equals(type)) {
                    FloatParameter fp = new FloatParameter(type, id);
                    parent.addParameter(fp);
                } else if ("boolean".equals(type)) {
                    BooleanParameter bp = new BooleanParameter(type, id);
                    parent.addParameter(bp);
                }
            }
        }
    }

    public Matrix3f parseRotationMatrix(String matrix) {
        String withoutBrackets = matrix.substring(1, matrix.length() - 1);
        String[] axises = withoutBrackets.split(";");
        if (axises.length == 3) {
            Vector3f xa = parseFloat3(axises[0]);
            Vector3f ya = parseFloat3(axises[1]);
            Vector3f za = parseFloat3(axises[2]);
            Matrix3f result = new Matrix3f(xa.x, ya.x, za.x, xa.y, ya.y, za.y, xa.z, ya.z, za.z);
            //Matrix3f result = new Matrix3f(xa.x,xa.y,xa.z, ya.x,ya.y,ya.z,za.x,za.y,za.z );
            return result;
        } else {
            return Matrix3f.IDENTITY;
        }
    }

    private void readChoices(Node parentNode, ChoiceParameter cp) {
        NodeList choices = parentNode.getChildNodes();
        for (int i = 0; i < choices.getLength(); ++i) {
            Node current = choices.item(i);
            if ("option".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String label = getAttrContent("label", map);
                cp.addChoice(label);
            }
        }
    }

    private void readMagnets(Node parentNode, MagnetParameter mp) {
        NodeList magnets = parentNode.getChildNodes();
        for (int i = 0; i < magnets.getLength(); ++i) {
            Node current = magnets.item(i);
            if ("magnet".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                Magnet magnet = new Magnet();
                String radius = getAttrContent("radius", map);
                String translation = getAttrContent("translation", map);
                String type = getAttrContent("type", map);
                String pivotMagnet = getAttrContent("activePivot", map);
                String tangentFrame = getAttrContent("tangentFrame", map);
                String name = getAttrContent("name", map);
                String selectPivot = getAttrContent("selectPivot", map);

                Matrix3f rotation = (tangentFrame != null && tangentFrame.length() > 0) ? parseRotationMatrix(tangentFrame) : Matrix3f.IDENTITY;
                magnet.setRadius(parseFloat("radius", map));
                magnet.setLocation(this.parseFloat3(translation));
                magnet.setType(type);
                magnet.setLocalFrame(rotation);
                magnet.setName(name);
                magnet.setSelectPivot(selectPivot);
                boolean pivot = pivotMagnet != null ? Boolean.parseBoolean(pivotMagnet) : false;
                magnet.setPivotMagnet(pivot);

                readArea(current, magnet);
                mp.addMagnet(magnet);
            } else if ("gridmagnet".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                GridMagnet magnet = new GridMagnet();
                magnet.setLocalFrame(Matrix3f.IDENTITY);
                mp.addMagnet(magnet);
            }
        }
    }

    private void readArea(Node parentNode, Magnet magnet) {
        if (parentNode.hasChildNodes()) {
            NodeList list = parentNode.getChildNodes();
            for (int i = 0; i < list.getLength(); ++i) {
                Node node = list.item(i);
                if ("area".equals(node.getNodeName())) {
                    NamedNodeMap map = node.getAttributes();
                    Vector3f min = parseFloat3(getAttrContent("min", map));
                    Vector3f max = parseFloat3(getAttrContent("max", map));
                    MagnetArea ma = new MagnetArea();
                    ma.setMax(max);
                    ma.setMin(min);
                    magnet.setMagnetArea(ma);
                } else if ("rotation".equals(node.getNodeName())) {
                    NamedNodeMap map = node.getAttributes();
                    Vector3f axis = parseFloat3(getAttrContent("axis", map));

                    float min = parseFloat("min", map) * FastMath.DEG_TO_RAD;
                    float max = parseFloat("max", map) * FastMath.DEG_TO_RAD;
                    float value = parseFloat("value", map) * FastMath.DEG_TO_RAD;
                    float step = parseFloat("step", map) * FastMath.DEG_TO_RAD;

                    RotationRange range;
                    if (min == max) {
                        range = new RotationRange(step, value);
                    } else {
                        range = new RotationRange(min, max, step, value);
                    }

                    magnet.setRotationAxis(axis);
                    magnet.setRotationRange(range);
                    magnet.setRotation(range.getCurrentValue());
                }
            }
        }
    }

    private void readFiller(Node parentNode, FillerParameter fp) {
        if (parentNode.hasChildNodes()) {
            NodeList list = parentNode.getChildNodes();
            for (int i = 0; i < list.getLength(); ++i) {
                Node node = list.item(i);
                if ("quad".equals(node.getNodeName())) {
                    NamedNodeMap map = node.getAttributes();
                    Quad q = new Quad();
                    float maxLength = parseFloat("maxlength", map);
                    Vector3f connectorLoc = parseFloat3("connectorLoc", map);
                    Vector3f dir = parseFloat3("dir", map);
                    String name = getAttrContent("name", map);
                    boolean cw = parseBoolean("clockwise", map);
                    q.setName(name);
                    if (cw) {
                        q.setClockWise();
                    } else {
                        q.setCounterClockWise();
                    }

                    String triangles = getAttrContent("triangles", map);
                    String[] tis = triangles.split(",");
                    for (int j = 0; j < tis.length; ++j) {
                        try {
                            int index = Integer.parseInt(tis[j]);
                            q.addTriangleIndex(index);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    q.setMaxLength(maxLength);
                    q.setConnectorLocation(connectorLoc);
                    q.setConnectorDirection(dir);
                    fp.addQuad(q);
                    readQuadPoints(node, q);
                }
            }
        }
    }

    private void readQuadPoints(Node quadNode, Quad q) {
        if (quadNode.hasChildNodes()) {
            NodeList list = quadNode.getChildNodes();
            int pnumber = 0;
            for (int i = 0; i < list.getLength(); ++i) {
                Node node = list.item(i);
                if ("point".equals(node.getNodeName())) {
                    NamedNodeMap map = node.getAttributes();
                    Vector3f loc = parseFloat3("location", map);
                    Vector3f dir1 = parseFloat3("dir1", map);
                    Vector3f dir2 = parseFloat3("dir2", map);

                    q.setPData(pnumber, loc, dir1, dir2);
                    ++pnumber;
                }
            }
        }
    }

    private String getAttrContent(String key, NamedNodeMap map) {
        Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private float[] parseFloatArray(String values) {
        String withoutBrackets = values.substring(1, values.length() - 1);
        String[] cs = withoutBrackets.split(",");

        float rotationValues[] = new float[cs.length];
        for (int i = 0; i < cs.length; ++i) {
            rotationValues[i] = Float.parseFloat(cs[i]);
        }
        return rotationValues;
    }

    private float parseFloat(String attribute, NamedNodeMap map) {
        try {
            Node attr = map.getNamedItem(attribute);
            return attr != null ? Float.parseFloat(attr.getTextContent()) : 0.0f;
        } catch (NumberFormatException ex) {
            return 0.0f;
        }
    }

    private Vector3f parseFloat3(String attribute, NamedNodeMap map) {
        Node attr = map.getNamedItem(attribute);
        return attr != null ? parseFloat3(attr.getTextContent()) : new Vector3f(0, 0, 0);
    }

    private boolean parseBoolean(String attribute, NamedNodeMap map) {
        Node attr = map.getNamedItem(attribute);
        return attr != null ? Boolean.parseBoolean(attr.getTextContent()) : false;
    }

    public Vector3f parseFloat3(String float3) {
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
    }
}