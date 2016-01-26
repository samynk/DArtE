/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import dae.prefabs.Prefab;

/**
 *
 * @author Koen
 */
public class MessageBoardObject extends Prefab {

    private String text = "message_board";
    private BitmapFont font;
    private BitmapText bmText;
    private ColorRGBA fontColor = ColorRGBA.White;

    public MessageBoardObject() {
    }

    @Override
    public String getPrefix() {
        return "message";
    }

    @Override
    public void create( AssetManager manager, String extraInfo) {
        // Load font and create the Text
        font = manager.loadFont("Font/billboard.fnt");
        bmText = new BitmapText(font, true);
        bmText.setSize(0.5f);
        bmText.setText(text);
        bmText.setQueueBucket(RenderQueue.Bucket.Transparent);
        bmText.setColor(fontColor);

        this.attachChild(bmText);
        this.setShadowMode(shadowMode.Off);
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        bmText.setText(text);
        this.text = text;
    }

    public void setFontColor(ColorRGBA color) {
        fontColor = color;
        bmText.setColor(fontColor);
    }

    public ColorRGBA getFontColor() {
        return fontColor;
    }
}
