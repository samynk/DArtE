/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Koen
 */
public class MaterialReader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        Material material = new Material(assetInfo.getManager(),
                "Common/MatDefs/Light/Lighting.j3md");
        material.setColor("Ambient", ColorRGBA.DarkGray);

        InputStreamReader isr = new InputStreamReader(assetInfo.openStream());
        BufferedReader br = new BufferedReader(isr);



        String line;
        while ((line = br.readLine()) != null) {
            String[] components = line.split("=");
            String key = components[0];
            String value = components[1];

            if (key.equals("texture0")) {
                Texture diff = loadTexture(assetInfo, value);
                diff.setWrap(WrapMode.Repeat);
                material.setTexture("DiffuseMap", diff);
                break;
            } else if ("texture1".equals(key)) {
                Texture normal = loadTexture(assetInfo, value);
                normal.setWrap(WrapMode.Repeat);
                material.setTexture("NormalMap", normal);
                break;
            } else if ("glossiness".equals(key)) {
                material.setFloat("Shininess", Float.parseFloat(value));
                break;
            }

        }

        return material;
    }

    public String parseTexture(String gmfDifTex) {
        if (gmfDifTex.startsWith("\"")) {
            gmfDifTex = gmfDifTex.substring(1, gmfDifTex.length() - 1);
        }
        int indexOfAbstract = gmfDifTex.indexOf("abstract::");
        if (indexOfAbstract >= 0) {
            return gmfDifTex.substring(indexOfAbstract + 10);
        } else {
            return gmfDifTex;
        }
    }

    public Texture loadTexture(AssetInfo info, String gmfDifTex) {
        String textureName = parseTexture(gmfDifTex);
        String currentFolder = info.getKey().getFolder();
        AssetKey key = new AssetKey(currentFolder + textureName);
        AssetInfo textureInfo = info.getManager().locateAsset(key);
        if (textureInfo != null) {
            try {
                return info.getManager().loadTexture(currentFolder + textureName);
            } catch (NullPointerException ex) {
                System.out.println("Error trying to load : " + textureName);
                System.exit(0);
                return null;
            }
        } else {
            return info.getManager().loadTexture("Textures/" + textureName);
        }
    }
}
