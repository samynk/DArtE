package dae.animation.rig.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import dae.GlobalObjects;
import dae.animation.rig.AnimationController;
import dae.animation.rig.AnimationListControl;
import dae.animation.rig.InputConnector;
import dae.animation.rig.OutputConnector;
import dae.animation.rig.PrefabPlaceHolderCallback;
import dae.animation.rig.Rig;
import dae.io.SceneLoader;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import dae.prefabs.standard.PrefabPlaceHolder;
import dae.prefabs.types.ObjectType;
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

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        Rig result = null;
        try {
            InputStream is = assetInfo.openStream();
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
            result = readRig(is, assetInfo.getManager());
            
        } catch (ParserConfigurationException | SAXException | IllegalAccessException | NumberFormatException | ClassNotFoundException | InstantiationException ex) {
            Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public Rig readRig(InputStream is, AssetManager assetManager) throws IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException, SAXException, NumberFormatException, IllegalAccessException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        ObjectType objectType = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Animation", "Rig");
        Rig result = (Rig) objectType.createDefault(assetManager, "rig", true);
        result.setName("rig");
        NodeList nl = root.getChildNodes();

        SceneLoader.readNodeChildren(nl, assetManager, GlobalObjects.getInstance().getObjectsTypeCategory(), result);
        
        for (int i = 0; i < nl.getLength(); ++i) {
            org.w3c.dom.Node n = nl.item(i);
            switch(n.getNodeName())
            {
                case "fuzzysystems": readFuzzySystems(result, n);break;
                case "animationtargets": readAnimationTargets(result, n);break;
                case "controllerconnections":readControllerConnections(result,n);break;
            }
        }
        
        return result;
    }

    
    private String getAttrContent(String key, NamedNodeMap map) {
        Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private void readFuzzySystems(Rig rig, Node docNode) {
        NodeList nl = docNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("fuzzysystem")) {
                String name = getAttrContent("name", child.getAttributes());
                FuzzySystem system = new FuzzySystem(name,false);
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
            } else if (child.getNodeName().equals("fuzzyoutputs")) {
                readFuzzyOutputs(system, child);
            } else if (child.getNodeName().equals("fuzzyrules")) {
                readFuzzyRules(system, child);
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
                } else if ("right".equals(type)) {
                    float left = XMLUtils.parseFloat("left", atts);
                    float center = XMLUtils.parseFloat("center", atts);
                    RightSigmoidMemberShip rsm = new RightSigmoidMemberShip(left, center, name);
                    variable.addMemberShip(rsm);
                } else if ("triangular".equals(type)) {
                    float left = XMLUtils.parseFloat("left", atts);
                    float center = XMLUtils.parseFloat("center", atts);
                    float right = XMLUtils.parseFloat("right", atts);
                    SigmoidMemberShip tms = new SigmoidMemberShip(left, center, right, name);
                    variable.addMemberShip(tms);
                } else if ("trapezoid".equals(type)) {
                    float left = XMLUtils.parseFloat("left", atts);
                    float centerleft = XMLUtils.parseFloat("centerleft", atts);
                    float centerright = XMLUtils.parseFloat("centerright", atts);
                    float right = XMLUtils.parseFloat("right", atts);
                    TrapezoidMemberShip tms = new TrapezoidMemberShip(left, centerleft, centerright, right, name);
                    variable.addMemberShip(tms);
                } else if ("singleton".equals(type)) {
                    float center = XMLUtils.parseFloat("center", atts);
                    SingletonMemberShip sms = new SingletonMemberShip(name, center);
                    variable.addMemberShip(sms);
                }
            }
        }
    }

    private void readFuzzyRules(FuzzySystem system, Node child) {
        NodeList nl = child.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node block = nl.item(i);
            if (block.getNodeName().equals("ruleblock")) {
                String name = getAttrContent("name", block.getAttributes());
                FuzzyRuleBlock fblock = new FuzzyRuleBlock(system, name);

                NodeList ruleList = block.getChildNodes();
                for (int j = 0; j < ruleList.getLength(); ++j) {
                    Node ruleNode = ruleList.item(j);
                    if (ruleNode.getNodeName().equals("rule")) {
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
        for (int i = 0; i < nl.getLength(); ++i) {
            Node target = nl.item(i);
            if (target.getNodeName().equals("target")) {
                NamedNodeMap map = target.getAttributes();
                final String key = getAttrContent("key", map);
                String prefab = getAttrContent("target", map);
                PrefabPlaceHolder placeHolder = new PrefabPlaceHolder(prefab,
                        new PrefabPlaceHolderCallback() {
                    public void prefabFound(Prefab actualPrefab, PrefabPlaceHolder placeHolder) {
                        rig.setTarget(key, actualPrefab);
                        placeHolder.removeFromParent();
                    }
                }, 0.5f);
                rig.setTarget(key, placeHolder);
                rig.attachChild(placeHolder);
            }
        }

    }

    private void readControllerConnections(Rig rig, Node connections) {
        NodeList nl = connections.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node controller = nl.item(i);
            if (controller.getNodeName().equals("controller")) {
                NamedNodeMap map = controller.getAttributes();
                String systemname = XMLUtils.getAttribute("system", map);
                String name = XMLUtils.getAttribute("name", map);
                String inputToSystem = XMLUtils.getAttribute("inputToSystem", map);
                String outputOfSystem = XMLUtils.getAttribute("outputOfSystem", map);
                AnimationController ac = new AnimationController(name);
                ac.setControllerInputName(inputToSystem);
                ac.setControllerOutputName(outputOfSystem);

                NodeList ios = controller.getChildNodes();
                for (int j = 0; j < ios.getLength(); ++j) {
                    Node io = ios.item(j);
                    NamedNodeMap ioMap = io.getAttributes();
                    if (io.getNodeName().equals("input")) {
                        String className = XMLUtils.getAttribute("class", ioMap);

                        try {
                            Class inputClass = Class.forName(className);
                            if (InputConnector.class.isAssignableFrom(inputClass)) {
                                InputConnector ic = (InputConnector) inputClass.newInstance();
                                ic.fromXML(io);
                                ac.setInput(ic);
                            }
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else if (io.getNodeName().equals("output")) {
                        String className = XMLUtils.getAttribute("class", ioMap);
                        try {
                            Class outputClass = Class.forName(className);
                            if (OutputConnector.class.isAssignableFrom(outputClass)) {
                                OutputConnector oc = (OutputConnector) outputClass.newInstance();
                                oc.fromXML(io);
                                ac.setOutput(oc);
                            }
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(RigLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                AnimationListControl alc = rig.getControl(AnimationListControl.class);
                if (alc == null) {
                    alc = new AnimationListControl();
                    rig.addControl(alc);
                }
                alc.addAnimationController(ac);
            }
        }
    }
}
