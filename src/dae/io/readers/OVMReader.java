package dae.io.readers;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import dae.io.BinaryUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An asset loader for the ovm file format.
 * @author Koen Samyn
 */
public class OVMReader implements AssetLoader{

    private long vertexCount;
    private long indexCount;

    

   

    

    public enum BLOCKTYPE{            END,
                               HEADER,
                               POSITIONS,
                               INDICES,
                               NORMALS,
                               BINORMALS,
                               TANGENTS,                
                               COLORS,
                               TEXCOORDS,
                               BLENDINDICES,
                               BLENDWEIGHTS,
                               ANIMATIONCLIPS,
                               SKELETON};

    private Mesh currentMesh;
    private String currentMeshName;
    
    /**
     * Loads a spatial from an ovm file.
     * @param assetInfo the location of the file.
     * @return a Spatial object with the correct mesh.
     * @throws IOException 
     */
    public Object load(AssetInfo assetInfo) throws IOException {
        InputStream is = new BufferedInputStream(assetInfo.openStream());
        
        int majorVersion = BinaryUtils.ReadByte(is);
        int minorVersion = BinaryUtils.ReadByte(is);
        
        System.out.println("OVM version: " + majorVersion + "," + minorVersion);
        
        int blockId = 0;
        do{
            blockId = BinaryUtils.ReadByte(is);
            System.out.println("First block id : " + blockId);
            long blockLength = BinaryUtils.ReadUnsignedInt(is);
            System.out.println("Blocklenght is : " + blockLength);
            
            
            BLOCKTYPE type = BLOCKTYPE.values()[blockId];
            switch(type){
                case END: break;
                case HEADER:
                    currentMesh = new Mesh();
                    readHeader(is, blockLength);break;
                case POSITIONS:readPositionBuffer(is, blockLength);break;
                case INDICES:readIndexBuffer(is,blockLength);break;
                case NORMALS:readNormalBuffer(is,blockLength);break;
                case TEXCOORDS:readTexcoordBuffer(is,blockLength);break;
                case BINORMALS:readBinormalBuffer(is,blockLength);break;
                default:BinaryUtils.Skip(is, blockLength);
            }
        }while(blockId != BLOCKTYPE.END.ordinal());
        
        currentMesh.updateBound();
        Geometry geo = new Geometry(currentMeshName);
        geo.setMesh(currentMesh);
        
        Material m = assetInfo.getManager().loadMaterial("Materials/ErrorMaterial.j3m");
               geo.setMaterial(m);
        return geo;
    }
    
    private void readHeader(InputStream is, long blockLength) throws IOException {
        currentMeshName = BinaryUtils.ReadOVMAsciiString(is);
        System.out.println("The name of the mesh is : " + currentMeshName);
        vertexCount = BinaryUtils.ReadUnsignedInt(is);
        indexCount = BinaryUtils.ReadUnsignedInt(is);
        System.out.println("Vertex count is : " + vertexCount);
        System.out.println("Vertex count is : " + indexCount);
    }

    private void readPositionBuffer(InputStream is, long blockLength) throws IOException {
       float vertices[] = new float[(int)vertexCount*3];
        int vi = 0;
        for( int i = 0 ; i < vertexCount; ++i){
            float x = BinaryUtils.ReadFloat(is);
            vertices[vi++]=x;
            float y = BinaryUtils.ReadFloat(is);
            vertices[vi++]=y;
            float z = BinaryUtils.ReadFloat(is);
            vertices[vi++]=z;
            
        }
        currentMesh.setBuffer(VertexBuffer.Type.Position,3,vertices);
    }
    
    private void readIndexBuffer(InputStream is, long blockLength) throws IOException {
        int indices[] = new int[(int)indexCount];
        int iindex = 0;
        for(int i = 0 ; i < indexCount; ++i){
            int index = (int)BinaryUtils.ReadUnsignedInt(is);
            indices[iindex++]=index;
        }
        currentMesh.setBuffer(VertexBuffer.Type.Index,3,indices);
    }
    
    private void readNormalBuffer(InputStream is, long blockLength) throws IOException {
       float vertices[] = new float[(int)vertexCount*3];
        int vi = 0;
        for( int i = 0 ; i < vertexCount; ++i){
            float x = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-x;
            float y = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-y;
            float z = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-z;
            
        }
        currentMesh.setBuffer(VertexBuffer.Type.Normal,3,vertices);
    }
    
    private void readBinormalBuffer(InputStream is, long blockLength) throws IOException {
        float vertices[] = new float[(int)vertexCount*3];
        int vi = 0;
        for( int i = 0 ; i < vertexCount; ++i){
            float x = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-x;
            float y = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-y;
            float z = BinaryUtils.ReadFloat(is);
            vertices[vi++]=-z;
            
        }
        currentMesh.setBuffer(VertexBuffer.Type.Binormal,3,vertices);
    }
    
     private void readTexcoordBuffer(InputStream is, long blockLength) throws IOException {
       float vertices[] = new float[(int)vertexCount*2];
        int vi = 0;
        for( int i = 0 ; i < vertexCount; ++i){
            float x = BinaryUtils.ReadFloat(is);
            vertices[vi++]=x;
            float y = BinaryUtils.ReadFloat(is);
            vertices[vi++]=y;
            
        }
        currentMesh.setBuffer(VertexBuffer.Type.TexCoord,2,vertices);
    }
}
