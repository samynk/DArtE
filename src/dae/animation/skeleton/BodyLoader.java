/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.skeleton;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
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
public class BodyLoader implements AssetLoader {

    private Material limbMaterial;
    private Material ballJointMaterial;
    private Material revJointMaterial;
    private Material apMaterial;
    private AssetManager assetManager;

    public Object load(AssetInfo assetInfo) throws IOException {
        Body body = null;
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

            try {
                float bendAngleFlat = Float.parseFloat(root.getAttribute("bendAngleFlat"));
                float bendAngleFlatSupport = Float.parseFloat(root.getAttribute("bendAngleFlatSupport"));
                float bendAngleUp = Float.parseFloat(root.getAttribute("bendAngleDown"));
                float bendAngleDown = Float.parseFloat(root.getAttribute("bendAngleUp"));

                float lowerLegLength = Float.parseFloat(root.getAttribute("lowerLegLength"));
                float upperLegLength = Float.parseFloat(root.getAttribute("upperLegLength"));
                body = new Body(assetInfo.getManager(), this.apMaterial, bendAngleFlat, bendAngleFlatSupport,
                        bendAngleUp, bendAngleDown, lowerLegLength, upperLegLength);
                body.setSkeletonFile(assetInfo.getKey().toString());
            } catch (Exception ex) {
                body = new Body(assetInfo.getManager(), this.apMaterial);
                body.setSkeletonFile(assetInfo.getKey().toString());
            }





            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                constructChildren(n, body, assetInfo.getManager());
            }

            String mesh = root.getAttribute("mesh");
            if (mesh != null) {
                Spatial model = assetInfo.getManager().loadModel(mesh);
                model.setName("charactermesh");
                body.attachCharacter(mesh, model);
            }
            //body.createControllers();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        }
        return body;
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
        }
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
        //String stargetaxis = getAttrContent("targetAxis", map);
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
        String logName = getAttrContent("logName", map);

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
        rj.setLogName(logName);
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
}
