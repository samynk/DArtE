package dae.io.readers;

import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dae.GlobalObjects;
import dae.components.MeshComponent;
import dae.components.TransformComponent;
import dae.io.BinaryUtils;
import dae.prefabs.Prefab;
import dae.prefabs.types.ObjectType;
import dae.prefabs.types.ObjectTypeCategory;
import dae.project.Level;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class OVSceneImporter {

    private int majorVersion;
    private int minorVersion;
    private ObjectTypeCategory category;
    private AssetManager manager;
    private Node[] nodes = new Node[10];

    public Level readScene(File location) {
        Level level = new Level("Default", false);
        category = GlobalObjects.getInstance().getObjectsTypeCategory();
        manager = GlobalObjects.getInstance().getAssetManager();
        nodes[0] = level;
        try {
            InputStream bis = new BufferedInputStream(new FileInputStream(location));
            readHeader(bis);
            while (bis.available() > 0 )
            {
                readBlock(bis);
            }
            bis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OVSceneImporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OVSceneImporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return level;
    }

    private void readHeader(InputStream is) throws IOException {
        majorVersion = BinaryUtils.ReadShort(is);
        minorVersion = BinaryUtils.ReadShort(is);
    }

    private void readBlock(InputStream is) throws IOException {
        int blockId = BinaryUtils.ReadUnsignedShort(is);
        long gameObjectId = BinaryUtils.ReadUnsignedInt(is);
        long parentObjectId = BinaryUtils.ReadUnsignedInt(is);
        long nameLength = BinaryUtils.ReadUnsignedInt(is);
        String name = BinaryUtils.ReadLongString(is, (int) nameLength);

        Prefab prefab = null;
        switch (blockId) {
            case 0x100:
                ObjectType type = category.getObjectType("Standard", "Mesh");
                prefab = type.create(manager, name);
                storePrefab(prefab, (int) gameObjectId);
                linkToParent(prefab, (int) parentObjectId);
                break;
        }

        if (prefab == null) {
            return;
        }

        long componentLength = BinaryUtils.ReadUnsignedInt(is);
        System.out.println("Component length is : " + componentLength);
        if (componentLength == 0) {
            return;
        }

        do {
            long bytesRead = readComponent(is, prefab);
            componentLength -= bytesRead;
        } while (componentLength > 0);
        
        if ( componentLength < 0){
            System.out.println("Error during component read : " + componentLength);
        }
    }

    private long readComponent(InputStream is, Prefab prefab) throws IOException {
        int id = BinaryUtils.ReadUnsignedShort(is);
        long length = BinaryUtils.ReadUnsignedInt(is);
        switch (id) {
            case 1:
                readTransformComponent(is, prefab, length);
                break;
            case 2:
                readMeshComponent(is, prefab, length);
                break;
        }

        return length + 6;
    }

    private void readTransformComponent(InputStream is, Prefab prefab, long length) throws IOException {
        TransformComponent tc = (TransformComponent) prefab.getComponent("TransformComponent");
        if (tc == null) {
            return;
        }
        int id = BinaryUtils.ReadUnsignedShort(is);
        length -= (readTransformComponentParameter(is, id, tc) + 2);
        if (length > 0) {
            id = BinaryUtils.ReadUnsignedShort(is);
            length -= (readTransformComponentParameter(is, id, tc) + 2);
        }
        if (length > 0) {
            id = BinaryUtils.ReadUnsignedShort(is);
            length -= (readTransformComponentParameter(is, id, tc) + 2);
        }
        if (length != 0) {
            System.out.println("TransformComponent wrong length : " + length);
        }
        prefab.addPrefabComponent(tc);
    }

    private long readTransformComponentParameter(InputStream is, int id, TransformComponent tc) throws IOException {
        long length = BinaryUtils.ReadUnsignedInt(is);
        switch (id) {
            case 0:
                Vector3f translation = BinaryUtils.readOVFloat3(is);
                tc.setTranslation(translation);
                break;
            case 1:
                Vector3f scale = BinaryUtils.readOVFloat3(is);
                tc.setScale(scale);
                break;
            case 2:
                Vector3f r = BinaryUtils.readOVFloat3(is);
                Quaternion q = new Quaternion();
                q.fromAngles(r.x, r.y, r.z);
                tc.setRotation(q);
                break;
        }
        return length+4;
    }

    private void readMeshComponent(InputStream is, Prefab prefab, long length) throws IOException {
        MeshComponent mc = (MeshComponent) prefab.getComponent("MeshComponent");
        if (mc == null) {
            return;
        }
        int id = BinaryUtils.ReadUnsignedShort(is);
        length -= readMeshComponentParameter(is, id,mc) +2;
        if ( length > 0 )
        {
            id =  BinaryUtils.ReadUnsignedShort(is);
            length -= readMeshComponentParameter(is,id,mc)+2;
        }
        
         if (length != 0) {
            System.out.println("Mesh component wrong length : " + length);
        }
    }

    private long readMeshComponentParameter(InputStream is, int id, MeshComponent mc) throws IOException {
        switch(id){
            case 0:
                // mesh file location
            {
                long length = BinaryUtils.ReadUnsignedInt(is);
                String file = BinaryUtils.ReadLongString(is, (int)length);
                mc.setMeshFile(file);
                return length +4;
            }
            case 1:
            {
                long length = BinaryUtils.ReadUnsignedInt(is);
                long materialId = BinaryUtils.ReadUnsignedInt(is);
                System.out.println("Material id : " + materialId);
                return 8;
            }
        }
        return 0;
    }

    private void storePrefab(Prefab prefab, int gameObjectId) {
        if (gameObjectId > nodes.length) {
            Node[] nodeCopy = new Node[nodes.length * 2];
            System.arraycopy(nodes, 0, nodeCopy, 0, nodes.length);
            nodes = nodeCopy;
        }
        nodes[gameObjectId] = prefab;
    }

    private void linkToParent(Prefab prefab, int parentID) {
        Node parent = nodes[parentID];
        if (parent != null) {
            parent.attachChild(prefab);
        }
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}
