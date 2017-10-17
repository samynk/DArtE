package dae.io.export;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import static com.jme3.scene.VertexBuffer.Type.Binormal;
import dae.components.ComponentType;
import dae.components.MeshComponent;
import dae.components.PrefabComponent;
import dae.components.TransformComponent;
import dae.io.BinaryUtils;
import dae.io.readers.OVMReader.BLOCKTYPE;
import dae.io.writers.Exporter;
import dae.prefabs.Prefab;
import dae.prefabs.types.ObjectType;
import dae.project.Level;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Writes a scene to the OV format.
 *
 * @author Koen Samyn
 */
public class OVSceneExporter implements Exporter, SceneGraphVisitor {

    private File outputDir;
    private File location;
    private ArrayList<AssetKey> keys = new ArrayList<AssetKey>();
    private long blockId = 1;
    private OutputStream stream;
    private ByteArrayOutputStream byter;

    public void writeScene(File location, AssetManager manager, Level level) {
        try {
            keys.clear();
            this.outputDir = location.getParentFile();
            this.location = location;
            stream = new BufferedOutputStream(new FileOutputStream(location));
            byter = new ByteArrayOutputStream();
            writeSceneHeader(level);
            level.breadthFirstTraversal(this);
            stream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OVSceneExporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OVSceneExporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void writeSceneHeader(Level level) throws IOException {
        BinaryUtils.writeOVShort(stream, 0xDAE);
        BinaryUtils.writeOVShort(stream, 1);
    }

    /**
     *
     * @param spatial
     */
    public void visit(Spatial spatial) {
        if (spatial instanceof Prefab) {
            writePrefab((Prefab) spatial);
        }
        if (spatial instanceof Geometry) {
            writeMesh(spatial);
        }
    }

    private void writePrefab(Prefab prefab) {
        try {
            ObjectType ot = prefab.getObjectType();
            int cid = ot.getCID();
            if ( cid == -1) {
                return;
            }
            BinaryUtils.writeOVShort(stream, cid);
            BinaryUtils.writeOVInt(stream, blockId);
            prefab.setUserData("parentid", blockId);

            Node parent = prefab.getParent();
            if (parent.getUserData("parentid") != null) {
                long parentid = ((Prefab) parent).getUserData("parentid");
                BinaryUtils.writeOVInt(stream, parentid);
            } else {
                BinaryUtils.writeOVInt(stream, 0);
            }
            // delete non ascii characters
            //String nameToWrite = prefab.getName().replaceAll("[^\\x00-\\x7F]", "");
            BinaryUtils.writeOVMLongString(stream, prefab.getName());

            byter.reset();
            for (PrefabComponent pc : prefab.getComponents()) {
                writePrefabComponent(byter, pc, ot);
            }
            byte[] written = byter.toByteArray();
            BinaryUtils.writeUnsignedInt(stream, written.length);
            stream.write(written);

            blockId++;
        } catch (IOException ex) {
            Logger.getLogger(OVSceneExporter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void writePrefabComponent(OutputStream os, PrefabComponent pc, ObjectType type) throws IOException {
        ComponentType ct = type.getComponentType(pc.getId());
        if (ct != null) {
            switch (ct.getCID()) {
                case 0x01:
                    // Transform Component
                    TransformComponent tc = (TransformComponent) pc;
                    // ID is 1
                    BinaryUtils.writeOVShort(os, 0x01);
                    // block is 48 bytes long
                    BinaryUtils.writeUnsignedInt(os, 54); // 3*12 (float3) + 3 * 2 (id) + 3* 4 (length)
                    // Translation: ID: 0
                    BinaryUtils.writeOVShort(os, 0);
                    BinaryUtils.writeUnsignedInt(os, 12);

                    Vector3f t = tc.getTranslation();
                    BinaryUtils.writeOVFloat3(os, t);

                    // Scale: ID: 1
                    BinaryUtils.writeOVShort(os, 1);
                    BinaryUtils.writeUnsignedInt(os, 12);

                    Vector3f s = tc.getScale();
                    BinaryUtils.writeOVFloat3(os, s);

                    // Rotation: ID: 2
                    BinaryUtils.writeOVShort(os, 2);
                    BinaryUtils.writeUnsignedInt(os, 12);
                    float angles[] = new float[3];
                    tc.getRotation().toAngles(angles);
                    BinaryUtils.writeOVFloat3(os, angles[0], angles[1], angles[2]);
                    break;
                case 0x02:
                    // Mesh Component
                    MeshComponent mc = (MeshComponent) pc;
                    BinaryUtils.writeOVShort(os, 0x02);

                    String file = convertFileNameToOV(mc.getMeshFile());
                    
                    int length = 12 + BinaryUtils.utf8StringLength(file) + 4;
                    BinaryUtils.writeUnsignedInt(os, length);
                    // mesh file
                    BinaryUtils.writeOVShort(os, 0);
                    BinaryUtils.writeOVMLongString(os, file);

                    // material
                    BinaryUtils.writeOVShort(os, 1);
                    BinaryUtils.writeUnsignedInt(os, 4);
                    BinaryUtils.writeUnsignedInt(os, 0);
                    break;
            }
        }
    }

    private String flatten(String toFlatten) {
        toFlatten = toFlatten.replace('/', '_');
        toFlatten = toFlatten.replace('\\', '_');
        return toFlatten;
    }

    private void writeHeader(OutputStream stream, String name, Mesh mesh) throws IOException {
        stream.write(BLOCKTYPE.HEADER.ordinal());

        String nameToWrite = name.replaceAll("[^\\x00-\\x7F]", "");
        BinaryUtils.writeUnsignedInt(stream, nameToWrite.length() + 1 + 8);
        // length is the length of the string +1 + the vertex count and 
        // the index count as unsigned ints (4 bytes
        BinaryUtils.writeOVMAsciiString(stream, nameToWrite);

        BinaryUtils.writeUnsignedInt(stream, mesh.getVertexCount());
        BinaryUtils.writeUnsignedInt(stream, mesh.getIndicesAsList().size());
    }

    private void writeMesh(Spatial spatial) {
        Geometry g = (Geometry) spatial;
        System.out.println("Found a geometry  : " + g.getName());
        Mesh mesh = g.getMesh();
        AssetKey key = g.getKey();
        if (key != null && !(keys.contains(key))) {
            String fileName = key.getName();
            File meshFile = new File(outputDir, convertFileNameToOV(fileName));
            try {
                File parentDir = meshFile.getParentFile();
                if ( !parentDir.exists()){
                    parentDir.mkdirs();
                }
                OutputStream stream = new BufferedOutputStream(new FileOutputStream(meshFile));
                // major version
                stream.write(1);
                // minor version
                stream.write(1);
                System.out.println("Writing header");
                writeHeader(stream, spatial.getName(), mesh);

                // write buffer
                for (VertexBuffer vb : mesh.getBufferList()) {
                    switch (vb.getBufferType()) {
                        case Position:
                            writeVertices(BLOCKTYPE.POSITIONS, stream, vb);
                            break;
                        case Normal:
                            writeVertices(BLOCKTYPE.NORMALS, stream, vb);
                            break;
                        case Binormal:
                            writeVertices(BLOCKTYPE.BINORMALS, stream, vb);
                            break;
                        case TexCoord:
                            writeVertices(BLOCKTYPE.TEXCOORDS, stream, vb);
                            break;
                        case Index:
                            writeVertices(BLOCKTYPE.INDICES, stream, vb);
                            break;
                    }
                }
                stream.write(BLOCKTYPE.END.ordinal());
                stream.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger("DArtE").log(java.util.logging.Level.INFO, "Could not write to file :{0}", meshFile.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(java.util.logging.Level.INFO, "Error write {0} to file {1}.", new Object[]{spatial.getName(), meshFile.getAbsolutePath()});
            }
        }
    }

    private String convertFileNameToOV(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String stripped = fileName;
        if (dotIndex > - 1) {
            stripped = fileName.substring(0,dotIndex) +".ovm";
        }
        return stripped;
    }

    private void writeVertices(BLOCKTYPE blocktype, OutputStream stream, VertexBuffer vb) throws IOException {
        stream.write(blocktype.ordinal());

        BinaryUtils.writeUnsignedInt(stream, vb.getNumComponents() * vb.getNumElements() * vb.getFormat().getComponentSize());
        Buffer readOnlyBuffer = vb.getDataReadOnly();

        System.out.println("class:" + readOnlyBuffer.getClass().getName());
        if (readOnlyBuffer instanceof FloatBuffer) {
            FloatBuffer fb = (FloatBuffer) vb.getDataReadOnly();

            for (int i = 0; i < fb.limit(); ++i) {
                BinaryUtils.writeOVFloat(stream, fb.get(i));
            }
        } else if (readOnlyBuffer instanceof ShortBuffer) {
            ShortBuffer sb = (ShortBuffer) vb.getDataReadOnly();
            for (int i = 0; i < sb.limit(); ++i) {
                BinaryUtils.writeUnsignedInt(stream, sb.get(i));
            }
        }
    }
}
