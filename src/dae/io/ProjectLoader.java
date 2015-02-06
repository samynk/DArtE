package dae.io;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import dae.GlobalObjects;
import dae.project.AssetLevel;
import dae.project.Project;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
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
public class ProjectLoader {

    public Project load(File file, AssetManager manager) {
        // create an empty project.
        Project p = new Project(true);

        p.setProjectLocation(file);
        if (!p.getKlatchDirectory().exists()) {
            p.getKlatchDirectory().mkdirs();
        }
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(new BufferedInputStream(new FileInputStream(file)));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();

            String projectName = root.getAttribute("name");
            if (projectName.length() > 0) {
                p.setProjectName(projectName);
            }
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if ("assetfolders".equals(n.getNodeName())) {
                    readAssetFolders(p, n);
                    p.addAssetFoldersToClassPath();
                    for (File assetFolder : p.getAssetLocations()) {
                        manager.registerLocator(assetFolder.getPath(), FileLocator.class);
                    }
                } else if ("levels".equals(n.getNodeName())) {
                    readLevels(p, n, manager);
                } 
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            p = new Project();
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } 
        return p;
    }

    private void readAssetFolders(Project p, Node n) {
        NodeList assetFolders = n.getChildNodes();

        p.addAssetFolder(p.getKlatchDirectory());
        for (int i = 0; i < assetFolders.getLength(); ++i) {
            Node assetFolder = assetFolders.item(i);

            Node fileNode = assetFolder.getFirstChild();
            if (fileNode != null) {
                if ("file".equals(fileNode.getNodeName())) {
                    p.addAssetFolder(new File(fileNode.getFirstChild().getTextContent()));
                }
            }
        }

        // TODO make asset folder for 
    }

    private void readLevels(Project p, Node n, AssetManager manager) {
        NodeList levels = n.getChildNodes();
        File projectDir = p.getProjectLocation().getParentFile();
        for (int i = 0; i < levels.getLength(); ++i) {
            Node level = levels.item(i);
            if ("level".equals(level.getNodeName())) {
                String type = getAttrContent("type", level.getAttributes());
                String levelName = getAttrContent("name", level.getAttributes());
                boolean relativeLocation = XMLUtils.parseBoolean("relativelocation", level.getAttributes());
                if (type == null || type.length() == 0 || type.equals("scene")) {
                    dae.project.Level l = new dae.project.Level(levelName, false);
                    NodeList levelChildren = level.getChildNodes();
                    for (int j = 0; j < levelChildren.getLength(); ++j) {
                        Node fileNode = levelChildren.item(j);
                        if (fileNode != null) {
                            if ("file".equals(fileNode.getNodeName())) {
                                String relativeFile = fileNode.getFirstChild().getTextContent();
                                File sceneFile = new File(projectDir, fileNode.getFirstChild().getTextContent());
                                SceneLoader.loadScene(sceneFile, manager, l, GlobalObjects.getInstance().getObjectsTypeCategory(),
                                        manager.loadMaterial("Materials/SelectionMaterial.j3m"));
                                l.setLocation(new File(relativeFile));
                                l.setRelativeLocation(relativeLocation);
                            }else if ("exportsettings".equals(fileNode.getNodeName())){
                                readExportSettings(fileNode, l);
                            }
                        }
                    }
                     p.addLevel(l);
                }else if (type.equals("klatch")){
                    NodeList levelChildren = level.getChildNodes();
                    AssetLevel al = null;
                    for (int j = 0; j < levelChildren.getLength(); ++j) {
                        Node fileNode = levelChildren.item(j);
                        if (fileNode != null) {
                            if ("file".equals(fileNode.getNodeName())) {
                                String path = fileNode.getFirstChild().getTextContent();
                                al = new AssetLevel(Paths.get(path));
                                al.setLocation(new File(path));
                                al.setRelativeLocation(relativeLocation);
                            }else if ("exportsettings".equals(fileNode.getNodeName())){
                                if ( al != null)
                                {
                                    readExportSettings(fileNode, al);
                                }
                            }
                        }
                    }
                    p.addLevel(al);
                }
            }
        }
    }

    private String getAttrContent(String key, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(key);
        return attr != null ? attr.getTextContent() : "";
    }

    private void readExportSettings(Node fileNode, dae.project.Level l) {
        boolean exportOnSave = XMLUtils.parseBoolean("exportonsave", fileNode.getAttributes());
        l.setExportOnSave(exportOnSave);
        NodeList exportNodes = fileNode.getChildNodes();
        for( int i = 0 ; i < exportNodes.getLength(); ++i)
        {
            Node exportNode = exportNodes.item(i);
            if ( exportNode == null)
                continue;
            if ( "exportfile".equals(exportNode.getNodeName()))
            {
                String key = getAttrContent("key", exportNode.getAttributes());
                String path = exportNode.getFirstChild().getTextContent();
                l.setExportLocation(key, new File(path));
            }
        }
        fileNode.getAttributes();
    }
}
