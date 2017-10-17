package dae.prefabs.shapes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 *
 * @author Koen Samyn
 */
public class LineShape extends Mesh{
    private Vector3f from = Vector3f.ZERO;
    private Vector3f to = Vector3f.ZERO;
    
    public LineShape(Vector3f from, Vector3f to){
        this.from = from;
        this.to = to;
        setMode(Mesh.Mode.Lines);
        reshape();
    }
    
    public void setFrom(Vector3f from){
        this.from = from;
    }
    
    public void setTo(Vector3f to){
        this.to = to;
    }
    
    private void reshape(){
        setBuffer(VertexBuffer.Type.Position, 3, new float[]{ from.x,from.y,from.z, to.x,to.y,to.z});
        setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
        this.updateBound();
    }

    public void setLine(Vector3f from, Vector3f to) {
        setFrom(from);
        setTo(to);
        reshape();
    }
}
