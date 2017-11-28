package dae.io;

import dae.prefabs.parameters.EnumListParameter;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import dae.components.ComponentType;
import dae.io.readers.DefaultPrefabImporter;
import dae.prefabs.magnets.FillerParameter;
import dae.prefabs.magnets.GridMagnet;
import dae.prefabs.magnets.Magnet;
import dae.prefabs.magnets.MagnetArea;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.magnets.Quad;
import dae.prefabs.parameters.ActionParameter;
import dae.prefabs.parameters.BaseTypeParameter;
import dae.prefabs.parameters.BooleanParameter;
import dae.prefabs.parameters.ChoiceParameter;
import dae.prefabs.parameters.ColorParameter;
import dae.prefabs.parameters.ConnectorParameter;
import dae.prefabs.parameters.DictionaryParameter;
import dae.prefabs.parameters.FileParameter;
import dae.prefabs.parameters.Float2Parameter;
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
import dae.prefabs.types.ObjectTypeInstance;
import dae.prefabs.types.ObjectTypeParameter;
import dae.prefabs.types.ObjectTypeUI;
import dae.prefabs.types.ParameterSupport;
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
 * @author Koen Samyn
 */
public class ObjectTypeReader implements AssetLoader {

    private final ResourceBundle translations;
    private final DefaultPrefabImporter importer;

    public ObjectTypeReader() {
        translations = ResourceBundle.getBundle("i18n/ui");
        importer = new DefaultPrefabImporter();
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
                    ObjectTypeCollection newCollection
                            = new ObjectTypeCollection(getAttrContent("name", map));
                    result.addObjectTypeCollection(newCollection);
                    readObjects(current, result, newCollection);
                } else if ("components".equals(current.getNodeName())) {
                    readComponents(result, current);
                }
            }

            return result;
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            return result;
        }
    }

    private void readObjects(Node parentNode, ObjectTypeCategory main, ObjectTypeCollection parent) {
        NodeList objects = parentNode.getChildNodes();
        for (int i = 0; i < objects.getLength(); ++i) {
            Node current = objects.item(i);
            if ("object".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String label = getAttrContent("label", map);
                String className = getAttrContent("class", map);
                String extraInfo = getAttrContent("mesh", map);
                String cid = getAttrContent("cid", map);
                int cidi = -1;
                if (cid.length() > 0) {
                    if (cid.startsWith("0x") || cid.startsWith("0X")) {
                        cidi = Integer.parseInt(cid.substring(2), 16);
                    } else {
                        cidi = Integer.parseInt(cid);
                    }
                }
                boolean defaultLoader = parseBoolean("defaultloader", map);

                ObjectType ot = new ObjectType(parent.getName(), label, className, extraInfo, defaultLoader, cidi);
                parent.addObjectType(ot);

                readComponents(main, current, ot);
                readParameterSections(current, ot);
                readChildren(main, current, ot);
                readAdditionalUI(main,current,ot);
            }
        }
    }

    private void readParameterSections(Node parentNode, ParameterSupport parent) {
        NodeList sections = parentNode.getChildNodes();
        for (int i = 0; i < sections.getLength(); ++i) {
            Node current = sections.item(i);
            if ("parameters".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String section = getAttrContent("section", map);

                ParameterSection ps = new ParameterSection(section);
                parent.addParameterSection(ps);

                if (parent instanceof ComponentType) {
                    readParameters(current, ps, (ComponentType) parent);
                } else {
                    readParameters(current, ps, ComponentType.PREFAB);
                }
            }
        }
    }

    private void readParameters(Node parentNode, ParameterSection parent, ComponentType cType) {
        NodeList parameters = parentNode.getChildNodes();
        for (int i = 0; i < parameters.getLength(); ++i) {
            Node current = parameters.item(i);
            if ("parameter".equals(current.getNodeName())) {
                NamedNodeMap map = current.getAttributes();
                String type = getAttrContent("type", map);
                String id = getAttrContent("id", map);

                String collectionType = getAttrContent("collectiontype", map);

                String label;
                String converter = getAttrContent("converter", map);
                try {
                    label = translations.getString(id);
                } catch (MissingResourceException ex) {
                    label = id;
                }
                Parameter p = null;
                if ("float3".equals(type)) {
                    p = new Float3Parameter(cType, type, id);
                } else if ("string".equals(type)) {
                    p = new TextParameter(cType, type, id);
                } else if ("choice".equals(type)) {
                    ChoiceParameter cp = new ChoiceParameter(cType, type, id);
                    // if the values are supplied by a property in the object.
                    String listento = getAttrContent("listento", map);
                    String provider = getAttrContent("values", map);
                    cp.setListenTo(listento);
                    cp.setValuesProvider(provider);
                    readChoices(current, cp);
                    p = cp;
                } else if ("color".equals(type)) {
                    ColorParameter cp = new ColorParameter(cType, type, id);
                    p = cp;
                } else if ("float".equals(type)) {
                    FloatParameter fp = new FloatParameter(cType, type, id);
                    p = fp;
                } else if ("integer".equals(type)) {
                    IntParameter ip = new IntParameter(cType, type, id);
                    p = ip;
                } else if ("magnets".equals(type)) {
                    MagnetParameter mp = new MagnetParameter(cType, type, id);
                    readMagnets(current, mp);
                    p = mp;
                } else if ("filler".equals(type)) {
                    FillerParameter fp = new FillerParameter(cType, type, id);
                    readFiller(current, fp);
                    p = fp;
                } else if ("range".equals(type)) {
                    RangeParameter rp = new RangeParameter(cType, type, id);
                    float min = parseFloat("min", map);
                    float max = parseFloat("max", map);
                    float step = parseFloat("step", map);
                    rp.setMin(min);
                    rp.setMax(max);
                    rp.setStep(step);
                    p = rp;
                } else if ("object".equals(type)) {
                    ObjectParameter op = new ObjectParameter(cType, type, id);
                    p = op;
                } else if ("sound".equals(type)) {
                    ObjectParameter op = new ObjectParameter(cType, type, id);
                    p = op;
                } else if ("gmf_object".equals(type)) {
                    ObjectParameter op = new ObjectParameter(cType, type, id);
                    p = op;
                } else if ("action".equals(type)) {
                    ActionParameter ap = new ActionParameter(cType, type, id);
                    String method = this.getAttrContent("method", map);
                    ap.setMethodName(method);
                    p = ap;
                } else if ("boolean".equals(type)) {
                    BooleanParameter bp = new BooleanParameter(cType, type, id);
                    p = bp;
                } else if ("enumlist".equals(type)) {
                    EnumListParameter elp = new EnumListParameter(cType, type, id);
                    String enumClass = this.getAttrContent("enumclass", map);
                    elp.setEnumClass(enumClass);
                    p = elp;
                } else if ("fuzzy".equals(type)) {
                    FuzzyParameter frp = new FuzzyParameter(cType, type, id);
                    p = frp;

                } else if ("connector".equals(type)) {
                    ConnectorParameter frp = new ConnectorParameter(cType, type, id);
                    p = frp;

                } else if ("file".equals(type)) {
                    String extension = this.getAttrContent("extension", map);
                    FileParameter fp = new FileParameter(cType, type, id);
                    fp.setExtension(extension);
                    p = fp;
                } else if ("float2".equals(type)) {
                    p = new Float2Parameter(cType, type, id);
                } else if (type.startsWith("$")) {
                    // the type is an object type that is defined in the type
                    // parameter file.
                    String baseType = type.substring(1);
                    BaseTypeParameter bpt = new BaseTypeParameter(cType, baseType, id);
                    p = bpt;
                }
                if (p != null) {
                    if ("list".equals(collectionType)) {
                        ListParameter lp = new ListParameter(cType, collectionType, id, p);
                        parent.addParameter(lp);
                    } else if ("dictionary".equals(collectionType)) {
                        DictionaryParameter dp = new DictionaryParameter(cType, collectionType, id, p);
                        parent.addParameter(dp);
                    } else {
                        parent.addParameter(p);
                    }
                    p.setLabel(label);
                    p.setConverter(converter);

                    String defaultValue = getAttrContent("default", map);
                    if (defaultValue.length() > 0) {
                        Object parsed = importer.parseParameter(p, defaultValue);
                        if ( p.getConverter() != null ){
                            parsed = p.getConverter().convertFromUIToObject(parsed);
                        }
                        p.setDefault(parsed);
                    } else if (defaultValue.length() == 0 && current.hasChildNodes()) {
                        Object value = readDefault(current);
                        if (value instanceof String) {
                            Object parsed = importer.parseParameter(p, value.toString());
                            p.setDefault(parsed);
                        } else {
                            p.setDefault(value);
                        }
                    }
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

    /**
     * Reads the components that were declared.
     *
     * @param current the current components node.
     */
    private void readComponents(ObjectTypeCategory result, Node current) {
        NodeList nl = current.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("component")) {
                NamedNodeMap attrs = child.getAttributes();
                String id = getAttrContent("id", attrs);
                int order = XMLUtils.parseInt("order", attrs);
                if (order == 0) {
                    order = Integer.MAX_VALUE;
                }
                String className = getAttrContent("className", attrs);

                String cid = getAttrContent("cid", attrs);
                int cidi = 0;
                if (cid.startsWith("0x") || cid.startsWith("0X")) {
                    cidi = Integer.parseInt(cid.substring(2), 16);
                } else if (cid.length() > 0) {
                    cidi = Integer.parseInt(cid);
                }

                ComponentType ct = new ComponentType();
                ct.setId(id);
                ct.setClassName(className);
                ct.setOrder(order);
                ct.setCID(cidi);
                result.addComponent(ct);

                this.readParameterSections(child, ct);

                // loop through all the parameters and set
                // the component type as parent.
                for (Parameter p : ct.getAllParameters()) {
                    p.setComponentType(ct);
                }
            }
        }
    }

    private void readComponents(ObjectTypeCategory parent, Node current, ObjectType ot) {
        NodeList nl = current.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("component")) {
                String id = getAttrContent("id", child.getAttributes());
                ComponentType ct = parent.getComponent(id);
                if (ct != null) {
                    ot.addComponentType(ct);
                }
                readDefaults(child, ot, ct);
            }
        }
    }

    /**
     * Read children that should be constructed.
     *
     * @param main The objectype category with the types of objects.
     * @param current the current node in the xml document.
     * @param ot the ObjectType to add the child definitiontto.
     */
    private void readChildren(ObjectTypeCategory main, Node current, ObjectType ot) {
        NodeList nl = current.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("child")) {
                NamedNodeMap map = child.getAttributes();
                String category = getAttrContent("category", map);
                String typeToMake = getAttrContent("label", map);

                ObjectType childType = main.getObjectType(category, typeToMake);
                // read the default parameters.
                readDefaults(child, childType);
                ot.addChildObject(childType);
            }
        }
    }

    private void readDefaults(Node child, ObjectType ot) {
        NodeList defaults = child.getChildNodes();
        for (int i = 0; i < defaults.getLength(); ++i) {
            Node defaultNode = defaults.item(i);
            if (defaultNode.getNodeName().equals("default")) {
                NamedNodeMap map = defaultNode.getAttributes();
                String cid = getAttrContent("cid", map);
                String id = getAttrContent("id", map);
                String defaultValue = getAttrContent("value", map);

                ComponentType ct = ot.getComponentType(cid);
                
                if (defaultValue.length() == 0) {
                    defaultValue = XMLUtils.readCDATA(defaultNode);
                }
                if (defaultValue != null) {
                    Parameter p = ot.findParameter(ct.getId(), id);
                    Object parsed = importer.parseParameter(p, defaultValue);
                    ot.setDefaultValue(ct.getId(), id, parsed);
                }
            }
        }
    }

    private void readDefaults(Node child, ObjectType ot, ComponentType ct) {
        NodeList defaults = child.getChildNodes();
        for (int i = 0; i < defaults.getLength(); ++i) {
            Node defaultNode = defaults.item(i);
            if (defaultNode.getNodeName().equals("default")) {
                String id = getAttrContent("id", defaultNode.getAttributes());
                String defaultValue = getAttrContent("value", defaultNode.getAttributes());

                if (defaultValue.length() == 0) {
                    defaultValue = XMLUtils.readCDATA(defaultNode);
                }
                if (defaultValue != null) {
                    Parameter p = ot.findParameter(ct.getId(), id);
                    Object parsed = importer.parseParameter(p, defaultValue);
                    ot.setDefaultValue(ct.getId(), id, parsed);
                }
            }
        }
    }

    /**
     *
     * @param current
     * @return
     */
    private Object readDefault(Node current) {
        NodeList nl = current.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            if ("setdefault".equals(n.getNodeName())) {
                return readSetDefault(n);
            }
        }
        return XMLUtils.readCDATA(current);
    }

    private Object readSetDefault(Node n) {
        ObjectTypeInstance oti = null;
        NamedNodeMap map = n.getAttributes();
        String type = XMLUtils.getAttribute("type", map);
        String prefix = XMLUtils.getAttribute("prefix", map);
        String location = XMLUtils.getAttribute("location", map);
        if (type.startsWith("Prefab:")) {
            int startIndex = type.indexOf(':');
            int dotIndex = type.indexOf('.');
            if (startIndex > -1 && dotIndex > -1) {
                String category = type.substring(startIndex + 1, dotIndex);
                String ptype = type.substring(dotIndex + 1);
                oti = new ObjectTypeInstance(prefix, category, ptype);
                oti.setLocation(location);

                if (n.hasChildNodes()) {
                    NodeList setProperties = n.getChildNodes();
                    for (int i = 0; i < setProperties.getLength(); ++i) {
                        Node setProperty = setProperties.item(i);
                        if ("setproperty".equals(setProperty.getNodeName())) {
                            NamedNodeMap propertymap = setProperty.getAttributes();
                            String id = XMLUtils.getAttribute("id", propertymap);
                            String value = XMLUtils.getAttribute("value", propertymap);
                            oti.setValueConverter(this.importer);
                            oti.addParameter(new ObjectTypeParameter(id, value));
                        }
                    }
                }
            }
        }
        return oti;
    }

    private void readAdditionalUI(ObjectTypeCategory main, Node current, ObjectType ot) {
        NodeList nl = current.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("ui")) {
                NamedNodeMap map = child.getAttributes();
                
                String guiClass = getAttrContent("class", map);
                String location = getAttrContent("location", map);
                boolean replace = XMLUtils.parseBoolean("replace", map);
                boolean remove = XMLUtils.parseBoolean("remove",map);
                String tag = getAttrContent("label",map);
                String i18nTag = translations.getString(tag);
                
                ObjectTypeUI ui = new ObjectTypeUI(guiClass, location,i18nTag,replace,remove);
                ot.setObjectTypeUI(ui);
            }
        }
    }
}
