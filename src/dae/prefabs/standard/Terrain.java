package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import dae.GlobalObjects;
import dae.prefabs.Prefab;
import dae.prefabs.types.ObjectType;

/**
 * The terrain prefab allows the user to quickly configure a terrain for the
 * game.
 *
 * @author Koen Samyn
 */
public class Terrain extends Prefab {

    private Material terrainMaterial;
    private AssetManager manager;
    private TerrainQuad terrain;
    private String texture1;
    private float tex1Scale = 32.0f;
    private String texture2;
    private float tex2Scale = 32.0f;
    private String texture3;
    private float tex3Scale = 32.0f;
    private String alphaMap;
    private String heightMap;
    private Texture tex1;
    private Texture tex2;
    private Texture tex3;
    private Texture texAlpha;
    private AbstractHeightMap loadedHeightMap;
    private Vector3f cachedLocalScale = Vector3f.UNIT_XYZ;

    /**
     * Creates a new terrain.
     */
    public Terrain() {
    }

    @Override
    public void create(String name, AssetManager manager, ObjectType objectType, String extraInfo) {
        super.setName(name);
        this.objectType = objectType;
        terrainMaterial = new Material(manager,
                "Common/MatDefs/Terrain/Terrain.j3md");
        this.manager = manager;
        loadTextures();
        createTerrain();
    }

    /**
     * @return the texture1
     */
    public String getTexture1() {
        return texture1;
    }

    /**
     * @param texture1 the texture1 to set
     */
    public void setTexture1(String texture1) {
        if (!texture1.equals(this.texture1)) {
            this.texture1 = texture1;
            if (manager != null) {
                loadTexture1();
            }
        }
        createTerrain();
    }

    /**
     * @return the texture2
     */
    public String getTexture2() {
        return texture2;
    }

    /**
     * @param texture2 the texture2 to set
     */
    public void setTexture2(String texture2) {
        if (!texture2.equals(this.texture2)) {
            this.texture2 = texture2;
            if (manager != null) {
                loadTexture2();
            }
        }
        createTerrain();
    }

    /**
     * @return the texture3
     */
    public String getTexture3() {
        return texture3;
    }

    /**
     * @param texture3 the texture3 to set
     */
    public void setTexture3(String texture3) {
        if (!texture3.equals(this.texture3)) {
            this.texture3 = texture3;
            if (manager != null) {
                loadTexture3();
            }
        }

        createTerrain();
    }

    /**
     * @return the alphaMap
     */
    public String getAlphaMap() {
        return alphaMap;

    }

    /**
     * @param alphaMap the alphaMap to set
     */
    public void setAlphaMap(String alphaMap) {
        if (!alphaMap.equals(this.alphaMap)) {
            this.alphaMap = alphaMap;
            if (manager != null) {
                loadAlphaMap();
            }
        }
        createTerrain();
    }

    /**
     * @return the heightMap
     */
    public String getHeightMap() {
        return heightMap;
    }

    /**
     * @param heightMap the heightMap to set
     */
    public void setHeightMap(String heightMap) {
        if (!heightMap.equals(this.heightMap)) {
            this.heightMap = heightMap;
            if (manager != null) {
                loadHeightMap();
            }
        }
        createTerrain();
    }

    private void createTerrain() {
        if (tex1 != null && tex2 != null && tex3 != null && alphaMap != null && heightMap != null) {

            if (terrain != null) {
                terrain.removeFromParent();
            }

            if (loadedHeightMap.load()) {
                int patchSize = 65;
                terrain = new TerrainQuad("my terrain", patchSize, loadedHeightMap.getSize() + 1, loadedHeightMap.getHeightMap());
                terrain.setLocalScale(cachedLocalScale);
                terrain.setMaterial(terrainMaterial);

                this.attachChild(terrain);

                TerrainLodControl control = new TerrainLodControl(terrain, GlobalObjects.getInstance().getCamera().getCamera());
                terrain.addControl(control);
            }
        }
    }

    /**
     * @return the tex1Scale
     */
    public float getTex1Scale() {
        return tex1Scale;
    }

    /**
     * @param tex1Scale the tex1Scale to set
     */
    public void setTex1Scale(float tex1Scale) {
        this.tex1Scale = tex1Scale;
        if (terrainMaterial != null) {
            terrainMaterial.setFloat("Tex1Scale", tex1Scale);
        }
    }

    /**
     * @return the tex2Scale
     */
    public float getTex2Scale() {
        return tex2Scale;
    }

    /**
     * @param tex2Scale the tex2Scale to set
     */
    public void setTex2Scale(float tex2Scale) {
        this.tex2Scale = tex2Scale;
        if (terrainMaterial != null) {
            terrainMaterial.setFloat("Tex2Scale", tex2Scale);
        }
    }

    /**
     * @return the tex3Scale
     */
    public float getTex3Scale() {
        return tex3Scale;
    }

    /**
     * @param tex3Scale the tex3Scale to set
     */
    public void setTex3Scale(float tex3Scale) {
        this.tex3Scale = tex3Scale;
        if (terrainMaterial != null) {
            terrainMaterial.setFloat("Tex3Scale", tex3Scale);
        }
    }

    public TerrainQuad getTerrainQuad() {
        return terrain;
    }

    /*
     @Override
     public void setLocalScale(float localScale) {
     if (terrain != null ){
     terrain.setLocalScale(localScale);
     } else{
     cachedLocalScale = new Vector3f(localScale,localScale,localScale);
     }   
     }

     @Override
     public void setLocalScale(float x, float y, float z) {
     if ( terrain != null ){
     terrain.setLocalScale(x, y, z); 
     }else{
     cachedLocalScale = new Vector3f(x,y,z);
     }   
     }

     @Override
     public void setLocalScale(Vector3f localScale) {
     if (terrain != null){
     terrain.setLocalScale(localScale);
     }else{
     cachedLocalScale = localScale;
     } 
     }

     @Override
     public Vector3f getLocalScale() {
     if ( terrain != null ){
     return terrain.getLocalScale();
     }else{
     return cachedLocalScale;
     }
        
     }
     */
    private void loadTexture1() {
        tex1 = manager.loadTexture(texture1);
        tex1.setWrap(WrapMode.Repeat);
        terrainMaterial.setTexture("Tex1", tex1);
        terrainMaterial.setFloat("Tex1Scale", getTex1Scale());
    }

    private void loadTexture2() {
        tex2 = manager.loadTexture(texture2);
        tex2.setWrap(WrapMode.Repeat);
        terrainMaterial.setTexture("Tex2", tex2);
        terrainMaterial.setFloat("Tex2Scale", getTex1Scale());
    }

    private void loadTexture3() {
        tex3 = manager.loadTexture(texture3);
        tex3.setWrap(WrapMode.Repeat);
        terrainMaterial.setTexture("Tex3", tex3);
        terrainMaterial.setFloat("Tex3Scale", getTex1Scale());
    }

    private void loadTextures() {
        if (texture1 != null) {
            loadTexture1();
        }
        if (texture2 != null) {
            loadTexture2();
        }
        if (texture3 != null) {
            loadTexture3();
        }
        if (heightMap != null) {
            loadHeightMap();
        }
        if (alphaMap != null) {
            loadAlphaMap();
        }
    }

    private void loadHeightMap() {
        Texture texhm = manager.loadTexture(heightMap);
        this.loadedHeightMap = new ImageBasedHeightMap(texhm.getImage());
    }

    private void loadAlphaMap() {
        texAlpha = manager.loadTexture(alphaMap);
        terrainMaterial.setTexture("Alpha", texAlpha);
    }
}
