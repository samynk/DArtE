/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig.io;

import dae.animation.skeleton.*;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import dae.GlobalObjects;
import dae.animation.rig.PrefabPlaceHolderCallback;
import dae.animation.rig.Rig;
import static dae.io.SceneLoader.parseFloat3;
import static dae.io.SceneLoader.parseQuaternion;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import dae.prefabs.magnets.MagnetParameter;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.standard.PrefabPlaceHolder;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mlproject.fuzzy.FuzzyRule;
import mlproject.fuzzy.FuzzyRuleBlock;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;
import mlproject.fuzzy.LeftSigmoidMemberShip;
import mlproject.fuzzy.RightSigmoidMemberShip;
import mlproject.fuzzy.SigmoidMemberShip;
import mlproject.fuzzy.SingletonMemberShip;
import mlproject.fuzzy.TrapezoidMemberShip;
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
public class RigLoader implements AssetLoader {
    
    private Material limbMaterial;
    private Material ballJointMaterial;
    private Material revJointMaterial;
    private Material apMaterial;
    private AssetManager assetManager;
    
    public Object load(AssetInfo assetInfo) throws IOException {
        Rig result = null;
        try {
            InputStream is = assetInfo.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            
            assetManager = assetInfo.getManager();
            
            
            
            
            ballJointMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            ballJointMaterial.setColor("Color", ColorRGBA.White);
            ballJointMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));
            
            revJointMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            revJointMaterial.setColor("Color", ColorRGBA.LightGray);
            revJointMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));
            
            limbMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            limbMaterial.setColor("Color", ColorRGBA.DarkGray);
            limbMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));
            
            apMaterial = new Material(assetInfo.getManager(),
                    "Common/MatDefs/Misc/Unshaded.j3md");
            apMaterial.setColor("Color", ColorRGBA.Green);
            apMaterial.setTexture("ColorMap", assetInfo.getManager().loadTexture("Textures/refPattern.png"));
            
            Element root = doc.getDocumentElement();
            
            result = new Rig();
            result.create("rig", this.assetManager, null);
            
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                constructChildren(n, result, assetInfo.getManager());
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private void constructChildren(Node docNode, BodyElement jmeParentNode, AssetManager manager) {
        if (docNode.getNodeType() == Node.TEXT_NODE) {
            return;
        }
        String name = docNode.getNodeName();
        //System.out.println("Local name is :" + name);
        if (name.equals("joint") || name.equals("limb")) {
            BodyElement current = null;
            if (docNode.hasAttributes()) {
                NamedNodeMap map = docNode.getAttributes();
                Node type = map.getNamedItem("type");
                
                String stype = type.getTextContent();
                System.out.println("type is : " + stype);
                if (stype.equalsIgnoreCase("Ball")) {
                    current = createBallJoint(docNode);
                    jmeParentNode.attachBodyElement(current);
                } else if (stype.equalsIgnoreCase("Revolute")) {
                    current = createRevoluteJoint(docNode);
                    jmeParentNode.attachBodyElement(current);
                } else if (stype.equalsIgnoreCase("Revolute2")) {
                    current = createRevoluteJoint2(docNode);
                    jmeParentNode.attachBodyElement(current);
                } else if (stype.equalsIgnoreCase("Fixed")) {
                    current = createFixedJoint(docNode);
                    jmeParentNode.attachBodyElement(current);
                } else if (stype.equalsIgnoreCase("CYLINDRICAL")) {
                    current = createCylindricalLimb(docNode);
                    if (current != null) {
                        jmeParentNode.attachBodyElement(current);
                    }
                } else if (stype.equalsIgnoreCase("BOX")) {
                    current = createBoxLimb(docNode);
                    jmeParentNode.attachBodyElement(current);
                } else if (stype.equalsIgnoreCase("MESH")) {
                    String mesh = getAttrContent("mesh", map);
                    jmeParentNode.attachBodyElement(new MeshLimb(manager.loadModel(mesh)));
                }
            }
            if (current != null) {
                {
                    NodeList nl = docNode.getChildNodes();
                    for (int i = 0; i < nl.getLength(); ++i) {
                        Node n = nl.item(i);
                        constructChildren(n, current, manager);
                    }
                }
            }
        } else if (docNode.getNodeName().equals("target")) {
            jmeParentNode.attachBodyElement(createTarget(docNode));
            
        } else if (docNode.getNodeName().equals("attachmentpoint")) {
            NamedNodeMap map = docNode.getAttributes();
            String sname = getAttrContent("name", map);
            String slocation = getAttrContent("location", map);
            Vector3f loc = this.parseVector3f(slocation);
            String saxis1 = getAttrContent("alignmentAxis1", map);
            String saxis2 = getAttrContent("alignmentAxis2", map);
            Vector3f axis1 = Vector3f.UNIT_X;
            Vector3f axis2 = Vector3f.UNIT_Z;
            
            if (saxis1 != null) {
                axis1 = this.parseVector3f(saxis1);
            }
            if (saxis2 != null) {
                axis2 = this.parseVector3f(saxis2);
            }
            AttachmentPoint ap = new AttachmentPoint(sname, this.assetManager, loc, axis1, axis2);
            jmeParentNode.attachBodyElement(ap);
        } else if (docNode.getNodeName().equals("prefab")) {
            try {
                Prefab current = createPrefab(docNode.getAttributes(), this.assetManager, GlobalObjects.getInstance().getObjectsTypeCategory());
                if (current != null && current instanceof MeshObject) {
                    {
                        MeshObject mo = (MeshObject) current;
                        jmeParentNode.attachBodyElement(mo);
                        NodeList nl = docNode.getChildNodes();
                        for (int i = 0; i < nl.getLength(); ++i) {
                            Node n = nl.item(i);
                            constructChildren(n, mo, manager);
                        }
                    }
                }
            } catch (InstantiationException ex) {
                Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (docNode.getNodeName().equals("fuzzysystems")) {
            readFuzzySystems((Rig)jmeParentNode,docNode);
        } else if (docNode.getNodeName().equals("animationtargets")){
            readAnimationTargets((Rig)jmeParentNode,docNode);
        }
    }
    
    public Prefab createPrefab(NamedNodeMap map, AssetManager manager, ObjectTypeCategory objectsToCreate) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        
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
            RenderQueue.ShadowMode sm = RenderQueue.ShadowMode.valueOf(shadowMode);
            p.setShadowMode(sm);
        } catch (IllegalArgumentException ex) {
        }
        
        p.setLocalPrefabRotation(rotation);
        p.setLocalPrefabTranslation(translation);
        p.setLocalScale(scale);
        
        p.setOffset(offset);
        
        return p;
    }
    
    private BodyElement createBallJoint(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        String saxis = getAttrContent("axis", map);
        String slocation = getAttrContent("location", map);
        String srotation = getAttrContent("rotation", map);
        String sname = getAttrContent("name", map);
        String sminTheta = getAttrContent("minTheta", map);
        String smaxTheta = getAttrContent("maxTheta", map);
        String sminPhi = getAttrContent("minPhi", map);
        String smaxPhi = getAttrContent("maxPhi", map);
        String scurrentTheta = getAttrContent("currentTheta", map);
        String scurrentPhi = getAttrContent("currentPhi", map);
        
        Vector3f axis = parseVector3f(saxis);
        Vector3f location = parseVector3f(slocation);
        Vector3f rotation = parseVector3f(srotation);
        
        float mintheta = Float.parseFloat(sminTheta);
        float maxtheta = Float.parseFloat(smaxTheta);
        float minphi = Float.parseFloat(sminPhi);
        float maxphi = Float.parseFloat(smaxPhi);
        float currenttheta = Float.parseFloat(scurrentTheta);
        float currentphi = Float.parseFloat(scurrentPhi);
        
        BallJoint bj = new BallJoint(ballJointMaterial,
                rotation, location, axis,
                currenttheta, currentphi,
                minphi, maxphi,
                mintheta, maxtheta);
        bj.setName(sname);
        return bj;
    }
    
    private BodyElement createRevoluteJoint(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        
        String saxis = getAttrContent("axis", map);
        String slocation = getAttrContent("location", map);
        String sname = getAttrContent("name", map);
        
        String sgroup = getAttrContent("group", map);
        String sangle = getAttrContent("angle", map);
        String sminangle = getAttrContent("minAngle", map);
        String smaxangle = getAttrContent("maxAngle", map);
        
        String scentered = getAttrContent("centered", map);
        String sradius = getAttrContent("radius", map);
        String sheight = getAttrContent("height", map);
        String log = getAttrContent("log", map);
        boolean blog = Boolean.parseBoolean(log);
        String logScale = getAttrContent("logScale", map);
        float fScale = logScale.length() > 0 ? Float.parseFloat(logScale) : 1.0f;
        String logOffset = getAttrContent("logOffset", map);
        float fOffset = logOffset.length() > 0 ? Float.parseFloat(logOffset) : 0.0f;
        String logSymbol = getAttrContent("logSymbol", map);
        
        ColorRGBA jointColor = ColorRGBA.Blue;
        String sjointColor = getAttrContent("jointcolor", map);
        if (sjointColor.length() > 0) {
            jointColor = parseColor(sjointColor);
            
        }
        
        
        String logTranslation = getAttrContent("logTranslation", map);
        boolean blogTrans = Boolean.parseBoolean(logTranslation);
        
        boolean centered = scentered.length() > 0 ? Boolean.parseBoolean(scentered) : false;
        float radius = sradius.length() > 0 ? Float.parseFloat(sradius) : 0.1f;
        float height = sheight.length() > 0 ? Float.parseFloat(sheight) : 2.5f;
        
        Vector3f axis = parseVector3f(saxis);
        Vector3f location = parseVector3f(slocation);
        
        float angle = Float.parseFloat(sangle);
        float minangle = Float.parseFloat(sminangle);
        float maxangle = Float.parseFloat(smaxangle);
        
        RevoluteJoint rj = new RevoluteJoint(revJointMaterial, sname, sgroup, location, axis, minangle, maxangle, radius, height, centered);
        rj.setLogRotations(blog);
        rj.setLogOffset(fOffset);
        rj.setLogPostScale(fScale);
        rj.setLogSymbol(logSymbol);
        rj.setLogTranslation(blogTrans);
        
        String saxisx = getAttrContent("refaxisx", map);
        String saxisy = getAttrContent("refaxisy", map);
        String saxisz = getAttrContent("refaxisz", map);
        if (saxisx.length() > 0 && saxisy.length() > 0 && saxisz.length() > 0) {
            Vector3f xa = parseVector3f(saxisx);
            Vector3f ya = parseVector3f(saxisy);
            Vector3f za = parseVector3f(saxisz);
            rj.setInitialLocalFrame(xa, ya, za);
        }
        rj.setCurrentAngle(angle);
        
        String sChainWithChild = getAttrContent("chainwithchild", map);
        boolean chainwithchild = Boolean.parseBoolean(sChainWithChild);
        
        String sChainWithParent = getAttrContent("chainwithparent", map);
        boolean chainwithparent = Boolean.parseBoolean(sChainWithParent);
        
        rj.setChaining(chainwithchild, chainwithparent);
        String childName = getAttrContent("chainchildname", map);
        rj.setChainChildName(childName);
        rj.setJointColor(jointColor);
        rj.createVisualization(assetManager);
        
        
        return rj;
    }
    
    private BodyElement createRevoluteJoint2(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        
        String saxis1 = getAttrContent("axis1", map);
        String saxisLabel1 = getAttrContent("axislabel1", map);
        String saxis2 = getAttrContent("axis2", map);
        String saxisLabel2 = getAttrContent("axislabel2", map);
        String slocation = getAttrContent("location", map);
        String sname = getAttrContent("name", map);
        
        String sgroup = getAttrContent("group", map);
        String sangle1 = getAttrContent("angle1", map);
        String sangle1min = getAttrContent("minangle1", map);
        String sangle1max = getAttrContent("maxangle1", map);
        String sangle2 = getAttrContent("angle2", map);
        String sangle2min = getAttrContent("minangle2", map);
        String sangle2max = getAttrContent("maxangle2", map);
        String sradius = getAttrContent("radius", map);

        /*
         String log = getAttrContent("log", map);
         boolean blog = Boolean.parseBoolean(log);
         String logScale = getAttrContent("logScale", map);
         float fScale = logScale.length() > 0 ? Float.parseFloat(logScale) : 1.0f;
         String logOffset = getAttrContent("logOffset", map);
         float fOffset = logOffset.length() > 0 ? Float.parseFloat(logOffset) : 0.0f;
         String logSymbol = getAttrContent("logSymbol", map);
         String logName = getAttrContent("logName", map);
         String logTranslation = getAttrContent("logTranslation", map);
         boolean blogTrans = Boolean.parseBoolean(logTranslation);
         */
        
        float radius = sradius.length() > 0 ? Float.parseFloat(sradius) : 0.1f;
        
        
        Vector3f axis1 = parseVector3f(saxis1);
        Vector3f axis2 = parseVector3f(saxis2);
        Vector3f location = parseVector3f(slocation);
        
        float angle1 = Float.parseFloat(sangle1);
        float angle2 = Float.parseFloat(sangle2);
        
        float minAngle1 = Float.parseFloat(sangle1min);
        float maxAngle1 = Float.parseFloat(sangle1max);
        
        float minAngle2 = Float.parseFloat(sangle2min);
        float maxAngle2 = Float.parseFloat(sangle2max);
        
        RevoluteJointTwoAxis rj = new RevoluteJointTwoAxis(
                axis1,
                saxisLabel1,
                axis2,
                saxisLabel2,
                revJointMaterial,
                sname,
                sgroup,
                location,
                radius);
        
        String srotation = getAttrContent("rotation", map);
        if (srotation != null) {
            Vector3f rotation = parseVector3f(srotation);
            rj.setInitialLocalFrame(rotation);
        }
        String saxisx = getAttrContent("refaxisx", map);
        String saxisy = getAttrContent("refaxisy", map);
        String saxisz = getAttrContent("refaxisz", map);
        if (saxisx.length() > 0 && saxisy.length() > 0 && saxisz.length() > 0) {
            Vector3f xa = parseVector3f(saxisx);
            Vector3f ya = parseVector3f(saxisy);
            Vector3f za = parseVector3f(saxisz);
            rj.setInitialLocalFrame(xa, ya, za);
        }
        
        rj.setAngleConstraints(minAngle1, maxAngle1, minAngle2, maxAngle2);
        rj.setCurrentAngle1(angle1);
        rj.setCurrentAngle2(angle2);
        
        String sChainWithChild = getAttrContent("chainwithchild", map);
        boolean chainwithchild = Boolean.parseBoolean(sChainWithChild);
        
        String sChainWithParent = getAttrContent("chainwithparent", map);
        boolean chainwithparent = Boolean.parseBoolean(sChainWithParent);
        
        rj.setChaining(chainwithchild, chainwithparent);
        String childName = getAttrContent("chainchildname", map);
        rj.setChainChildName(childName);
        
        rj.createVisualization(assetManager);
        return rj;
    }
    
    private BodyElement createTarget(Node targetNode) {
        NamedNodeMap map = targetNode.getAttributes();
        
        
        String slocation = getAttrContent("location", map);
        String sname = getAttrContent("name", map);
        String srotation = getAttrContent("rotation", map);
        
        Vector3f location = parseVector3f(slocation);
        Vector3f rotation = parseVector3f(srotation);
        
        Handle result = new Handle();
        result.create(sname, this.assetManager, null);
        result.setTransformation(location, rotation);
        return result;
    }
    
    private BodyElement createFixedJoint(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        
        
        String slocation = getAttrContent("location", map);
        String sname = getAttrContent("name", map);
        String srotation = getAttrContent("rotation", map);
        
        Vector3f location = parseVector3f(slocation);
        Vector3f rotation = parseVector3f(srotation);
        return new FixedJoint(this.limbMaterial, sname, location, rotation);
    }
    
    private BodyElement createCylindricalLimb(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        String type = getAttrContent("type", map);
        if (type.equalsIgnoreCase("CYLINDRICAL")) {
            String sname = getAttrContent("name", map);
            String sradius = getAttrContent("radius", map);
            String sheight = getAttrContent("height", map);
            CylindricalLimb l = new CylindricalLimb(limbMaterial, sname, Float.parseFloat(sradius), Float.parseFloat(sheight));
            return l;
        } else {
            return null;
        }
    }
    
    private BodyElement createBoxLimb(Node docNode) {
        NamedNodeMap map = docNode.getAttributes();
        
        String sname = getAttrContent("name", map);
        String slength = getAttrContent("length", map);
        String swidth = getAttrContent("width", map);
        String sheight = getAttrContent("height", map);
        BoxLimb box = new BoxLimb(limbMaterial, sname, Float.parseFloat(slength), Float.parseFloat(swidth), Float.parseFloat(sheight));
        return box;
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
    
    private ColorRGBA parseColor(String vector3f) {
        if (vector3f == null) {
            return ColorRGBA.Pink;
        }
        int startIndex = vector3f.indexOf('[');
        int endIndex = vector3f.lastIndexOf(']');
        if (startIndex < 0 || endIndex < 0) {
            return ColorRGBA.Pink;
        }
        
        String[] xyz = vector3f.substring(startIndex + 1, endIndex).split(",");
        if (xyz.length == 3) {
            float x = Float.parseFloat(xyz[0]);
            float y = Float.parseFloat(xyz[1]);
            float z = Float.parseFloat(xyz[2]);
            return new ColorRGBA(x / 255.0f, y / 255.0f, z / 255.0f, 1.0f);
        } else if (xyz.length == 4) {
            float x = Float.parseFloat(xyz[0]);
            float y = Float.parseFloat(xyz[1]);
            float z = Float.parseFloat(xyz[2]);
            float a = Float.parseFloat(xyz[3]);
            return new ColorRGBA(x / 255.0f, y / 255.0f, z / 255.0f, a / 255.0f);
        } else {
            return ColorRGBA.Pink;
        }
    }
    
    private void readFuzzySystems(Rig rig, Node docNode) {
        NodeList nl = docNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("fuzzysystem")) {
                String name = getAttrContent("name", child.getAttributes());
                FuzzySystem system = new FuzzySystem(name);
                rig.setFuzzySystem(system);
                readFuzzySystem(system, child);
            }
        }
    }
    
    private void readFuzzySystem(FuzzySystem system, Node fuzzySystem) {
        NodeList nl = fuzzySystem.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("fuzzyinputs")) {
                readFuzzyInputs(system, child);
            }else if ( child.getNodeName().equals("fuzzyoutputs")){
                readFuzzyOutputs(system,child);
            }else if ( child.getNodeName().equals("fuzzyrules")){
                readFuzzyRules(system,child);
            }
        }
    }
    
    private void readFuzzyInputs(FuzzySystem system, Node inputs) {
        NodeList nl = inputs.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("input")) {
                NamedNodeMap atts = child.getAttributes();
                String name = getAttrContent("name", atts);
                FuzzyVariable variable = new FuzzyVariable(name);
                system.addFuzzyInput(variable);
                readMemberships(variable, child);
            }
        }
    }
    
    private void readFuzzyOutputs(FuzzySystem system, Node outputs) {
        NodeList nl = outputs.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("output")) {
                NamedNodeMap atts = child.getAttributes();
                String name = getAttrContent("name", atts);
                FuzzyVariable variable = new FuzzyVariable(name);
                system.addFuzzyOutput(variable);
                readMemberships(variable, child);
            }
        }
    }
    
    private void readMemberships(FuzzyVariable variable, Node memberships) {
        NodeList nl = memberships.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("membership")) {
                NamedNodeMap atts = child.getAttributes();
                String name = getAttrContent("name", atts);
                String type = getAttrContent("type", atts);
                if ("left".equals(type)) {
                    float center = XMLUtils.parseFloat("center", atts);
                    float right = XMLUtils.parseFloat("right", atts);
                    LeftSigmoidMemberShip lsm = new LeftSigmoidMemberShip(center, right, name);
                    variable.addMemberShip(lsm);
                }else if ("right".equals(type)){
                    float left = XMLUtils.parseFloat("left", atts);
                    float center = XMLUtils.parseFloat("center", atts);
                    RightSigmoidMemberShip rsm = new RightSigmoidMemberShip(left,center,name);
                    variable.addMemberShip(rsm);
                }else if ("triangular".equals(type)){
                    float left = XMLUtils.parseFloat("left", atts);
                    float center = XMLUtils.parseFloat("center", atts);
                    float right = XMLUtils.parseFloat("right", atts);
                    SigmoidMemberShip tms = new SigmoidMemberShip(left,center,right,name);
                    variable.addMemberShip(tms);
                }else if ("trapezoid".equals(type)){
                     float left = XMLUtils.parseFloat("left", atts);
                    float centerleft = XMLUtils.parseFloat("centerleft", atts);
                    float centerright = XMLUtils.parseFloat("centerright", atts);
                    float right = XMLUtils.parseFloat("right", atts);
                    TrapezoidMemberShip tms = new TrapezoidMemberShip(left,centerleft,centerright,right,name);
                    variable.addMemberShip(tms);
                }else if ("singleton".equals(type)){
                    float center = XMLUtils.parseFloat("center", atts);
                    SingletonMemberShip sms = new SingletonMemberShip(name,center);
                    variable.addMemberShip(sms);
                }
            }
        }
    }

    private void readFuzzyRules(FuzzySystem system, Node child) {
        NodeList nl = child.getChildNodes();
        for ( int i = 0 ; i < nl.getLength(); ++i)
        {
            Node block = nl.item(i);
            if ( block.getNodeName().equals("ruleblock")){
                String name = getAttrContent("name", block.getAttributes());
                FuzzyRuleBlock fblock = new FuzzyRuleBlock(system, name);
                
                NodeList ruleList = block.getChildNodes();
                for ( int j = 0 ; j < ruleList.getLength(); ++j)
                {
                    Node ruleNode = ruleList.item(j);
                    if ( ruleNode.getNodeName().equals("rule")){
                        String rule = ruleNode.getFirstChild().getTextContent();
                        FuzzyRule frule = new FuzzyRule(rule);
                        fblock.addFuzzyRule(frule);
                    }
                }
                system.addFuzzyRuleBlock(fblock);
            }
        }
    }

    private void readAnimationTargets(final Rig rig, Node animationTargets) {
        NodeList nl = animationTargets.getChildNodes();
        for ( int i = 0; i < nl.getLength(); ++i)
        {
            Node target = nl.item(i);
            if ( target.getNodeName().equals("target"))
            {
                NamedNodeMap map = target.getAttributes();
                final String key = getAttrContent("key", map);
                String prefab = getAttrContent("target", map);
                System.out.println("found key : " + key + "," + prefab);
                PrefabPlaceHolder placeHolder = new PrefabPlaceHolder(prefab,
                        new PrefabPlaceHolderCallback() {

                    public void prefabFound(Prefab actualPrefab, PrefabPlaceHolder placeHolder) {
                        System.out.println("Found the actual prefab : " + actualPrefab);
                        rig.setTarget(key, actualPrefab);
                        placeHolder.removeFromParent();
                    }
                }, 0.5f);
                rig.setTarget(key, placeHolder );
                rig.attachChild(placeHolder);
            }
        }
        
    }
}
