/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.controller;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.io.ParseFCL;

/**
 *
 * @author Koen
 */
public class ControllerLoader implements AssetLoader{

    public Object load(AssetInfo assetInfo) throws IOException {
        ParseFCL fclParser = new ParseFCL(assetInfo.openStream());
        FuzzySystem system = fclParser.getResult();
        return system;
    }
    
}
