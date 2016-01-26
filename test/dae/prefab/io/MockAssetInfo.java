package dae.prefab.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import java.io.InputStream;

/**
 *
 * @author Koen Samyn
 */
public class MockAssetInfo extends AssetInfo{

    public MockAssetInfo(AssetManager manager, AssetKey key)
    {
        super(manager, key);
    }
    
    @Override
    public InputStream openStream() {
        System.out.println(key.getName());
        return getClass().getResourceAsStream(key.getName());
    }

}
