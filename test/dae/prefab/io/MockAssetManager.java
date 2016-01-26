package dae.prefab.io;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.FilterKey;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shader.Shader;
import com.jme3.shader.ShaderGenerator;
import com.jme3.shader.ShaderKey;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The mock asset manager provides an empty implementation of the asset manager
 * and returns empty spatials.
 *
 * @author Koen Samyn
 */
public class MockAssetManager implements AssetManager {

    public void addClassLoader(ClassLoader loader) {
    }

    public void removeClassLoader(ClassLoader loader) {
    }

    public List<ClassLoader> getClassLoaders() {
        return new ArrayList<ClassLoader>();
    }

    public void registerLoader(String loaderClassName, String... extensions) {
    }

    public void registerLocator(String rootPath, String locatorClassName) {
    }

    public void registerLoader(Class<? extends AssetLoader> loaderClass, String... extensions) {
    }

    public void unregisterLoader(Class<? extends AssetLoader> loaderClass) {
    }

    public void registerLocator(String rootPath, Class<? extends AssetLocator> locatorClass) {
    }

    public void unregisterLocator(String rootPath, Class<? extends AssetLocator> locatorClass) {
    }

    public void addAssetEventListener(AssetEventListener listener) {
    }

    public void removeAssetEventListener(AssetEventListener listener) {
    }

    public void clearAssetEventListeners() {
    }

    public void setAssetEventListener(AssetEventListener listener) {
    }

    public AssetInfo locateAsset(AssetKey<?> key) {
        return new MockAssetInfo(this, key);
    }

    public <T> T loadAsset(AssetKey<T> key) {
        return null;
    }

    public Object loadAsset(String name) {
        return null;
    }

    public Texture loadTexture(TextureKey key) {
        return new Texture2D();
    }

    public Texture loadTexture(String name) {
        return new Texture2D();
        
    }

    public AudioData loadAudio(AudioKey key) {
        return new MockAudioData();
    }

    public AudioData loadAudio(String name) {
        return new MockAudioData();
    }

    public Spatial loadModel(ModelKey key) {
        Box box1 = new Box(1,1,1);
        Geometry g = new Geometry("box",box1);
        return g;
    }

    public Spatial loadModel(String name) {
        Box box1 = new Box(1,1,1);
        Geometry g = new Geometry("box",box1);
        return g;
    }

    public Material loadMaterial(String name) {
        return new Material();
    }

    public Shader loadShader(ShaderKey key) {
        return new Shader();
    }

    public BitmapFont loadFont(String name) {
        return new BitmapFont();
    }

    public FilterPostProcessor loadFilter(FilterKey key) {
        return new FilterPostProcessor();
    }

    public FilterPostProcessor loadFilter(String name) {
        return new FilterPostProcessor();
    }

    public void setShaderGenerator(ShaderGenerator generator) {
        
    }

    public ShaderGenerator getShaderGenerator(EnumSet<Caps> caps) {
        return null;
    }
}
