package dae.io.export;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import static com.jme3.scene.VertexBuffer.Type.Binormal;
import dae.io.BinaryUtils;
import dae.io.game.GameSceneLoader;
import dae.io.readers.OVMReader.BLOCKTYPE;
import dae.io.writers.Exporter;
import dae.project.Level;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
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

    private File location;
    private ArrayList<AssetKey> keys = new ArrayList<AssetKey>();

    public void writeScene(File location, AssetManager manager, Level level) {
        keys.clear();
        File levelLocation = level.getAbsoluteLocation();
        Node result = new Node(level.getName());
        GameSceneLoader.loadScene(levelLocation, manager, result);

        this.location = location;
        result.depthFirstTraversal(this);
    }

    /**
     *
     * @param spatial
     */
    public void visit(Spatial spatial) {
        if (spatial instanceof Geometry) {
            Geometry g = (Geometry) spatial;
            System.out.println("Found a geometry  : " + g.getName());
            Mesh mesh = g.getMesh();
            AssetKey key = g.getKey();
            if (!(keys.contains(key))) {
                File meshFile = new File(location.getParentFile(), flatten(key.getName()) + ".ovm");
                try {
                    OutputStream stream = new BufferedOutputStream(new FileOutputStream(meshFile));
                    // major version
                    stream.write(1);
                    // minor version
                    stream.write(1);
                    System.out.println("Writing header");
                    writeHeader(stream, spatial.getName(), mesh);
                    
                    // write buffer
                    for ( VertexBuffer vb : mesh.getBufferList()){
                        switch( vb.getBufferType())
                        {
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

    private void writeVertices(BLOCKTYPE blocktype,OutputStream stream, VertexBuffer vb) throws IOException {
        stream.write(blocktype.ordinal());
        
        BinaryUtils.writeUnsignedInt(stream,vb.getNumComponents()*vb.getNumElements()*vb.getFormat().getComponentSize());
        Buffer readOnlyBuffer = vb.getDataReadOnly();
        
        System.out.println("class:" + readOnlyBuffer.getClass().getName());
        if (readOnlyBuffer instanceof FloatBuffer)
        {
            FloatBuffer fb  = (FloatBuffer)vb.getDataReadOnly();
            
            for(int i = 0 ; i < fb.limit();++i){
                BinaryUtils.writeOVFloat(stream, fb.get(i));
            }
        }else if (readOnlyBuffer instanceof ShortBuffer)
        {
            ShortBuffer sb = (ShortBuffer)vb.getDataReadOnly();
            for(int i = 0 ; i < sb.limit();++i){
                BinaryUtils.writeUnsignedInt(stream, sb.get(i));
            }
        }
    }
}
