/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import dae.GlobalObjects;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class ConnectorPrefab extends Prefab {

    private Vector3f[] points = new Vector3f[8];
    private static int connectNr = 0;

    public ConnectorPrefab() {
        this.setType("connector");
    }

    /**
     * Set the points of this connector prefab. The points are defined in world
     * space
     */
    public void setPoints(Vector3f[] ps, boolean clockWise) {
        System.arraycopy(ps, 0, points, 0, points.length);

        Vector2f[] texCoord = new Vector2f[8];
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);

        texCoord[4] = new Vector2f(1, 0);
        texCoord[5] = new Vector2f(0, 1);
        texCoord[6] = new Vector2f(1, 1);
        texCoord[7] = new Vector2f(0, 0);

        //int indices[]={0, 4, 1, 1, 4, 5, 1, 6, 2, 1, 5, 6, 3, 2, 7, 7, 2, 6, 0, 7, 3, 0, 4, 7};
        int indices[] = {0, 4, 1, 1, 4, 5, 1, 6, 2, 1, 5, 6, 3, 2, 7, 7, 2, 6, 3, 7, 0, 7, 4, 0};
        if (clockWise) {
            for (int i = 0; i < indices.length / 2; ++i) {
                int temp = indices[i];
                indices[i] = indices[indices.length - i - 1];
                indices[indices.length - i - 1] = temp;
            }
        }


        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(points));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Geometry geo = new Geometry("ConnectionMesh" + (++connectNr), mesh);
        AssetManager assetManager = GlobalObjects.getInstance().getAssetManager();
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture ref = assetManager.loadTexture("Textures/refPattern.png");
        ref.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", ref);
        mat.setColor("Color", ColorRGBA.White); // purple
        geo.setMaterial(mat);

        this.setOriginalMaterial(mat);
        this.attachChild(geo);
    }
}
